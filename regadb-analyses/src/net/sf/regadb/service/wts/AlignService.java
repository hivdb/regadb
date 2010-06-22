package net.sf.regadb.service.wts;

import net.sf.regadb.db.AnalysisStatus;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.IAnalysis;

public class AlignService extends AbstractService implements IAnalysis{
	private int viralIsolateIi;
	private String sequences;
	private String genome;
	
	private Login login;

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
		for(int i=0; i<lines.length; ++i){
			if(lines[i].startsWith("sequence=")){
				String ii = lines[i].substring("sequence=".length());
				System.out.println("Sequence: '"+ ii +"'");
			}
			else if(lines[i].startsWith("protein=")){
				String f[] = lines[i].split(",");
				String protein = f[0].substring("protein=".length());
				String start = f[1].substring("start=".length());
				String stop = f[2].substring("end=".length());
				String muts[] = f[3].substring("mutations=".length()).split(" ");
			}
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
