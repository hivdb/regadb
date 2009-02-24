package net.sf.hivgensim.preprocessing;

import java.util.ArrayList;

import net.sf.regadb.csv.Table;

/**
 * todo:
 * 
 * add selection for different cases:
 * WT - MUT
 * MUT - MUT
 * POLY - POLY
 * 
 * @author gbehey0
 *
 */
public class RemoveMixtures {
	
	private Table table;
	
	public RemoveMixtures(Table t){
		this.table = t;
	}
	
	public void removeMixtures(){
		for(int i=1;i<table.numColumns();i++){
			ArrayList<String> coli = table.getColumn(i);
			String namei = coli.get(0);
			for(int j=i+1;j<table.numColumns();j++){
				ArrayList<String> colj = table.getColumn(j);
				String namej = colj.get(0);
				if(namei.substring(0,namei.length()-1).equals(namej.substring(0,namej.length()-1))){
					for(int row = 1; row < table.numRows();row++){
						if(table.valueAt(i, row).equals("y") && table.valueAt(j, row).equals("y")){
							if(Math.random() < 0.5){
								table.setValue(i, row, "n");
							}else{
								table.setValue(j, row, "n");
							}
						}
					}
				}
			}
		}
	}
	
	//C++ code:
//	void SeqMutTable::removeMixtures()
//	{
//	  const FactorList& cols = factors();
//	  
//	  for (unsigned i = 0; i < cols.size(); ++i) {
//	    const Factor& fi = cols[i];
//
//	    const std::string namei = fi.name();
//
//	    for (unsigned j = i+1; j < cols.size(); ++j) {
//	      const Factor& fj = cols[j];
//
//	      const std::string namej = fj.name();
//
//	      if (namei.substr(0, namei.length() - 1)
//	          == namej.substr(0, namej.length()-1)) {
//	        std::cerr << namei << " " << namej << std::endl;
//
//	        int yi = fi.getLevel("y");
//	        int yj = fj.getLevel("y");
//	        int ni = fi.getLevel("n");
//	        int nj = fj.getLevel("n");
//
//	        for (int row = 0; row < rows(); ++row) {
//	          if (levelAt(i, row) == yi) {
//	            if (levelAt(j, row) == yj) {
//	              std::cerr << ".";
//
//	              double f = drand48();
//
//	              if (f < 0.5)
//	                levelAt(i, row) = ni;
//	              else
//	                levelAt(j, row) = nj;
//	            }
//	          }
//	        }
//	      }
//	    }
//	  }
//	}


}
