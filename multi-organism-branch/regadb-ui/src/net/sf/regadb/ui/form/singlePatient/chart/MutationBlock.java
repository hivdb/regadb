package net.sf.regadb.ui.form.singlePatient.chart;

import java.util.ArrayList;

public class MutationBlock
{
	String proteinName;

	short minBound, maxBound;

	ArrayList<String> mutations;

	MutationBlock(String proteinAbbrev, short firstAaPos, short lastAaPos)
	{
		this.proteinName = proteinAbbrev;
		this.minBound = firstAaPos;
		this.maxBound = lastAaPos;
		this.mutations = new ArrayList<String>();
	}

	int numLines()
	{
		return Math.max(2, 1 + mutations.size());
	}
}
