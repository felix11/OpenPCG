/**
 * 
 */
package worldgenerator.geometry.terrain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import worldgenerator.geometry.WorldObject;
import worldgenerator.geometry.resource.Resource;
import worldgenerator.geometry.resource.Resources;
import worldgenerator.geometry.river.River;
import worldgenerator.util.grid.Grid2D;

/**
 * @author Felix Dietrich
 */
public class Terrain extends WorldObject
{
	private final Grid2D<Double> heights;
	private final Grid2D<Integer> watersheds;
	private final Grid2D<Double> soilQuality;
	private final Map<Resources, Grid2D<Double>> resources;
	private final Collection<River> rivers;
	private Map<Integer, Grid2D<Double>> forests;
	
	public Terrain(Grid2D<Double> heightmap, Map<Resources, Grid2D<Double>> resources, Grid2D<Integer> watersheds, Collection<River> rivers, Grid2D<Double> soilQuality, Map<Integer, Grid2D<Double>> forests) {
		this.heights = heightmap;
		this.rivers = rivers;
		this.watersheds = watersheds;
		this.soilQuality = soilQuality;
		this.resources = resources;
		this.forests = forests;
	}

	public Collection<River> getRivers() {
		return Collections.unmodifiableCollection(rivers);
	}

	public Grid2D<Double> getResourceMap(Resources gold) {
		if(resources.containsKey(gold))
		{
			return resources.get(gold);
		}
		else
		{
			return null;
		}
	}
	
	public Grid2D<Double> getHeightMap()
	{
		return this.heights;
	}
	
	public Grid2D<Integer> getWatershedMap()
	{
		return this.watersheds;
	}
	
	public Grid2D<Double> getSoilQualityMap()
	{
		return this.soilQuality;
	}

	public Grid2D<Double> getForestMap(int forestType)
	{
		return this.forests.get(forestType);
	}
}
