/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util;

import java.util.*;
import java.io.*;

public class MStreamTokenizer {

	protected BufferedReader reader;
	protected int lineno;
	protected String pushbacktoken;
	protected boolean prevcr;
	protected int pushbackchar;
	protected String chartokens;
	protected int commentchar;
	
	public MStreamTokenizer(InputStream stream) {
		this(new InputStreamReader(stream));
	}
	
	public MStreamTokenizer(Reader myreader) {
		reader = new BufferedReader(myreader);
		lineno = 1;
		pushbacktoken = null;
		prevcr = false;
		pushbackchar = -1;
		chartokens = ":;,[]{}";
		commentchar = '#';
	}
	
	public String getToken() throws IOException {
		if (pushbacktoken == null) {
			int ch = readSignificantChar();
			if (ch != -1) {
				StringBuffer strg = new StringBuffer();
				boolean done = false;
				do {
					strg.append((char)ch);
					if (chartokens.indexOf((char)ch) == -1) {
						ch = readChar();
						if (ch == ' ' || ch == 10 || ch == 13 || ch == -1 || ch == '\t') {
							pushBackChar(ch);
							done = true;
						}
						if (chartokens.indexOf((char)ch) != -1) {
							pushBackChar(ch);
							done = true;
						}
					} else {
						done = true;
					}
				} while (!done);
				return strg.toString();
			} else {
				return null;
			}
		} else {
			String betw = pushbacktoken;
			pushbacktoken = null;
			return betw;
		}
	}
	
	public String readToken() throws IOException {
		int saveline = getLine();
		String token = getToken();
		if (token == null)
			throw new IOException("Unexpected end of file at line: "+saveline);
		return token;
	}
	
	public boolean hasMoreTokens() throws IOException {
		String token = getToken();
		if (token == null) {
			return false;
		} else {
			pushBackToken(token);
			return true;
		}
	}
	
	public int readSignificantChar() throws IOException {
		int ch;
		do {
			ch = readChar();
			if (ch == commentchar) {
				do {
					ch = readChar();
				} while (ch != -1 && ch != 10 && ch != 13);
			}
		} while (ch == ' ' || ch == 10 || ch == 13 || ch == '\t');
		return ch;
	}
	
	public int readChar() throws IOException {
		if (pushbackchar == -1) {
			int ch = reader.read();
			if (ch == 10 && prevcr == false) lineno++;
			prevcr = (ch == 13);
			if (prevcr) lineno++;
			return ch;
		} else {
			int betw = pushbackchar;
			pushbackchar = -1;
			return betw;
		}
	}
	
	public String readTillEol() throws IOException {
		int ch;
		StringBuffer strg = new StringBuffer();
		if (pushbacktoken == null) {
			ch = readSignificantChar();
			if (ch == -1) return null;
		} else {
			strg.append(pushbacktoken);
			pushbacktoken = null;
			ch = readChar();
		}
		while (ch != 10 && ch != 13 && ch != -1) {
			strg.append((char)ch);
			ch = readChar();
		}
		pushBackChar(ch);
		return strg.toString();
	}
	
	public int readInteger() throws IOException {
		int saveline = getLine();
		String token = readToken();
		try {
			return Integer.parseInt(token);
		} catch (NumberFormatException e) {
			throw new IOException("Integer value expected at line: "+saveline);
		}
	}
	
	public float readFloat() throws IOException {
		int saveline = getLine();
		String token = readToken();
		try {
			Float fl = new Float(token);
			return fl.floatValue();
		} catch (NumberFormatException e) {
			throw new IOException("Float value expected at line: "+saveline);
		}
	}
	
	public String isNextTokenIn(String strg) throws IOException {
		String token = getToken();
		if (token == null || strg.indexOf(token) != -1) {
			return token;
		} else {
			pushBackToken(token);
			return null;
		}
	}
	
	public boolean isNextToken(String strg) throws IOException {
		boolean yes = false;
		String token = getToken();
		if (token == null) return false;
		if (token.equals(strg)) yes = true;
		else pushBackToken(token);
		return yes;
	}

        public boolean isNextTokenUpperCased(String strg) throws IOException {
		boolean yes = false;
		String token = getToken();
		if (token == null) return false;
		if (token.toUpperCase().equals(strg)) yes = true;
		else pushBackToken(token);
		return yes;
	}
	
	public int readChar(String which) throws IOException {
		int saveline = getLine();
		String character = getToken();
		if (character == null)
			throw new IOException("Unexpected end of file reading character at line: "+saveline);
		int idx = which.indexOf(character.charAt(0));
		if (idx < 0 || character.length() != 1)
			throw new IOException("Character '"+which+"' expected at line: "+saveline);
		return idx;
	}
	
	public void readChar(char ch) throws IOException {
		int saveline = getLine();
		String character = getToken();
		if (character == null)
			throw new IOException("Unexpected end of file reading character at line: "+saveline);
		if (!character.equals(String.valueOf(ch)))
			throw new IOException("Character '"+ch+"' expected at line: "+saveline);
	}
	
	public void pushBackChar(int ch) {
		pushbackchar = ch;
	}
	
	public void pushBackToken(String token) {
		pushbacktoken = token;
	}
	
	public String setCharTokens(String chartokens) {
		String result = this.chartokens;
		this.chartokens = chartokens;
		return result;
	}	
	
	public int setCommentChar(int commentchar) {
		int result = this.commentchar;
		this.commentchar = commentchar;
		return result;
	}
	
	public String getCharTokens() {
		return chartokens;
	}	
	
	public int setCommentChar() {
		return commentchar;
	}	
	
	public int getLine() {
		return lineno;
	}
	
	public void close() throws IOException {
		reader.close();
	}		
	
	public String toString() {
		return "MStreamTokenizer ["+commentchar+"] ["+chartokens+"]";
	}
	
}
