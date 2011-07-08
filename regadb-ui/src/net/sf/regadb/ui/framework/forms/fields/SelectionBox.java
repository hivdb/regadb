package net.sf.regadb.ui.framework.forms.fields;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import eu.webtoolkit.jwt.SelectionMode;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WFormWidget;
import eu.webtoolkit.jwt.WSelectionBox;

public class SelectionBox<ComboDataType> extends FormField {

	private WSelectionBox box;
    private ArrayList<DataComboMessage<ComboDataType>> list_ = null;
    private Set<Integer> selectedIndexes = new TreeSet<Integer>();

	public SelectionBox(InteractionState state, IForm form) {
		super(form);
		
		if(state == InteractionState.Adding || state == InteractionState.Editing){
			box = new WSelectionBox();
			addWidget(box);
		} else {
            list_ = new ArrayList<DataComboMessage<ComboDataType>>();
			initViewWidget();
		}
		
		if(form != null){
			form.addFormField(this);
		}
		
		setText(null);
	}

	@Override
	public WFormWidget getFormWidget() {
		return box;
	}

	@Override
	public String getFormText() {
		StringBuilder sb = new StringBuilder();
		
		if(box != null){
			for(int i : box.getSelectedIndexes()){
				sb.append(", ");
				sb.append(UIUtils.keyOrValue(box.getItemText(i)));
			}
		}else{
			for(int i : selectedIndexes){
				sb.append(", ");
				sb.append(UIUtils.keyOrValue(list_.get(i)));
			}
		}
		
		return sb.length() > 2 ? sb.substring(2) : "";
	}

	@Override
	public void setFormText(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flagErroneous() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flagValid() {
		// TODO Auto-generated method stub
		
	}

    public void addChangeListener(Signal.Listener listener)
    {
    	if(box !=null){
    		box.changed().addListener(this, listener);
    	}
    }

    public void addItem(DataComboMessage<ComboDataType> item)
    {
        if(box !=null){
            box.addItem(item);
        }
        else{
            list_.add(item);
        }
    }
    
    public void selectIndex(int index){
        if(box != null){
            if(box.getCount()>0){
                Set<Integer> indexes = box.getSelectedIndexes();
                indexes.add(index);
                box.setSelectedIndexes(indexes);
            }
        }
        else{
        	selectedIndexes.add(index);
            setViewMessage(getFormText());
        }
    }
    
    public Set<Integer> getSelectedIndexes(){
    	return box == null ? null : box.getSelectedIndexes();
    }
    
    @SuppressWarnings("unchecked")
	public Set<DataComboMessage<ComboDataType>> currentItems()
    {
    	Set<DataComboMessage<ComboDataType>> items = new TreeSet<DataComboMessage<ComboDataType>>();
    	if(box!=null)
    	{
    		for(int i : box.getSelectedIndexes()){
    			items.add((DataComboMessage<ComboDataType>)box.getItemText(i));
    		}
    	}
    	else
    	{
    		for(int i : selectedIndexes){
    			items.add(list_.get(i));
    		}
    	}
    	
    	return items;
    }
    
    public void selectItem(String messageValueRepresentation)
    {
        if(box!=null)
        {
            for(int i = 0; i < box.getCount(); i++) {
                if(box.getItemText(i).getValue().equals(messageValueRepresentation)) {
                    selectIndex(i);
                    return;
                }
            }
        }
        else
        {
            for(int i = 0; i < list_.size(); i++) {
                if(list_.get(i).getValue().equals(messageValueRepresentation)) {
                    selectIndex(i);
                    return;
                }
            }
        }
    }
    
    public void clearItems()
    {
    	if(box!=null)
    	{
    		box.clear();
    	}
        else
        {
            list_.clear();
        }
    }
    
    public void setEnabled(boolean enabled)
    {
        if(box!=null)
        {
            box.setEnabled(enabled);
        }
    }
    
    public void setHidden(boolean hide)
    {
        if(box!=null)
        {
            box.setHidden(hide);
        }
        else
        {
            getViewWidget().setHidden(hide);
        }
    }
    
    public boolean isHidden()
    {
        if(box!=null)
        {
            return box.isHidden();
        }
        else
        {
            return getViewWidget().isHidden();
        }
    }
    
    public int size(){
        return box.getCount();
    }
    public int count(){
        return size();
    }
    
    public void sort() {
        if(box!=null) {
//            box.sort();
        }
    }
    
    public void setSelectionMode(SelectionMode mode){
    	if(box != null)
    		box.setSelectionMode(mode);
    }
    
    public SelectionMode getSelectionMode(){
    	return box == null ? null : box.getSelectionMode();
    }
}
