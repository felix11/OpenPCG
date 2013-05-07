package worldgenerator.test.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import worldgenerator.util.noise.RandomIntervals;

public class TestRandomIntervals {
	
	private RandomIntervals randInts;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testRandomIntervals() {
		randInts = new RandomIntervals(1);
		
		assertEquals("Single random interval start is not 0.", 0, randInts.intervals[0][0], 1e-8);
		assertEquals("Single random interval end is not 1.", 1, randInts.intervals[0][1], 1e-8);
	}

	@Test
	public void testRandomIntervalsSum() {
		randInts = new RandomIntervals(2);

		double sum = 0;
		sum += randInts.intervals[0][1] - randInts.intervals[0][0];
		sum += randInts.intervals[1][1] - randInts.intervals[1][0];
		assertEquals("Sum of random intervals is not 1.", 1, sum, 1e-8);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRandomIntervalsException() {
		randInts = new RandomIntervals(0);
	}

	@Test
	public void testSize() {
		randInts = new RandomIntervals(2);
		assertEquals("Size is not 2.", 2, randInts.size());
	}

}
