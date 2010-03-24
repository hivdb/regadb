package be.kuleuven.rega.research.zehava;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

import net.sf.hivgensim.queries.framework.IQuery;

public class EtravirineOutput implements IQuery<EtravirineRecord>{
	
//	private List<String> mutations = Arrays.asList("90I","98G","100I","101P","101E","101H","101N","101Q","101R","103H","103N","103S","103T","106M","106A","106I","108I","138K","138Q","138A","138G","179F","179D","179E","179T","181C","181I","181V","181S","188L","188H","188C","188F","190E","190A","190Q","190S","190C","190T","190V","221Y","225H","227C","227L","230L","234I","236L","238N","238T"); 
	private boolean first = true;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	private PrintStream sequences;
	private PrintStream general;
	
	public EtravirineOutput(boolean append) {
		try {
			sequences = new PrintStream(new FileOutputStream("/home/gbehey0/zehava/sequences.csv",append));
			general = new PrintStream(new FileOutputStream("/home/gbehey0/zehava/general.csv",append));
			first = !append;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void process(EtravirineRecord input) {
		if(first){
			printHeader();
			first = false;
		}
		general.print(input.getPatientId()+",");
		general.print(input.isNaive()+",");
		general.print(input.isNnrti()+",");
		general.print(input.isNvp()+",");
		general.print(input.isEfv()+",");
		general.print(sdf.format(input.getSampleDate())+",");
		general.print(input.getCountry().replace(",","")+",");
		general.print(input.getSubtype()+",");
		general.print(input.getGender()+",");
		general.print(input.getTransmissionGroup()+",");
		general.print(input.getDataset());
		general.println();
		sequences.println(input.getPatientId()+","+input.getConcatenatedSequence());		
	}
	
	private void printHeader(){
		general.println("patient_id,naive,nnrti,nvp,efv,sample date,country,subtype,gender,transmission group,dataset");
		sequences.println("id,sequences");
	}
	
	public void close() {
		sequences.flush();
		sequences.close();
	}

}
