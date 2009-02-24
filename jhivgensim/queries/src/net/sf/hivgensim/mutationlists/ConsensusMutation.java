package net.sf.hivgensim.mutationlists;

public class ConsensusMutation {
	
	private String listName;
	private String version;
	private String proteinAbbreviation;
	private char referenceAminoAcid;
	private int position;
	private char mutationAminoAcid;
	private String drugClassId;
	
	public ConsensusMutation(String listName, String version, String proteinAbbreviation,
			char referenceAminoAcid, int position, char mutationAminoAcid, String drugClassId){
		setListName(listName);
		setVersion(version);
		setProteinAbbreviation(proteinAbbreviation);
		setReferenceAminoAcid(referenceAminoAcid);
		setPosition(position);
		setMutationAminoAcid(mutationAminoAcid);
		setDrugClassId(drugClassId);		
	}
	
	public String getListName() {
		return listName;
	}
	public String getVersion() {
		return version;
	}
	public String getProteinAbbreviation() {
		return proteinAbbreviation;
	}
	public char getReferenceAminoAcid() {
		return referenceAminoAcid;
	}
	public int getPosition() {
		return position;
	}
	public char getMutationAminoAcid() {
		return mutationAminoAcid;
	}
	public String getDrugClassId() {
		return drugClassId;
	}
	
	private void setListName(String listName) {
		this.listName = listName;
	}
	private void setVersion(String version) {
		this.version = version;
	}
	private void setProteinAbbreviation(String proteinAbbreviation) {
		this.proteinAbbreviation = proteinAbbreviation;
	}
	private void setReferenceAminoAcid(char referenceAminoAcid) {
		this.referenceAminoAcid = referenceAminoAcid;
	}
	private void setPosition(int position) {
		this.position = position;
	}
	private void setMutationAminoAcid(char mutationAminoAcid) {
		this.mutationAminoAcid = mutationAminoAcid;
	}
	private void setDrugClassId(String drugClassId) {
		this.drugClassId = drugClassId;
	}
	
	public String toString(){
		char delimiter = '\t';
		return 	getListName() + delimiter + 
				getVersion() + delimiter +
				getProteinAbbreviation() + delimiter +
				getReferenceAminoAcid() + delimiter +
				getPosition() + delimiter +
				getMutationAminoAcid() + delimiter +
				getDrugClassId();
	}
	
}
