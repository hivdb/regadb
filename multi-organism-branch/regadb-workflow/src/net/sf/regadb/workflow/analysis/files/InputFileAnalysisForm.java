package net.sf.regadb.workflow.analysis.files;

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

import static net.sf.regadb.swing.i18n.I18n.tr;

public class InputFileAnalysisForm extends BaseAnalysisForm {
    public final static String inputFile = "file.inputFile";
    
    private JLabel fileL;
    private JLabel fileContentL;
    private JButton fileChooser;
    
    public InputFileAnalysisForm(InputFileAnalysis analysis) {
        super(analysis);
    }
    
    @Override
    public boolean saveSpecificUI(Analysis analysis) {
        if("".equals(fileContentL.getText()) || fileContentL.getText()==null) {
            JOptionPane.showMessageDialog(this, tr("analysis.form.file.inputFile.noInputFile"), tr("workflow.general.warningMessage"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        analysis.putAttributeValue(inputFile, fileContentL.getText());
        return true;
    }

    @Override
    public JPanel specificUI(Analysis analysis) {
        JPanel p = new JPanel();
        
        p.setBorder(new TitledBorder(tr("analysis.form.file.chooseInputFile")));
        
        fileL = new JLabel(tr("analysis.form.file.inputFileLabel"));
        fileContentL = new JLabel();
        fileChooser = new JButton("...");
        fileChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showDialog(null, tr("analysis.form.file.fileChooser.inputFile"));
                if(returnVal==JFileChooser.APPROVE_OPTION) {
                    fileContentL.setText(fc.getSelectedFile().getAbsolutePath());
                }
            }
        });
        
        p.setLayout(new RiverLayout());
        
        p.add(fileL);
        p.add(fileContentL);
        p.add(fileChooser);
        
        fileContentL.setText(analysis.getAttributeValue(inputFile));
        
        return p;
    }

}
