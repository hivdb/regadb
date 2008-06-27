package net.sf.regadb.io.db.euresist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.io.db.util.MysqlDatabase;

public class CheckStopCauses {
	//140 therapies
	//140/63149
	public static void main(String [] args) {
		MysqlDatabase mysql = new MysqlDatabase("EuResist", "root", "Eatnomeat001");
		int counter = 0;
		try {
			ResultSet rs = mysql.executeQuery("SELECT * FROM Therapies");
        	while(rs.next()) {
        		int therapyID = rs.getInt("therapyID");
        		int stopCause = rs.getInt("stop_causeID");
        		if(stopCause==0) {
        			ResultSet rs_compounds = mysql.executeQuery("SELECT * FROM TherapyCompounds WHERE therapyID="+therapyID);
        			Set<Integer> causes = new HashSet<Integer>();
        			
        			while(rs_compounds.next()) {
        				int compoundStopCause = rs_compounds.getInt("stop_causeID");
        				if(compoundStopCause!=0 && compoundStopCause!=7 && compoundStopCause!=6 && compoundStopCause!=5) {
        					causes.add(compoundStopCause);
        				}
        			}
        			if(causes.size()>1) {
        				System.err.print("oeps: "+therapyID + " -> ");
        				for(Integer c : causes) {
        					System.err.print(c + " ");
        				}
        				System.err.println();
        				counter++;
        			}
        		}
        	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.err.println(counter);
	}
}
