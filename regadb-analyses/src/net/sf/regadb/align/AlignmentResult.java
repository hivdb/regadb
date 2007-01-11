/*
 * Created on Jan 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.align;

import java.util.ArrayList;
import java.util.List;

public class AlignmentResult {
    private List<Mutation> mutations = new ArrayList<Mutation>();

    private int firstAa;
    private int lastAa;

    public int getFirstAa() {
        return firstAa;
    }
    public int getLastAa() {
        return lastAa;
    }
    public List<Mutation> getMutations() {
        return mutations;
    }

    public void setFirstAa(int firstAa) {
        this.firstAa = firstAa;
    }
    public void setLastAa(int lastAa) {
        this.lastAa = lastAa;
    }
    public void addMutation(Mutation mutation) {
        mutations.add(mutation);
    }    
}
