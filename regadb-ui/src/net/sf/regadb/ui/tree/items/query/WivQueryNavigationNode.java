package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.ui.form.query.wiv.WivArcCd4Form;
import net.sf.regadb.ui.form.query.wiv.WivArcDeathsForm;
import net.sf.regadb.ui.form.query.wiv.WivArcLastContactForm;
import net.sf.regadb.ui.form.query.wiv.WivArcTherapyAtcForm;
import net.sf.regadb.ui.form.query.wiv.WivArcViralLoadForm;
import net.sf.regadb.ui.form.query.wiv.WivArlCd4Form;
import net.sf.regadb.ui.form.query.wiv.WivArlConfirmedHivForm;
import net.sf.regadb.ui.form.query.wiv.WivArlEpidemiologyForm;
import net.sf.regadb.ui.form.query.wiv.WivArlViralLoadForm;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.tree.DefaultNavigationNode;
import net.sf.regadb.ui.tree.FormNavigationNode;
import eu.webtoolkit.jwt.WString;

public class WivQueryNavigationNode extends DefaultNavigationNode {

	public WivQueryNavigationNode(TreeMenuNode parent) {
		super(WString.tr("menu.query.wiv"), parent);
		
		new FormNavigationNode(WString.tr("menu.query.wiv.arl.confirmedHiv"),this, true){
			@Override
			public IForm createForm() {
				return new WivArlConfirmedHivForm();
			}
		};
		
		new FormNavigationNode(WString.tr("menu.query.wiv.arl.epidemiology"),this, true){
			@Override
			public IForm createForm() {
				return new WivArlEpidemiologyForm();
			}
		};

		new FormNavigationNode(WString.tr("menu.query.wiv.arl.cd4"),this, true){
			@Override
			public IForm createForm() {
				return new WivArlCd4Form();
			}
		};

		new FormNavigationNode(WString.tr("menu.query.wiv.arl.viralLoad"),this, true){
			@Override
			public IForm createForm() {
				return new WivArlViralLoadForm();
			}
		};

		new FormNavigationNode(WString.tr("menu.query.wiv.arc.cd4"),this, true){
			@Override
			public IForm createForm() {
				return new WivArcCd4Form();
			}
		};
		
		new FormNavigationNode(WString.tr("menu.query.wiv.arc.viralLoad"),this, true){
			@Override
			public IForm createForm() {
				return new WivArcViralLoadForm();
			}
		};
		
		new FormNavigationNode(WString.tr("menu.query.wiv.arc.therapyAtc"),this, true){
			@Override
			public IForm createForm() {
				return new WivArcTherapyAtcForm();
			}
		};
		
		new FormNavigationNode(WString.tr("menu.query.wiv.arc.lastContact"),this, true){
			@Override
			public IForm createForm() {
				return new WivArcLastContactForm();
			}
		};

		new FormNavigationNode(WString.tr("menu.query.wiv.arc.deaths"),this, true){
			@Override
			public IForm createForm() {
				return new WivArcDeathsForm();
			}
		};
	}

}
