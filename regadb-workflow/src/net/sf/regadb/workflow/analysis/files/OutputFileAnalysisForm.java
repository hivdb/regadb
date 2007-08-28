package net.sf.regadb.workflow.analysis.files;

import static net.sf.regadb.workflow.i18n.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.sf.regadb.swing.layout.RiverLayout;
import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.analysis.ui.BaseAnalysisForm;

public class OutputFileAnalysisForm extends BaseAnalysisForm {
    private final static String outputFile = "file.outputFile";
    
    private JLabel fileL;
    private JLabel fileContentL;
    private JButton fileChooser;
    
    public OutputFileAnalysisForm(OutputFileAnalysis analysis) {
        super(analysis);
    }
    
    @Override
    public boolean saveSpecificUI(Analysis analysis) {
        if("".equals(fileContentL.getText()) || fileContentL.getText()==null) {
            JOptionPane.showMessageDialog(this, tr("analysis.form.file.outputFile.noOutputFile"), tr("workflow.general.warningMessage"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        analysis.putAttributeValue(outputFile, fileContentL.getText());
        return true;
    }

    @Override
    public JPanel specificUI(Analysis analysis) {
        JPanel p = new JPanel();
        
        p.setBorder(new TitledBorder(tr("analysis.form.file.chooseOutputFile")));
        
        fileL = new JLabel(tr("analysis.form.file.outputFileLabel"));
        fileContentL = new JLabel();
        fileChooser = new JButton("...");
        fileChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showDialog(null, tr("analysis.form.file.fileChooser.outputFile"));
                if(returnVal==JFileChooser.APPROVE_OPTION) {
                    fileContentL.setText(fc.getSelectedFile().getAbsolutePath());
                }
            }
        });
        
        p.setLayout(new RiverLayout());
        
        p.add(fileL);
        p.add(fileContentL);
        p.add(fileChooser);
        
        fileContentL.setText(analysis.getAttributeValue(outputFile));
        
        return p;
    }

}
