package be.kuleuven.rega.research.discordance;

import java.util.Arrays;
import java.util.Collection;

public class PatientLine {

	private RuleFromAlgorithm[] rfas;
	private double error;
	private Collection<SimpleMutation> mutlist;
	private String subtype;
	private boolean treated;

	public PatientLine(Collection<SimpleMutation> mutlist, double error, boolean treated, String subtype, RuleFromAlgorithm[] rfas) {
		this.mutlist = mutlist;
		this.error = error;
		this.treated = treated;
		this.subtype = subtype;
		this.rfas = rfas;
	}

	public final String getSubtype() {
		return subtype;
	}

	public final boolean isTreated() {
		return treated;
	}

	public RuleFromAlgorithm[] getRfas() {
		return rfas;
	}

	public Collection<SimpleMutation> getMutations() {
		return mutlist;
	}

	public RuleFromAlgorithm[] getRules() {
		return rfas;
	}

	@Override
	public String toString() {
		return "PatientLine [error=" + error + ", mutlist=" + mutlist
				+ ", rfas=" + Arrays.toString(rfas) + ", subtype=" + subtype
				+ ", treated=" + treated + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatientLine other = (PatientLine) obj;
		if (Double.doubleToLongBits(error) != Double
				.doubleToLongBits(other.error))
			return false;
		if (mutlist == null) {
			if (other.mutlist != null)
				return false;
		} else if (!mutlist.equals(other.mutlist))
			return false;
		if (!Arrays.equals(rfas, other.rfas))
			return false;
		if (subtype == null) {
			if (other.subtype != null)
				return false;
		} else if (!subtype.equals(other.subtype))
			return false;
		if (treated != other.treated)
			return false;
		return true;
	}

	public double getError() {
		return error;
	}

}
