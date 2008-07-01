package net.sf.regadb.genbank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Page {
	List<Integer> startPos = new ArrayList<Integer>();
	List<Integer> endPos = new ArrayList<Integer>();
	
	Map<String, String> seqs = new HashMap<String, String>();
}
