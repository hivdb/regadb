package net.sf.hivgensim.fastatool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class FastaToNexus{

	private FastaScanner scanner;
	private PrintStream out;
	private ArrayList<FastaSequence> sequences;

	public FastaToNexus(String fastaFilename, String nexusFilename)
			throws FileNotFoundException {
		scanner = new FastaScanner(new File(fastaFilename));
		out = new PrintStream(new FileOutputStream(new File(nexusFilename)));
		sequences = new ArrayList<FastaSequence>();
	}

	public void convert() {
		int seqlength = -1;
		while (scanner.hasNextSequence()) {
			FastaSequence fs = scanner.nextSequence();
			if (seqlength != fs.getSequence().length()) {
				if (seqlength == -1) {
					seqlength = fs.getSequence().length();
				} else {
//					 seqs not the same length
					throw new IllegalArgumentException();
				}
			}
			sequences.add(fs);
		}
		
		generateHeader(sequences.size(),seqlength);
		generateDataMatrix(seqlength);
		generatePaupBlock();
		generateFooter();
	}
	
	private void generateDataMatrix(int nchar){
		for(int i = 0; i < nchar;i+=100){
			generateDataMatrixBlock(i);
		}
		out.println(";");
		out.println("end;");
	}
	
	private void generateDataMatrixBlock(int start){
		int end = Math.min(start+100, sequences.get(0).getSequence().length());
		
		for(FastaSequence fs : sequences){
			out.print(formatId(fs.getId(),20));
			int i = start;
			for(; i < end-20; i+=20){
				out.print(fs.getSequence().substring(i,i+20)+" ");
			}
			if(i < end){
				out.print(fs.getSequence().substring(i,end)+" ");
			}
			out.println();
		}
		out.println();
	}
	
	private void generateHeader(int ntax, int nchar){
		out.println("#NEXUS");
		out.println();
		for(FastaSequence fs : sequences){
			out.println("[Name: "+formatId(fs.getId(),20)+"Len: "+nchar+"\tCheck: 0]");
		}
		out.println();
		out.println("begin data;");
		out.println("dimensions ntax="+ntax+" nchar="+nchar+";");
		out.println("format datatype=DNA interleave missing=- gap=?;");
		out.println("matrix");
	}
	
	private void generateFooter(){
//		out.println(";");
//		out.println("end;");
		out.close();
	}
	
	private void generatePaupBlock(){
		out.println("begin paup;");
//	    charset piresist=232-234 280-282 292-294 304-306 355-357 361-363 388-390 394-396 406-408 412-414;
//	    charset rtiresist=562-564 571-573 634-636 640-642 649-651 661-663 664-666 670-672 739-741 748-750 757-759 763-765 784-786 787-789 892-894 982-984 991-993
//	 	1003-1005 1009-1011 1069-1071 1084-1086 1096-1098;
//	    exclude piresist;
//	    exclude rtiresist;
		out.println("\tset criterion=distance;");
		out.println("\tdset distance=hky;");
		out.println("\tdset rates=gamma;");
		out.println("\tdset shape=0.5;");
		out.println("\tlog file=tree.log;");
		out.println("\tnj treefile=tree.nj.phy;");
		out.println("\tsavetrees file=tree.phy brlens format=phylip;");
		out.println("\tquit;\nend;");
	}
	
	private String formatId(String id, int length){
		String result = id.replace(">","").replace("-","").replace(" ", "");
		while(result.length() < length){
			result = " "+result;
		}
		return result.substring(0, length) + " ";
	}
	
	
	
	
}
