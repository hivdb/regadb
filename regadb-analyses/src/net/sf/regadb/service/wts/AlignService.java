package net.sf.regadb.service.wts;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaInsertionId;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaMutationId;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.service.IAnalysis;

public class AlignService extends AbstractService implements IAnalysis{
	private int viralIsolateIi;
	private String sequences;
	private String organismName;
	
	private Login login = null;
	
	private ViralIsolate viralIsolate = null;
	private Genome genome = null;
	
	private SequenceDb sequenceDb;
	
	private static final Pattern mutationPattern = Pattern.compile("^([^0-9]*)([0-9]+)([^0-9]*)$");

	public AlignService(ViralIsolate viralIsolate, String organismName, SequenceDb sequenceDb){
		this.viralIsolateIi = viralIsolate.getViralIsolateIi();
		this.organismName = organismName;
		this.sequences = ViralIsolateAnalysisHelper.toFasta(viralIsolate);
		this.sequenceDb = sequenceDb;
	}
	
	public AlignService(ViralIsolate viralIsolate, Genome genome, SequenceDb sequenceDb){
		this.viralIsolate = viralIsolate;
		this.genome = genome;
		this.organismName = genome.getOrganismName();
		this.sequences = ViralIsolateAnalysisHelper.toFasta(viralIsolate);
		this.sequenceDb = sequenceDb;
	}
	
	@Override
	protected void init() {
		setService("regadb-align");
		
		getInputs().put("genome", organismName);
		getInputs().put("sequences", sequences);
		getOutputs().put("mutations", null);
	}

	@Override
	protected void processResults() throws ServiceException {
		String lines[] = getOutputs().get("mutations").split("\n");
		
		ViralIsolate vi = this.viralIsolate;
		Genome g = this.genome;
		Transaction t = null;
		
		if(login != null){
			t = login.createTransaction();
			vi = t.getViralIsolate(viralIsolateIi);
			g = t.getGenome(organismName);
		}
		
		NtSequence nt = null;
		
		for(int i=0; i<lines.length; ++i){
			if(lines[i].startsWith("sequence=")){
				String label = lines[i].substring("sequence=".length());
				
				for(NtSequence nti : vi.getNtSequences()){
					if(nti.getLabel().replace(' ','_').replace(',','-').equals(label)){
						nt = nti;
						break;
					}
				}
			}
			else if(lines[i].startsWith("protein=") && nt != null){
				String f[] = lines[i].split(",");
				String protein = f[0].substring("protein=".length());
				String start = f[1].substring("start=".length());
				String stop = f[2].substring("end=".length());
				String muts[] = f[3].substring("mutations=".length()).trim().split(" ");
				
				Protein pr = null;
				for(OpenReadingFrame orf : g.getOpenReadingFrames()){
					for(Protein pri : orf.getProteins()){
						if(pri.getAbbreviation().equals(protein)){
							pr = pri;
							break;
						}
					}
					if(pr != null)
						break;
				}
				if(pr != null){
					int prevPos = 0;
					short insertionOrder = 0;
					
					AaSequence aaseq = new AaSequence(nt, pr,
							Short.parseShort(start), Short.parseShort(stop));
					
					for(String mut : muts){
						int pos = mut.indexOf(';');
						if(pos == -1)
							continue;
						String aaMut = mut.substring(0,pos);
						String ntMut = mut.substring(pos+1);
						
						
						Matcher m = mutationPattern.matcher(aaMut);
						if(!m.matches())
							System.err.println(aaMut);
	
						String aaref = m.group(1);
						pos = Integer.parseInt(m.group(2));
						String aatar = m.group(3);
							
						m = mutationPattern.matcher(ntMut);
						if(!m.matches())
							System.err.println(ntMut);
	
						String ntref = m.group(1).toLowerCase();
						String nttar = m.group(3).toLowerCase();
						
						
						if(aaref.equals("-")){
							AaInsertion aains = new AaInsertion();
							aains.setAaInsertion(aatar);
							aains.setNtInsertionCodon(nttar);
							
							if(pos == prevPos)
								++insertionOrder;
							else
								insertionOrder=0;
							
							prevPos = pos;
							
							aains.setId(new AaInsertionId((short)pos, aaseq, insertionOrder));
							aaseq.getAaInsertions().add(aains);
						}
						else{
							AaMutation aamut = new AaMutation();
							aamut.setAaReference(aaref);
							aamut.setAaMutation(aatar);
							aamut.setNtReferenceCodon(ntref);
							aamut.setNtMutationCodon(nttar);
							aamut.setId(new AaMutationId((short)pos, aaseq));
							aaseq.getAaMutations().add(aamut);
						}
					}
					nt.setAligned(true);
					nt.getAaSequences().add(aaseq);
				}
			}
		}
		
		if(t != null)
			t.commit();
		
		if(sequenceDb != null){
			for(NtSequence ntseq : vi.getNtSequences())
				sequenceDb.sequenceAligned(ntseq);
		}
	}

    //IAnalysis methods
    public AnalysisStatus getStatus() 
    {
        return null;
    }

    public String getUser() 
    {
        return null;
    }

    public void kill() 
    {
        
    }

    public void pause()
    {
        
    }

    public Long removeFromLogging()
    {
        return 10000L;
    }

    public void launch(Login sessionSafeLogin) throws ServiceException{
        this.login = sessionSafeLogin;
        launch();
    }
}
