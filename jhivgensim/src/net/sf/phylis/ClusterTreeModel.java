package net.sf.phylis;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * A tree model for displaying clusters & taxa
 */
public class ClusterTreeModel implements TreeModel {
	class ClusterTreeModelRoot {
		public String toString() {
			return "Clusters";
		}
	}

	private List clusters;
	private List taxa;	
	private List listeners;
	private ClusterTreeModelRoot root;

	public ClusterTreeModel(List clusters, List taxa) {
		this.clusters = clusters;
		this.taxa = taxa;
		this.listeners = new ArrayList();
		this.root = new ClusterTreeModelRoot();
	}

	public Object getRoot() {
		return root;
	}

	public Object getChild(Object parent, int index) {
		if (parent instanceof ClusterTreeModelRoot) {
			
			return clusters.get(index);
		} else if (parent instanceof PhylisConfig.Cluster) {
			PhylisConfig.Cluster c = (PhylisConfig.Cluster) parent;
			
			return taxa.get(c.getTaxaIndexes()[index]);
		} else if (parent instanceof String) {
			throw new RuntimeException("Taxa have no children !?");
		} else
			throw new RuntimeException("Weird parent type !?");
	}

	public int getChildCount(Object parent) {
		if (parent instanceof ClusterTreeModelRoot) {
			
			return clusters.size();
		} else if (parent instanceof PhylisConfig.Cluster) {
			PhylisConfig.Cluster c = (PhylisConfig.Cluster) parent;
			
			return c.getTaxaIndexes().length;
		} else if (parent instanceof String) {
			return 0;
		} else
			throw new RuntimeException("Weird parent type !?");
	}

	public boolean isLeaf(Object arg0) {
		return (arg0 instanceof String);
	}

	public void valueForPathChanged(TreePath arg0, Object arg1) {
		System.err.println("valueForPathChanged called ? huh ?");
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent == null || child == null)
			return -1;

		if (parent instanceof ClusterTreeModelRoot) {
			PhylisConfig.Cluster c = (PhylisConfig.Cluster) child;

			return clusters.indexOf(child);
		} else if (parent instanceof PhylisConfig.Cluster) {
			PhylisConfig.Cluster c = (PhylisConfig.Cluster) parent;
			String taxon = (String) child;
			/*
			 * Find taxon in cluster
			 */
			for (int i = 0; i < c.getTaxaIndexes().length; ++i) {
				if (taxa.get(c.getTaxaIndexes()[i]).equals(taxon)) {
					return i;
				}
			}
			throw new RuntimeException("Taxon not found in cluster ?!");
		} else
			throw new RuntimeException("Weird parent type !?");
	}

	public void addTreeModelListener(TreeModelListener listener) {
		listeners.add(listener);
	}

	public void removeTreeModelListener(TreeModelListener listener) {
		listeners.remove(listener);
	}

	protected void remove(TreePath[] selection) {
		for (int i = 0; i < selection.length; ++i) {
			TreePath selected = selection[i];

			/*
			 * Ignore removing root node
			 */
			if (selected.getLastPathComponent() == root) {
				continue;
			}

			/*
			 * Removing a cluster
			 */
			if (selected.getPath().length == 2) {
				PhylisConfig.Cluster c = (PhylisConfig.Cluster) selected.getLastPathComponent();
				TreePath parentPath = selected.getParentPath();
				int clusterIndex = getIndexOfChild(parentPath.getLastPathComponent(), c);
				clusters.remove(c);
				
				fireNodeRemoved(parentPath, clusterIndex, c);
			}
			
			/*
			 * Removing a taxon
			 */
			if (selected.getPath().length == 3) {
				String taxon = (String) selected.getLastPathComponent();
				TreePath parentPath = selected.getParentPath();
				PhylisConfig.Cluster c = (PhylisConfig.Cluster) parentPath.getLastPathComponent();
				
				/*
				 * Check that cluster has not been removed in the mean while !
				 */
				if (!clusters.contains(c)) {
					continue;
				}
				
				int taxonIndex = getIndexOfChild(c, taxon);
				int taxonIndexes[] = c.getTaxaIndexes();
				int newIndexes[] = new int[taxonIndexes.length - 1];
				for (int j = 0; j < newIndexes.length; ++j) {
					newIndexes[j] = taxonIndexes[(j >= taxonIndex) ? j + 1 : j];
				}
				c.setTaxaIndexes(newIndexes);
				
				fireNodeRemoved(parentPath, taxonIndex, taxon);
			}
		}
	}

	private void fireNodeRemoved(TreePath parentPath, int nodeIndex, Object node) {
		int len = listeners.size();
		TreeModelEvent e = new TreeModelEvent(this, parentPath.getPath());
		for (int i = 0; i < len; i++) {
			((TreeModelListener)listeners.get(i)).treeStructureChanged(e);
		}
	}

	protected boolean addCluster(String clusterName) {
		/*
		 * Check that cluster with given name does not yet exist.
		 */
		for (int i = 0; i < clusters.size(); ++i) {
			if (clusterName.equals(clusters.get(i).toString()))
				return false;
		}
		
		clusters.add(new PhylisConfig.Cluster(clusterName, new int[0]));
		fireNodeAdded(new Object[] { root });

		return true;
	}

	private void fireNodeAdded(Object[] path) {
		int len = listeners.size();
		TreeModelEvent e = new TreeModelEvent(this, path);
		for (int i = 0; i < len; i++) {
			((TreeModelListener)listeners.get(i)).treeStructureChanged(e);
		}		
	}

	protected void addTaxa(TreePath selectedClusterPath, int[] indices) {
		PhylisConfig.Cluster c = (PhylisConfig.Cluster) selectedClusterPath.getLastPathComponent();
		int newindices[] = c.getTaxaIndexes();

		for (int i = 0; i < indices.length; ++i) {
			if (Arrays.binarySearch(newindices, indices[i]) < 0) {
				/*
				 * copy & sort again
				 */
				int oldindices[] = newindices;
				newindices = new int[oldindices.length + 1];

				for (int j = 0; j < oldindices.length; ++j) {
					newindices[j] = oldindices[j];
				}
				newindices[oldindices.length] = indices[i];
				
				Arrays.sort(newindices);
			}
		}
		
		c.setTaxaIndexes(newindices);
		
		fireNodeAdded(selectedClusterPath.getPath());
	}

	protected boolean renameCluster(TreePath selectedClusterPath, PhylisConfig.Cluster c, String newName) {
		if (c.getName().equals(newName))
			return false;

		/*
		 * Check that cluster with new name does not yet exist.
		 */
		for (int i = 0; i < clusters.size(); ++i) {
			if (newName.equals(clusters.get(i).toString()))
				return false;
		}

		c.setName(newName);
		
		fireNodeChanged(selectedClusterPath);
		
		return true;
	}

	private void fireNodeChanged(TreePath path) {
		int len = listeners.size();
		TreeModelEvent e = new TreeModelEvent(this, path.getParentPath(),
											  new int[] { getIndexOfChild(root, path.getLastPathComponent()) },
											  new Object[] { path.getLastPathComponent() });
		for (int i = 0; i < len; i++) {
			((TreeModelListener)listeners.get(i)).treeNodesChanged(e);
		}		
	}
}
