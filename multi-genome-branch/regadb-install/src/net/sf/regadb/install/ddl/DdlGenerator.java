package net.sf.regadb.install.ddl;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public abstract class DdlGenerator {
	public static class Query{
		private String[] words;
		private String str;
		
		public Query(String query){
			str = query;
			words = query.split(" ");
			if(words.length < 3)
				System.err.println(query);
		}
		
		public String getOp(){
			return words[0];
		}
		public String getType(){
			return words[1];
		}
		public String getName(){
			return words[2];
		}
		public String toString(){
			return str;
		}
	}
	
	public static class QueryComparator implements Comparator<Query>{
		protected ArrayList<String> opOrder;
		protected ArrayList<String> typeOrder;
		
		public QueryComparator(){
			opOrder = new ArrayList<String>();
			opOrder.add("drop");
			opOrder.add("create");
			opOrder.add("alter");			

			typeOrder = new ArrayList<String>();
			typeOrder.add("schema");
			typeOrder.add("sequence");
			typeOrder.add("table");
			typeOrder.add("index");
		}
		
		public int compare(Query q1, Query q2) {
			if(q1.getType().equals(q2.getType())){
				if(q1.getOp().equals(q2.getOp())){
					return q1.toString().compareTo(q2.toString());
				}
				else{
					return opOrder.indexOf(q1.getOp()) - opOrder.indexOf(q2.getOp()); 
				}
			}
			else{
				return typeOrder.indexOf(q1.getType()) - typeOrder.indexOf(q2.getType());
			}
		}
	}

	private String dialect;
	private String driver;
	
	public DdlGenerator(String dialect, String driver){
		this.setDialect(dialect);
		this.setDriver(driver);
	}
	
	public void generate(String fileName){
		export(fileName);
		List<Query> queries = process(fileName);
		sort(queries);
		write(fileName, queries);
	}
	
	protected void export(String fileName){
		Configuration config = new Configuration().configure();
        config.setProperty("hibernate.dialect", getDialect());
        config.setProperty("hibernate.connection.driver_class", getDriver());
        SchemaExport export = new SchemaExport(config);
        export.setOutputFile(fileName);
        export.create(true, false);
	}
	
	protected List<Query> process(String fileName){
		List<Query> queries = new ArrayList<Query>();
		try{
        	BufferedReader in = new BufferedReader(new FileReader(fileName));
        	String line;
        	
        	while((line = in.readLine()) != null){
        		if(!ignore(line)){
	        		line = processLine(line);
	        		if(line != null && line.length() != 0)
	        			queries.add(new Query(line));
        		}
        	}
        	
        	in.close();
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        return queries;
	}
	
	protected boolean ignore(String line){
		if(line.length() == 0)
			return true;
		
		if(line.startsWith("drop "))
			return true;
		
		if(line.indexOf(" drop ") != -1)
			return true;
		
		return false;
	}
	
	protected void sort(List<Query> queries){
		java.util.Collections.sort(queries, new QueryComparator());
	}
	
	protected void write(String fileName, List<Query> qs){
		try{
			PrintStream out = new PrintStream(new FileOutputStream(fileName));
			for(Query q : qs)
				out.println(q.toString() +";");
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected abstract String processLine(String line);

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getDriver() {
		return driver;
	}
	
    public static String truncate(int max, String delim, String s1, String s2){
        String ret=s1 +"_"+ s2;
        
        if(ret.length() > max){
            String [] w1 = s1.split(delim);
            String [] w2 = s2.split(delim);
            
            int i=0;
            int j=Math.min(w1.length, w2.length);
            while(i < j && w1[i].equals(w2[i]))
                ++i;
            
            ret = "";
            for(j=0; j<i; ++j)
                ret += delim+w1[j];
            for(j = i; j < w1.length; ++j)
                ret += delim+w1[j];
            for(j = i; j < w2.length; ++j)
                ret += delim+w2[j];
            
            ret = ret.substring(1);
        }
        return ret;
    }
}
