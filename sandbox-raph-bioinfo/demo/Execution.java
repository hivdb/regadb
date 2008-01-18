package demo;

/*demo*
 * A class illustrating support from Eclipse in executing
 * and debugging code.
 * 
 * @author   Eric Steegmans
 */
public class Execution {

	// 1. Execute the main method for this class.

	// 2. Start debugger.
	// - Set breakpoint at first line of method sum.
	// - Execute successive instructions step by step.

	public static void main(String[] args) {
		int[] elements = { 10, 20, 30, 40, 50, 5, 1 };
		System.out.print("Total sum: ");
		System.out.println(sum(elements));
	}

	public static int sum(int[] elements) {
		int result = 0;
		for (int i = 0; i < elements.length; i++)
			result += elements[i];
		return result;
	}

}
