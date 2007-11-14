/*
 * MatrixDoubleSetting.java
 *
 * Created on March 2, 2004, 5:59 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;
import com.pharmadm.num.dhb.DhbMatrixAlgebra.Matrix;
import java.io.*;
import java.util.StringTokenizer;
import java.lang.StringBuffer;

/**
 *
 * @author  ldh
 */

public class MatrixDoubleSetting extends Setting {
    
    /** Creates a new instance of MatrixDoubleSetting */
    
    public MatrixDoubleSetting(XMLSettings xs, String name) {
        super(xs, name);
    }
    
    public MatrixDoubleSetting(XMLSettings xs, String name, Matrix defaultValue) {
        this(xs, name);
        setDefaultValue(defaultValue);
    }
    
    public MatrixDoubleSetting(XMLSettings xs, String name, double[][] matrix) {
        this(xs, name);
        setDefaultValue(new Matrix(matrix));
    }
    
    public boolean setValue(double[][] matrix) {
        return super.setValue(new Matrix(matrix));
    }
    
    public String toString() {
        Matrix matrix = ((Matrix)getValue());
        if (matrix == null) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(matrix.rows()).append(" ").append(matrix.columns()).append(" ");
            for (int row = 0 ; row < matrix.rows(); row++) {
                for (int col = 0; col < matrix.columns(); col++) {
                    sb.append(matrix.component(row,col)).append(" ");
                }
            }
            return sb.toString();
        }
    }
    
    public boolean write(java.io.PrintStream writer) {
        //System.out.println("About to write MatrixDoubleSetting...");
        writer.print(this.toString());
        return true;
    }
    
    public boolean read(String s) {
        if (s==null) {return false;}
        
        StringTokenizer stok = new StringTokenizer(s, " ");
        try {
            int nrRows = 0;
            int nrCols = 0;
            
            if (stok.hasMoreTokens()) {
                nrRows = Integer.parseInt(stok.nextToken());
                if (stok.hasMoreTokens()) {
                    nrCols = Integer.parseInt(stok.nextToken());
                }
            }
            
            double[][] matrix = new double[nrRows][nrCols];
            
            for (int row = 0; row < nrRows; row++) {
                for (int col = 0; col < nrCols; col++ ) {
                    matrix[row][col] = Double.parseDouble(stok.nextToken());
                }
            }
            
            this.setValue(matrix);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    public double[][] doubleValue() {
        Matrix matrix = ((Matrix)getValue());
        if (matrix != null) {
            return matrix.toComponents();
        } else {
            matrix = (Matrix)getDefaultValue();
            if (matrix != null) {
                return matrix.toComponents();
            } else {
                return null;
            }
        }
    }
    
    
    public double doubleValue(int row, int col) {
        Matrix matrix = ((Matrix)getValue());
        if (matrix != null) {
            return matrix.component(row,col);
        } else {
            matrix = (Matrix)getDefaultValue();
            if (matrix != null) {
                return matrix.component(row,col);
            } else {
                return Double.NaN;
            }
        }
    }
    
    protected javax.swing.JComponent getConfigurationControlImpl(javax.swing.JDialog parent) {
        return null;
    }
    
}
