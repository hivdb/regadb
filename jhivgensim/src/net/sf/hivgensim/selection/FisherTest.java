package net.sf.hivgensim.selection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.hivgensim.preprocessing.MutationTable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class FisherTest extends AbstractSelection {
	
	private RSession rSession = new RSession();
	private String targetName;
	private double pValue;
	
	public FisherTest(File complete, File selection, String targetName, double pValue) throws FileNotFoundException{
		super(complete,selection);
		this.targetName = targetName.replace("/", ".");		
		this.pValue = pValue;
	}
	
	public void select() {
		rSession.addCommandln("invisible(options(echo = TRUE))");
		rSession.addCommandln("data = read.csv('"+getCompleteTable().getAbsolutePath()+"', na.strings=\"\")");
		rSession.addCommandln("p = c()");
		rSession.addCommandln("infoNames = c()");
		rSession.addCommandln("targetselect = data$" + targetName + " == 'y'");
		for(String mutString : getNames()){
			if(!MutationTable.MUT_PATTERN.matcher(mutString).matches()){
				rSession.addCommandln("infoNames = c(infoNames,\""+mutString+"\")");
			}else{
				addFisherTestCommand(mutString);
			}
		}	
		rSession.addCommandln("p <- p.adjust(p, method=\"fdr\")");
		rSession.addCommandln("write(infoNames,file=\""+getSelectionTable().getAbsolutePath()+"\",append=F,sep=\",\")");
		rSession.addCommandln("write.table(names(p)[p < "+pValue+"],\""+getSelectionTable().getAbsolutePath()+"\",sep=\",\",quote=F,col.names=F,row.names=F,append=T)");
		try{
			rSession.execute();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addFisherTestCommand(String mutationName){
		rSession.addCommandln("varselect = data$" + mutationName + " == 'y'");
		rSession.addCommandln("if ((length(levels(as.factor((targetselect[!is.na(varselect)])))) < 2) | (length(levels(as.factor(varselect))) < 2)) {");
		rSession.addCommandln("  p[\"" + mutationName + "\"] = 1.");
		rSession.addCommandln("} else {");
		rSession.addCommandln("  p[\"" + mutationName + "\"] = fisher.test(varselect, targetselect)$p.value");
		rSession.addCommandln("}");
	}	
	
	protected boolean[] calculateSelection(){
		//will not be called by select method of this class
		throw new NotImplementedException();
	}

}
