package net.sf.hivgensim.bayesian;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Learner {

	private double paramCost;
	private double ess;
	private int coolings;
	private int iterations;
	private String learnerPath;
	private int bootstraps;

	public Learner(String learnerPath, int iterations, int coolings,
			double ess, double paramCost, int bootstraps) {
		this.learnerPath = learnerPath;
		this.iterations = iterations;
		this.coolings = coolings;
		this.ess = ess;
		this.paramCost = paramCost;
		this.bootstraps = bootstraps;
	}

	public List<BCourseNetwork> learnNetworks(int bootNumber, List<List<String>> currentValues) throws IOException {
		String vdFilename = bootNumber+".vd";
		List<BCourseNetwork> result = new ArrayList<BCourseNetwork>();
		for (int i = 0; i < bootstraps; i++) {
			String prefix = bootNumber+"-"+i;
			writeIdtFile(prefix, currentValues);
			String command = learnerPath+" "+vdFilename+" "+prefix+".idt "+currentValues.size()+" "+
			ess+" "+prefix+".report "+prefix+".str "+iterations+" "+coolings+" "+
			paramCost+" "+prefix+".progress";
			System.err.println("Running "+command);
			Process child = Runtime.getRuntime().exec(command);

			InToOutConnector outConn = new InToOutConnector(child.getInputStream(), System.err);
			InToOutConnector errConn = new InToOutConnector(child.getErrorStream(), System.err);
			File progressFile = new File(prefix+".progress");
			while(!progressFile.exists()){
				try{
					if(child.exitValue()!=0){
						System.err.println("learner not exited with 0: bootstrap "+bootNumber+" run "+i);
						break;
					}
				} catch(IllegalThreadStateException e){ /* working */ }
				Thread.yield(); 
			}
			InToOutConnector progress = new InToOutConnector(new FileInputStream(progressFile), System.err);
			outConn.start();
			errConn.start();
			progress.start();

//			outConn.stop();
//			errConn.stop();
//			progress.stop();
//			child.destroy();
//			strFile.createNewFile();
//			vdFile.createNewFile();
//			FileWriter strFileWriter = new FileWriter(strFile);
//			FileWriter vdFileWriter = new FileWriter(vdFile);
//			if(bootNumber == 0){
//				strFileWriter.write("4\n1 0\n1 1 0\n0 1 1\n0 0\n");
//				vdFileWriter.write("drug\ty\tn\n1A\ty\tn\n2B\ty\tn\n3C\ty\tn\n");
//			} else if(bootNumber == 1){
//				strFileWriter.write("3\n1 0\n0 1 0\n0 0\n");
//				vdFileWriter.write("drug\ty\tn\n1A\ty\tn\n2B\ty\tn\n");
//			} else {
//				strFileWriter.write("3\n1 0\n0 1 0\n");
//				vdFileWriter.write("drug\ty\tn\n1A\ty\tn\n");
//			}
//			strFileWriter.close();
//			vdFileWriter.close();
			try {
				if(child.waitFor() != 0){
					System.err.println("learner not exited with 0: bootstrap "+bootNumber+" run "+i);
					System.exit(1);
				}
			} catch (InterruptedException e) {
				System.err.println("interrupted waiting for learner");
				e.printStackTrace();
				System.exit(1);
			}
			outConn.stop();
			errConn.stop();
			progress.stop();
			File strFile = new File(prefix+".str");
			File vdFile = new File(vdFilename);
			BCourseNetwork newNet = new BCourseNetwork(new FileInputStream(strFile), new FileInputStream(vdFile));
			result.add(newNet);
		}
		return result;
	}

	private void writeIdtFile(String prefix, List<List<String>> allValues) throws IOException {
		String filename = prefix+".idt";
		FileWriter fw = new FileWriter(filename);
		Random rand = new Random();
		for (int i = 0; i < allValues.size(); i++) {
			String line = "";
			List<String> selected = allValues.get(rand.nextInt(allValues.size()));
			int comma=0;
			for (String value : selected) {
				if(comma!=0){
					line += "\t";
				}
				line += value;
				comma++;
			}
			fw.write(line+"\n");
		}
		fw.close();
	}


}
