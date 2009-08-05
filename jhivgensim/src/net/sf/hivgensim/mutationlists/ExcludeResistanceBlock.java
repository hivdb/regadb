package net.sf.hivgensim.mutationlists;

/*
 * @author gbehey0
 */

public class ExcludeResistanceBlock {
	
	
	/*
	 * a part of a paupblock used for excluding known resistance mutations.
	 * I've not changed the position "calculation", I've deciphered it from
	 * the old paupblock file on zolder. More explanation can be found in
	 * my labnotes from 1 dec 2008. (Or here when I have time to update this explanation). 
	 */
	
//    charset piresist=232-234 280-282 292-294 304-306 355-357 361-363 388-390 394-396 406-408 412-414;
//    charset rtiresist=562-564 571-573 634-636 640-642 649-651 661-663 664-666 670-672 739-741 748-750 757-759 763-765 784-786 787-789 892-894 982-984 991-993
// 	1003-1005 1009-1011 1069-1071 1084-1086 1096-1098;
//    exclude piresist;
//    exclude rtiresist;
	
	private String listname;
	
	public String getListname() {
		return listname;
	}

	public void setListname(String listname) {
		this.listname = listname;
	}

	public ExcludeResistanceBlock(String listname){
		setListname(listname);
	}
	
	public String toString(){
		int prStart = 48*3;
		int rtStart = 147*3;
		
		int start = 0;
		int stop = 0;
		StringBuffer buf = new StringBuffer();
		buf.append("charset resist=");
		
		for(ConsensusMutation mut : ConsensusMutationList.getList(getListname())){
			if(mut.getProteinAbbreviation().equals("PR")){
				stop = (mut.getPosition()*3)+prStart;				
			}else if(mut.getProteinAbbreviation().equals("RT")){
				stop = (mut.getPosition()*3)+rtStart;
			}else{
				//no PR or RT
				continue;
			}			
			if(start == stop-2){
				//different mutation on same position can be skipped
				continue;
			}
			
			start = stop-2;
			buf.append(""+start+"-"+stop+" ");
			System.out.println(mut.getListName()+" "+mut.getProteinAbbreviation()+" "+mut.getPosition());
		}
		buf.append(";\nexclude resist;\n");
		return buf.toString();
	}

}
