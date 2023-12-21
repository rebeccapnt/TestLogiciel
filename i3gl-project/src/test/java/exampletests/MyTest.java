package exampletests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyTest {
	@BeforeEach
	void setUp() {

	}

	@Test
	void a_simple_unit_test() {
		int value = 2 + 2;

		assertEquals(4, value);
	}
}
