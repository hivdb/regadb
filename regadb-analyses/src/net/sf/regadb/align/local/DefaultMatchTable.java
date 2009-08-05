package net.sf.regadb.align.local;


public class DefaultMatchTable<S extends Symbol> implements ISymbolMatchTable<S>{
    private double[][] scores;
    private boolean symmetric = true;
    private IAlphabet<S> alphabet = null;
    
    public DefaultMatchTable(IAlphabet<S> alphabet){
        this(alphabet.size());
        setAlphabet(alphabet);
    }
    public DefaultMatchTable(int alphabetsize){
        scores = new double[alphabetsize][alphabetsize];
    }

    public double getScore(S s1, S s2) {
        return scores[s1.toInt()][s2.toInt()];
    }

    public void setScore(S s1, S s2, double score) {
        scores[s1.toInt()][s2.toInt()] = score;
        if(symmetric)
            scores[s2.toInt()][s1.toInt()] = score;
    }
    
    
	public void setAlphabet(IAlphabet<S> alphabet) {
		this.alphabet = alphabet;
	}
	public IAlphabet<S> getAlphabet() {
		return alphabet;
	}
	
	public void fill(double match, double mismatch){
        for(int i=0; i<scores.length; ++i){
            for(int j=0; j<scores.length; ++j){
                if(i == j)
                    scores[i][i] = match;
                else
                    scores[i][j] = mismatch;
            }
        }
    }
    
    public void construct(String matrix){
    	String[] rows = matrix.split("\n");
    	
    	String[] h = rows[0].split("[ \t\n\f\r]+");
    	for(int i=1; i<rows.length; ++i){
    		String[] r = rows[i].split("[ \t\n\f\r]+");
    		for(int j=1; j<rows.length; ++j){
    			double s = Double.parseDouble(r[j]);
    			setScore(
    					getAlphabet().get(h[i]),
    					getAlphabet().get(h[j]),
    					s);
    		}
    	}
    }
}
