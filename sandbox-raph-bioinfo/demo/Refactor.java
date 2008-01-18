package demo;

/*demo*
 * A class illustrating support from Eclipse in refactoring
 * code.
 * 
 * @author   Eric Steegmans
 */
public class Refactor {

	// 1. Put this method and the next one in comments,
	// using "Toggle Comment" from menu Source.
	// 2. Remove the comments using "Toggle Comment" again.
	public void m() {
		n();
		System.out.println();
	}

	// 2. Rename this method to 'myMethod',
	// using "Rename" from menu Refactor.
	public void n() {
	}

	// 3. Extract nested for statement and 2 preceding
	// declarations in a separate method,
	// using "Extract Method" from menu Refactor.
	// 4. Undo the refactoring by inline substitution,
	// using "Inline" from menu Refactor.
	public boolean test(int[] array1, int[] array2) {
		boolean result = true;
		for (int i = 0; i < array1.length; i++) {
			int array_element1 = array1[i];
			boolean result1111 = result;
			for (int j = 0; j < array2.length; j++) {
				int array_element2111 = array2[j];
				if (array_element1 < array_element2111) {
					result1111 = false;
				}
			}
			boolean result111 = result1111;
			boolean result11 = result111;
			boolean result1 = result11;
			result = result1;
		}
		// 5. Search for the declaration of this variable
		// using "Open Declaration" from menu Navigate.
		return result;
	}
}