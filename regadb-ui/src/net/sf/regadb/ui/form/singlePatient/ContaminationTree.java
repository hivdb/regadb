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
import eu.webtoolkit.jwt.AlignmentFlag;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WBoxLayout;
import eu.webtoolkit.jwt.WBoxLayout.Direction;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WVBoxLayout;
import figtree.panel.SimpleLabelPainter;
import figtree.treeviewer.decorators.AttributableDecorator;
import figtree.treeviewer.decorators.DiscreteColorDecorator;
import figtree.treeviewer.treelayouts.PolarTreeLayout;
import figtree.treeviewer.treelayouts.RadialTreeLayout;
import figtree.treeviewer.treelayouts.RectilinearTreeLayout;
import figtree.webui.TreeWidget;

public class ContaminationTree extends WContainerWidget {

	private String sequenceLabel;
	private String sampleId;
	private String patientId;

	private Map<String, Map<String, String>> sequenceAttributes = new TreeMap<String, Map<String, String>>();

	private TreeWidget treeWidget;
	private WCheckBox showBootstrapValues;
	private WComboBox treeLayoutBox;

	private enum TreeLayout {
		RADIAL(WString.tr("form.viralIsolate.similarity.tree.layout.radial"), new RadialTreeLayout()), 
		RECTILINEAR(WString.tr("form.viralIsolate.similarity.tree.layout.rectilinear"),	new RectilinearTreeLayout()), 
		POLAR(WString.tr("form.viralIsolate.similarity.tree.layout.polar"), new PolarTreeLayout());

		private WString name;
		private figtree.treeviewer.treelayouts.TreeLayout layout;

		private TreeLayout(WString name, figtree.treeviewer.treelayouts.TreeLayout layout) {
			this.name = name;
			this.layout = layout;
		}

		public String toString() {
			return name.toString();
		}
	}

	private String newickTree;

	private SimpleLabelPainter tipPainter;
	private SimpleLabelPainter nodePainter;

	public ContaminationTree(WContainerWidget parent) {
		super(parent);

		WVBoxLayout layout = new WVBoxLayout(this);
		WBoxLayout treeSettings = new WBoxLayout(Direction.LeftToRight);
		layout.addLayout(treeSettings, 0, AlignmentFlag.AlignLeft);

		WLabel treeLayoutLabel = new WLabel(WString.tr("form.viralIsolate.similarity.tree.layout"));
		treeSettings.addWidget(treeLayoutLabel);

		treeLayoutBox = new WComboBox();
		for (TreeLayout tl : TreeLayout.values()) {
			treeLayoutBox.addItem(tl.toString());
		}
		treeLayoutLabel.setBuddy(treeLayoutBox);
		treeLayoutBox.changed().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				treeWidget.getTreePane().setTreeLayout(TreeLayout.values()[treeLayoutBox.getCurrentIndex()].layout);
			}
		});
		treeSettings.addWidget(treeLayoutBox);

		showBootstrapValues = new WCheckBox(WString.tr("form.viralIsolate.similarity.tree.show.bootstrap.values"));
		showBootstrapValues.setChecked(false);
		showBootstrapValues.changed().addListener(this, new Signal.Listener() {
			@Override
			public void trigger() {
				nodePainter.setVisible(showBootstrapValues.isChecked());
			}
		});
		treeSettings.addWidget(showBootstrapValues);

		layout.addWidget(treeWidget = new TreeWidget(), 2);

		treeWidget.setStyleClass("phylotree");

	}

	public void calculateTree(Map<String, Map<String, String>> annotatedSequences, NtSequence ntSequence, String organism) {
		this.sequenceAttributes = annotatedSequences;
		this.sequenceLabel = ntSequence.getLabel();
		this.sampleId = ntSequence.getViralIsolate().getSampleId();
		this.patientId = new Patient(ntSequence.getViralIsolate().getPatient(), Privileges.READONLY.getValue()).getPatientId();

		try {
			NucleotideAlignment nucAligner = new NucleotideAlignment(toFasta(annotatedSequences), organism, 0.5); // TODO config?
			nucAligner.launch();
			String aligned = nucAligner.getAlignedSequences();
			TreeBuilder tb = new TreeBuilder(aligned);
			tb.launch();
			newickTree = tb.getNewickTree();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	private String toFasta(Map<String, Map<String, String>> sequenceAttributes) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Map<String, String>> e : sequenceAttributes.entrySet()) {
			sb.append(">");
			sb.append(e.getKey());
			sb.append("\n");
			sb.append(e.getValue().get("nucleotides"));
			sb.append("\n");
		}
		return sb.toString();
	}

	public void showTree() {
		treeWidget.setMinimumSize(new WLength(10), new WLength(sequenceAttributes.size() * 20));
		try {
			StringReader reader = new StringReader(newickTree);
			List<Tree> trees = new ArrayList<Tree>();
			NewickImporter importer = new NewickImporter(reader, true);
			while (importer.hasTree()) {
				Tree tree = importer.importNextTree();
				trees.add(tree);
			}
			RootedTree treeToLoad = (RootedTree) trees.get(0);

			Font defaultFont = new Font("sans-serif", Font.PLAIN, 10);
			// show bootstrap values
			treeWidget.getTreePane().setNodeLabelPainter(nodePainter = new SimpleLabelPainter(SimpleLabelPainter.PainterIntent.NODE));
			nodePainter.setupAttributes(trees);
			nodePainter.setDisplayAttribute("label");
			nodePainter.setTextDecorator(new AttributableDecorator());
			nodePainter.setFont(defaultFont);
			nodePainter.setVisible(false);

			// set sample ids on tips - coloring indicates patient
			for (Node n : treeToLoad.getExternalNodes()) {
				Taxon taxon = treeToLoad.getTaxon(n);
				n.setAttribute("patient", sequenceAttributes.get(taxon.getName()).get("patient").equals(patientId));
				n.setAttribute("label", sequenceAttributes.get(taxon.getName()).get("sample"));
			}

			treeWidget.getTreePane().setTipLabelPainter(tipPainter = new SimpleLabelPainter(SimpleLabelPainter.PainterIntent.TIP));
			tipPainter.setupAttributes(trees);
			tipPainter.setFont(defaultFont);
			tipPainter.setDisplayAttribute("label");
			tipPainter.setTextDecorator(new DiscreteColorDecorator("patient", treeToLoad.getExternalNodes(), new Color[] { Color.BLACK, Color.RED }, false));

			// set tree layout
			treeWidget.getTreePane().setTreeLayout(TreeLayout.RADIAL.layout);
			// load tree
			treeWidget.getTreePane().setTree((RootedTree) trees.get(0));
			show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
