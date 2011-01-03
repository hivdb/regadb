package net.sf.regadb.ui.tree;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.items.query.CustomQueryNavigation;
import net.sf.regadb.ui.tree.items.query.QueryToolNavigation;
import net.sf.regadb.ui.tree.items.query.WivQueryNavigationNode;
import net.sf.regadb.ui.tree.items.singlePatient.PatientTreeNode;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WString;

public class TreeContent
{
	public PatientTreeNode patientTreeNode;
	public AccountNavigationNode accountNode;
    
    
	public TreeMenuNode setContent(RootItem rootItem)
	{
		return createNavigation(rootItem);
	}
    
    public TreeMenuNode createNavigation(RootItem root){
    	
    	patientTreeNode = new PatientTreeNode(root);
    	
    	TreeMenuNode query = new DefaultNavigationNode(WString.tr("menu.query"), root){
    	    @Override
    	    public boolean isDisabled()
    	    {
    	        return RegaDBMain.getApp().getLogin()==null || RegaDBMain.getApp().getRole().isSinglePatientView();
    	    }
    	};
    	
    	new QueryToolNavigation(query);
//    	new QueryDefinitionNavigation(query, new QueryDefinitionRunNavigation(query));
    	new CustomQueryNavigation(query);
    	
    	if(RegaDBSettings.getInstance().getInstituteConfig().getWivConfig() != null)
    		new WivQueryNavigationNode(query);
    	
        accountNode = new AccountNavigationNode(root);
		
		new AdministratorNavigationNode(root);
		
    	root.refresh();
    	
    	if(patientTreeNode.isEnabled())
    		return patientTreeNode;
    	else
    		return accountNode;
    }
}
