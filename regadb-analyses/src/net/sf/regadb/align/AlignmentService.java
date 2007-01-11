/*
 * Created on Jan 5, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.align;

import org.biojava.bio.seq.Sequence;

public interface AlignmentService {
    AlignmentResult alignTo(Sequence target, Sequence ref);
}
