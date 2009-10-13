package net.sf.regadb.io.db.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;

public class DrugMap {
	private Map<String,DrugGeneric> gmap = null;
	
	public DrugMap(){
		Map<String,DrugGeneric> gmap = new HashMap<String, DrugGeneric>();
		for(DrugGeneric dg : Utils.prepareRegaDrugGenerics())
			gmap.put(dg.getGenericId(), dg);
		setGenericsMap(gmap);
	}
	
	public DrugMap(Collection<DrugGeneric> generics){
		Map<String,DrugGeneric> gmap = new HashMap<String, DrugGeneric>();
		for(DrugGeneric dg : generics)
			gmap.put(dg.getGenericId(), dg);
		setGenericsMap(gmap);
	}
	
	public DrugMap(Map<String,DrugGeneric> gmap){
		setGenericsMap(gmap);
	}

	public DrugGeneric get(String genericId){
		return getGenericsMap().get(genericId);
	}

	public DrugGeneric getBoosted(DrugGeneric pi){
		return get(pi.getGenericId()+"/r");
	}
	
	public void toBoosted(Set<DrugGeneric> dgs){
		for(DrugGeneric rtv : dgs){
			if(isRTV(rtv)){
				for(DrugGeneric pi : dgs){
					DrugGeneric bpi = getBoosted(pi);
					if(bpi != null){
						dgs.remove(pi);
						dgs.remove(rtv);
						dgs.add(bpi);
					}
				}
				return;
			}
		}
	}
	
	public void toBoosted(Therapy t){
		for(TherapyGeneric tg : t.getTherapyGenerics()){
			DrugGeneric rtv = tg.getId().getDrugGeneric();
			if(isRTV(rtv)){
				for(TherapyGeneric pi : t.getTherapyGenerics()){
					DrugGeneric bpi = getBoosted(pi.getId().getDrugGeneric());
					if(bpi != null){
						t.getTherapyGenerics().remove(tg);
						pi.getId().setDrugGeneric(bpi);
					}
				}
				return;
			}
		}
	}

	public void setGenericsMap(Map<String,DrugGeneric> gmap) {
		this.gmap = gmap;
	}

	public Map<String,DrugGeneric> getGenericsMap() {
		return gmap;
	}
	
	public boolean isRTV(DrugGeneric drug){
		return drug.getGenericId().equals("RTV");
	}
}
