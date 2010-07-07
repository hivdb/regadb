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
import net.sf.regadb.service.IAnalysis;

public class AlignService extends AbstractService implements IAnalysis{
	private int viralIsolateIi;
	private String sequences;
	private String genome;
	
	private Login login;
	
	private static final Pattern mutationPattern = Pattern.compile("^([^0-9]*)([0-9]+)([^0-9]*)$");

	public AlignService(ViralIsolate viralIsolate, String genome){
		this.viralIsolateIi = viralIsolate.getViralIsolateIi();
		this.genome = genome;
		this.sequences = ViralIsolateAnalysisHelper.toFasta(viralIsolate);
	}
	
	@Override
	protected void init() {
		setService("regadb-align");
		
		getInputs().put("genome", genome);
		getInputs().put("sequences", sequences);
		getOutputs().put("mutations", null);
	}

	@Override
	protected void processResults() throws ServiceException {
		String lines[] = getOutputs().get("mutations").split("\n");
		
		Transaction t = login.createTransaction();
		ViralIsolate vi = t.getViralIsolate(viralIsolateIi);
		Genome g = t.getGenome(genome);
		
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
				
				System.out.println("Sequence: '"+ label +"'");
			}
			else if(lines[i].startsWith("protein=") && nt != null){
				String f[] = lines[i].split(",");
				String protein = f[0].substring("protein=".length());
				String start = f[1].substring("start=".length());
				String stop = f[2].substring("end=".length());
				String muts[] = f[3].substring("mutations=".length()).split(" ");
				
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
		
		t.commit();
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
