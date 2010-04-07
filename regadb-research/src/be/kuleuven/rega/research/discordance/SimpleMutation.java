package be.kuleuven.rega.research.discordance;

public class SimpleMutation implements Comparable<SimpleMutation> {

	private char aminoAcid;
	private int position;
	private boolean ambiguous;
	
	public boolean isAmbiguous() {
		return ambiguous;
	}

	public char getAminoAcid() {
		if(this.isAmbiguous()){
			throw new IllegalStateException();
		}
		return aminoAcid;
	}

	public int getPosition() {
		return position;
	}
	
	public SimpleMutation(int position){
		this.position = position;
		this.ambiguous = true;
	}
	
	public SimpleMutation(int position, char aminoAcid){
		this.position = position;
		this.aminoAcid = aminoAcid;
		this.ambiguous = false;
	}

	public int compareTo(SimpleMutation o) {
		if(o.getPosition() != this.getPosition()){
			return new Integer(this.getPosition()).compareTo(o.getPosition());
		}
		if(isAmbiguous())
			return 0;
		if(o.isAmbiguous())
			return 0;
		return new Character(this.getAminoAcid()).compareTo(o.getAminoAcid());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleMutation other = (SimpleMutation) obj;
		if (position != other.position)
			return false;
		if(isAmbiguous())
			return true;
		if (aminoAcid != other.aminoAcid)
			return false;
		return true;
	}

	@Override
	public String toString(){
		return ""+getPosition()+(isAmbiguous() ? "?" : getAminoAcid());
	}
	
}
