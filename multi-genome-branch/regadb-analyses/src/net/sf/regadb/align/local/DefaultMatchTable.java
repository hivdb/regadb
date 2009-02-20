package net.sf.regadb.align.local;


public class DefaultMatchTable<S extends Symbol> implements ISymbolMatchTable<S>{
    private double[][] scores;
    private boolean symmetric = true;
    
    public DefaultMatchTable(IAlphabet<S> alphabet){
        this(alphabet.size());
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
}
