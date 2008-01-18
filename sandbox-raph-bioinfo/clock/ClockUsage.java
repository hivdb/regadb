package clock;

/**
 * A class illustrating how to manipulate digital clocks by means of the methods
 * offered by their class.
 */
public class ClockUsage {

	/**
	 * Method illustrating how to apply methods to digital clocks.
	 */
	public static void main(String args[]) {
		// Initialize a new digital clock, referenced by the variable
		// myClock, and initialized to 7h35.
		DigitalClock myClock = new DigitalClock(7, 35);
		System.out.println("Expected time: 7h35");
		System.out.println("   " + myClock.getHours() + "h"
				+ myClock.getMinutes());
		// Initialize another new digital clock, referenced by the variable
		// yourClock, and initialized to 0h00.
		DigitalClock yourClock = new DigitalClock();
		System.out.println("Expected time: 0h00");
		System.out.println("   " + yourClock.getHours() + "h"
				+ yourClock.getMinutes());
		// Change the hours diplayed by myClock to 33.
		myClock.setHours(33);
		System.out.println("Expected time: 9h35");
		System.out.println("   " + myClock.getHours() + "h"
				+ myClock.getMinutes());
		// Introduce a local variable myMinutes, initialized with the
		// minutes displayed by myClock.
		int myMinutes = myClock.getMinutes();
		// Make the variable yourClock reference the digital clock referenced
		// by the variable myClock.
		yourClock = myClock;
		// Advance the minutes of the digital clock referenced by the
		// variable yourClock.
		yourClock.advanceMinutes();
		System.out.println("Expected time: 9h36");
		System.out.println("   " + myClock.getHours() + "h"
				+ myClock.getMinutes());
		System.out.println("Expected value: 35");
		System.out.println("   " + myMinutes);
	}

}
