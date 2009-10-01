package be.kuleuven.rega.research.discordance;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.io.importXML.ResistanceDiscordanceInterpretationParser;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.io.util.StandardObjects;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import be.kuleuven.rega.research.FilterVISubtype;
import be.kuleuven.rega.research.conserved.groupers.SubtypeGrouper;

public class PatientDiscordance {

	final static private TestType gss = StandardObjects.getGssTestType(StandardObjects.getHiv1Genome());
	final static private String snapshot = "/home/tm/labo/test.snapshot.20090807";

	final static private HashMap<String, Writer> drugFiles = new HashMap<String, Writer>();
	final static private List<DrugGeneric> drugs = DrugGenericUtils.prepareRegaDrugGenerics();
	final static private Map<String, AlgorithmResultBuffer> buffer = new HashMap<String, AlgorithmResultBuffer>();

	public static void main(String[] args) throws IOException {
		init();

		QueryInput in = new FromDatabase("admin", "admin", new IQuery<Patient>() {

			@Override
			public void process(Patient input) {
				for (ViralIsolate vi : input.getViralIsolates()) {
					for (TestResult tr : vi.getTestResults()) {
						if (!Equals.isSameTestType(tr.getTest().getTestType(),gss)) {
							continue;
						}
						Collection<DrugClass> before = TherapyUtils.getClassesBefore(input.getTherapies(), tr.getTestDate());
						processTestResult(tr, before);
					}
					handleTestResults(vi, input.getPatientId());
				}
			}

			@Override
			public void close() {
				for (DrugGeneric drug : drugs) {
					if (drug.getDrugClass().getClassId().equals("Unknown"))
						continue;
					try {
						drugFiles.get(drug.getGenericId()).close();
					} catch (IOException e) {
						System.out.println("error closing file for drug "+drug.getGenericName());
						e.printStackTrace();
					}
				}
			}
		});
		in.run();
	}

	private static void init() throws IOException {
		for (DrugGeneric drug : drugs) {
			if (drug.getDrugClass().getClassId().equals("Unknown"))
				continue;
			buffer.put(drug.getGenericId(), new AlgorithmResultBuffer());
			String fn = "muts-" + drug.getGenericId().replaceAll("/", "");
			File file = new File(fn);
			System.out.println(file.getCanonicalPath());
			file.delete();
			file.createNewFile();
			drugFiles.put(drug.getGenericId(), new BufferedWriter(new FileWriter(file)));
		}
	}

	private static void processTestResult(final TestResult tr, final Collection<DrugClass> before) {
		final Algorithm current = Algorithm.getAlgorithmFor(tr.getTest().getDescription());
		if (current == Algorithm.other) {
			return;
		}

		ResistanceInterpretationParser inp = new ResistanceDiscordanceInterpretationParser() {
			@Override
			public void completeScore(String drug, Integer level, double gss,
					String description, Character sir,
					ArrayList<String> mutations, int rule, String remarks) {
				boolean treated = false;
				if(DrugGenericUtils.containsClass(before, drug)){
					treated = true;
				}
				buffer.get(drug).updateScores(current, sir, gss, rule, treated);
			}
		};
		try {
			inp.parse(new InputSource(new ByteArrayInputStream(tr.getData())));
		} catch (SAXException e) {
			System.err.println("Parsing of resistance test failed");
		} catch (IOException e) {
			System.err.println("Parsing of resistance test failed");
		}
	}

	private static void handleTestResults(ViralIsolate vi, String patient) {
		String subtype = FilterVISubtype.determineSubtype(vi);
		if(subtype==null)
			subtype = "unknown";
		
		for (DrugGeneric drug : drugs) {
			if (drug.getDrugClass().getClassId().equals("Unknown"))
				continue;
			boolean closed = false;
			for (NtSequence nt : vi.getNtSequences()) {
				for (AaSequence aa : nt.getAaSequences()) {
					if(!Equals.isSameGenome(aa.getProtein().getOpenReadingFrame().getGenome(), StandardObjects.getHiv1Genome()))
						continue;
					if (!Util.getProtein(drug).getAbbreviation().equals(aa.getProtein().getAbbreviation()))
						continue;
					AlgorithmResultBuffer result = buffer.get(drug.getGenericId());
					try {
						Writer writer = drugFiles.get(drug.getGenericId());
						result.writeDiscordances(writer, aa, subtype);
					} catch (IOException e) {
						System.err.println("error writing for drug "+ drug.getGenericId());
					}
					result.end();
					closed = true;
					break;
				}
				if(!closed){
					buffer.get(drug.getGenericId()).end();
					break;
				}
			}
		}
	}
}