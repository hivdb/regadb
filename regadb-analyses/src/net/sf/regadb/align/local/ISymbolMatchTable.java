package net.sf.regadb.align.local;

public interface ISymbolMatchTable<S extends Symbol> {
    
    public void setScore(S s1, S s2, double score);
    public double getScore(S s1, S s2);
    public IAlphabet<S> getAlphabet();
}
