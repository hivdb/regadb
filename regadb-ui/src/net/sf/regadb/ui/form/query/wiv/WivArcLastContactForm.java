package net.sf.regadb.ui.form.query.wiv;

import java.io.File;

public class WivArcLastContactForm extends WivIntervalQueryForm {
    
    public WivArcLastContactForm(){
        super(tr("menu.query.wiv.arc.lastContact"),tr("form.query.wiv.label.arc.lastContact"),tr("file.query.wiv.arc.lastContact"));
    }

    @Override
    protected File postProcess(File csvFile) {
        return csvFile;
    }
}
