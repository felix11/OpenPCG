package worldgenerator.test.util;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import worldgenerator.io.GridPlotter2D;
import worldgenerator.util.grid.ComparableGrid2D;
import worldgenerator.util.grid.GridFactory;
import worldgenerator.util.grid.GridFactory.GridAttributes;
import worldgenerator.util.grid.GridType;

public class TestPerlinNoise3D {

	private static final int height = 256;
	private static final int width = 256;
	private static final int seed = 0;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testNoise() throws IOException {
		GridAttributes attributes = new GridAttributes(height, width, seed);
		ComparableGrid2D<Double> grid = GridFactory.create2D(GridType.PERLIN_NOISE_2D, attributes );
		
		GridPlotter2D plotter = new GridPlotter2D(grid);
		plotter.plot2file("perlin_noise_grid2d_test.txt");
	}

}
