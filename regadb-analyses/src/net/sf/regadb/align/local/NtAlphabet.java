package net.sf.regadb.align.local;

public class NtAlphabet extends DefaultAlphabet<Symbol>{

	public NtAlphabet(){
		super();
		init();
	}
	
	public static final Symbol GAP = new Symbol("-");
	public static final Symbol A = new Symbol("A");
	public static final Symbol T = new Symbol("T");
	public static final Symbol G = new Symbol("G");
	public static final Symbol C = new Symbol("C");
	public static final Symbol S = new Symbol("S");
	public static final Symbol W = new Symbol("W");
	public static final Symbol R = new Symbol("R");
	public static final Symbol Y = new Symbol("Y");
	public static final Symbol K = new Symbol("K");
	public static final Symbol M = new Symbol("M");
	public static final Symbol B = new Symbol("B");
	public static final Symbol V = new Symbol("V");
	public static final Symbol H = new Symbol("H");
	public static final Symbol D = new Symbol("D");
	public static final Symbol N = new Symbol("N");

	
	protected void init(){
		addSymbol(GAP);
		addSymbol(A);
		addSymbol(T);
		addSymbol(G);
		addSymbol(C);
		addSymbol(S);
		addSymbol(W);
		addSymbol(R);
		addSymbol(Y);
		addSymbol(K);
		addSymbol(M);
		addSymbol(B);
		addSymbol(V);
		addSymbol(H);
		addSymbol(D);
		addSymbol(N);
	}

	public Symbol getGap() {
		return GAP;
	}
}
