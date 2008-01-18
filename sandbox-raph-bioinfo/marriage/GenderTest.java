package marriage;

import static org.junit.Assert.*;

import org.junit.Test;

public class GenderTest {

	@Test
	public void toStringMALE() {
		assertEquals("MALE", Gender.MALE.toString());
	}

	@Test
	public void toStringFEMALE() {
		assertEquals("FEMALE", Gender.FEMALE.toString());
	}
}
