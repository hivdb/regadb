package marriage;

/**
 * A enumeration of genders. In its current definition, the class only
 * distinguishes between the male gender and the female gender.
 * 
 * @version 2.0
 * @author Eric Steegmans
 */
public enum Gender {

	MALE {

		/**
		 * Return a textual representation of the male gender.
		 * 
		 * @return The string "MALE" | result.equals("MALE")
		 */
		@Override
		public String toString() {
			return "MALE";
		}

	},
	FEMALE {

		/**
		 * Return a textual representation of the female gender.
		 * 
		 * @return The string "FEMALE" | result.equals("FEMALE")
		 */
		@Override
		public String toString() {
			return "FEMALE";
		}
	}
}