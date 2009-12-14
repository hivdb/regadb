import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.preprocessing.Utils;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.framework.utils.AaSequenceUtils;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.hivgensim.queries.framework.utils.PatientUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.parse.tags.write.WriteXML;
import org.encog.util.logging.Logging;

public class NNTest {

	public static void main(String[] args) {
		String drug = args[0];
		String banedir = args[1];
		String network = args[2];
		String landscape = args[3];
		String snapshot = args[4];
		
		List<String> algs = new ArrayList<String>();
		for (int i = 5; i < args.length; i++) {
			algs.add(args[i]);
		}
		
		new NNTest(algs,drug, banedir, network, landscape, snapshot);
	}

	private List<String> algorithms;
	private String drug;
	private String baneDir;
	private String banePrefix;
	private String landscape;
	private String snapshot;

	private Set<String> allMutations = new HashSet<String>();
	private NeuralDataSet trainingData = new BasicNeuralDataSet();
	private BasicNetwork network;
	private NeuralDataSet validationData = new BasicNeuralDataSet();

	public NNTest(List<String> algorithms, String drug, String baneDir, String network, String landscape, String snapshot){
		this.algorithms = algorithms;
		this.drug = drug;
		this.baneDir = baneDir;
		this.landscape = landscape;
		this.banePrefix = network;
		this.snapshot = snapshot;
		RegaDBSettings.createInstance();
		setMutations();
		runNetwork();
	}

	private void setMutations() {
		for(String algorithm: algorithms){
			MutationsForDrugService mutserv = new MutationsForDrugService(algorithm, drug);
			try {
				mutserv.launch();
			} catch (ServiceException e) {
				System.err.println("Error reaching "+mutserv.getService());
				e.printStackTrace();
				System.exit(1);
			}
			allMutations.addAll(mutserv.getMutations());
		}
		MutationsFromNetworkService mutnetserv = new MutationsFromNetworkService(this.baneDir+System.getProperty("file.separator")+"mut_treated");
		try {
			mutnetserv.launch();
		} catch (ServiceException e) {
			System.err.println("Error reaching "+mutnetserv.getService());
			e.printStackTrace();
			System.exit(1);
		}
		allMutations.addAll(mutnetserv.getMutations());
	}

	private void runNetwork() {
		constructData();
		constructNetwork();

		Logging.stopConsoleLogging();
		final Train train = new NeuralSimulatedAnnealing(network, trainingData, 10, 1, 10);
		int epochSinceMax = 0;
		double minError = Double.MAX_VALUE;
		double error;
		do {
			train.iteration();
			error = getValidationError();
			if(error < minError){
				minError = error;
				try {
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("network.best"));
					out.writeObject(train.getNetwork());
					out.flush();
					out.close();
					train.getNetwork().createPersistor().save(train.getNetwork(), new WriteXML(new FileOutputStream("network.xml")));
				} catch (IOException e) {
					System.err.println("could not write network");
					e.printStackTrace();
				}
				epochSinceMax = 0;
			} else {
				epochSinceMax++;
			}
			System.out.println("Epoch since max: " + epochSinceMax + " Training error:" + train.getError()+" Validation error: "+error);
		} while (epochSinceMax < 100);
	}

	private double getValidationError() {
		return network.calculateError(validationData);
	}

	private void constructData(){
		final List<NeuralDataPair> dataPairs = new ArrayList<NeuralDataPair>();

		QueryInput in = new FromSnapshot(new File(snapshot), new IQuery<Patient>() {
			int i=0;
			@Override
			public void process(Patient p) {
				if(allDrugs==null){
					allDrugs = DrugGenericUtils.prepareRegaDrugGenerics();
				}
				List<Therapy> therapies = TherapyUtils.sortTherapiesByStartDate(p.getTherapies());

				for (Therapy t : therapies) {
					if (t.getStartDate() == null || t.getStopDate() == null) {
						continue;
					}
					if (TherapyUtils.hasDrugExperience(NNTest.this.drug, t)) {
						Set<NtSequence> seqs = PatientUtils.getSequencesForProtein(p, t, 30, "RT");

						for (NtSequence seq : seqs) {
							long ttf = TimeToFailureService.timeToFailure(p, t);

							if (ttf > 8*7 && ttf < 2*365) {
								ttf = ttf - 200;
								Set<AaMutation> muts = null;
								for(AaSequence aas : seq.getAaSequences()){
									if(AaSequenceUtils.coversRegion(aas, "HIV-1", "RT")){
										muts = aas.getAaMutations();
										break;
									}
								}
								i++;
								System.out.println(i);
								String aligned = Utils.getAlignedNtSequenceString(seq, SelectionWindow.RT_WINDOW_REGION);
								NeuralDataPair pair = createDataPair(aligned, ttf, t.getTherapyGenerics(), muts);
								dataPairs.add(pair);
							}
						}
					}
				}
			}
			@Override public void close() {}
		});
		in.run();

		int training = (int) (0.9*dataPairs.size());
		for (int i = 0; i < training; i++) {
			trainingData.add(dataPairs.get(i));
		}
		for (int i = training; i < dataPairs.size(); i++) {
			validationData.add(dataPairs.get(i));
		}
	}

	//	muts uit bn moet maar 1 keer -> mutsfile variabele parameter
	//	NN willen we eigenlijk in de vorm van als zo'n mutatie dan lineaire combinatie van predicties
	//	hoe het beste layers/functies/learning doen in dat geval?
	//	fuzzy if then rules?

	private List<DrugGeneric> allDrugs;

	private NeuralDataPair createDataPair(String sequence, long ttf, Set<TherapyGeneric> drugs, Set<AaMutation> muts) {
		List<Double> inData = new ArrayList<Double>();
		inData.add(1d);
		for(String mut: allMutations){
			boolean found = false;
			for(AaMutation patientMut: muts){
				String aaStr = patientMut.getAaMutation();
				String mutString = patientMut.getId().getMutationPosition()+(aaStr==null ? "d" : aaStr.toUpperCase());
				if(mutString.equals(mut)){
					inData.add(1d);
					found = true;
					break;
				}
			}
			if(!found){
				inData.add(-1d);
			}
		}

		for(DrugGeneric drug: allDrugs){
			boolean found = false;
			for(TherapyGeneric tdrug: drugs){
				if(Equals.isSameDrugGeneric(drug, tdrug.getId().getDrugGeneric())){
					inData.add(1d);
					found = true;
					break;
				}
			}
			if(!found){
				inData.add(-1d);
			}
		}

		System.err.println("TTF");
		double gens = TimeToFailureService.getTTFGens(sequence, this.baneDir, this.banePrefix, this.landscape);
		inData.add(gens-1900);
		
		for(String xml: algorithms){
			System.err.println("Interpretation");
			double gss = getDrugInterpretation(sequence, xml);
			inData.add(gss);
		}
		
		System.out.println("input: "+inData);
		System.out.println("output: "+ttf);
		if(inData.size() != algorithms.size() + allMutations.size() + allDrugs.size() + 2){
			throw new IllegalStateException();
		}

		double[] converted = new double[inData.size()];
		for (int i = 0; i < converted.length; i++) {
			double d = inData.get(i);
			converted[i] = d;
		}
		BasicNeuralData input = new BasicNeuralData(converted);
		BasicNeuralData output = new BasicNeuralData(new double[] {(double)ttf});
		return new BasicNeuralDataPair(input, output);
	}

	private double getDrugInterpretation(String sequence, String xml) {
		SingleDrugInterpretationService alg = new SingleDrugInterpretationService(xml, drug, sequence);
		try {
			alg.launch();
		} catch (ServiceException e) {
			System.err.println("Service failed!");
			e.printStackTrace();
			System.exit(1);
		}
		return alg.getGss();
	}

	public void constructNetwork(){
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, algorithms.size()*2 + allMutations.size() + allDrugs.size()));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, allMutations.size() + allDrugs.size()));
		network.addLayer(new BasicLayer(new ActivationLinear(),true,1));
		network.getStructure().finalizeStructure();
		network.reset();
	}

	public Dataset getDatasource(Patient p) {
		for (Dataset ds : p.getDatasets()) {
			if (ds.getClosedDate() == null) {
				return ds;
			}
		}

		return null;
	}

}
