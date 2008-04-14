/*
 * ColorBar.java
 *
 * Created on October 7, 2002, 10:53 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.gui.chart;

import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;
import java.util.*;
/**
 *
 * @author  ldh
 */
public class ColorBar extends javax.swing.JPanel {
    private Rectangle2D.Double rect;
    private Graphics2D g2;
    private Color minColor;
    private Color maxColor;
    private float hueMin, hueMax;
    private double min;
    private double max;
    private double black;
    private double minRainbow;
    private double maxRainbow;
    private boolean twoColorScheme;
    private final static Color NAN_COLOR = Color.white;
    
    /** Creates new form ColorBar */
    public ColorBar(Color minColor, Color maxColor, double min, double black, double max, boolean twoColorScheme, double minRainbow, double maxRainbow) {
        initComponents();
        setMinColor(minColor);
        setMaxColor(maxColor);
        setMin(min);
        setMax(max);
        setBlack(black);
        setTwoColorScheme(twoColorScheme);
        this.setMinRainbow(minRainbow);
        this.setMaxRainbow(maxRainbow);
        this.rect = new Rectangle2D.Double();
    }
    public ColorBar(Color minColor, Color maxColor, double min, double black, double max, boolean twoColorScheme) {
        this(minColor,maxColor,min,black,max,twoColorScheme,0,1);
    }
    
    public ColorBar() {
        //this(new Color(255,0,0), new Color(0,255,255), 0, 0.5, 1, true);
        this(new Color(255,0,0), new Color(0,0,255), 0, 0.5, 1, false,0,1);
    }     
    
    public static ColorBar copyColorBar(ColorBar b){
        return new ColorBar(b.getMinColor(),b.getMaxColor(),b.getMin(),b.getBlack(),b.getMax(),b.getTwoColorScheme(), b.getMinRainbow(), b.getMaxRainbow());
    }
    
    public static boolean equalColorBars(ColorBar b1, ColorBar b2){
        boolean result = false;
        
        if (b1 != null & b2!= null)
            if (b1.getMinColor().equals(b2.getMinColor()) & b1.getMaxColor().equals(b2.getMaxColor()) &
            b1.getMin() == b2.getMin() & b1.getMax() == b2.getMax() &
            b1.getBlack() == b2.getBlack() & b1.getTwoColorScheme()==b2.getTwoColorScheme() & b1.getMinRainbow()==b2.getMinRainbow() & 
            b1.getMaxRainbow() == b2.getMaxRainbow())
                result = true;
        return result;
    }
    
    public Color getColor(double value, double min, double max) {
        Color color;
        if (Double.isNaN(value)) {
            color = this.NAN_COLOR;
        } else if ((max-min) == 0) {
            color = this.getColor(0);
        } else {
            color = this.getColor((value-min)/(max-min));
        }
        return color;
    }
    
    public Color getColor(double val) {
        //value should be between 0 and 1
        Color color = this.NAN_COLOR;
        if (! Double.isNaN(val)) {
            double value = Math.max(0.0, Math.min(val,1.0));
            if (this.twoColorScheme) {
                if (value <= this.min){
                    color = this.minColor;
                } else if (value >= this.max){
                    color = this.maxColor;
                } else if (value <= black) {
                    double relVal = black - value;
                    double leftWidth = black - min;
                    float red = (float) ( (relVal/leftWidth ) * minColor.getRed()/255.f);
                    float green = (float) (( relVal/leftWidth ) * minColor.getGreen()/255.f);
                    float blue = (float) (  (relVal/leftWidth) * minColor.getBlue()/255.f);
                    color = new Color(red,green,blue);
                } else { // value > black
                    double relVal = value - black;
                    double rightWidth = max - black;
                    float red = (float) ( (relVal/rightWidth ) * maxColor.getRed()/255.f);
                    float green = (float) (( relVal/rightWidth ) * maxColor.getGreen()/255.f);
                    float blue = (float) (  ( relVal/rightWidth) * maxColor.getBlue()/255.f);
                    color = new Color(red,green,blue);
                }
                
            } else { //rainbow
                if (value <= this.minRainbow) {
                    return this.minColor;
                } else if (value >= this.maxRainbow){
                    return this.maxColor;
                } else { // in rainbow zone
                    float[] hsbMin = this.minColor.RGBtoHSB(minColor.getRed(),minColor.getGreen(),minColor.getBlue(),null);
                    float[] hsbMax = this.maxColor.RGBtoHSB(maxColor.getRed(),maxColor.getGreen(),maxColor.getBlue(),null);
                    float hueMin = hsbMin[0];
                    float hueMax = hsbMax[0];
                    if (hueMax < hueMin) {
                        hueMax = hueMax + 1.f;
                    }
                    float newVal = (float)(hueMin + ((value-this.minRainbow)/(this.maxRainbow-this.minRainbow) * (hueMax-hueMin)));
                    color = new Color(Color.HSBtoRGB(newVal, 1.0f, 1.0f));
                }
            }
        }
        return color;
    }
    
    /**
     * An efficient way to get the same color is getColor()
     * The int returned is the default RGB packed encoding.
     */
    public final int getColorInt(double value, double min, double max) {
        if (Double.isNaN(value)) {
            return this.NAN_COLOR.getRGB();
        } else if ((max-min) == 0){
            return this.getColorInt(0.0);
        }else {
            return this.getColorInt((value-min)/(max-min));
        }
    }
    
    public final int getColorInt(double val) {
        //value should be between 0 and 1
        if (Double.isNaN(val)) {
            return this.NAN_COLOR.getRGB();
        } else {
            double value = Math.max(0.0, Math.min(val,1.0));
            
            if (this.twoColorScheme) {
                //System.out.println("Val:" + value + " min:" + min + " black:" + black);
                if (value <= this.min) {
                    return this.minColor.getRGB();
                } else if (value >= this.max){
                    return this.maxColor.getRGB();
                } else if (value <= black) {
                    double relVal = black - value;
                    double leftWidth = black - min;
                    double factor = Math.min(1.0,relVal/leftWidth);
                    int red = (int) ( factor * minColor.getRed());
                    int green = (int) ( factor * minColor.getGreen());
                    int blue = (int) (  factor * minColor.getBlue());
                    //System.out.println("Val:" + value + " min:" + min + " RV:" + relVal + " LW:" + leftWidth + " F:" + factor + " R:" + red + " G:" + green + " B:" + blue);
                    return ( 0xFF000000 | (red<<16) | (green<<8) | (blue) );
                } else { // value > black
                    double relVal = value - black;
                    double rightWidth = max - black;
                    double factor = Math.min(1.0,relVal/rightWidth);
                    int red = (int) ( factor * maxColor.getRed());
                    int green = (int) ( factor * maxColor.getGreen());
                    int blue = (int) ( factor * maxColor.getBlue());
                    return ( 0xFF000000 | (red<<16) | (green<<8) | (blue) );
                }
            } else { //rainbow
                if (value <= this.minRainbow) {
                    return this.minColor.getRGB();
                } else if (value >= this.maxRainbow){
                    return this.maxColor.getRGB();
                } else { // in rainbow zone
                    float newVal = (float)(hueMin + ((value-this.minRainbow)/(this.maxRainbow-this.minRainbow) * (hueMax-hueMin)));
                    return Color.HSBtoRGB(newVal, 1.0f, 1.0f);
                }
            }
        }
    }
    
    public void setMinRainbow(double value){
        this.minRainbow = value;
    }
    public void setMaxRainbow(double value){
        this.maxRainbow = value;
    }
    public void setMin(double value){
        this.min = value;
    }
    public void setBlack(double value){
        this.black = value;
    }
    public void setMax(double value){
        this.max = value;
    }
    public void setTwoColorScheme(boolean b) {
        this.twoColorScheme = b;
    }
    public void setMinColor(Color color){
        this.minColor = color;
        float[] hsbMin = this.minColor.RGBtoHSB(minColor.getRed(),minColor.getGreen(),minColor.getBlue(),null);
        hueMin = hsbMin[0];
        if (hueMax < hueMin) hueMax = hueMax + 1.f;
        
    }
    public void setMaxColor(Color color){
        this.maxColor = color;
        float[] hsbMax = this.maxColor.RGBtoHSB(maxColor.getRed(),maxColor.getGreen(),maxColor.getBlue(),null);
        hueMax = hsbMax[0];
        if (hueMax < hueMin) hueMax = hueMax + 1.f;
    }
    
    public double getMinRainbow(){
        return this.minRainbow;
    }
    
    public double getMaxRainbow(){
        return this.maxRainbow;
    }
    public double getMin(){
        return this.min;
    }
    public double getBlack(){
        return this.black;
    }
    public double getMax(){
        return this.max;
    }
    public boolean getTwoColorScheme() {
        return this.twoColorScheme;
    }
    public Color getMinColor(){
        return this.minColor;
    }
    public Color getMaxColor(){
        return this.maxColor;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

        setLayout(null);

    }//GEN-END:initComponents
    
    protected void paintComponent(java.awt.Graphics graphics) {
        double height = this.getHeight();
        double width = this.getWidth();
        
        this.g2 = (Graphics2D)graphics;
        
        if (this.twoColorScheme) {
            double leftWidth = (black-min) * width;
            double rightWidth = ((1-black)-(1-max))*width;
            this.rect.setFrame(0,0,black*width,height);
            this.g2.setColor(minColor);
            g2.fill(this.rect);
            this.rect.setFrame(black*width,0,(1-black)*width,height);
            this.g2.setColor(maxColor);
            g2.fill(this.rect);
            
            for (int i = 0; i < leftWidth; i++) {
                this.rect.setFrame(min*width,0,leftWidth-i,height);
                float red = (float) ( (i/leftWidth ) * minColor.getRed()/255.f);
                float green = (float) (( i/leftWidth ) * minColor.getGreen()/255.f);
                float blue = (float) (  ( i/leftWidth) * minColor.getBlue()/255.f);
                g2.setColor(new Color(red,green,blue));
                g2.fill(this.rect);
            }
            for (int i = 0; i < rightWidth; i++) {
                this.rect.setFrame((black*width)+i,0,rightWidth-i,height);
                float red = (float) (( i/rightWidth ) * maxColor.getRed()/255.f);
                float green = (float) (  ( i/rightWidth ) * maxColor.getGreen()/255.f);
                float blue = (float) (  ( i/rightWidth ) * maxColor.getBlue()/255.f);
                g2.setColor(new Color(red,green,blue));
                g2.fill(this.rect);
            }
        } else { //rainbow
            double leftWidth = this.minRainbow * width;
            double rightWidth = (1-this.maxRainbow) * width;
            double rainbowWidth = width - leftWidth - rightWidth;
            this.rect.setFrame(0,0,leftWidth,height);
            this.g2.setColor(minColor);
            g2.fill(this.rect);
            this.rect.setFrame(this.maxRainbow*width,0,rightWidth,height);
            this.g2.setColor(maxColor);
            g2.fill(this.rect);
            float[] hsbMin = this.minColor.RGBtoHSB(minColor.getRed(),minColor.getGreen(),minColor.getBlue(),null);
            float[] hsbMax = this.maxColor.RGBtoHSB(maxColor.getRed(),maxColor.getGreen(),maxColor.getBlue(),null);
            float hueMin = hsbMin[0];
            float hueMax = hsbMax[0];
            if (hueMax < hueMin) hueMax = hueMax + 1.f;
            
            for (int i = 0; i < rainbowWidth; i++){
                this.rect.setFrame(leftWidth+i,0,rainbowWidth-i,height);
                float newVal = hueMin + (1.f*i/(float)rainbowWidth) * (hueMax-hueMin);
                this.g2.setColor( new Color(Color.HSBtoRGB(newVal, 1.0f, 1.0f)));
                g2.fill(this.rect);
            }
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
