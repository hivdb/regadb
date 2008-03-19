package net.sf.regadb.ui.form.query.wiv;

import java.io.File;

public class WivArlViralLoadForm extends WivQueryForm {
    public WivArlViralLoadForm(){
        super(tr("menu.query.wiv.arl.viralLoad"),tr("form.query.wiv.label.arl.viralLoad"),tr("file.query.wiv.arl.viralLoad"));
    }

    @Override
    protected File postProcess(File csvFile) {
        return csvFile;
    }
}
