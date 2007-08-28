package net.sf.regadb.workflow.analysis.ui;

import static net.sf.regadb.workflow.i18n.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sf.regadb.swing.layout.RiverLayout;

public class AnalysisDialog extends JDialog {
    public AnalysisDialog(JFrame mainFrame, final IAnalysisUI analysisUI) {
        super(mainFrame, analysisUI.getAnalysis().getType() + "Analysis Dialog", true);
        JPanel main = new JPanel();
        main.setLayout(new RiverLayout());
        main.add("hfill", analysisUI.getPanel());
        JPanel buttons = new JPanel();
        buttons.setLayout(new RiverLayout());
        main.add("br hfill", buttons);
        JButton okButton = new JButton(tr("workflow.general.analysisDialog.okButton"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean saveData = analysisUI.saveData();
                if(saveData) {
                saveData = analysisUI.saveSpecificUI(analysisUI.getAnalysis());
                }
                if(saveData) {
                    dispose();
                }
            }
        });
        buttons.add("vright", okButton);
        JButton cancelButton = new JButton(tr("workflow.general.analysisDialog.cancelButton"));
        buttons.add("vright", cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        this.getContentPane().add(main);
    }
}
