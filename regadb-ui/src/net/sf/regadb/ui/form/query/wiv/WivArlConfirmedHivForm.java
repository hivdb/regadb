package net.sf.regadb.ui.form.query.wiv;

import java.io.File;

public class WivArlConfirmedHivForm extends WivIntervalQueryForm {
    
    public WivArlConfirmedHivForm(){
        super(tr("menu.query.wiv.arl.confirmedHiv"),tr("form.query.wiv.label.arl.confirmedHiv"),tr("file.query.wiv.arl.confirmedHiv"));
    }

    @Override
    protected File postProcess(File csvFile) {
        return csvFile;
    }
}
