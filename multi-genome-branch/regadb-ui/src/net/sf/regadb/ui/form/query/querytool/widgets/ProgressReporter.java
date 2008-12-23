package net.sf.regadb.ui.form.query.querytool.widgets;

import eu.webtoolkit.jwt.WString;


public interface ProgressReporter {
	void start();
	boolean isDone();
	int getProgress();
	WString getMessage();
}
