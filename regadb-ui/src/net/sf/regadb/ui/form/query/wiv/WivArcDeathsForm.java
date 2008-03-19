package net.sf.regadb.ui.form.query.wiv;

import java.io.File;

public class WivArcDeathsForm extends WivIntervalQueryForm {
    
    public WivArcDeathsForm(){
        super(tr("menu.query.wiv.arc.deaths"),tr("form.query.wiv.label.arc.deaths"),tr("file.query.wiv.arc.deaths"));
        setQuery("from PatientImpl as p where p.deathDate >= :var_start_date and p.deathDate <= :var_end_date order by p.deathDate desc");
    }

    @Override
    protected File postProcess(File csvFile) {
        return csvFile;
    }
}
