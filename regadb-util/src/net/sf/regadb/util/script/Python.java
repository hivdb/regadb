package net.sf.regadb.util.script;

import java.util.Collection;
import java.util.Map;

import org.python.core.PyDictionary;
import org.python.core.PyFunction;
import org.python.util.PythonInterpreter;

public class Python {
	private static Python instance;
	
	private PythonInterpreter interpreter;
	
	private Python() {
		interpreter = new PythonInterpreter();
	}
	
	public void execute(String code, String functionName, Map<String, String> argument) {
		interpreter.exec(code);
        PyFunction func = interpreter.get(functionName, PyFunction.class);
        PyDictionary dict = new PyDictionary();
        dict.putAll(argument);
        func.__call__(dict);        
        Collection values = dict.keys();
        argument.clear();
        for (Object v : values) {
            argument.put((String)v, (String)dict.get(v));
        }
	}
	
	public static Python getInstance() {
		if (instance == null)
			instance = new Python();
		
		return instance;
	}
}
