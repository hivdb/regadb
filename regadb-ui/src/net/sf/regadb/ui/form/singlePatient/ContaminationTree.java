package net.sf.regadb.ui.form.singlePatient;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import jebl.evolution.io.NewickImporter;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.Tree;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WVBoxLayout;
import figtree.panel.SimpleLabelPainter;
import figtree.webui.TreeWidget;

public class ContaminationTree extends WContainerWidget {

	private TreeWidget treeWidget;
	private SimpleLabelPainter tipPainter;
	private SimpleLabelPainter nodePainter;
	private SimpleLabelPainter branchPainter;

	public ContaminationTree(WContainerWidget parent) {
		super(parent);

		WVBoxLayout layout = new WVBoxLayout(this);
		layout.addWidget(treeWidget = new TreeWidget(), 1);
		treeWidget.setMinimumSize(new WLength(200), new WLength(200));
		treeWidget.setStyleClass("phylotree");
		loadTree("((B:0.2,(C:0.3,D:0.4)E:0.5)F:0.1)A;");

		treeWidget.getTreePane().setTipLabelPainter(tipPainter = new SimpleLabelPainter(SimpleLabelPainter.PainterIntent.TIP));
		treeWidget.getTreePane().setNodeLabelPainter(nodePainter = new SimpleLabelPainter(SimpleLabelPainter.PainterIntent.NODE));
		treeWidget.getTreePane().setBranchLabelPainter(branchPainter = new SimpleLabelPainter(SimpleLabelPainter.PainterIntent.BRANCH));
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
			System.err.println(trees.size());
			treeWidget.getTreePane().setTree((RootedTree) trees.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addSequence(String patientId, String sampleId, String sequenceLabel, String nucleotides) {
		// TODO
	}

	public void clear() {
		// TODO
	}

}
