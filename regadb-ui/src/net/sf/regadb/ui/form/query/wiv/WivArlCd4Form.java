package net.sf.regadb.ui.form.query.wiv;

import java.io.File;

import net.sf.regadb.io.util.StandardObjects;

public class WivArlCd4Form extends WivQueryForm {
    
    public WivArlCd4Form(){
        super(tr("menu.query.wiv.arl.cd4"),tr("form.query.wiv.label.arl.cd4"),tr("file.query.wiv.arl.cd4"));
        
        setQuery("select tr from TestResult tr where tr.test.testType.description='"+ StandardObjects.getCd4TestType().getDescription() +"'");
    }

    @Override
    protected File postProcess(File csvFile) {
        return csvFile;
    }


}
