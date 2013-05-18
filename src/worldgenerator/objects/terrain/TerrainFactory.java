package worldgenerator.objects.terrain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import worldgenerator.objects.forest.ForestFactory;
import worldgenerator.objects.forest.ForestFactory.ForestAttributes;
import worldgenerator.objects.resource.ResourceType;
import worldgenerator.objects.river.River;
import worldgenerator.objects.river.RiverFactory;
import worldgenerator.objects.soil.SoilFactory;
import worldgenerator.util.factory.IWorldObjectFactory;
import worldgenerator.util.grid.ComparableGrid2D;
import worldgenerator.util.grid.GridCellDouble;
import worldgenerator.util.grid.GridFactory;
import worldgenerator.util.grid.GridType;
import worldgenerator.util.grid.GridFactory.GridAttributes;

public class TerrainFactory implements IWorldObjectFactory<Terrain>
{
	public static Terrain create(GridAttributes defaultAttributes, ForestAttributes forestAttributes)
	{
		// heightmap
		ComparableGrid2D<Double> heightmap = GridFactory.create2D(GridType.PERLIN_NOISE_2D, defaultAttributes);
		heightmap.add(new GridCellDouble(0.5));
		heightmap.mult(new GridCellDouble(1.0/1.5)); // rescale to [-1/3, 1.0]
		
		// resources
		Map<ResourceType, ComparableGrid2D<Double>> resources = new HashMap<ResourceType, ComparableGrid2D<Double>>();
		
		// Gold
		GridAttributes goldAttributes = new GridAttributes(defaultAttributes.height, defaultAttributes.width, defaultAttributes.seed+1);
		goldAttributes.factor = 0.1;
		ComparableGrid2D<Double> goldMap = GridFactory.create2D(GridType.SPARSE_PERLIN_NOISE_2D, goldAttributes);
		resources.put(ResourceType.GOLD, goldMap);
		
		// Coal
		GridAttributes coalAttributes = new GridAttributes(defaultAttributes.height, defaultAttributes.width, defaultAttributes.seed+2);
		coalAttributes.factor = 0.8;
		ComparableGrid2D<Double> coalMap = GridFactory.create2D(GridType.SPARSE_PERLIN_NOISE_2D, coalAttributes);
		resources.put(ResourceType.COAL, coalMap);
		
		// watershed and rivers
		GridAttributes waterAttributes = new GridAttributes(defaultAttributes.height, defaultAttributes.width, defaultAttributes.seed+3);
		ComparableGrid2D<Integer> watersheds = RiverFactory.createWatersheds(heightmap, waterAttributes);
		
		Collection<River> rivers = RiverFactory.createMultiple(heightmap, 0);
		
		// soil quality
		ComparableGrid2D<Double> soilQuality = SoilFactory.computeQualityMap(heightmap, watersheds, defaultAttributes);
		
		// forests
		Map<Integer, ComparableGrid2D<Double>> forests = ForestFactory.create(heightmap, soilQuality, forestAttributes);
		
		// create untesselated terrain
		Terrain result = new Terrain(heightmap, resources, watersheds, rivers, soilQuality, forests);
		
		// tesselate for greater detail
		result.tesselate(2, defaultAttributes.seed);
		
		return result;
	}
}
