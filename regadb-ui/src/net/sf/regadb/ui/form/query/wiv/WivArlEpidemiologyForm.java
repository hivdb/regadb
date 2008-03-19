package net.sf.regadb.ui.form.query.wiv;

import java.io.File;

public class WivArlEpidemiologyForm extends WivQueryForm {
    
    public WivArlEpidemiologyForm(){
        super(tr("menu.query.wiv.arl.epidemiology"),tr("form.query.wiv.label.arl.epidemiology"),tr("file.query.wiv.arl.epidemiology"));
    }

    @Override
    protected File postProcess(File csvFile) {
        return csvFile;
    }
}
