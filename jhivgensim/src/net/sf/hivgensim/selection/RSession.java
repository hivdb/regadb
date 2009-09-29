package net.sf.hivgensim.selection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;


public abstract class RSession {
	
	private final static String R_PATH = "R";
	private final static boolean DEBUG = true;
	
	private StringBuilder script = new StringBuilder();
		
	public void execute() throws IOException{
		File batchFile = File.createTempFile("batch", ".R");
		File outputFile = new File(batchFile.getName() + "out");
		if (!DEBUG){
			batchFile.deleteOnExit();
			outputFile.deleteOnExit();
		}
		writeBufferToBatchFile(batchFile);
		
		String cmd = R_PATH + " --no-restore --no-save CMD BATCH " + batchFile.getAbsolutePath();
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(cmd);
		try {
			p.waitFor();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
//		FileOutputStream fos = new FileOutputStream(new File("/home/gbehey0/pi/program.R"));
//		fos.write(script.toString().getBytes());
//		fos.flush();
//		fos.close();
	}
	
	private void writeBufferToBatchFile(File batchFile) throws FileNotFoundException{
		PrintStream ps = new PrintStream(batchFile);
		ps.println(script);
		ps.flush();
		ps.close();
	}
	
	protected void addCommand(String command){
		script.append(command);
	}
	
	protected void addCommandln(String command){
		script.append(command);
		script.append("\n");
	}

}
