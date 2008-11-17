package net.sf.regadb.io.generation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.sf.regadb.util.hbm.InterpreteHbm;

public class PersistenceWriteCodeGen {

	public void writeClassToFile() {
		String total = "";

		//package declaration
//		total = "package net.sf.regadb.io.persistence;\n\n";        

		//imports
		InterpreteHbm interpreter = InterpreteHbm.getInstance();
		String imports = "import net.sf.regadb.db.Patient;\n";
		Package p = Package.getPackage("net.sf.regadb.db");
		
//		for(String className : interpreter.getClassNames())
//		{
//			imports += "import "+className.replace("PatientImpl", "Patient") +";\n";
//		}
		imports += "import org.hibernate.Hibernate;\n";
//		total+=imports+"\n";       

		total+="public class ExportToPersistentObjects {\n";
		total+="public void initialize(Patient p){\n";
		total+="write(p);\n";
		total+="}\n\n";
	
		
		ArrayList<String> classes = new ArrayList<String>();
		for(String className : interpreter.getClassNames())
		{
			classes.add(className.replace("net.sf.regadb.db.", "").replace("PatientImpl", "Patient"));
		}
		
		ArrayList<String> queue = new ArrayList<String>();
		ArrayList<String> done = new ArrayList<String>();
		queue.add("Patient"); //starting class = patient
		while(!queue.isEmpty()){
			System.out.println("queue size: "+queue.size()+" done size: "+done.size());
			String shortClassName = null;
			
				shortClassName = queue.get(0);
			
			total+= "private void write(" + shortClassName + " " + toObjectName(shortClassName) + "){\n";
			Class c = null;
			try {
				c = Class.forName("net.sf.regadb.db."+shortClassName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			for(Method m : c.getMethods()){
				String methodName = m.getName().replace("PatientDatasets", "Datasets");
				if(!ignoreMethod(m)){
					total+="\tHibernate.initialize("+ toObjectName(shortClassName) +"."+methodName+"());\n";
					if(m.getReturnType().equals(java.util.Set.class) && !done.contains(toClassName(m)) && !queue.contains(toClassName(m))){
						total+="\tfor("+toClassName(m)+ " " + toObjectName(toClassName(m)) + ":" + toObjectName(shortClassName) + "." + methodName + "()){\n";
						total+="\twrite("+toObjectName(toClassName(m)) + ");\n";
						total+="\t}\n";
						imports+= "import net.sf.regadb.db."+toClassName(m)+";\n";
						queue.add(toClassName(m));
					}else if(!done.contains(toClassName(m)) && !queue.contains(toClassName(m))){
						total+="\tif("+toObjectName(shortClassName) +"."+methodName+"() != null"+")write("+toObjectName(shortClassName) +"."+methodName+"());\n";
						imports+= "import net.sf.regadb.db."+toClassName(m)+";\n";
						queue.add(toClassName(m));
					}
//				}else if(m.getName().startsWith("get") && m.getParameterTypes().length ==0 ){
//					total+="\tHibernate.initialize("+toObjectName(shortClassName) +"."+methodName+"());\n";
				}

			}
			total+= "}\n\n";
			done.add(shortClassName);
			queue.remove(queue.indexOf(shortClassName));
		}
		total += "\n}";

		String srcDir = GenerateIO.getSrcPath("net.sf.regadb.io.persistence");
		File exportJavaCodeFile = new File(srcDir+File.separatorChar+"ExportToPersistentObjects.java");
		FileWriter fw;
		try {
			fw = new FileWriter(exportJavaCodeFile);
			fw.write("package net.sf.regadb.io.persistence;\n\n");
			fw.write(imports);
			fw.write(total);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String toObjectName(String className){
		return className.substring(0,1).toLowerCase() + className.substring(1);
	}
	
	private String toClassName(Method m){
		return m.getGenericReturnType().toString().replace("java.util.Set<", "").replace("net.sf.regadb.db.","").replace(">", "").replace("class ", "");
	}
	
	
	
	private boolean ignoreMethod(Method m){
		return (!m.getName().startsWith("get") || // method must be a getter
				m.getName().equals("getClass") ||
				m.getName().equals("getPatient") ||
				m.getReturnType().equals(Long.class) ||
				m.getReturnType().equals(Float.class) ||
				m.getReturnType().equals(Double.class) ||
				m.getReturnType().equals(Boolean.class) ||
				m.getReturnType().equals(Integer.class) ||  
				m.getReturnType().equals(String.class) ||
				m.getReturnType().equals(Byte.class) ||
				m.getReturnType().equals(java.util.Date.class) ||
				m.getReturnType().isEnum() ||
				m.getReturnType().isArray() ||
				m.getReturnType().isPrimitive() ||
				m.getParameterTypes().length !=0
				);		
	}

	public static void main(String[] args){
		(new PersistenceWriteCodeGen()).writeClassToFile();
	}

}
