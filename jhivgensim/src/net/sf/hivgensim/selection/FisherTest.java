package net.sf.hivgensim.selection;

import java.io.File;
import java.io.IOException;

import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;

public class FisherTest extends RSession {
	
	private File table;
	private String targetName;
	private String[] mutationNames;
	
	public FisherTest(File table, String targetName, String[] mutationNames){
		this.table = table;
		this.targetName = targetName.replace("/", ".");
		this.mutationNames = mutationNames;
	}
	
	public void execute() throws IOException{
		addCommandln("invisible(options(echo = TRUE))");
		addCommandln("data = read.csv('"+table.getAbsolutePath()+"', na.strings=\"\")");
		addCommandln("p = c()");
		addCommandln("targetselect = data$" + targetName + " == 'y'");
		for(String mutString : mutationNames){
			addFisherTestCommand(mutString);
		}	
		addCommandln("p <- p.adjust(p, method=\"fdr\")");
		addCommandln("write.table(p,\""+table.getAbsolutePath()+"."+targetName+".FT\",col.names=F,sep=\",\")");
		addCommandln("write.table(names(p)[p < 0.05],\""+table.getAbsolutePath()+"."+targetName+".FT.names\",sep=\",\",quote=F,col.names=F,row.names=F)");
		super.execute();
	}
	
	private void addFisherTestCommand(String mutationName){
		addCommandln("varselect = data$" + mutationName + " == 'y'");
		addCommandln("if ((length(levels(as.factor((targetselect[!is.na(varselect)])))) < 2) | (length(levels(as.factor(varselect))) < 2)) {");
		addCommandln("  p[\"" + mutationName + "\"] = 1.");
		addCommandln("} else {");
		addCommandln("  p[\"" + mutationName + "\"] = fisher.test(varselect, targetselect)$p.value");
		addCommandln("}");
	}
	
	public static void main(String[] args) throws IOException {
		String[] mutations = {"PR1L","PR1P","PR1S","PR2H","PR2K","PR2Q","PR3F","PR3I","PR3L","PR3N","PR3V","PR4A","PR4C","PR4D","PR4E","PR4F","PR4G","PR4H","PR4I","PR4K","PR4L","PR4M","PR4N","PR4P","PR4Q","PR4R","PR4S","PR4T","PR4V","PR4W","PR4Y","PR5A","PR5C","PR5D","PR5E","PR5F","PR5G","PR5H","PR5I","PR5K","PR5L","PR5M","PR5N","PR5P","PR5Q","PR5R","PR5S","PR5T","PR5V","PR5W","PR5Y","PR6A","PR6C","PR6D","PR6E","PR6F","PR6G","PR6H","PR6I","PR6K","PR6L","PR6M","PR6N","PR6P","PR6Q","PR6R","PR6S","PR6T","PR6V","PR6W","PR6Y","PR7A","PR7C","PR7D","PR7E","PR7F","PR7G","PR7H","PR7I","PR7K","PR7L","PR7M","PR7N","PR7P","PR7Q","PR7R","PR7S","PR7T","PR7V","PR7W","PR7Y","PR8A","PR8C","PR8D","PR8E","PR8F","PR8G","PR8H","PR8I","PR8K","PR8L","PR8M","PR8N","PR8P","PR8Q","PR8R","PR8S","PR8T","PR8V","PR8W","PR8Y","PR9A","PR9C","PR9D","PR9F","PR9G","PR9H","PR9I","PR9L","PR9N","PR9P","PR9Q","PR9R","PR9S","PR9T","PR9V","PR9Y","PR10F","PR10H","PR10I","PR10L","PR10M","PR10P","PR10R","PR10T","PR10V","PR10Y","PR11A","PR11F","PR11I","PR11L","PR11V","PR12A","PR12D","PR12E","PR12I","PR12K","PR12M","PR12N","PR12P","PR12Q","PR12R","PR12S","PR12T","PR12V","PR13A","PR13I","PR13K","PR13L","PR13M","PR13T","PR13V","PR14E","PR14G","PR14I","PR14K","PR14M","PR14N","PR14Q","PR14R","PR14T","PR15I","PR15L","PR15M","PR15V","PR16A","PR16E","PR16G","PR16R","PR17A","PR17D","PR17E","PR17G","PR18E","PR18H","PR18I","PR18K","PR18L","PR18P","PR18Q","PR18R","PR19A","PR19E","PR19I","PR19K","PR19L","PR19M","PR19P","PR19Q","PR19R","PR19S","PR19T","PR19V","PR20I","PR20K","PR20L","PR20M","PR20R","PR20T","PR20V","PR21D","PR21E","PR21G","PR21K","PR21Q","PR22A","PR22V","PR23I","PR23L","PR24F","PR24I","PR24L","PR24M","PR25D","PR25E","PR25N","PR26T","PR27G","PR28A","PR29D","PR29V","PR30D","PR30N","PR31P","PR31T","PR32I","PR32L","PR32V","PR33F","PR33I","PR33L","PR33V","PR34A","PR34D","PR34E","PR34G","PR34K","PR34Q","PR35D","PR35E","PR35G","PR35K","PR35N","PR35Q","PR36I","PR36L","PR36M","PR36T","PR36V","PR37A","PR37C","PR37D","PR37E","PR37G","PR37H","PR37I","PR37K","PR37N","PR37P","PR37Q","PR37R","PR37S","PR37T","PR37Y","PR38F","PR38I","PR38L","PR38M","PR38W","PR39A","PR39I","PR39L","PR39P","PR39Q","PR39S","PR39T","PR40E","PR40G","PR40K","PR40R","PR40T","PR40V","PR41I","PR41K","PR41N","PR41R","PR41S","PR41T","PR42T","PR42W","PR43E","PR43I","PR43K","PR43N","PR43Q","PR43R","PR43T","PR44P","PR44T","PR45I","PR45K","PR45Q","PR45R","PR45T","PR46I","PR46L","PR46M","PR46T","PR46V","PR47A","PR47I","PR47M","PR47T","PR47V","PR48A","PR48G","PR48M","PR48R","PR48T","PR48V","PR49E","PR49G","PR49T","PR4del","PR50I","PR50L","PR50V","PR51A","PR51G","PR51L","PR52G","PR52L","PR53F","PR53L","PR53Y","PR54A","PR54I","PR54L","PR54M","PR54S","PR54T","PR54V","PR55K","PR55L","PR55N","PR55R","PR56L","PR56V","PR57K","PR57L","PR57R","PR58E","PR58K","PR58L","PR58Q","PR59F","PR59L","PR59Y","PR5del","PR60D","PR60E","PR60N","PR60W","PR61D","PR61E","PR61H","PR61K","PR61N","PR61Q","PR61R","PR61S","PR61W","PR62I","PR62M","PR62T","PR62V","PR62W","PR63A","PR63C","PR63D","PR63E","PR63F","PR63G","PR63H","PR63I","PR63L","PR63M","PR63N","PR63P","PR63Q","PR63R","PR63S","PR63T","PR63V","PR63W","PR63Y","PR64I","PR64L","PR64M","PR64V","PR64W","PR65D","PR65E","PR65K","PR65W","PR66F","PR66I","PR66L","PR66V","PR66W","PR67C","PR67D","PR67E","PR67F","PR67G","PR67H","PR67S","PR67W","PR67Y","PR68E","PR68G","PR68W","PR69H","PR69I","PR69K","PR69N","PR69Q","PR69R","PR69T","PR69W","PR69Y","PR70E","PR70K","PR70N","PR70Q","PR70R","PR70T","PR71A","PR71I","PR71L","PR71Q","PR71T","PR71V","PR72E","PR72I","PR72K","PR72L","PR72M","PR72Q","PR72R","PR72T","PR72V","PR73A","PR73C","PR73G","PR73Q","PR73S","PR73T","PR74A","PR74K","PR74P","PR74Q","PR74S","PR74T","PR75I","PR75Q","PR75V","PR76L","PR76Q","PR76V","PR77I","PR77L","PR77Q","PR77T","PR77V","PR78E","PR78G","PR78Q","PR78R","PR79A","PR79D","PR79H","PR79P","PR79Q","PR79R","PR79S","PR80R","PR80T","PR81L","PR81P","PR81R","PR82A","PR82C","PR82F","PR82I","PR82L","PR82M","PR82R","PR82S","PR82T","PR82V","PR83D","PR83N","PR83R","PR83S","PR84I","PR84R","PR84V","PR85I","PR85L","PR85M","PR85R","PR85V","PR86G","PR86R","PR87K","PR87R","PR88D","PR88G","PR88N","PR88R","PR88S","PR88T","PR89I","PR89L","PR89M","PR89R","PR89T","PR89V","PR90F","PR90L","PR90M","PR90P","PR91A","PR91I","PR91N","PR91P","PR91S","PR91T","PR91V","PR92E","PR92H","PR92K","PR92L","PR92P","PR92Q","PR92R","PR93F","PR93I","PR93L","PR93M","PR93P","PR93T","PR93V","PR94G","PR94P","PR94S","PR94T","PR95A","PR95C","PR95F","PR95L","PR95P","PR95R","PR95S","PR95T","PR95V","PR95W","PR96A","PR96P","PR96S","PR96T","PR97A","PR97F","PR97I","PR97L","PR97P","PR97S","PR97T","PR98A","PR98D","PR98F","PR98G","PR98H","PR98I","PR98K","PR98L","PR98N","PR98P","PR98R","PR98S","PR98T","PR98V","PR98Y","PR98del","PR99A","PR99C","PR99F","PR99H","PR99I","PR99L","PR99P","PR99R","PR99S","PR99T","PR99V","PR99Y","PR99del"};
//		String[] other = {"id","APV","APV/r","ATV","ATV/r","DRV","DRV/r","FPV","FPV/r","IDV","IDV/r","LPV/r","NFV","PI","RTV","SQV","SQV/r","TPV","TPV/r"};
		FisherTest ft;
		for(String drug : DrugGenericUtils.getPI()){
			ft = new FisherTest(new File("/home/gbehey0/pi/pi.selected.csv"),drug,mutations);
			ft.execute();
		}		
	}
	
	

}
