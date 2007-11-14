/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.gui.chart;
/**
 *
 * @author  henkv
 */
import java.awt.Color;

public class NColors {
    public NColors(){
    }
    
    public static Color[] nColors(int n){
	Color[] result = new Color[n];
	int nrRounds = (int) Math.ceil(log(3.0, (double) n)) ;
	//System.out.println("nrRounds:" + nrRounds);
	int colorID = 0;
	
	float jumpSB = 0.5f/n;
	
	for (int round = 0; round < nrRounds; round++) {
	    for (int i = 0; (i + round) < n; i = i + nrRounds) {
		//System.out.println("round:" + round + " i:" + i + " colorID:" + colorID);
		Color color;
		color=new Color(Color.HSBtoRGB(colorID * 1f/n,(1.0f-i*jumpSB),0.95f));
		//color = new Color(Color.HSBtoRGB(colorID * 1f/classStrings.length, 1.0f, 1.0f));
		
		result[i+round] = color;
		colorID++;
	    }
	}
	return result;
     }   

    private static double log(double base, double value) {
        return Math.log(value)/Math.log(base);
    }
}
