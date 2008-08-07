package cpp2java.scripts.pretty;

import java.io.File;
import java.io.IOException;

public class CheckAll {
	public static void main(String [] args) {
		String srcDir = args[0];
		String reportDir = args[1];
		
		CheckAll ca = new CheckAll();
		ca.run(srcDir, reportDir);
	}
	
	public void run(String srcDir, String reportDir) {
		try {
			CheckPrettyBatch.run(srcDir + File.separatorChar + "Wt", reportDir + File.separatorChar + "Wt.csv");
			CheckPrettyBatch.run(srcDir + File.separatorChar + "web", reportDir+ File.separatorChar + "web.csv");
			CheckPrettyBatch.run(srcDir + File.separatorChar + "Wt/Chart", reportDir+ File.separatorChar + "Chart.csv");
			CheckPrettyBatch.run(srcDir + File.separatorChar + "Wt/Ext", reportDir+ File.separatorChar + "Ext.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
