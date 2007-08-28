package net.sf.regadb.workflow.analysis.ui;

import static net.sf.regadb.workflow.i18n.I18n.tr;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.sf.regadb.swing.layout.RiverLayout;
import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.analysis.AnalysisInput;
import net.sf.regadb.workflow.analysis.AnalysisOutput;

public abstract class BaseAnalysisForm extends JPanel implements IAnalysisUI {
    private Analysis analysis;
    
    private JPanel filesPanel = new JPanel();
    private JPanel inputFilesPanel = new JPanel();
    private JPanel outputFilesPanel = new JPanel();
    
    private JLabel specTypeL = new JLabel(tr("analysis.form.text.typeLabel"));
    private JTextField specTypeTF = new JTextField();
    private JLabel nameL = new JLabel(tr("analysis.form.text.nameLabel"));
    private JTextField nameTF = new JTextField();
    private JLabel descriptionL = new JLabel(tr("analysis.form.text.descriptionLabel"));
    private JTextArea descriptionTA = new JTextArea();
    
    public BaseAnalysisForm(Analysis analysis) {
        this.analysis = analysis;
        setLayout(new RiverLayout());
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new RiverLayout());
        add("hfill", generalPanel);
        generalPanel.setBorder(new TitledBorder(tr("analysis.form.text.generalBorder")));
        generalPanel.add("", specTypeL);
        generalPanel.add("tab", specTypeTF);
        specTypeTF.setEnabled(false);
        generalPanel.add("br", nameL);
        generalPanel.add("tab hfill", nameTF);
        generalPanel.add("br", descriptionL);
        generalPanel.add("tab", descriptionTA);
        
        filesPanel.setLayout(new RiverLayout());
        add("br hfill",filesPanel);
        filesPanel.setBorder(new TitledBorder(tr("analysis.form.text.fileBorder")));
        
        inputFilesPanel.setLayout(new RiverLayout());
        filesPanel.add("hfill", inputFilesPanel);
        inputFilesPanel.setBorder(new TitledBorder(tr("analysis.form.text.inputFileBorder")));
        
        outputFilesPanel.setLayout(new RiverLayout());
        filesPanel.add("br hfill", outputFilesPanel);
        outputFilesPanel.setBorder(new TitledBorder(tr("analysis.form.text.outputFileBorder")));
        
        fillUI();
        
        add("br hfill",specificUI(analysis));
    }
    
    public void fillUI() {
        specTypeTF.setText(analysis.getSpecType());
        nameTF.setText(analysis.getName());
        descriptionTA.setText(analysis.getDescription());
        inputFilesPanel.removeAll();
        for(AnalysisInput ai : analysis.getInputs()) {
            inputFilesPanel.add(new JLabel(ai.getName()));
        }
        outputFilesPanel.removeAll();
        for(AnalysisOutput ao : analysis.getOutputs()) {
            outputFilesPanel.add(new JLabel(ao.getName()));
        }
    }
    
    public boolean saveData() {
        if("".equals(nameTF.getText())) {
            JOptionPane.showMessageDialog(this, tr("analysis.form.base.noNameWarning"), tr("workflow.general.warningMessage"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        analysis.setName(nameTF.getText());
        return true;
    }
    
    public abstract JPanel specificUI(Analysis analysis);

    public abstract boolean saveSpecificUI(Analysis analysis);

    public Analysis getAnalysis() {
        return analysis;
    }
    
    public JPanel getPanel() {
        return this;
    }
}
