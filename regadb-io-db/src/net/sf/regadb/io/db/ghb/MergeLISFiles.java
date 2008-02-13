package net.sf.regadb.io.db.ghb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MergeLISFiles {
    public static void main(String [] args) {
        MergeLISFiles mlisf = new MergeLISFiles();
    }
    
    public MergeLISFiles() {
        File dir = new File("/home/plibin0/import/ghb/");
        ArrayList<String> headers = getHeaders(dir);
        if(headers == null) {
            System.err.println("Error: there is something wrong with the headers!");
            System.exit(0);
        }
        
        File outputFile = new File("/home/plibin0/import/ghb/merge.csv");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        
        try {
            for(int j = 0; j<headers.size(); j++) {
                bw.write("\""+headers.get(j)+"\"");
                if(j!=(headers.size()-1)) {
                    bw.write(","); 
                }
                bw.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        File[] files = dir.listFiles();
        for(File f : files) {
            if(f.getAbsolutePath().endsWith(".txt")) {
                try {
                    InputStream is = new FileInputStream(f);
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    ArrayList<String> outputLine = new ArrayList<String>(); 
                    while((line=br.readLine())!=null) {
                        if(!line.trim().equals("")) {
                            StringTokenizer st = new StringTokenizer(line, "\t");
                            int i = 0;
                            boolean noHeader = false;
                            while(st.hasMoreTokens()) {
                                String token = st.nextToken();
                                if(!token.equals(headers.get(i))) {
                                    if(i!=0)
                                    System.err.println(token + " " + headers.get(i));
                                    noHeader = true;
                                    break;
                                }
                                i++;
                            }
                            if(noHeader){
                                st = new StringTokenizer(line, "\t");
                                int amountOfTokens = st.countTokens();
                                while(st.hasMoreTokens()) {
                                    outputLine.add(st.nextToken());
                                }
                                for(int j = 0; j<headers.size()-amountOfTokens; j++) {
                                    outputLine.add("");
                                }
                                for(int j = 0; j<outputLine.size(); j++) {
                                    bw.write("\""+outputLine.get(j)+"\"");
                                    if(j!=(outputLine.size()-1)) {
                                        bw.write(","); 
                                    }
                                }
                                bw.write("\n");
                                outputLine.clear();
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        try {
            bw.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private ArrayList<String> getHeaders(File dir) {
        File[] files = dir.listFiles();
        boolean ok = true;
        int firstAmount = -1;
        ArrayList<String> headers = new ArrayList<String>();
        for(File f : files) {
            if(f.getAbsolutePath().endsWith(".txt")) {
                try {
                    InputStream is = new FileInputStream(f);
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String header = br.readLine();
                    StringTokenizer st = new StringTokenizer(header, "\t");
                    if(firstAmount==-1) {
                        firstAmount = st.countTokens();
                        while(st.hasMoreTokens()) {
                            headers.add(st.nextToken());
                        }
                    }
                    st = new StringTokenizer(header, "\t");
                    if(firstAmount!=st.countTokens()) {
                        System.err.println("Error: headers should be the same! " + firstAmount + " " + st.countTokens());
                        ok = false;
                    }
                    if(firstAmount!=-1) {
                        int i = 0;
                        while(st.hasMoreTokens()) {
                            String token = st.nextToken();
                            if(!token.equals(headers.get(i))) {
                                System.err.println("Error: headers should be the same! " + token + " " + headers.get(i));
                                ok = false;
                            }
                            i++;
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    ok = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    ok = false;
                }
            }
        }
        if(ok)
            return headers;
        else 
            return null;
    }
}
