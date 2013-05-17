package worldgenerator.test.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import worldgenerator.io.TerrainPlotter;
import worldgenerator.objects.forest.ForestFactory.ForestAttributes;
import worldgenerator.objects.forest.ForestFactory.ForestLevels;
import worldgenerator.objects.terrain.Terrain;
import worldgenerator.objects.terrain.TerrainFactory;
import worldgenerator.util.grid.GridFactory.GridAttributes;

public class TestTerrainPlotter {
	
	private static final int height = 256;
	private static final int width = 256;
	private static final int Nrivers = 0;
	private static final int seed = 0;
	private static TerrainPlotter plotter;
	private Terrain terrain;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTerrainPlotter() throws IOException {

		Map<Integer, ForestLevels> forestLevels = new HashMap<Integer, ForestLevels>();
		//forestLevels.put(1, new ForestLevels(0.0, 0.5));
		double fdensity = 0.0;
		ForestAttributes forestAttributes = new ForestAttributes(forestLevels , fdensity, seed);
		
		terrain = TerrainFactory.create(new GridAttributes(height, width, seed), forestAttributes);
		plotter = new TerrainPlotter(terrain);
		
		String filename = "test_plot_terrain.txt";
		plotter.plot2file(filename);
	}

}
