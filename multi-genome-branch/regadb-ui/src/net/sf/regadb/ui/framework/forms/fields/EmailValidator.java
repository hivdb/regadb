package net.sf.regadb.ui.framework.forms.fields;

import eu.webtoolkit.jwt.WRegExpValidator;
import eu.webtoolkit.jwt.utils.WtRegex;

class EmailValidator extends WRegExpValidator
{
    public EmailValidator()
    {
        super(new WtRegex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}"));
    }
}