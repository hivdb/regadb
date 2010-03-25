package be.kuleuven.rega.research.zehava;

import java.util.Date;
import java.util.Set;

public class EtravirineRecord {

	private String patientId;
	private String dataset;
	private String country;
	private String subtype;
	private String gender;
	private String transmissionGroup;
	private Date sampleDate;
	private boolean naive;
	private boolean nnrti;
	private boolean efv;
	private boolean nvp;
	private String regaGss;
	private String regaRemarks;
	private String regaMutations;
	private String hivdbGss;
	private String hivdbRemarks;
	private String hivdbMutations;
	private String concatenatedSequence;
	private Set<String> mutations; // only resistance mutations
	private Set<String> drugExperience; // only experience with NRTI drugs

	public Set<String> getMutations() {
		return mutations;
	}

	public void setMutations(Set<String> mutations) {
		this.mutations = mutations;
	}

	public Set<String> getDrugExperience() {
		return drugExperience;
	}

	public void setDrugExperience(Set<String> drugExperience) {
		this.drugExperience = drugExperience;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public String getConcatenatedSequence() {
		return concatenatedSequence;
	}

	public void setConcatenatedSequence(String concatenatedSequence) {
		this.concatenatedSequence = concatenatedSequence;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Date getSampleDate() {
		return sampleDate;
	}

	public void setSampleDate(Date sampleDate) {
		this.sampleDate = sampleDate;
	}

	public boolean isNaive() {
		return naive;
	}

	public void setNaive(boolean naive) {
		this.naive = naive;
	}

	public boolean isNnrti() {
		return nnrti;
	}

	public void setNnrti(boolean nnrti) {
		this.nnrti = nnrti;
	}

	public boolean isEfv() {
		return efv;
	}

	public void setEfv(boolean efv) {
		this.efv = efv;
	}

	public boolean isNvp() {
		return nvp;
	}

	public void setNvp(boolean nvp) {
		this.nvp = nvp;
	}

	public String getRegaGss() {
		return regaGss;
	}

	public void setRegaGss(String regaGss) {
		this.regaGss = regaGss;
	}

	public String getRegaRemarks() {
		return regaRemarks;
	}

	public void setRegaRemarks(String regaRemarks) {
		this.regaRemarks = regaRemarks;
	}

	public String getRegaMutations() {
		return regaMutations;
	}

	public void setRegaMutations(String regaMutations) {
		this.regaMutations = regaMutations;
	}

	public String getHivdbGss() {
		return hivdbGss;
	}

	public void setHivdbGss(String hivdbGss) {
		this.hivdbGss = hivdbGss;
	}

	public String getHivdbRemarks() {
		return hivdbRemarks;
	}

	public void setHivdbRemarks(String hivdbRemarks) {
		this.hivdbRemarks = hivdbRemarks;
	}

	public String getHivdbMutations() {
		return hivdbMutations;
	}

	public void setHivdbMutations(String hivdbMutations) {
		this.hivdbMutations = hivdbMutations;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getTransmissionGroup() {
		return transmissionGroup;
	}

	public void setTransmissionGroup(String transmissionGroup) {
		this.transmissionGroup = transmissionGroup;
	}

}
