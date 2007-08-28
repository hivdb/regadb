package net.sf.regadb.workflow.analysis.text;

import static net.sf.regadb.workflow.i18n.I18n.tr;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.sf.regadb.swing.layout.RiverLayout;
import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.analysis.ui.BaseAnalysisForm;

public class ReplaceAnalysisForm extends BaseAnalysisForm{
    private static final String toReplace = "text.toReplace";
    private static final String replaceValue = "text.replaceValue";
    
    private JLabel toReplaceL;
    private JTextField toReplaceTF;
    private JLabel replaceWithL;
    private JTextField replaceWithTF;
    
    public ReplaceAnalysisForm(ReplaceAnalysis analysis) {
        super(analysis);
    }
    
    @Override
    public boolean saveSpecificUI(Analysis analysis) {
        if("".equals(toReplaceTF.getText())) {
            JOptionPane.showMessageDialog(this, tr("analysis.form.text.replaceAll.noReplaceText"), tr("workflow.general.warningMessage"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        analysis.putAttributeValue(toReplace, toReplaceTF.getText());
        analysis.putAttributeValue(replaceValue, replaceWithTF.getText());
        return true;
    }

    @Override
    public JPanel specificUI(Analysis analysis) {
        JPanel p = new JPanel();
        
        p.setBorder(new TitledBorder(tr("analysis.form.text.replaceAllBorder")));
        
        toReplaceL = new JLabel(tr("analysis.form.text.replaceAll.toReplace"));
        toReplaceTF = new JTextField();
        replaceWithL = new JLabel(tr("analysis.form.text.replaceAll.replaceWith"));
        replaceWithTF = new JTextField();
        
        p.setLayout(new RiverLayout());
        p.add("", toReplaceL);
        p.add("tab hfill", toReplaceTF);
        p.add("br", replaceWithL);
        p.add("tab hfill", replaceWithTF);
        
        toReplaceTF.setText(analysis.getAttributeValue(toReplace));
        replaceWithTF.setText(analysis.getAttributeValue(replaceValue));
        
        return p;
    }
    
    public static void main(String [] args) {
        JFrame f = new JFrame();
        f.getContentPane().add(new ReplaceAnalysisForm(new ReplaceAnalysis()));
        f.setVisible(true);
        f.setSize(300,400);
    }

}
