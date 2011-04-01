package net.sf.regadb.analyses.queries.egazMoniz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

public class M184IV {

	@SuppressWarnings("unchecked")
	public static void main(String args[]) throws WrongUidException, WrongPasswordException, DisabledUserException, FileNotFoundException{
		Arguments as = new Arguments();
		
		PositionalArgument user = as.addPositionalArgument("user", true);
		PositionalArgument pass = as.addPositionalArgument("pass", true);
		PositionalArgument file = as.addPositionalArgument("result.csv", true);
		
		if(!as.handle(args))
			return;
		
		RegaDBSettings.createInstance();
		
		Login login = Login.authenticate(user.getValue(), pass.getValue());
		
		Transaction t = login.createTransaction();
		
		//get list of vi's with M184IV mut
		Query q = t.createQuery("select nt.viralIsolate.viralIsolateIi from AaMutation m join m.id.aaSequence aa join aa.ntSequence nt"
						+" where m.id.mutationPosition = 184 and m.aaReference = 'M' and (m.aaMutation like '%I%' or m.aaMutation like '%V%')"
						+" order by nt.viralIsolate.viralIsolateIi");
		StringBuilder sb = new StringBuilder();
		for(Object o : q.list()){
			sb.append(',').append((Integer)o);
		}
		
		//quit if no M184IV muts found
		if(sb.length() == 0)
			return;
		
		String viiList = '('+ sb.substring(1) +')';
		
		//get list of Viral Load tests
		q = t.createQuery("select t.testIi from Test t join t.testType tt join tt.genome g" +
				" where tt.description = 'Viral Load (copies/ml)' and g.organismName = 'HIV-1'");
		sb.delete(0, sb.length());
		for(Object o : q.list())
			sb.append(',').append((Integer)o);
		String tiiList = '('+ sb.substring(1) +')';
		
		//get list of patients whose first therapy includes 3TC or FTC
		q = t.createQuery("select p.patientIi, p.patientId, t from PatientImpl p join p.therapies t"
				+" where t.therapyIi in (select tg.id.therapy.therapyIi from TherapyGeneric tg where tg.id.drugGeneric.genericId in ('3TC', 'FTC'))"
				+" and t.startDate = (select min(t2.startDate) from Therapy t2 where t2.patient = t.patient)"
				+" order by t.startDate");
		
		PrintStream out = new PrintStream(new FileOutputStream(new File(file.getValue())));
		out.println("patient_id,birth_date,gender,therapy,therapy_start,therapy_stop,vl_date,vl,sample_id,sample_date");
		
		List<Object[]> r = q.list();
		for(Object[] o : r){
			Integer patientIi = (Integer)o[0];
			String patientId = (String)o[1];
			Therapy therapy = (Therapy)o[2];

			//check if the patient has a M184IV vi, less than 3 months after therapy stop
			String baseQuery = "select v from ViralIsolate v where v.patient.patientIi = "+ patientIi
				+" and v.sampleDate > :vStartDate and v.viralIsolateIi in "+ viiList;
			Query q2;
			
			if(therapy.getStopDate() != null){
				Calendar c = Calendar.getInstance();
				c.setTime(therapy.getStopDate());
				
				if(c.get(Calendar.YEAR) < 1900){
					//special date, include
					q2 = t.createQuery(baseQuery +" order by v.sampleDate asc");
				} else {
					c.add(Calendar.MONTH, 3);
					
					q2 = t.createQuery(baseQuery +" and v.sampleDate <= :vStopDate order by v.sampleDate asc");
					q2.setDate("vStopDate", c.getTime());
				}
			} else
				//still going on
				q2 = t.createQuery(baseQuery +" order by v.sampleDate asc");
			
			q2.setParameter("vStartDate", therapy.getStartDate());
			
			List<Object> r2 = q2.list();
			if(r2.size() == 0)
				//no M184IV sequences
				continue;
			
			ViralIsolate vi = (ViralIsolate)r2.get(0);
			
			//get viral load
			TestResult vl = null;
			q2 = t.createQuery("select tr from TestResult tr where tr.patient.patientIi = "+ patientIi
					+" and tr.test.testIi in "+ tiiList +" and tr.testDate = :vSampleDate");
			q2.setDate("vSampleDate",vi.getSampleDate());
			r2 = q2.list();
			if(r2.size() > 0)
				vl = (TestResult)r2.get(0);
			
			//get regiment
			q2 = t.createQuery("select dg.genericId from TherapyGeneric tg join tg.id.drugGeneric dg" 
					+" where tg.id.therapy.therapyIi = "+ therapy.getTherapyIi() +" order by dg.genericId");
			r2 = q2.list();
			sb.delete(0, sb.length());
			for(Object o2 : r2)
				sb.append('+').append((String)o2);
			String regiment = sb.substring(1);
			
			String gender = null;
			q2 = t.createQuery("select pav.attributeNominalValue.value from PatientAttributeValue pav join pav.attribute a"
					+" where pav.patient.patientIi = "+ patientIi +" and a.name = 'Gender'");
			r2 = q2.list();
			if(r2.size() > 0)
				gender = (String)r2.get(0);
			
			Date birthDate = null;
			q2 = t.createQuery("select pav.value from PatientAttributeValue pav join pav.attribute a"
					+" where pav.patient.patientIi = "+ patientIi +" and a.name = 'Birth date'");
			r2 = q2.list();
			if(r2.size() > 0)
				birthDate = new Date(Long.parseLong((String)r2.get(0)));
			
			out.print(patientId +','+ format(birthDate) +','+ gender);
			out.print(','+ regiment +','+ format(therapy.getStartDate()) +','+ format(therapy.getStopDate()));
			if(vl != null)
				out.print(","+ format(vl.getTestDate()) +','+ vl.getValue());
			else
				out.print(",,");
			out.print(','+ vi.getSampleId() +','+ format(vi.getSampleDate()));
			out.println();
			
			t.clearCache();
			t.flush();
		}
		
		t.commit();
	}
	
	static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public static String format(Date date){
		return date == null ? "" : sdf.format(date);
	}
}
