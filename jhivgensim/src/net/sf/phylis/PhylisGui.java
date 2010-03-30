package net.sf.phylis;
/*
 * Created on May 5, 2003
 */
import jargs.gnu.CmdLineParser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class PhylisGui extends JPanel implements PhylisUi {
	private static final String ABOUT_MESSAGE = "SlidingBayes 0.95 (c) 2004 K.U.Leuven\n"
											  + "\n"
											  + "Authors:\n"
											  + "    Koen Deforche\n"
											  + "    Dimitrios Paraskevis\n"
											  + "\n"
											  + "Contact:\n"
											  + "    annemie.vandamme@uz.kuleuven.ac.be";
	
	private static File MrBayesExecutable;
	
	private JFileChooser fc;
	private JTextField inputFileField;
	private JTextField outputDirField;
	private JFormattedTextField windowSizeField;
	private JFormattedTextField stepSizeField;
	private JRadioButton aaButton, dnaButton;
	private JButton runButton;
	private JButton cancelButton;
	private JTextArea mrBayesArea;
	private JProgressBar progress;
	private JTextField mrBayesDirField;

	private File inputFile;
	private File outputDir;
	private Thread runThread;
	private List taxa;
	private List clusters;
	private JFrame frame;
    
	private PhylisConfig plotConfig;
	private File mrBayesDir;
	
	private TreePath selectedClusterPath;
	
	private DefaultListModel taxaListModel;
	private JList taxaList;

	private PlotWidget plotWidget;

	PhylisGui(JFrame frame) {
		this.frame = frame;
		this.taxa = new ArrayList();
		this.clusters = new ArrayList();
		outputDir = null;
		inputFile = null;
		createGUI(frame);
	}

	private void createGUI(final JFrame frame) {
		fc = new JFileChooser();
        
        /*
         * Main Widget: a tabbed pane.
         */
        final JTabbedPane tabs = new JTabbedPane();
        
        Component configPane = createConfigPane();
        tabs.addTab("Run", null, configPane, "Configuring and running SlidingBayes");
    
		Component analysisPane = createAnalysisPane();
		tabs.addTab("Analysis", null, analysisPane, "Analysing results of SlidingBayes run");
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(tabs);

		/*
		 * Menu's : configMenuBar & analysisMenuBar
		 */
		final JMenuBar configMenuBar = new JMenuBar();
		frame.setJMenuBar(configMenuBar);

		JMenu menu = new JMenu("File");
		configMenuBar.add(menu);

		JMenuItem menuItem = new JMenuItem("Open alignment...",
										   createImageIcon("images/Open16.gif"));
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = fc.showOpenDialog(PhylisGui.this);
		
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					inputFile = fc.getSelectedFile();
					try {
						taxa = PhylisAlgo.retrieveTaxa(inputFile);
						inputFileField.setText(inputFile.getName());
					} catch (ApplicationException e1) {
						JOptionPane.showMessageDialog(PhylisGui.this, "Error: " + e1.getMessage(),
													  "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
				}
			}
		});

		menuItem = new JMenuItem("Set output directory...");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);        
				int returnVal = fc.showDialog(PhylisGui.this, "Set output directory");

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					outputDir = fc.getSelectedFile();
					outputDirField.setText(outputDir.getName());         
				} else {
				}
			}
		});
		
		menu.addSeparator();

		menuItem = new JMenuItem("Open configuration (.xml)...",
								 createImageIcon("images/Open16.gif"));
		menu.add(menuItem);
		
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);        
				int returnVal = fc.showOpenDialog(PhylisGui.this);
		
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						setCurrentConfig(new PhylisConfig(file));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
				}
			}
		});

		menuItem = new JMenuItem("Save configuration (.xml)...",
								 createImageIcon("images/Save16.gif"));
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = fc.showSaveDialog(PhylisGui.this);
		
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						getCurrentConfig().save(file);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ApplicationException e) {
						JOptionPane.showMessageDialog(PhylisGui.this, "Error: " + e.getMessage(),
													  "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
				}
			}
		});
		
		menu.addSeparator();
		
		menuItem = new JMenuItem("Quit");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		});
		
		menu = new JMenu("Configure");
		configMenuBar.add(menu);
		
		menuItem = new JMenuItem("Define clusters...");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configureClusters(clusters, taxa);
			}
		});
		
		menu = new JMenu("Settings");
		configMenuBar.add(menu);
		
		menuItem = new JMenuItem("MrBayes executable location ...");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setSelectedFile(MrBayesExecutable);
				int returnVal = fc.showOpenDialog(PhylisGui.this);
		
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					MrBayesExecutable = fc.getSelectedFile();
					PhylisAlgo.mrBayesCommand = MrBayesExecutable.getAbsolutePath();
				}
			}
		});

		configMenuBar.add(Box.createHorizontalGlue());
		
		menu = new JMenu("Help");
		configMenuBar.add(menu);
		
		menuItem = new JMenuItem("About SlidingBayes");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(PhylisGui.this,
											  ABOUT_MESSAGE,
											  "About", JOptionPane.PLAIN_MESSAGE);
			}

		});

		final JMenuBar analysisMenuBar = new JMenuBar();
		
		menu = new JMenu("File");
		analysisMenuBar.add(menu);
		
		JMenuItem openSlidingBayesRunItem = new JMenuItem("Open SlidingBayes run...",
														  createImageIcon("images/Open16.gif"));
		menu.add(openSlidingBayesRunItem);

		menu.addSeparator();
		
		menuItem = new JMenuItem("Quit");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		});

		final JMenu analysisMenu = new JMenu("Analysis");
		analysisMenuBar.add(analysisMenu);
		analysisMenu.setEnabled(false);
		
		JMenuItem analysisDefineClustersMenuItem = new JMenuItem("Define clusters...");
		analysisMenu.add(analysisDefineClustersMenuItem);
		
		analysisMenu.addSeparator();
		
		JMenuItem plotQueryMenuItem = new JMenuItem("Plot support for query");
		analysisMenu.add(plotQueryMenuItem);
		
		JMenuItem plotClustersMenuItem = new JMenuItem("Plot support for query against clusters");
		analysisMenu.add(plotClustersMenuItem);
		
		JMenuItem savePlotMenuItem = new JMenuItem("Save plot...");
		analysisMenu.add(savePlotMenuItem);

		analysisMenuBar.add(Box.createHorizontalGlue());
		
		menu = new JMenu("Help");
		analysisMenuBar.add(menu);
		
		menuItem = new JMenuItem("About SlidingBayes");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(PhylisGui.this,
											  ABOUT_MESSAGE,
											  "About", JOptionPane.PLAIN_MESSAGE);
			}
		});


		openSlidingBayesRunItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);        
				int returnVal = fc.showOpenDialog(PhylisGui.this);
		
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					mrBayesDir = fc.getSelectedFile();
					File file = new File(mrBayesDir.getAbsolutePath() + File.separator + "config.xml");
		
					try {
						plotConfig = new PhylisConfig(file);
		                
						taxaListModel.clear();
						for (Iterator i = plotConfig.getTaxa().iterator(); i.hasNext();) {
							String taxus = (String) i.next();
		                    
							taxaListModel.addElement(taxus);
						}
		                
		                analysisMenu.setEnabled(true);
						mrBayesDirField.setText(mrBayesDir.getName());
					} catch (IOException e) {
						JOptionPane.showMessageDialog(PhylisGui.this, "Error: " + e.getMessage(),
													  "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
				}
		        
			}
		});

		plotQueryMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File outputFile = fc.getSelectedFile();
		
				try {
					List plotData = PhylisAlgo.plotSelected(taxaList.getSelectedIndices(), plotConfig.getTaxa().size(),
													    mrBayesDir, PhylisAlgo.MRBAYES_ANALYSIS);
					List plotLabels = new ArrayList();
					plotLabels.add("Query");

					plotWidget.setData(plotData, plotLabels,
									   createPlotXLabels(plotConfig, (List) plotData.get(0)));
				} catch (ApplicationException e1) {
					JOptionPane.showMessageDialog(PhylisGui.this, "Error: " + e1.getMessage(),
												  "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		plotClustersMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (plotConfig.getClusters().isEmpty()) {
					JOptionPane.showMessageDialog(PhylisGui.this, "Error: no clusters defined !",
												  "Error", JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				try {
					List plotData = PhylisAlgo.retrieveClustersValues(taxaList.getSelectedIndices(), plotConfig.getTaxa().size(),
												   				  plotConfig.getClusters(), mrBayesDir,
												   				  PhylisAlgo.MRBAYES_ANALYSIS);
					List plotLabels = new ArrayList();
					List clusters = plotConfig.getClusters();
					for (int i = 0; i < clusters.size(); ++i) {
						PhylisConfig.Cluster c = (PhylisConfig.Cluster) clusters.get(i);
						plotLabels.add(c.getName());
					}

					plotWidget.setData(plotData, plotLabels, createPlotXLabels(plotConfig,
									   (List) plotData.get(0)));
				} catch (ApplicationException e1) {
					JOptionPane.showMessageDialog(PhylisGui.this, "Error: " + e1.getMessage(),
												  "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		savePlotMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (plotWidget.hasPlotData()) {
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int returnVal = fc.showSaveDialog(PhylisGui.this);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File outputFile = fc.getSelectedFile();
						
						try {
							PhylisAlgo.plotResults(plotWidget.getPlotData(),
											   plotWidget.getPlotLabels(),
											   plotWidget.getPlotXValues(),
											   outputFile);
						} catch (ApplicationException e1) {
							JOptionPane.showMessageDialog(PhylisGui.this, "Error: " + e1.getMessage(),
														  "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					
				} else {
					JOptionPane.showMessageDialog(PhylisGui.this, "Error: no plot data to save!",
												  "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			
		});

		analysisDefineClustersMenuItem.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 configureClusters(plotConfig.getClusters(), plotConfig.getTaxa());
			 }
		});

		tabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (tabs.getSelectedIndex() == 0) {
					frame.setJMenuBar(configMenuBar);
				} else {
					frame.setJMenuBar(analysisMenuBar);
				}
			}
		});
	}

	private List createPlotXLabels(PhylisConfig plotConfig, List YValues) {
		List result = new ArrayList(YValues.size());
		
		for (int i = 0; i < YValues.size(); ++i) {
			result.add(new Integer(i * plotConfig.getStepSize() + plotConfig.getWindowSize() / 2));
		}
		
		return result;
	}

	private JPanel createAnalysisPane() {
		JPanel plotPane = new JPanel();
		plotPane.setLayout(new BorderLayout());
		plotPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel mrBayesDirFieldLabel = new JLabel("MrBayes run: ");

		mrBayesDirField = new JTextField("", 30);
		mrBayesDirField.setEditable(false);
		mrBayesDirFieldLabel.setLabelFor(mrBayesDirField);

		JPanel openMrBayesPane = new JPanel();
		openMrBayesPane.add(mrBayesDirFieldLabel);
		openMrBayesPane.add(mrBayesDirField);

		plotPane.add(openMrBayesPane, BorderLayout.NORTH);

		taxaListModel = new DefaultListModel();
		taxaList = new JList(taxaListModel);
		taxaList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		JScrollPane taxaScrollPane = new JScrollPane(taxaList);
		taxaScrollPane.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder("Query"),
								BorderFactory.createEmptyBorder(5,5,5,5)),
				taxaScrollPane.getBorder()));

		plotWidget = new PlotWidget();

		JSplitPane scrollPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
											   taxaScrollPane, plotWidget);
		scrollPane.setDividerLocation(0.5);
		JPanel midPane = new JPanel();
		midPane.setLayout(new BoxLayout(midPane, BoxLayout.Y_AXIS));
		midPane.add(scrollPane);

		plotPane.add(midPane, BorderLayout.CENTER);

		return plotPane;		
	}

	private JPanel createConfigPane() {
		JPanel configPane = new JPanel();
		configPane.setLayout(new BorderLayout());

		JPanel generalSet = new JPanel();
		generalSet.setLayout(new BoxLayout(generalSet, BoxLayout.PAGE_AXIS));
		generalSet.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

		JPanel rowPane = new JPanel();

		inputFileField = new JTextField("", 20);
		inputFileField.setEditable(false);
		JLabel inputFileFieldLabel = new JLabel("Input sequences: ");
		inputFileFieldLabel.setLabelFor(inputFileField);
		rowPane.add(inputFileFieldLabel);
		rowPane.add(inputFileField);

		outputDirField = new JTextField("", 20);
		outputDirField.setEditable(false);
		JLabel outputDirFieldLabel = new JLabel("Output directory: ");
		outputDirFieldLabel.setLabelFor(outputDirField);
		rowPane.add(outputDirFieldLabel);
		rowPane.add(outputDirField);
		rowPane.add(Box.createHorizontalGlue());
		
		generalSet.add(rowPane);
		

		configPane.add(generalSet, BorderLayout.NORTH);
		
		JLabel windowLabel = new JLabel("Window size: ");
		windowSizeField = new JFormattedTextField();
		windowSizeField.setValue(new Integer(1000));
		windowSizeField.setColumns(5);
		windowLabel.setLabelFor(windowSizeField);
		
		JLabel stepLabel = new JLabel("Step size: ");        
		stepSizeField = new JFormattedTextField();
		stepSizeField.setValue(new Integer(200));
		stepSizeField.setColumns(5);
		stepLabel.setLabelFor(stepSizeField);
		
		JPanel labelPane = new JPanel();
		labelPane.setLayout(new GridLayout(0, 1));
		labelPane.add(windowLabel);
		labelPane.add(stepLabel);
		
		JPanel fieldPane = new JPanel();
		fieldPane.setLayout(new GridLayout(0, 1));
		fieldPane.add(windowSizeField);
		fieldPane.add(stepSizeField);
		
		JPanel settingsPane = new JPanel();
		settingsPane.setBorder(BorderFactory.createEmptyBorder(5, 10,
															   5, 10));
		settingsPane.setLayout(new BorderLayout());
		settingsPane.add(labelPane, BorderLayout.CENTER);
		settingsPane.add(fieldPane, BorderLayout.EAST);

		JPanel radioPanel = new JPanel();
		radioPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.PAGE_AXIS));
		dnaButton = new JRadioButton("Nucleotide");
		aaButton = new JRadioButton("Amino acid");
		dnaButton.setSelected(true);
		ButtonGroup seqTypeGroup = new ButtonGroup();
		seqTypeGroup.add(dnaButton);
		seqTypeGroup.add(aaButton);
		radioPanel.add(dnaButton);
		radioPanel.add(aaButton);

		JPanel settingsSpacerPane = new JPanel(new BorderLayout());
		settingsSpacerPane.add(settingsPane, BorderLayout.NORTH);
		settingsSpacerPane.add(radioPanel, BorderLayout.CENTER);

		//Create a text area.
		mrBayesArea = new JTextArea(
				"begin mrbayes;\n"
					+ "\tset autoclose = yes;\n"
					+ "\tset nowarnings = yes;\n"
					+ "\tlset nst=6 rates=gamma Ngammacat=8;\n"
					+ "\tmcmcp ngen=120000 printfreq=50 samplefreq=50\n"
					+ "\t\tnchains=4 savebrlens=yes filename=${FILE};\n"
					+ "\tmcmc;\n"
					+ "\tsumt filename=${FILE}.t burnin=5;\n"
					+ "\tquit;"
		);

		mrBayesArea.setFont(new Font("Courier", Font.PLAIN, 12));
		mrBayesArea.setLineWrap(false);
		JScrollPane areaScrollPane = new JScrollPane(mrBayesArea);
		areaScrollPane.setVerticalScrollBarPolicy(
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		areaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		areaScrollPane.setPreferredSize(new Dimension(150, 150));
		areaScrollPane.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder("MrBayes command block"),
								BorderFactory.createEmptyBorder(5,5,5,5)),
				areaScrollPane.getBorder()));
		
		
		JPanel midPane = new JPanel();
		midPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		midPane.setLayout(new BorderLayout());
		midPane.add(settingsSpacerPane, BorderLayout.WEST);
		midPane.add(areaScrollPane, BorderLayout.CENTER);        
		
		configPane.add(midPane, BorderLayout.CENTER);
		
		runButton = new JButton("Run");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runButton.setEnabled(false);
				cancelButton.setEnabled(true);
		
				Runnable doRun;
				try {
					/*
					 * Collect problem data
					 */
					final PhylisConfig config = getCurrentConfig();

					/*
					 * Spawn thread that will do the actual run
					 */
					doRun = new Runnable() {
						public void run() {
							try {
								PhylisAlgo.runScanAnalysis(
									config,
									outputDir,
									PhylisGui.this,
									PhylisAlgo.MRBAYES_ANALYSIS);
							} catch (ApplicationException e) {
								try {
									showMessage(e.getMessage());
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								} catch (InvocationTargetException e1) {
									e1.printStackTrace();
								}
							}
							runDone();
						}
					};
		
					runThread = new Thread(doRun);
					runThread.start();
				} catch (ApplicationException e1) {
					JOptionPane.showMessageDialog(PhylisGui.this, "Error: " + e1.getMessage(),
												  "Error", JOptionPane.ERROR_MESSAGE);
					runDone();
				}
			}
		});
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runThread.interrupt();
			}
		});
		cancelButton.setEnabled(false);
		
		JButton makeJobButton = new JButton("Make job");
		makeJobButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					PhylisAlgo.makeSlidingBayesJob(getCurrentConfig(), outputDir);
				} catch (ApplicationException e1) {
					JOptionPane.showMessageDialog(PhylisGui.this, "Error: " + e1.getMessage(),
												  "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JPanel buttonPane = new JPanel();
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		
		buttonPane.add(runButton);
		buttonPane.add(cancelButton);
		buttonPane.add(makeJobButton);
		
		progress = new JProgressBar();
		progress.setStringPainted(true);
		progress.setString("Ready");
		
		JPanel lowPane = new JPanel();
		lowPane.add(buttonPane);
		lowPane.setLayout(new BoxLayout(lowPane, BoxLayout.Y_AXIS));
		lowPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JPanel progressPane = new JPanel();
		progressPane.setLayout(new BoxLayout(progressPane, BoxLayout.X_AXIS));
		progressPane.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 30));
		progressPane.add(new JLabel("MrBayes progress: "));
		progressPane.add(progress);
		lowPane.add(progressPane);

		configPane.add(lowPane, BorderLayout.SOUTH);
		
		return configPane;
	}

	protected void configureClusters(final List clusters, final List taxa) {
		/*
		 * Show a dialog box with on the left a tree view of clusters.
		 * 
		 * Taxa may be added by selecting taxa, selecting a cluster,
		 * and hitting 'add taxa'.
		 * 
		 * Taxa may be removed by selecting taxa and hitting 'remove taxon'
		 * 
		 * Clusters may be added by 'new cluster'
		 * Clusters may be removed by hitting 'remove cluster'
		 */
		
		/*
		 * Show a dialog box that allows to edit the current
		 * cluster definitions.
		 */
		final JDialog dialog = new JDialog(frame, "Clusters", true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		/*
		 * First, the tree.
		 */
		final ClusterTreeModel clusterTreeModel = new ClusterTreeModel(clusters, taxa);
		final JTree clusterTree = new JTree(clusterTreeModel);

		/*
		 * Next to it, the list of all taxa.
		 */        
		final JList taxaList = new JList(taxa.toArray());
		taxaList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		JScrollPane clusterScrollPane = new JScrollPane(clusterTree);
		clusterScrollPane.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder("Clusters"),
								BorderFactory.createEmptyBorder(5,5,5,5)),
				clusterScrollPane.getBorder()));

		JScrollPane taxaScrollPane = new JScrollPane(taxaList);
		taxaScrollPane.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder("Taxa"),
								BorderFactory.createEmptyBorder(5,5,5,5)),
				taxaScrollPane.getBorder()));

		JSplitPane scrollPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
										   	   clusterScrollPane, taxaScrollPane);

		final JButton renameClusterButton = new JButton("Rename");
		renameClusterButton.setEnabled(false);

		renameClusterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PhylisConfig.Cluster c = (PhylisConfig.Cluster) selectedClusterPath.getLastPathComponent();
				String newName = JOptionPane.showInputDialog("New cluster name", c.getName());
				
				clusterTreeModel.renameCluster(selectedClusterPath, c, newName);
			}
		});
		
		final JButton newClusterButton = new JButton("New cluster");
		
		newClusterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newName = JOptionPane.showInputDialog("New cluster name", "NewCluster");
				
				clusterTreeModel.addCluster(newName);
			}
		});
		
		final JButton removeButton = new JButton("Remove");
		removeButton.setEnabled(false);
		
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TreePath[] selection = clusterTree.getSelectionPaths();

				clusterTreeModel.remove(selection);
			}
		});
		
		final JButton addTaxaButton = new JButton("Add taxa");
		
		addTaxaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clusterTreeModel.addTaxa(selectedClusterPath, taxaList.getSelectedIndices());
			}
		});
		
		clusterTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath[] selection = clusterTree.getSelectionPaths();
				
				/*
				 * if a single cluster is selected: can add taxa, or rename cluster
				 */
				if (selection != null && selection.length == 1
					&& selection[0].getPath().length == 2) {
					renameClusterButton.setEnabled(true);
					addTaxaButton.setEnabled(true);
					selectedClusterPath = selection[0];
				} else {
					renameClusterButton.setEnabled(false);
					addTaxaButton.setEnabled(false);
					selectedClusterPath = null;
				}

				/*
				 * if something is selected, can remove
				 */
				if (selection != null && selection.length > 0) {
					removeButton.setEnabled(true);
				} else {
					removeButton.setEnabled(false);
				}
			}
		});

		final JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});

		JPanel buttonPane = new JPanel();
		buttonPane.add(renameClusterButton);
		buttonPane.add(newClusterButton);
		buttonPane.add(removeButton);
		buttonPane.add(addTaxaButton);
		buttonPane.add(closeButton);

		Container contentPane = dialog.getContentPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.SOUTH);

		dialog.pack();
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}

	private String getClusterNameFromDialog() {
		return JOptionPane.showInputDialog("New cluster name", "NewCluster");
	}

	private int interpretIntValue(Object object) {
		if (object instanceof Long)
			return ((Long) object).intValue();
		else
			return ((Integer) object).intValue();
	}

	protected void setCurrentConfig(PhylisConfig config) {
		windowSizeField.setValue(new Integer(config.getWindowSize()));
		stepSizeField.setValue(new Integer(config.getStepSize()));
		mrBayesArea.setText(config.getMrBayesBlock());
		inputFile = new File(config.getInputFileName());
		inputFileField.setText(inputFile.getName());
		taxa = config.getTaxa();
		clusters = config.getClusters();
	}

	private PhylisConfig getCurrentConfig() throws ApplicationException {
		int windowSize = interpretIntValue(windowSizeField.getValue());
		int stepSize = interpretIntValue(stepSizeField.getValue());
		boolean aminoAcid = aaButton.isSelected();
		String mrBayesBlock = mrBayesArea.getText();
		if (inputFile == null)
			throw new ApplicationException("No input sequences defined");

		String inputFileName = inputFile.getAbsolutePath();

		PhylisConfig result
			= new PhylisConfig(windowSize, stepSize, mrBayesBlock, inputFileName, taxa, clusters,
							   aminoAcid);
		return result;
	}

	public void updateProgress(final int current, final int maximum) {
		Runnable updateAComponent = new Runnable() {
			public void run() {
				progress.setMaximum(maximum);
				progress.setValue(current);
				progress.setString("Running MrBayes: window " + current
				        + " of " + maximum);
				//progress.setString(null);
			}
		};

		SwingUtilities.invokeLater(updateAComponent);
	}

	private void showMessage(final String message)
			throws InterruptedException, InvocationTargetException {
		Runnable updateAComponent = new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(PhylisGui.this, message,
											  "Error", JOptionPane.ERROR_MESSAGE);
			}
		};

		SwingUtilities.invokeAndWait(updateAComponent);
	}

	public void runDone() {
		Runnable updateAComponent = new Runnable() {
			public void run() {
				runButton.setEnabled(true);
				cancelButton.setEnabled(false);
				progress.setValue(0);       
				progress.setString("Ready");
			}
		};

		SwingUtilities.invokeLater(updateAComponent);
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	private static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = PhylisGui.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	private static void printUsage() {
		System.err.println("usage: sb [ {-m,--mrbayespath} path ]");
	}
 
	public static void main(String[] args) {
		try {
			String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) { 
			e.printStackTrace();
		}

		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option mrBayesOption = parser.addStringOption('m', "mrbayespath");

		try {
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			printUsage();
			System.exit(2);
		}

		String mrBayesPathString = (String) parser.getOptionValue(mrBayesOption);
		if (mrBayesPathString != null)
			PhylisAlgo.mrBayesCommand = mrBayesPathString;
		MrBayesExecutable = new File(PhylisAlgo.mrBayesCommand);

		//Create the top-level container and add contents to it.
		JFrame frame = new JFrame("Sliding window MrBayes");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new PhylisGui(frame));

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}
}
