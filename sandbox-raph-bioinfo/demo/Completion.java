package demo;

/*demo*
 * A class illustrating some support from Eclipse in editing
 * Java programs.
 * 
 * @author   Eric Steegmans
 */
class Completion {

	public void m() {
		// 1. Invoke method with very long name, using
		// using code completion (Ctrl+Space).
		thisMethodHasAPrettyLongName();
		// aMethodWithParameters(first, second, last);

	}

	void f() {
	}

	/**
	 * @param first
	 * @param second
	 * @param last
	 */
	public void aMethodWithParameters(String first, int second, Object last) {

	}

	public void thisMethodHasAPrettyLongName() {
		// 3. Invoke method with parameters,
		// using Ctrl+Space to get information on its arguments.
		// aMethodWithParameters(first, second, last);
		// 4. Generate print statement,
		// using template "sysout", followed by code completion (Ctrl+Space).

	}

	// 5. Define main method,
	// using template "main", followed by code completion.
	public static void main(String[] args) {
		System.out.println("Only testing");

	}

	// 6. Check predefined templates in Preferences/Java/Editor/Templates.
	// Examine definition of templates 'main' and 'new'.
	// Apply the template 'new' to create a new object of class Date.

	// 7. Add task "Improve implementation!"
	// by clicking in the left border on the following line.

	// 8. Add an abstract method "abstract void f();"
	// Use autocorrection to remove error.

}