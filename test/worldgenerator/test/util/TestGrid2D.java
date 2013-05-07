/**
 * 
 */
package worldgenerator.test.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import worldgenerator.util.grid.Grid2D;
import worldgenerator.util.grid.GridCellDouble;


/**
 * @author Felix Dietrich
 *
 */
public class TestGrid2D {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor1() {
		new Grid2D<Double>(-1, 1, new GridCellDouble(0.0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor2() {
		new Grid2D<Double>(1, -1, new GridCellDouble(0.0));
	}

	@Test
	public void testConstructor3() {
		Grid2D<Double> grid = new Grid2D<Double>(0, 0, new GridCellDouble(0.0));
		
		assertEquals("Rows does not not match.", 0, grid.rows());
		assertEquals("Cols does not not match.", 0, grid.cols());
		
		grid = new Grid2D<Double>(1, 2, new GridCellDouble(0.0));
		
		assertEquals("Rows does not not match.", 1, grid.rows());
		assertEquals("Cols does not not match.", 2, grid.cols());
	}
}
