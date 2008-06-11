package cpp2java.scripts.pretty;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class CreatePrettyBatchScript {
	public static void main(String [] args ) {
		String workDirectory = args[0];
		String scriptDirectory = args[1];
		String ccparse = args[2];
		
		run(workDirectory, scriptDirectory, ccparse);
	}
	
	public static void run(String workDirectory, String scriptDirectory, String ccparse) {
		StringTokenizer st = new StringTokenizer(workDirectory, File.separatorChar+"");
		String lastToken = "";
		while(st.hasMoreTokens()) {
			lastToken = st.nextToken();
		}
		
		String options="";
		boolean first = true;
		
		try {
			FileWriter script = new FileWriter(scriptDirectory + File.separatorChar + lastToken + "_no_override_batch.sh");
			
			File dirF = new File(workDirectory);
			File dirS = new File(scriptDirectory);
	    	for(File f : dirF.listFiles()) {
	    	    if(f.isFile() && f.getAbsolutePath().endsWith(".C")) {
	    	    	String preproc = dirS.getAbsolutePath() +"/preprocess.sh "+ options +" " + f.getAbsolutePath() 
	    	    		+ " -c cat > " + f.getAbsolutePath() + ".preproc";
	    	    	script.write(preproc+"\n");
	    	    	
	    	    	String pretty = dirS.getAbsolutePath() +"/pretty.sh "
	    	    	    + ccparse +" "
	    	    		+ f.getAbsolutePath() + ".ii 2> " 
	    	    		+ f.getAbsolutePath() + ".ii.pretty.err";
	    	    	script.write(pretty+"\n");
	    	    	
	    	    	if(first){
	    	    	    options = "-n";
	    	    	    first = false;
	    	    	}
	    	    }
	    	}
	    	
	    	script.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
