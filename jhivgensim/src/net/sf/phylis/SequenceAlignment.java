package net.sf.phylis;
import java.io.*;
import java.util.*;

/*
 * Created on Apr 7, 2003
 */

/**
 * Represents multiple sequences that are aligned
 */
public class SequenceAlignment
{
    private List sequences;
    int sequenceType;

    public final static int FILETYPE_FASTA = 0;
    public final static int FILETYPE_CLUSTAL = 1;
    public final static int FILETYPE_NEXUS = 2;
    public final static int FILETYPE_PHYLIP = 3;

	public final static int SEQUENCE_DNA = 0;
	public final static int SEQUENCE_AA = 1;
    
    public final static int MAX_NEXUS_TAXUS_LENGTH = 20;
    public final static int MAX_PHYLIP_TAXUS_LENGTH = 8;

    public SequenceAlignment(InputStream inputFile,
                             int fileType)
        throws ParameterProblemException, IOException, FileFormatException
    {
        this.sequences = new ArrayList();

        switch (fileType) {
            case FILETYPE_FASTA:
                readFastaFile(inputFile);
                break;
            case FILETYPE_CLUSTAL:
                //readClustalFile(inputFile);
                throw new ParameterProblemException("Reading clustal not yet supported");
            case FILETYPE_NEXUS:
                //readClustalFile(inputFile);
                throw new ParameterProblemException("Reading nexus not yet supported");
             default:
		throw new ParameterProblemException("Illegal value for fileType");
        }
    }

	/*
	 * The most unefficient java code imaginable ... ! Rewrite !
	 */
	public void degap() {
		for (int i = 0; i < getLength();) {
			boolean hasGap = false;

			for (int j = 0; j < sequences.size(); ++j) {
				SequenceInterface s = (SequenceInterface) sequences.get(j);
				
				if (s.getSequence().charAt(i) == '-') {
					hasGap = true;
					break;
				}
			}

			if (hasGap) {
				for (int j = 0; j < sequences.size(); ++j) {
					SequenceInterface s = (SequenceInterface) sequences.get(j);

					s.removeChar(i);
				}
			} else
				++i;
		}
	}

    public boolean areAllEqualLength(boolean force) {
        Iterator i = sequences.iterator();
        int length = -1;

        while (i.hasNext()) {
            SequenceInterface s = (SequenceInterface) i.next();

            if (length == -1)
                length = s.getLength();
            else
                if (s.getLength() != length) {
                    if (force)
                        i.remove();
                    else
                        return false;
                }
        }

        return true;
    }

    SequenceAlignment(List sequences, int sequenceType)
    {
        this.sequences = sequences;
        this.sequenceType = sequenceType;
    }

    private void readFastaFile(InputStream inputFile)
        throws IOException, FileFormatException
    {
        /*
         * The fasta format (for multiple sequences) as described in
         * http://www.molbiol.ox.ac.uk/help/formatexamples.htm#fasta
         * and
         * http://www.ncbi.nlm.nih.gov/BLAST/fasta.html
         */

        LineNumberReader reader
            = new LineNumberReader(new InputStreamReader(inputFile));

        for (;;) {
            Sequence s = readFastaFileSequence(reader);

            if (s != null) {
                sequences.add(s);
                //System.err.println(s.getName() + " " + s.getLength());
            } else
                return;
        }
    }

    private Sequence readFastaFileSequence(LineNumberReader reader)
        throws IOException, FileFormatException
    {
        /*
         * first read the header
         */
        String header;
        do {
            header = reader.readLine();
            if (header == null)
                return null; // no new sequence to be found
        } while (header.length() == 0);

        if (header.charAt(0) != '>')
            throw new FileFormatException("Expecting a '>'",
                                          reader.getLineNumber());

        // eat '>'
        header = header.substring(1);
        while (header.charAt(0) == ' ')
			header = header.substring(1);
        // seperate name from description
        int spacePos = header.indexOf(' ');
        String name;
        String description;
        if (spacePos != -1) {   // only a name
           name = header.substring(0, spacePos);
           description = header.substring(spacePos);
        } else {
           name = header;
           description = "";
        }

        /*
         * next read the sequence
         */
        StringBuffer sequence = new StringBuffer();
        
        String s;
        do {
            reader.mark(1000);
            s = reader.readLine();
            if (s != null) {
                 if (s.length() > 0 && s.charAt(0) == '>') {
                    // this is the start of a next sequence
                    reader.reset(); 
                    s = null;
                 } else
                    sequence.append(s);
            }
        } while (s != null);

        return new Sequence(name, description, sequence.toString());
    }

    public List getSequences() {
        return sequences;
    }

    public int getLength() {
        return ((SequenceInterface) sequences.get(0)).getLength();
    }
    
    public SequenceAlignment getSubSequence(int startIndex, int endIndex)
    {
        List subSequences = new ArrayList();
        
        Iterator i = sequences.iterator();
        while (i.hasNext()) {
            SequenceInterface sequence = (SequenceInterface) i.next();
            
            String name = sequence.getName();
            String description = sequence.getDescription() + " [" + startIndex + ", " + endIndex + "]";
            
            subSequences.add(new SubSequence(name, description, sequence, startIndex, endIndex));
        }

        return new SequenceAlignment(subSequences, sequenceType);
    }

    void writeOutput(OutputStream outputFile, int fileType)
        throws IOException, ParameterProblemException
    {
        int a;
        switch (fileType) {
            case FILETYPE_FASTA:
                writeFastaOutput(outputFile);
                break;
            case FILETYPE_CLUSTAL:
                //writeClustalOutput(outputFile);
                throw new ParameterProblemException("Writing clustal not yet supported");
            case FILETYPE_NEXUS:
                writeNexusOutput(outputFile);
                break;
            case FILETYPE_PHYLIP:
                writePhylipOutput(outputFile);
                break;
             default:
                throw new ParameterProblemException("Illegal value for fileType");
        }
    }
    
    void writeFastaOutput(OutputStream outputFile) throws IOException
    {
        /*
         * The fasta format (for multiple sequences) as described in
         * http://www.molbiol.ox.ac.uk/help/formatexamples.htm#fasta
         * and
         * http://www.ncbi.nlm.nih.gov/BLAST/fasta.html
         */

        Writer writer = new OutputStreamWriter(outputFile);
        final char endl = '\n';
         
        Iterator i = sequences.iterator();
        
        while (i.hasNext()) {
            SequenceInterface seq = (SequenceInterface) i.next();
            
            writer.write('>' + seq.getName() + " " + seq.getDescription() + endl);
            
            final int lineLength = 50;
            
            for (int j = 0; j < getLength(); j += lineLength) {
                int end = Math.min(j + lineLength, seq.getSequence().length());
                writer.write(seq.getSequence().substring(j, end));
                writer.write(endl);
            }
        }
        
        writer.flush();
    }

    void writePhylipOutput(OutputStream outputFile) throws IOException
    {
        /*
         * The phylip format (for multiple sequences)
         */

        Writer writer = new OutputStreamWriter(outputFile);
        final char endl = '\n';

        writer.write(sequences.size() + " " + getLength() + endl);
        Iterator i = sequences.iterator();
        
        Set nameSet = new HashSet();
        while (i.hasNext()) {
            SequenceInterface seq = (SequenceInterface) i.next();

            String name = nexusName(seq, nameSet, MAX_PHYLIP_TAXUS_LENGTH);
            nameSet.add(name);

            writer.write(padBack(new StringBuffer(name), MAX_PHYLIP_TAXUS_LENGTH + 2)
            		     + seq.getSequence() + endl);
        }
        
        writer.flush();
    }

    void writeNexusOutput(OutputStream outputFile) throws IOException
    {
        /*
         * The Nexus file format, as taken from an example file
         */
        Writer writer = new OutputStreamWriter(outputFile);
        final char endl = '\n';
        
        writer.write("#NEXUS" + endl);
        writer.write(endl);
        
        Set nameSet = new HashSet();
        List nameList = new ArrayList();

        for (Iterator i = sequences.iterator(); i.hasNext();) {
            SequenceInterface seq = (SequenceInterface) i.next();

            String name = nexusName(seq, nameSet, MAX_NEXUS_TAXUS_LENGTH);
            nameList.add(name);
            nameSet.add(name);
            writer.write("[Name: " + padBack(new StringBuffer(name), MAX_NEXUS_TAXUS_LENGTH + 2)
                        + "Len: " +  padBack(new StringBuffer().append(seq.getLength()), 10)
                        + "Check: 0]" + endl);
            
        }
        
        writer.write(endl);
        
        String dataType[] = { "DNA", "protein" };
        
        writer.write("begin data;" + endl);
        writer.write(" dimensions ntax=" + sequences.size() + " nchar=" + getLength() + ";" + endl);
        writer.write(" format datatype=" + dataType[sequenceType] + " interleave missing=? gap=-;" + endl);
        writer.write("  matrix" + endl);

        final int blockUnit = 20;
        final int blockUnitsPerBlock = 5;
        for (int j = 0; j < getLength(); j += (blockUnit * blockUnitsPerBlock)) {
            for (int i = 0; i < sequences.size(); ++i) {
                SequenceInterface seq = (SequenceInterface) sequences.get(i);
                
                writer.write(new String(padFront(new StringBuffer((String) nameList.get(i)), MAX_NEXUS_TAXUS_LENGTH + 2)));
                for (int k = 0; k < blockUnitsPerBlock; ++k) {
                    int start = j + (k * blockUnit);
                    int end = Math.min(start + blockUnit, seq.getSequence().length());
                    String s = seq.getSequence().substring(start, end);
                    
                    writer.write(" " + s);
                    if (s.length() < blockUnit)
                        break;
                }
                writer.write(endl);
            }
            
            writer.write(endl);
        }
        
        writer.write("  ;" + endl);
        writer.write("end;" + endl);
        writer.flush();
    }

    String nexusName(SequenceInterface seq, Set names, int maxlength) {
        String name = seq.getName();
        name = name.substring(0, Math.min(maxlength, name.length()));
        name = name.replace('-', '_');
        name = name.replace(',', '_');
        name = name.replace('/', '_');
        if (names.contains(name)) {
  
            String base = name.substring(0, name.length() - 3) + '_';

            int c = 0;
            do {
                if (c < 10)    
                    name = base + '0' + c;
                else
                    name = base + c;
                ++c;
            } while (names.contains(name));
        }

        return name;
    }

    private static StringBuffer padBack(StringBuffer s, int total) {
        StringBuffer result = s;
        int numAdded = total - s.length();
        for (int i = 0; i < numAdded; ++i)
          result.append(' ');
        
        return result;
    }

    private static StringBuffer padFront(StringBuffer s, int total) {
        StringBuffer result = new StringBuffer();
        int numAdded = total - s.length();
        for (int i = 0; i < numAdded; ++i)
          result.append(' ');

        result.append(s);

        return result;
    }

	public void reverseTaxa() {
		Collections.reverse(sequences);
	}

	public int getSequenceType() {
		return sequenceType;
	}

	public void setSequenceType(int i) {
		sequenceType = i;
	}

    public void removeAllEmpty() {
        for (int j = 0; j < sequences.size(); ++j) {
            SequenceInterface s = (SequenceInterface) sequences.get(j);
            
            if (s.firstNonGapPosition() == s.getLength()) {
                sequences.remove(j);
                --j;
            } else {
                final boolean checkPr = true;
                final boolean checkRt = false;
                /*
                 * remove sequences without protease 10-95
                 */
                if (checkPr) {
                    if (s.firstNonGapPosition() > (48+95)*3) {
                        System.err.println("Removing non-protease sequence: "
                                + s.getName() + "[" + s.firstNonGapPosition()
                                + " - " + s.lastNonGapPosition() + "]");
                        sequences.remove(j);
                        --j;
                    }
                }

                /*
                 * remove sequences without reverse transcriptase 40-215
                 */
                if (checkRt) {
                    if (s.firstNonGapPosition() > (48+99+39)*3) {
                        System.err.println("Removing non-RT sequence: "
                                + s.getName() + "[" + s.firstNonGapPosition()
                                + " - " + s.lastNonGapPosition() + "]");
                        sequences.remove(j);
                        --j;
                    } else if (s.lastNonGapPosition() < (48+99+214)*3) {
                        System.err.println("Removing non-RT sequence: "
                                + s.getName() + "[" + s.firstNonGapPosition()
                                + " - " + s.lastNonGapPosition() + "]");
                        sequences.remove(j);
                        --j;                    
                    }
                }
            }
        }
    }
}
