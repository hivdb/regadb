package cpp2java.scripts.pretty;

import java.io.File;
import java.io.IOException;

public class CheckAll {
	public static void main(String [] args) {
		String srcDir = args[0];
		String reportDir = args[1];
		try {
			CheckPrettyBatch.run(srcDir + File.separatorChar + "wt", reportDir + File.separatorChar + "wt.csv");
			CheckPrettyBatch.run(srcDir + File.separatorChar + "web", reportDir+ File.separatorChar + "web.csv");
			CheckPrettyBatch.run(srcDir + File.separatorChar + "Chart", reportDir+ File.separatorChar + "Chart.csv");
			CheckPrettyBatch.run(srcDir + File.separatorChar + "Ext", reportDir+ File.separatorChar + "Ext.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
