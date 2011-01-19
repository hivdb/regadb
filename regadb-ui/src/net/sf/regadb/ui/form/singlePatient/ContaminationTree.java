package net.sf.regadb.ui.form.singlePatient;

import java.awt.Color;
import java.awt.Font;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.NewickImporter;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.Tree;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.service.wts.NucleotideAlignment;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.service.wts.TreeBuilder;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WVBoxLayout;
import figtree.panel.SimpleLabelPainter;
import figtree.treeviewer.decorators.AttributableDecorator;
import figtree.treeviewer.decorators.DiscreteColorDecorator;
import figtree.webui.TreeWidget;

public class ContaminationTree extends WContainerWidget {

	private String sequenceLabel;
	private String sampleId;
	private String patientId;
	
	private Map<String, Map<String, String>> sequenceAttributes = new TreeMap<String, Map<String, String>>();

	private TreeWidget treeWidget;
	private String newickTree;

	private SimpleLabelPainter tipPainter;
	private SimpleLabelPainter nodePainter;

	public ContaminationTree(WContainerWidget parent, String organism, Map<String, Map<String, String>> annotatedSequences, NtSequence ntSequence) {
		super(parent);

		this.sequenceAttributes = annotatedSequences;
		this.sequenceLabel = ntSequence.getLabel();
		this.sampleId = ntSequence.getViralIsolate().getSampleId();
		this.patientId = new Patient(ntSequence.getViralIsolate().getPatient(), Privileges.READONLY.getValue()).getPatientId();
		
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Map<String, String>> e : sequenceAttributes.entrySet()) {
			sb.append(">");
			sb.append(e.getKey());
			sb.append("\n");
			sb.append(e.getValue().get("nucleotides"));
			sb.append("\n");
		}
		String fastaSequences = sb.toString();

		try {
			NucleotideAlignment nucAligner = new NucleotideAlignment(sb.toString(), organism, 0.5); // TODO config?
			nucAligner.launch();
			String aligned = nucAligner.getAlignedSequences();
			TreeBuilder tb = new TreeBuilder(aligned);
			tb.launch();
			newickTree = tb.getNewickTree();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		WVBoxLayout layout = new WVBoxLayout(this);
		layout.addWidget(treeWidget = new TreeWidget(), 1);
		treeWidget.setMinimumSize(new WLength(200), new WLength(sequenceAttributes.size() * 20));
		treeWidget.setStyleClass("phylotree");
		loadTree(newickTree);
	}

	private void loadTree(String stree) {
		try {
			StringReader reader = new StringReader(stree);
			List<Tree> trees = new ArrayList<Tree>();
			NewickImporter importer = new NewickImporter(reader, true);
			while (importer.hasTree()) {
				Tree tree = importer.importNextTree();
				trees.add(tree);
			}
			RootedTree treeToLoad = (RootedTree) trees.get(0);
			
			Font defaultFont = new Font("sans-serif", Font.PLAIN, 10);
			//show bootstrap values
			treeWidget.getTreePane().setNodeLabelPainter(nodePainter = new SimpleLabelPainter(SimpleLabelPainter.PainterIntent.NODE));
			nodePainter.setupAttributes(trees);
			nodePainter.setDisplayAttribute("label");
			nodePainter.setTextDecorator(new AttributableDecorator());
			nodePainter.setFont(defaultFont);
						
			//set sample ids on tips - coloring indicates patient
			for(Node n : treeToLoad.getExternalNodes()) {
				Taxon taxon = treeToLoad.getTaxon(n);
				n.setAttribute("patient", sequenceAttributes.get(taxon.getName()).get("patient").equals(patientId));
				n.setAttribute("label",sequenceAttributes.get(taxon.getName()).get("sample"));
			}
			
			treeWidget.getTreePane().setTipLabelPainter(tipPainter = new SimpleLabelPainter(SimpleLabelPainter.PainterIntent.TIP));
			tipPainter.setupAttributes(trees);
			tipPainter.setFont(defaultFont);
			tipPainter.setDisplayAttribute("label");
			tipPainter.setTextDecorator(new DiscreteColorDecorator("patient", treeToLoad.getExternalNodes(), new Color[]{Color.BLACK, Color.RED}, false));
			
			//load tree
			treeWidget.getTreePane().setTree((RootedTree) trees.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
