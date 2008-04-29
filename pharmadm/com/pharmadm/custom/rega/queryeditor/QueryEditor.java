package com.pharmadm.custom.rega.queryeditor;

import javax.swing.event.TreeModelListener;

import com.pharmadm.custom.rega.savable.Savable;

public interface QueryEditor extends Savable{
	public void setDirty(boolean dirty);
	public Query getQuery();
	public void addTreeModelListener(TreeModelListener listener);
}
