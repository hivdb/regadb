package net.sf.phylis;
/*
 * Created on Apr 7, 2003
 */

/**
 * Represents a single sequence
 */
public class Sequence implements SequenceInterface
{
    private String name;
    private String description;
    private String sequence;

    public Sequence(String name, String description, String sequence) {
        this.name = name;
        this.description = description;
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSequence() {
        return sequence;
    }

    public int getLength() {
        return sequence.length();
    }

	public void removeChar(int i) {
		sequence = new StringBuffer(sequence).deleteCharAt(i).toString();
	}
	
	public int firstNonGapPosition() {
		for (int i = 0; i < sequence.length(); ++i) {
			if (sequence.charAt(i) != '-')
				return i;
		}
		return sequence.length();
	}

	public int lastNonGapPosition() {
		for (int i = sequence.length() - 1; i >= 0; --i) {
			if (sequence.charAt(i) != '-')
				return i;
		}

		return -1;
	}
}
