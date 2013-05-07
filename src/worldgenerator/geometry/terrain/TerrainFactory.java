package worldgenerator.geometry.terrain;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import worldgenerator.geometry.forest.ForestFactory;
import worldgenerator.geometry.forest.ForestFactory.ForestAttributes;
import worldgenerator.geometry.forest.ForestFactory.ForestLevels;
import worldgenerator.geometry.resource.Resource;
import worldgenerator.geometry.resource.Resources;
import worldgenerator.geometry.river.River;
import worldgenerator.geometry.river.RiverFactory;
import worldgenerator.geometry.soil.SoilFactory;
import worldgenerator.util.grid.Grid2D;
import worldgenerator.util.grid.GridCellDouble;
import worldgenerator.util.grid.GridFactory;
import worldgenerator.util.grid.GridType;
import worldgenerator.util.grid.GridFactory.GridAttributes;

public class TerrainFactory
{
	public static Terrain create(GridAttributes defaultAttributes, ForestAttributes forestAttributes)
	{
		// heightmap
		Grid2D<Double> heightmap = GridFactory.create2D(GridType.PERLIN_NOISE_2D, defaultAttributes);
		heightmap.add(new GridCellDouble(0.5));
		heightmap.mult(new GridCellDouble(1.0/1.5)); // rescale to [-1/3, 1.0]
		
		// resources
		Map<Resources, Grid2D<Double>> resources = new HashMap<Resources, Grid2D<Double>>();
		GridAttributes goldAttributes = new GridAttributes(defaultAttributes.height, defaultAttributes.width, defaultAttributes.seed);
		goldAttributes.factor = 0.5;
		Grid2D<Double> goldMap = GridFactory.create2D(GridType.SPARSE_PERLIN_NOISE_2D, goldAttributes);
		resources.put(Resources.GOLD, goldMap);
		
		// watershed and rivers
		GridAttributes waterAttributes = new GridAttributes(defaultAttributes.height, defaultAttributes.width, defaultAttributes.seed+2);
		Grid2D<Integer> watersheds = RiverFactory.createWatersheds(heightmap, waterAttributes);
		
		Collection<River> rivers = RiverFactory.createMultiple(heightmap, 0);
		
		// soil quality
		Grid2D<Double> soilQuality = SoilFactory.computeQualityMap(heightmap, watersheds, defaultAttributes);
		
		// forests
		Map<Integer, Grid2D<Double>> forests = ForestFactory.create(heightmap, soilQuality, forestAttributes);
		
		return new Terrain(heightmap, resources, watersheds, rivers, soilQuality, forests);
	}
}
