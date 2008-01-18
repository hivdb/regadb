package demo;

import java.io.IOException;

/*demo*
 * A class illustrating support from Eclipse in correcting
 * non-trivial errors.
 * 
 * @author   Eric Steegmans
 */
public class ExceptionErrors {

	public void m() throws Exception {
		n();
	}

	// 1. Correct error in the heading of this method
	// using autocorrection.
	public void o() throws IOException {
	}

	// 2. Correct the error in the body of this method again
	// using autocorrection.
	public void q() {
		try {
			n();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void n() throws Exception {
	}

}
