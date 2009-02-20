package net.sf.regadb.align.local;

public interface IAlphabet<S extends Symbol> {
    public void addSymbol(S s);
    public S get(int i);
    public S get(String s);
    public int size();
}
