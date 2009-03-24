package net.sf.hivgensim.scripts;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.preprocessing.Utils;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

public class LongitudinalEstimate {
	
	/**
	 * Assumes we already have a cross sectional estimate of the landscape?
	 * 
	 */
	
	public LongitudinalEstimate(){
		Login login = null;
		try {
			login = Login.authenticate("gbehey0", "bla123");
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		}
	}
	
	
//
//	Predictability must be done at /indinavir3
//	cp ../../../indinavir3/pred.csv .
//	csvtool select-columns -c 1 -i pred.csv -o longitudinalbaseline.csv
//	csvtool select-columns -c 2 -i pred.csv -o longitudinalfollowup.csv 
//
//	#remove the headers in these files before running regadb-mutationtable 
//	sed -i '1d' longitudinalbaseline.csv
//	sed -i '1d' longitudinalfollowup.csv
//	#get naive sequences
//	regadb-mutationtable longitudinalbaseline.csv fasta PR long_naive.fasta
//	#get mutations
//	regadb-mutationtable longitudinalfollowup.csv aamut PR all_mut_treatedlong.csv
//	#
//	csvtool select-columns -h -c `head -n 1 mut_treated_idv.csv` -i all_mut_treatedlong.csv -o mut_treated_mix.csv
//	#if error like `No column matching PR15L` ie the idv mutation not in long mutations #modify in R as follows
//	#data <- read.csv("all_mut_treatedlong.csv") 
//	#data$PR15L <- 'n'
//	#write.table(data, "all_mut_treatedlong2.csv", quote=FALSE, sep=",", row.names=FALSE)
//
//	#write.table(data, "all_mut_treatedlong2.csv", quote=FALSE, sep=",", row.names=FALSE)
//	#repeat 
//	csvtool select-columns -h -c `head -n 1 mut_treated_idv.csv` -i all_mut_treatedlong2.csv -o mut_treated_mix.csv
//	untill no error
//	#but now read 
//	#data <- read.csv("all_mut_treatedlong2.csv") 
//	# add PR33V, PR37C,  PR67S, 
//	RemoveMixtures mut_treated_mix.csv PR mut_treated.csv
//	csvtool vd -i mut_treated.csv -o mut_treated
//	#diff mut_treated_idv.vd mut_treated.vd #will show you y n difference for mutations added in R so simply
//	mv mut_treated.vd mut_treated_long.vd
//	mv mut_treated_idv.vd mut_treated.vd # just use the old mut_treated.vd if dont want to edit it
//	wc mut_treated.idt; grep '>' long_naive.fasta| wc # Ok if both have the same number of lines
//
//	touch mutagenesis doublepositions
//	Estimate mut_treated.csv long_naive.fasta mut_treated best.cft wildtypes 200 doublepositions 10 mutagenesis
//	 ~kdforc0/project/c++/fastatool/build/src/fastaregion allpi_long.fasta PR > allpi_long_pr.fasta
//	#make sure the mut_treated sequences are not in allpi_long.fasta evaluation set for reasons of independency see allpi2/README
//	ComputeFitness allpi_long.fasta  PR mut_treated current.cft > indinavirlong1fitness.csv
//	cp ../../../allpi3/data/long.fasta .
//	ComputeFitness long.fasta PR mut_treated current.cft > fitness_long2.csv

}
