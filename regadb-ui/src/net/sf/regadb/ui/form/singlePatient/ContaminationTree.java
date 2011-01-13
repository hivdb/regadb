package net.sf.regadb.ui.form.singlePatient;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import jebl.evolution.io.NewickImporter;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.Tree;
import net.sf.regadb.service.wts.NucleotideAlignment;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.service.wts.TreeBuilder;
import net.sf.regadb.ui.framework.widgets.warning.WarningMessage;
import net.sf.regadb.ui.framework.widgets.warning.WarningMessage.MessageType;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WTimer;
import eu.webtoolkit.jwt.WVBoxLayout;
import figtree.webui.TreeWidget;

public class ContaminationTree extends WContainerWidget {

	private TreeWidget treeWidget;

	private WarningMessage warningMessage = new WarningMessage(new WImage("pics/formWarning.gif"), tr("form.viralIsolate.similarity.tree.building"), MessageType.INFO);
	private WTimer treeBuildingTimer = new WTimer(warningMessage);

	private String organism;
	private boolean dirty = true;
	private List<String> sequences = new ArrayList<String>();
	private String newickTree = "";

	public ContaminationTree(WContainerWidget parent, String organism) {
		super(parent);
		this.organism = organism;
	}

	public void show() {
		super.show();
		if (dirty) {
			addWidget(warningMessage);
			treeBuildingTimer.setInterval(2000);
			treeBuildingTimer.timeout().addListener(this, new Signal.Listener() {
				public void trigger() {
					checkTreeBuilding();
				}
			});
			treeBuildingTimer.start();
			calculateTree();
		}
	}

	private void calculateTree() {
		StringBuilder sb = new StringBuilder();
		for (String seq : sequences) {
			sb.append(seq);
		}
		try {
			NucleotideAlignment nucAligner = new NucleotideAlignment(sb.toString(), organism, "0.5"); // TODO config?
			nucAligner.launch();
			String aligned = nucAligner.getAlignedSequences();
			TreeBuilder tb = new TreeBuilder(aligned);
			tb.launch();
			newickTree = tb.getNewickTree();
			dirty = false;
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void addSequence(String patientId, String sampleId, String sequenceLabel, String nucleotides) {
		dirty = true;
		sequences.add(">" + patientId + "_" + sampleId + "_" + sequenceLabel + "\n" + nucleotides + "\n");
	}

	public void clear() {
		dirty = true;
		sequences.clear();
	}

	private void checkTreeBuilding() {
		if (!dirty) {
			treeBuildingTimer.stop();
			warningMessage.hide();
			WVBoxLayout layout = new WVBoxLayout(this);
			layout.addWidget(treeWidget = new TreeWidget(), 1);
			treeWidget.setMinimumSize(new WLength(200), new WLength(200));
			treeWidget.setStyleClass("phylotree");
			loadTree(newickTree);
		}
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
			treeWidget.getTreePane().setTree((RootedTree) trees.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
