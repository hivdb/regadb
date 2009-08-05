package net.sf.regadb.align.local;

public class AaAlphabet extends DefaultAlphabet<Symbol>{

	public AaAlphabet(){
		super();
		
		init();
	}
	
	public static final Symbol GAP = new Symbol("-");
    public static final Symbol A = new Symbol("A");
    public static final Symbol R = new Symbol("R");
    public static final Symbol N = new Symbol("N");
    public static final Symbol D = new Symbol("D");
    public static final Symbol C = new Symbol("C");
    public static final Symbol Q = new Symbol("Q");
    public static final Symbol E = new Symbol("E");
    public static final Symbol G = new Symbol("G");
    public static final Symbol H = new Symbol("H");
    public static final Symbol I = new Symbol("I");
    public static final Symbol L = new Symbol("L");
    public static final Symbol K = new Symbol("K");
    public static final Symbol M = new Symbol("M");
    public static final Symbol F = new Symbol("F");
    public static final Symbol P = new Symbol("P");
    public static final Symbol S = new Symbol("S");
    public static final Symbol T = new Symbol("T");
    public static final Symbol W = new Symbol("W");
    public static final Symbol Y = new Symbol("Y");
    public static final Symbol V = new Symbol("V");
    public static final Symbol B = new Symbol("B");
    public static final Symbol Z = new Symbol("Z");
    public static final Symbol X = new Symbol("X");
    public static final Symbol ANY = new Symbol("*");
	
	protected void init(){
		addSymbol(GAP);
        addSymbol(A);
        addSymbol(R);
        addSymbol(N);
        addSymbol(D);
        addSymbol(C);
        addSymbol(Q);
        addSymbol(E);
        addSymbol(G);
        addSymbol(H);
        addSymbol(I);
        addSymbol(L);
        addSymbol(K);
        addSymbol(M);
        addSymbol(F);
        addSymbol(P);
        addSymbol(S);
        addSymbol(T);
        addSymbol(W);
        addSymbol(Y);
        addSymbol(V);
        addSymbol(B);
        addSymbol(Z);
        addSymbol(X);
        addSymbol(ANY);
	}

	public Symbol getGap() {
		return GAP;
	}
}
