package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.util.List;

public interface ComposedAWCEditorPanel {
	public abstract ComposedAWCManager getManager();
	public abstract void initConfigurers();
	public abstract List<WordConfigurer> getConfigurers();
}
