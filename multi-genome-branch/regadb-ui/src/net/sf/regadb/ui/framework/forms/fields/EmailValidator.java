package net.sf.regadb.ui.framework.forms.fields;

import eu.webtoolkit.jwt.WRegExpValidator;

class EmailValidator extends WRegExpValidator
{
    public EmailValidator()
    {
        super("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}");
    }
}