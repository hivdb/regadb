package net.sf.regadb.ui.datatable.utils;

import java.util.StringTokenizer;

public class TextUtils {
	public static String summarize(String text) {
		final int maxWords = 10;
		StringTokenizer st = new StringTokenizer(text);
		int words = 0;
		StringBuilder sb = new StringBuilder();
		while (st.hasMoreTokens()) {
			words++;
			
			String word = st.nextToken();
			sb.append(' ');
			sb.append(word);
			if (words > maxWords)
				return sb.toString() + " ...";
		}
		return text;
	}
}
