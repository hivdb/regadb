package net.sf.phylis;

/**
 * An interface for accessing sequence information
 */
public interface SequenceInterface {
    /**
     * The sequence name
     */
    String getName();
    
    /**
     * The sequence description
     */
    String getDescription();
    
    /**
     * The sequence itself
     */
    String getSequence();

    /**
     * The sequence length (== getSequence().getLength())
     */
    int getLength();

	void removeChar(int i);

    int firstNonGapPosition();

    int lastNonGapPosition();
}