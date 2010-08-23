package net.sf.regadb.ui.datatable.patient;

import java.util.List;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.framework.widgets.MyComboBox;
import net.sf.regadb.ui.framework.widgets.datatable.FilterTools;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.ListFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.TimestampFilter;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;

public class AttributeFilter extends WContainerWidget implements IFilter 
{
    private static CharSequence noAttribute = "No attribute";
    
    private Attribute attribute_=null;
    private IFilter filter_=null;
    private Transaction transaction_;
    
    private MyComboBox attributeCombo_;
    
    public AttributeFilter(Transaction transaction, Attribute attribute)
    {
        super();
        transaction_ = transaction;

        setAttribute(attribute);
        init();
    }
    
    protected void init(){
        Transaction t = getTransaction();
        List<Attribute> l = t.getAttributes();
        
        setAttributeCombo(new MyComboBox());
        addWidget(getAttributeCombo());
        
        for(Attribute a : l){
            getAttributeCombo().addItem(a.getName());
        }
        getAttributeCombo().sort();
        getAttributeCombo().insertItem(0, noAttribute);
        
        if(getAttribute() != null)
            getAttributeCombo().setCurrentItem(getAttribute().getName());
        else
            getAttributeCombo().setCurrentItem(noAttribute);
        
        getAttributeCombo().changed().addListener(this, new Signal.Listener()
                {
            public void trigger()
            {
                changeAttribute(getAttributeCombo().getCurrentText().getValue());
                FilterTools.findDataTable(getAttributeCombo()).applyFilter();
            }
        });
        
        changeAttribute(getAttribute());
    }
    
    public IFilter getFilter(){
        return filter_;
    }
    
    public Attribute getAttribute(){
        return attribute_;
    }
    
    public void setAttribute(Attribute attribute){
        attribute_ = attribute;
    }
    
    public void changeAttribute(String attributeName){
        Attribute a = null;
        
        if(!attributeName.equals(noAttribute.toString())){
            List<Attribute> l = getTransaction().getAttributes(attributeName);
            if(l != null || l.size() > 0)
                a = l.get(0);
        }
        changeAttribute(a);
    }
    
    public void changeAttribute(Attribute attribute){
        setAttribute(attribute);
        
        if(filter_ != null){
            removeWidget(filter_.getFilterWidget());
        }
        
        if(attribute != null){
            filter_ = createFilter(attribute_, getTransaction());
            addWidget(filter_.getFilterWidget());
        }
        else{
            filter_ = null;
        }
            
    }

    public WContainerWidget getFilterWidget()
    {
        return this;
    }

    public Transaction getTransaction()
    {
        return transaction_;
    }
    
    public static IFilter createFilter(Attribute attribute, Transaction t){
        ValueTypes vt = ValueTypes.getValueType(attribute.getValueType());
        if(vt == ValueTypes.DATE){
            return new TimestampFilter(RegaDBSettings.getInstance().getDateFormat());
        }
        if(vt == ValueTypes.NOMINAL_VALUE){
            return new AttributeNominalValueFilter(t,attribute);
        }
        return new StringFilter();
    }
    
    public static String formatValue(Attribute attribute, String value){
        if(value == null)
            return "";
        
        ValueTypes vt = ValueTypes.getValueType(attribute.getValueType());
        if(vt == ValueTypes.DATE)
            return DateUtils.format(value);
        else
            return value;
    }
    
    private void setAttributeCombo(MyComboBox attributeCombo_) {
        this.attributeCombo_ = attributeCombo_;
    }

    private MyComboBox getAttributeCombo() {
        return attributeCombo_;
    }

    public static class AttributeNominalValueFilter extends ListFilter
    {
        private Attribute attribute_;
        
        public AttributeNominalValueFilter(Transaction transaction, Attribute attribute)
        {
            super();
            setTransaction(transaction);
            attribute_ = attribute;
            init();
        }
        
        public Attribute getAttribute(){
            return attribute_;
        }
        
        @Override
        public void setComboBox(MyComboBox combo)
        {
            for(AttributeNominalValue anv : getAttribute().getAttributeNominalValues())
            {
                combo.addItem(anv.getValue());
            }
        }
    }

	public HibernateFilterConstraint getConstraint(String varName, int filterIndex) {
		if(filter_!=null)
			return filter_.getConstraint(varName, filterIndex);
		else
			return null;
	}

	@Override
	public boolean isValid() {
		return filter_ == null || filter_.isValid();
	}
}