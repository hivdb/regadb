package net.sf.regadb.ui.framework.forms;

public interface FormListener {
	public void canceled(IForm form, InteractionState interactionState);
	public void confirmed(IForm form, InteractionState interactionState);
}
