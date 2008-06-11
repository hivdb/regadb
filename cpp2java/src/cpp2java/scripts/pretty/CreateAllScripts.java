package cpp2java.scripts.pretty;

import java.io.File;

public class CreateAllScripts {
	public static void main(String [] args) {
		String srcDirectory = args[0];
		String scriptDirectory = args[1];
		String ccparse = args[2];
		
		CreatePrettyBatchScript.run(srcDirectory + File.separatorChar + "wt", scriptDirectory, ccparse);
		CreatePrettyBatchScript.run(srcDirectory + File.separatorChar + "web", scriptDirectory, ccparse);
		CreatePrettyBatchScript.run(srcDirectory + File.separatorChar + "Ext", scriptDirectory, ccparse);
		CreatePrettyBatchScript.run(srcDirectory + File.separatorChar + "Chart", scriptDirectory, ccparse);
	}
}
