package net.sf.phylis;
/*
 * Created on Apr 7, 2003
 */

/**
 * @author kdf
 */
public class SubSequence implements SequenceInterface {
    private String name;
    private String description;
    private SequenceInterface sequence;
    private int beginIndex;
    private int endIndex;

    public SubSequence(String name, String description,
                       SequenceInterface sequence, int beginIndex,
                       int endIndex)
    {
        this.name = name;
        this.description = description;
        this.sequence = sequence;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSequence() {
        return sequence.getSequence().substring(beginIndex, endIndex);
    }

    public int getLength() {
        return (endIndex - beginIndex);
    }

	public void removeChar(int i) {
		sequence.removeChar(beginIndex + i);
	}

    public int firstNonGapPosition() {
        throw new RuntimeException("Not implemented");
    }

    public int lastNonGapPosition() {
        throw new RuntimeException("Not implemented");
    }
}
