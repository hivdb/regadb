package cpp2java.scripts.pretty;

import java.io.File;

public class CreateAllScripts {
	public static void main(String [] args) {
		String srcDirectory = args[0];
		String scriptDirectory = args[1];
		String ccparse = args[2];
		
		CreateAllScripts cas = new CreateAllScripts();
		cas.run(srcDirectory, scriptDirectory, ccparse);
	}
	
	public void run(String srcDirectory, String scriptDirectory, String ccparse) {
		CreatePrettyBatchScript.run(srcDirectory + File.separatorChar + "Wt", scriptDirectory, ccparse);
		CreatePrettyBatchScript.run(srcDirectory + File.separatorChar + "Wt/Ext", scriptDirectory, ccparse);
		CreatePrettyBatchScript.run(srcDirectory + File.separatorChar + "Wt/Chart", scriptDirectory, ccparse);
		CreatePrettyBatchScript.run(srcDirectory + File.separatorChar + "web", scriptDirectory, ccparse);
	}
}