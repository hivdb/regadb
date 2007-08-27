package net.sf.regadb.workflow.analysis.ui;

import static net.sf.regadb.workflow.i18n.I18n.tr;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sf.regadb.swing.layout.RiverLayout;
import net.sf.regadb.workflow.analysis.unix.BaseAnalysisForm;

public class AnalysisDialog extends JDialog {
    public AnalysisDialog(BaseAnalysisForm baseForm) {
        JPanel main = new JPanel();
        main.setLayout(new RiverLayout());
        main.add("hfill", baseForm);
        JPanel buttons = new JPanel();
        buttons.setLayout(new RiverLayout());
        main.add("br hfill", buttons);
        JButton okButton = new JButton(tr("workflow.general.analysisDialog.okButton"));
        buttons.add("vleft", okButton);
        JButton cancelButton = new JButton(tr("workflow.general.analysisDialog.cancelButton"));
        buttons.add("vleft", cancelButton);
        this.getContentPane().add(main);
    }
}
