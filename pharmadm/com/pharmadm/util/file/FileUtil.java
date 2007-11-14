/*
 * FileUtil.java
 *
 *  This file contains some code from the StarAlert project.
 *  Original class name: star_alert.storage.FileUtil
 *  Star Alert by Group 12
 *  Copyright (C) 1999 K. De Grave, J. Struyf, P. Vanbroekhoven, and R. Vandeginste
 *
 *  Changes and additions (C) 2001 PharmaDM N.V.
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.file;

import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.*;

/**
 * A Helper Class for working with Files on disk.
 *
 * @author	Jeans, kdg
 * @version	1.0
 *
 * 02 feb 2001 : added chopLastSlashes by kdg.
 */

public class FileUtil {
    
    public static String chopLastSlashes(String pathName) {
        boolean chopAgain = true;
        while (chopAgain) {
            char lastPathNameChar = pathName.charAt(pathName.length() - 1);
            if ( (lastPathNameChar == '/') || (lastPathNameChar == '\\') ) {
                pathName = pathName.substring(0, pathName.length()-1);
                if (pathName.length() == 0) {
                    chopAgain = false;
                }
            } else {
                chopAgain = false;
            }
        }
        return pathName;
    }
    
    public static String chopInitialSlashes(String pathName) {
        boolean chopAgain = true;
        while (chopAgain) {
            char firstPathNameChar = pathName.charAt(0);
            if ( (firstPathNameChar == '/') || (firstPathNameChar == '\\') ) {
                pathName = pathName.substring(1, pathName.length());
                if (pathName.length() == 0) {
                    chopAgain = false;
                }
            } else {
                chopAgain = false;
            }
        }
        return pathName;
    }
    
    /**
     * Return the Extension of a given File.
     *
     * @param f	The File.
     *
     * @return	The Extension of f.
     */
    public static String getExtension(File f) {
        return getExtension(f.getName());
    }
    
    public static String chopExtension(String extension, String fileName) {
        String result = fileName;
        if (result.endsWith(extension)) {
            result = result.substring(0, result.length() - extension.length() - 1);
        }
        return result;
    }
    
    /**
     * Returns a File with the given extension.
     * The name and path is based on the given file's name and path.
     * This method is not case-sensitive.
     * The returned File may or may not exist.
     * The returned File may or may not be the same as the given File.
     */
    public static File forceExtension(File file, String extension) {
        if (!file.getName().toLowerCase().endsWith(extension)) {
            return new File(file.getAbsolutePath() + extension);
        } else {
            return file;
        }
    }
    
    
    /**
     * Return the Name of a given File (without the Extension).
     *
     * @param f	The File.
     *
     * @return	The Name of f.
     */
    public static String getName(File f) {
        return getName(f.getName());
    }
    
    /**
     * Return the current working Directory of the File SASystem.
     *
     * @return	The working Directory.
     */
    public static File getCurrentDir() {
        return new File(".");
    }
    public static File askNewFileOpen(String extension, String description, File currentDir, JFrame parentFrame) {
        File file = null;
        JFileChooser chooser = new JFileChooser();
        BasicFileFilter filter = new BasicFileFilter(extension,description);
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Open");
        chooser.setCurrentDirectory(currentDir);
        int returnVal = chooser.showOpenDialog(parentFrame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
        }
        return file;
    }
    
    public static File askNewDirOpen(File currentDir, JFrame parentFrame) {
        File dir = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open directory");
        chooser.setCurrentDirectory(currentDir);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(parentFrame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            dir = chooser.getSelectedFile();
        }
        return dir;
    }
    
    public static File askNewFileSave(String extension, String description, File currentDir, JFrame parentFrame) {
        File file = null;
        JFileChooser chooser = new JFileChooser();
        BasicFileFilter filter = new BasicFileFilter(extension,description);
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Save");
        chooser.setCurrentDirectory(currentDir);
        int returnVal = chooser.showSaveDialog(parentFrame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            
            if (!file.getPath().endsWith("." + extension)) {
                file = new File(file.getPath() + "." + extension);
            }
        }
        return file;
    }
    
    public static File askNewFileSave(String extension, String description, File currentDir, String fileName, JFrame parentFrame) {
        return askNewFileSaveOrSaveAs("Save", extension, description, currentDir, fileName, parentFrame, false);
    }
    
    public static File askNewFileSaveAs(String extension, String description, File currentDir, String fileName, JFrame parentFrame) {
        return askNewFileSaveOrSaveAs("Save As", extension, description, currentDir, fileName, parentFrame,false);
    }
    
    public static File askNewFileSaveOrSaveAs(String dialog, String extension, String description, File currentDir, String fileName, JFrame parentFrame, boolean useDefaultFileName) {
        File file = null;
        int ind = 1;
        String newFilename = fileName;
        File Default = new File(currentDir,fileName+"."+extension);
        
        while(Default.exists()) {
            if (ind==1) {
                newFilename = fileName;
            } else {
                newFilename = fileName + "_" + ind;
            }
            Default = new File(currentDir,newFilename+"."+extension);
            ind++;
        }
        
        if (useDefaultFileName) {
            file = Default;
        } else {
            JFileChooser chooser = new JFileChooser();
            BasicFileFilter filter = new BasicFileFilter(extension,description);
            chooser.setFileFilter(filter);
            chooser.setDialogTitle(dialog);
            chooser.setCurrentDirectory(currentDir);
            chooser.setSelectedFile(Default);
            
            int returnVal = chooser.showSaveDialog(parentFrame);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
                
                if (!file.getPath().endsWith("." + extension)) {
                    file = new File(file.getPath() + "." + extension);
                }
            }
        }
        
        return file;
    }
    
    public static File askNewFileSave(String extension, String description, File currentDir, File Default, JFrame parentFrame) {
        return askNewFileSave(extension, description, currentDir, Default, parentFrame, false);
    }
    
    public static File askNewFileSave(String extension, String description, File currentDir, File Default, JFrame parentFrame, boolean useDefaultFileName) {
        File file = null;
        int ind = 1;
        
        String fileName = Default.getName();
        
        String newFilename = fileName;
        
        Default = new File(currentDir,Default.getName()+"."+extension);
        
        while(Default.exists()) {
            if (ind==1) {
                newFilename = fileName;
            } else {
                newFilename = fileName + "_" + ind;
            }
            Default = new File(currentDir,newFilename+"."+extension);
            ind++;
        }
        
        if (useDefaultFileName) {
            file = Default;
        } else {
            JFileChooser chooser = new JFileChooser();
            BasicFileFilter filter = new BasicFileFilter(extension,description);
            chooser.setFileFilter(filter);
            chooser.setDialogTitle("Save");
            chooser.setCurrentDirectory(currentDir);
            chooser.setSelectedFile(Default);
            
            int returnVal = chooser.showSaveDialog(parentFrame);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
                
                if (!file.getPath().endsWith("." + extension)) {
                    file = new File(file.getPath() + "." + extension);
                }
            }
        }
        
        return file;
    }
    
    public static File askNewDirSave(String title, File currentDir, JFrame parentFrame) {
        File file = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setDialogTitle(title);
        chooser.setCurrentDirectory(currentDir);
        int returnVal = chooser.showSaveDialog(parentFrame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
        }
        return file;
    }
    /**
     * Add an Extension to a given File name.
     *
     * @param fname	The name of the File.
     * @param ext	The Extension.
     *
     * @return	The File name with the Extension.
     */
    public static String addExtension(String fname, String ext) {
        String myext = FileUtil.getExtension(fname);
        if (myext == null || (!myext.equals(ext))) return fname + "." + ext;
        else return fname;
    }
    
    /**
     * Return the File SASystem Extension of a given (by name) File.
     *
     * @param s	The name of the File.
     *
     * @return	The Extension of the File.
     */
    public static String getExtension(String s) {
        String ext = "";
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    /**
     * Return the Name of a given File (without the Extension).
     *
     * @param s	The name of the File (possibly including an Extension).
     *
     * @return	The Name of the File.
     */
    public static String getName(String s) {
        String name = s;
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            name = s.substring(0,i);
        }
        return name;
    }
    
    /**
     * Return a list of all the files in a given (by name) Directory.
     *
     * @param dirname	The name of the Directory.
     *
     * @return	The Names of all the Files in that Directory.
     *
     * @exception FileNotFoundException if the Directory in not found.
     */
    public static String[] dirList(String dirname) throws FileNotFoundException {
        File directory = new File(dirname);
        if (directory != null && directory.isDirectory()) {
            return directory.list();
        }
        throw new FileNotFoundException();
    }
    
    
    /**
     * Return a list of all the files in a given (by name) Directory, filtered by Extension.
     *
     * @param dirname	The name of the Directory.
     * @param extension	The Extension to filter the Files on.
     *
     * @return	The Names of all the Files in that Directory with the given Extension.
     *
     * @exception FileNotFoundException if the Directory in not found.
     */
    public static String[] dirList(String dirname, String extension) throws FileNotFoundException {
        File directory = new File(dirname);
        if (directory != null && directory.isDirectory()) {
            return directory.list(new ExtensionFilter(extension));
        }
        throw new FileNotFoundException();
    }
    
    /**
     * Reads a Text File and Returns a list of all the Lines of Text.
     *
     * @param filename	The Name of the File to read.
     *
     * @return The list of Text Lines.
     *
     * @exception FileNotFoundException if the File was not found in the File SASystem.
     * @exception IOException if an error occured while reading the File.
     */
    public static String[] readTextFile(String filename) throws FileNotFoundException, IOException {
        Vector lines = new Vector();
        BufferedReader in;
        String line;
        System.out.println("Reading text file: "+filename);
        in = new BufferedReader(new FileReader(filename));
        do {
            line = in.readLine();
            if (line != null) lines.addElement(line);
        } while (line != null);
        in.close();
        String text[] = new String[lines.size()];
        int idx = 0;
        for (Enumeration e = lines.elements(); e.hasMoreElements() ;) {
            text[idx++] = (String)e.nextElement();
        }
        return text;
    }
    
    /**
     * Reads a Text File and Returns the number of Lines of Text.
     *
     * @param filename	The Name of the File to read.
     *
     * @return The number of Text Lines.
     *
     * @exception FileNotFoundException if the File was not found in the File SASystem.
     * @exception IOException if an error occured while reading the File.
     */
    public static int countLines(File file)  throws FileNotFoundException, IOException {
        int nrLines;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        for (nrLines=0; reader.readLine() != null; nrLines++) {}
        reader.close();
        return nrLines;
    }
    
    public static final int OVERWRITE_ALWAYS = 100;
    public static final int OVERWRITE_NEVER = 101;
    public static final int OVERWRITE_ASK = 102;
    /**
     * Copies one file to another, possibly overwriting the destination file.
     *
     * @return true iff copy succeeded
     */
    public static boolean copy(File fromFile, File fileToWrite, int overWrite) {
        boolean firstTry = true;
        boolean succeeded = false;
        boolean retry = false;
        while (firstTry || retry) {
            retry = false;
            firstTry = false;
            try {
                if (fileToWrite.exists()) {
                    if ((overWrite == OVERWRITE_ALWAYS) || ((overWrite == OVERWRITE_ASK) && okToOverwrite(fileToWrite))) {
                        fileToWrite.delete();
                    } else {
                        System.out.println("File did already exist while I did not expect it to!  Failed to copy " + fromFile + " to " + fileToWrite);
                        return false;
                    }
                }
                OutputStream writer = new FileOutputStream(fileToWrite);
                InputStream reader = new FileInputStream(fromFile);
                int maxBufSize = Math.min((int)fromFile.length(), 65536);
                byte[] cbuf = new byte[maxBufSize];
                int totalSize = (int)fromFile.length();
                int read = 0;
                int totalRead = 0;
                while (totalRead < totalSize) {
                    read = reader.read(cbuf);
                    if (read != -1) {
                        // System.out.println("writing " + read + " bytes, beginning with byte "+ totalRead + ". Total size is " + totalSize + ". buffer is " + cbuf.length);
                        writer.write(cbuf, 0, read);
                        totalRead+= read;
                    } else {
                        totalRead = totalSize + 1;
                    }
                }
                cbuf = null;
                reader.close();
                writer.flush();
                writer.close();
                succeeded = true;
            } catch (SecurityException se) {
                se.printStackTrace();
                if (okToRetry()) {
                    retry = true;
                }
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
                // do not retry
            } catch (IOException ioe) {
                ioe.printStackTrace();
                if (okToRetry()) {
                    retry = true;
                }
            }
        }
        return succeeded;
    }
    
    public static boolean saveAs(InputStream inputStream, File fileToWrite, int overWrite) {
        boolean firstTry = true;
        boolean succeeded = false;
        boolean retry = false;
        while (firstTry || retry) {
            retry = false;
            firstTry = false;
            try {
                if (fileToWrite.exists()) {
                    if ((overWrite == OVERWRITE_ALWAYS) || ((overWrite == OVERWRITE_ASK) && okToOverwrite(fileToWrite))) {
                        fileToWrite.delete();
                    } else {
                        System.out.println("File did already exist while I did not expect it to!  Failed to write " + fileToWrite);
                        return false;
                    }
                }
                OutputStream writer = new FileOutputStream(fileToWrite);
                int maxBufSize = 65536;
                byte[] cbuf = new byte[maxBufSize];
                int totalRead = 0;
                
                int read = inputStream.read(cbuf);
                while (read > 0) {
                    // System.out.println("writing " + read + " bytes, beginning with byte "+ totalRead + ". Total size is " + totalSize + ". buffer is " + cbuf.length);
                    writer.write(cbuf, 0, read);
                    totalRead+= read;
                    read = inputStream.read(cbuf);
                }
                cbuf = null;
                inputStream.close();
                writer.flush();
                writer.close();
                succeeded = true;
            } catch (SecurityException se) {
                se.printStackTrace();
                if (okToRetry()) {
                    retry = true;
                }
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
                // do not retry
            } catch (IOException ioe) {
                ioe.printStackTrace();
                if (okToRetry()) {
                    retry = true;
                }
            }
        }
        return succeeded;
    }
    
    public static  boolean okToOverwrite(File f) {
        Object[] options = { "Overwrite", "Cancel" };
        int option = JOptionPane.showOptionDialog(null, "Do you want to overwrite " + f + "?", "Confirmation",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        return (option == 0);
    }
    
    public static  boolean okToRetry() {
        Object[] options = { "Retry", "Cancel" };
        int option = JOptionPane.showOptionDialog(null, "File operation did not succeed. Retry?", "Confirmation",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        return (option == 0);
    }
    
    /*
    private static void evaluateSuccess(boolean success, File file) {
        if (!success) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "delete failed!! I/O troubles!!\nFile: " + file, "delete failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
     
    public static boolean recursiveDelete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (!recursiveDelete(files[i])) {
                    return false;
                }
            }
            boolean success = file.delete();
            evaluateSuccess(success, file);
            return success;
        } else if (file.exists()) {
            boolean success = file.delete();
            evaluateSuccess(success, file);
            return success;
        }
        return true;
    }
     */
    public static boolean recursiveDelete(File file) {
        boolean ok = recursiveDeleteNoGUI(file);
        if (!ok) {
            System.err.println("Could not delete file: " + file + ", trying again...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
            }
            ok = recursiveDeleteNoGUI(file);
            if (!ok) {
                System.err.println("Retry for deleting file " + file + " didn't work.");
                //new Throwable().printStackTrace();
                JOptionPane.showMessageDialog(null, "Could not delete file: " + file, "Filesystem error",  JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println("Retry successful! (file " +file+")");
            }
        }
        return ok;
    }
    
    private static boolean recursiveDeleteNoGUI(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {  // added this test to avoid potential problems with NFS-automounted directories
                for (int i = 0; i < files.length; i++) {
                    if (!recursiveDeleteNoGUI(files[i])) {
                        return false;
                    }
                }
            }
            boolean ok = file.delete();
            report(ok, file);
            return ok;
        } else if (file.exists()) {
            boolean ok = file.delete();
            report(ok, file);
            return ok;
        }
        return true;
    }
    
    private static void report(boolean ok, File file) {
        if (!ok) {
            System.err.println("recursiveDeleteNoGUI of file " + file + " didn't work.");
        }
    }
}
