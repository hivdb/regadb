package net.sf.regadb.install.wizard;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class FilePicker extends JPanel {
	private static final long serialVersionUID = -8366732988677218798L;
	private JTextField txtFile;
	private JButton browse;
	private boolean directoryOnly = false;
	private FileFilter ff_ = null;
	private ArrayList<ChangeListener> listeners_ = new ArrayList<ChangeListener>();
	
	public FilePicker() {
		this(null, null);
	}
	public FilePicker(String key) {
		this(key, null);
	}
	public FilePicker(String key, String uri) {
		setLayout(new FlowLayout());
		
		txtFile = new JTextField();
		if ( uri != null ) {
			txtFile.setText(uri);
			handleFile( new File(uri) );
		}
		
		if ( key != null ) setName(key);
		add(txtFile);
		
		browse = new JButton("Browse");
		txtFile.setPreferredSize(new Dimension(350, browse.getPreferredSize().height));
		
		txtFile.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				fireEvent();
			}
		});
		
		browse.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				JFileChooser picker = new JFileChooser(new File(txtFile.getText()));
				
				if ( directoryOnly ) {
					picker.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				}
				
				if ( ff_ != null ) {
					picker.setFileFilter(ff_);
				}
				
				int returnVal = picker.showDialog(null, "Choose");
				
				if ( returnVal == JFileChooser.APPROVE_OPTION ) {
					handleFile(picker.getSelectedFile());
				}
			}
		});
		
		add(browse);
	}
	
	public void setDirectoryOnly(boolean b) {
		directoryOnly = b;
	}
	
	private void handleFile(File f) {
		txtFile.setText(f.getAbsolutePath());
		fireEvent();
	}
	
	public void setName(String key) {
		txtFile.setName(key);
	}
	
	public void setFileFilter(FileFilter ff) {
		ff_ = ff;
	}
	
	public File getFile() {
		return new File(txtFile.getText());
	}
	
	public JTextField getTextField() {
		return txtFile;
	}
	
	public JButton getButton() {
		return browse;
	}
	
	public void addListener(ChangeListener cl) {
		listeners_.add(cl);
	}
	
	public void fireEvent() {
		for( ChangeListener cl : listeners_ ) {
			cl.changed();
		}
	}
}
