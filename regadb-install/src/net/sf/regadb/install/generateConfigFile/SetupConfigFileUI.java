package net.sf.regadb.install.generateConfigFile;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.sf.regadb.swing.layout.RiverLayout;

import org.apache.commons.io.FileUtils;

public class SetupConfigFileUI {
    private static JFrame frame = new JFrame("Setup Configuration File");
    private static DialogSeparator separator1 = new DialogSeparator("Configuration Settings");
    private static JLabel directoryL   = new JLabel("Directory:");
    final JLabel directorybar          = new JLabel("Please choose directory here");
    private static JLabel databaseL    = new JLabel("Database:");
    private static JLabel usernameL    = new JLabel("Username:");
    private static JLabel passwordL    = new JLabel("Password:");
    private static JLabel urlL         = new JLabel("URL:");
    
    private static JButton directoryB        = new JButton("Choose directory");
    private static JComboBox databaseCB      = new JComboBox(new String[] {"PostgreSQL","HSQLDB"});
    private static JTextField usernameTF     = new JTextField(10);
    private static JTextField passwordTF     = new JTextField(10);
    private static JTextField urlTF          = new JTextField(10);
    
    private static DialogSeparator separator2 = new DialogSeparator("Proxy Settings");
    private static JLabel proxyHost1L = new JLabel("Proxyhost1:");
    private static JLabel proxyPort1L = new JLabel("Proxyport1:");
    private static JLabel proxyHost2L = new JLabel("Proxyhost2:");
    private static JLabel proxyPort2L = new JLabel("Proxyport2:");
    
    private static JTextField proxyHost1TF = new JTextField(10);
    private static JTextField proxyPort1TF = new JTextField(10);
    private static JTextField proxyHost2TF = new JTextField(10);
    private static JTextField proxyPort2TF = new JTextField(10);
    
    private static JButton generateButton = new JButton("Generate Configfile");
    
    public static void main(String args[]) {
        SetupConfigFileUI rlf = new SetupConfigFileUI();
        rlf.showFrame();
    }
    
    private void showFrame()
    {
        Container content = frame.getContentPane();
        RiverLayout layout = new RiverLayout();
        content.setLayout(layout);
        
        //DirectoryChooser
        directoryB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae)
            {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = chooser.showOpenDialog(frame);
                if (option == JFileChooser.APPROVE_OPTION)
                {
                    File sf = chooser.getSelectedFile();
                    String filelist = "nothing";
                    filelist = sf.getAbsolutePath();
                    directorybar.setText(filelist);
                }
            }
        });
        
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae)
            {
                generateFile();
            }
        });
        
        content.add(separator1);
        content.add("br", directoryL);
        content.add("tab", directorybar);
        content.add(directoryB);
        content.add("br", databaseL);
        content.add("tab", databaseCB);
        content.add("br", usernameL);
        content.add("tab", usernameTF);
        content.add("br", passwordL);
        content.add("tab", passwordTF);
        content.add("br", urlL);
        content.add("tab", urlTF);
        content.add("br", separator2);
        content.add("br", proxyHost1L);
        content.add("tab", proxyHost1TF);
        content.add("br", proxyPort1L);
        content.add("tab", proxyPort1TF);
        content.add("br", proxyHost2L);
        content.add("tab", proxyHost2TF);
        content.add("br", proxyPort2L);
        content.add("tab", proxyPort2TF);
        content.add("br tab", generateButton);
        
        frame.setSize(500, 500);
        frame.setResizable (false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void generateFile()
    {
        File tmpFile = null;
        
        try
        {
            tmpFile = File.createTempFile("tmp-config", ".xml");
        }
        catch(IOException ioe){}
        
        String content = "";
        content += "install_dir " + directorybar.getText() + "\n";
        String dialect = "HSQLDB".equals(databaseCB.getSelectedItem())?"Default":databaseCB.getSelectedItem().toString();
        content += "db_dialect " + dialect + "\n";
        content += "db_url " + urlTF.getText() + "\n";
        content += "db_user " + usernameTF.getText() + "\n";
        content += "db_password " + passwordTF.getText() + "\n";
        content += "proxy_url_a " + proxyHost1TF.getText() + "\n";
        content += "proxy_port_a " + proxyPort1TF.getText() + "\n";
        content += "proxy_url_b " + proxyHost2TF.getText() + "\n";
        content += "proxy_port_b " + proxyPort2TF.getText() + "\n";
        
        try
        {
            FileUtils.writeStringToFile(tmpFile, content);
        }
        catch(IOException ioe){}
        
        GenerateConfigFile.main(new String[] {tmpFile.getAbsolutePath()});
    }
}