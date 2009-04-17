package be.kuleuven.rega.research.conserved.output;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import be.kuleuven.rega.research.conserved.MutationsPrevalence;

public class JMolOutputter implements ConservedRegionsOutputter {
	private File directory;
	
	private static Color[] gradient;
	
	static {
		gradient = getGradient(101, Color.green, Color.red);
	}
	
	public JMolOutputter(File directory) {
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
		return gradient[((int)percentage)];
//		if(between(0.0, 25.0, percentage)) 
//			return Color.blue;
//		else if(between(26.0, 50.0, percentage)) 
//			return Color.green;
//		else if(between(51.0, 75.0, percentage))
//			return Color.orange;
//		else if(between(76.0, 100, percentage))
//			return Color.red;
//		else return Color.black;
	}
	
	private static Color[] getGradient(int amountOfSteps, Color start, Color end) {
		Color [] steps = new Color[amountOfSteps];
		
		int r = end.getRed() - start.getRed();
		int g = end.getGreen() - start.getGreen();
		int b = end.getBlue() - start.getBlue();
		
		double r_step = r/amountOfSteps;
		double g_step = g/amountOfSteps;
		double b_step = b/amountOfSteps;
		
		for(int i = 0; i < amountOfSteps; i++) {
			steps[i] = new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
			//steps[i] = new Color((int)(start.getRed()+i*r_step), (int)(start.getGreen()+i*g_step), (int)(start.getBlue()+i*b_step));
		}
		
		return steps;
	}
	
	private boolean between(double min, double max, double value) {
		return value>=min && value<=max;
	}
}
