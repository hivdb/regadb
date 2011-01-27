package net.sf.regadb.align.local;

import org.biojava.bio.BioException;
import org.biojava.bio.alignment.SubstitutionMatrix;
import org.biojava.bio.seq.io.SymbolTokenization;
import org.biojava.bio.symbol.FiniteAlphabet;
import org.biojava.bio.symbol.SimpleSymbolList;
import org.biojava.bio.symbol.Symbol;

public class AASubstitutionMatrix extends SubstitutionMatrix{
	private Symbol X;
	private Symbol J, L, I;
	private Symbol Z, Q, E;
	private Symbol B, N, D;

	public AASubstitutionMatrix(FiniteAlphabet alpha, String matrixString, String name)
			throws BioException {
		super(alpha, matrixString, name);
		
		SymbolTokenization st = alpha.getTokenization("default");
		X = new SimpleSymbolList(st,"X").symbolAt(1);
		J = new SimpleSymbolList(st,"J").symbolAt(1);
		L = new SimpleSymbolList(st,"L").symbolAt(1);
		I = new SimpleSymbolList(st,"I").symbolAt(1);
		Z = new SimpleSymbolList(st,"Z").symbolAt(1);
		Q = new SimpleSymbolList(st,"Q").symbolAt(1);
		E = new SimpleSymbolList(st,"E").symbolAt(1);
		B = new SimpleSymbolList(st,"B").symbolAt(1);
		N = new SimpleSymbolList(st,"N").symbolAt(1);
		D = new SimpleSymbolList(st,"D").symbolAt(1);
	}

	@Override
	public int getValueAt(Symbol row, Symbol col) throws BioException {
		return super.getValueAt(getAmbiguousAminoAcid(row), getAmbiguousAminoAcid(col));
	}
	
	private Symbol getAmbiguousAminoAcid(Symbol s){
		FiniteAlphabet a = (FiniteAlphabet)s.getMatches();
		if(a.size() > 2)
			return X;
		else if(a.size() == 2){
			if(a.contains(L) && a.contains(I))
				return J;
			else if(a.contains(Q) && a.contains(E))
				return Z;
			else if(a.contains(N) && a.contains(D))
				return B;
			else
				return X;
		}
		else
			return s;
	}
}
