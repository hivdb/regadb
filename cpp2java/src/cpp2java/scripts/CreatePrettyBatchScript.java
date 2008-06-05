package cpp2java.scripts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CreatePrettyBatchScript {
	public static void main(String [] args ) {
		String workDirectory = args[0];
		String scriptDirectory = args[1];
		
		try {
			FileWriter script = new FileWriter(scriptDirectory + File.separatorChar + "no_override_batch.sh");
			
			File dirF = new File(workDirectory);
	    	for(File f : dirF.listFiles()) {
	    	    if(f.isFile() && f.getAbsolutePath().endsWith(".C")) {
	    	    	String preproc = "./preprocess.sh " + f.getAbsolutePath() 
	    	    		+ " -c cat > " + f.getAbsolutePath() + ".preproc";
	    	    	script.write(preproc+"\n");
	    	    	
	    	    	String pretty = "./pretty.sh /home/plibin0/projects/oink/oink-stack/elsa/ccparse " 
	    	    		+ f.getAbsolutePath() + ".ii 2> " 
	    	    		+ f.getAbsolutePath() + ".ii.pretty.err";
	    	    	script.write(pretty+"\n");
	    	    }
	    	}
	    	
	    	script.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}