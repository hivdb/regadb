package be.kuleuven.rega.research.conserved;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ConservedRegionsJMolOutputter implements ConservedRegionsOutputter {
	private File directory;
	
	public ConservedRegionsJMolOutputter(File directory) {
		this.directory = directory;
	}
	
	public void export(String group, Map<Integer, MutationsPrevalence> prevalences, int amountOfSequences) {
		try {
			FileWriter fw = new FileWriter(new File(directory.getAbsolutePath()+File.separatorChar+group+".jmol"));
		
			fw.append("select *;\n");
			fw.append("wireframe off;\n");
			fw.append("spacefill off;\n");
			fw.append("cartoon;\n");
			
			//TODO 
			//1 mixture -> more than 1 mutation?
			
			double perc;
			Color color;
			for(Map.Entry<Integer, MutationsPrevalence> e : prevalences.entrySet()) {
				perc = (double)e.getValue().totalMutations() / amountOfSequences * 100.0;
				fw.append("select " + e.getKey() + ";\n");
				color = getColor(perc);
				fw.append("color [" + color.getRed() + " " + color.getGreen() + " " + color.getBlue() + "];\n");
			}
			
			fw.flush();
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private Color getColor(double percentage) {
		if(between(0.0, 25.0, percentage)) 
			return Color.blue;
		else if(between(26.0, 50.0, percentage)) 
			return Color.green;
		else if(between(51.0, 75.0, percentage))
			return Color.orange;
		else if(between(76.0, 100, percentage))
			return Color.red;
		else return Color.black;
	}
	
	private boolean between(double min, double max, double value) {
		return value>=min && value<=max;
	}
}
