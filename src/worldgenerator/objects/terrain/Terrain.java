/**
 * 
 */
package worldgenerator.objects.terrain;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

import worldgenerator.objects.resource.Resources;
import worldgenerator.objects.river.River;
import worldgenerator.util.factory.WorldObject;
import worldgenerator.util.grid.ComparableGrid2D;
import worldgenerator.util.grid.Grid2D;
import worldgenerator.util.grid.Grid2D.Grid2DIterator;
import worldgenerator.util.grid.Grid2DLayer;
import worldgenerator.util.grid.GridCell;
import worldgenerator.util.grid.GridCellComparable;
import worldgenerator.util.grid.GridFactory;
import worldgenerator.util.grid.GridType;
import worldgenerator.util.grid.ISubdivisionAlgorithm;
import worldgenerator.util.grid.MipMapGrid2D;
import worldgenerator.util.grid.GridFactory.GridAttributes;
import worldgenerator.util.noise.PerlinNoiseMap;

/**
 * @author Felix Dietrich
 */
public class Terrain extends WorldObject
{
	private final MipMapGrid2D<Double> heights;
	private final ComparableGrid2D<Integer> watersheds;
	private final ComparableGrid2D<Double> soilQuality;
	private final Map<Resources, ComparableGrid2D<Double>> resources;
	private final Collection<River> rivers;
	private Map<Integer, ComparableGrid2D<Double>> forests;
	
	/**
	 * The subdivision algorithm that is used to subdivide height maps.
	 */
	private ISubdivisionAlgorithm<Double> heightSubdivisionAlgorithm = new HeightSubdivisionAlgorithm();
	
	public Terrain(ComparableGrid2D<Double> heightmap, Map<Resources, ComparableGrid2D<Double>> resources, ComparableGrid2D<Integer> watersheds, Collection<River> rivers, ComparableGrid2D<Double> soilQuality, Map<Integer, ComparableGrid2D<Double>> forests) {
		// 1 means only subdivide once per layer, i.e. no tesselation at all
		this.heights = new MipMapGrid2D<Double>(heightmap, 2);
		this.rivers = rivers;
		this.watersheds = watersheds;
		this.soilQuality = soilQuality;
		this.resources = resources;
		this.forests = forests;
	}

	public Collection<River> getRivers() {
		return Collections.unmodifiableCollection(rivers);
	}

	public ComparableGrid2D<Double> getResourceMap(Resources gold) {
		if(resources.containsKey(gold))
		{
			return resources.get(gold);
		}
		else
		{
			return null;
		}
	}
	
	public ComparableGrid2D<Double> getHeightMap(int layer, int row, int col)
	{
		return (ComparableGrid2D<Double>) this.heights.getLayer(layer).getDataAt(row, col).getData();
	}
	
	/**
	 * Get the heightmap at row=0, col=0.
	 * @param layer
	 * @return
	 */
	public ComparableGrid2D<Double> getHeightMap(int layer)
	{
		return getHeightMap(layer, 0, 0);
	}
	
	public Grid2DLayer<Double> getHeightmapLayer(int layer)
	{
		if(this.heights.getLayer(layer) == null)
			throw new IllegalArgumentException(String.format("layer %d does not exist.",layer));
		return this.heights.getLayer(layer);
	}
	
	public ComparableGrid2D<Integer> getWatershedMap()
	{
		return this.watersheds;
	}
	
	public ComparableGrid2D<Double> getSoilQualityMap()
	{
		return this.soilQuality;
	}

	public ComparableGrid2D<Double> getForestMap(int forestType)
	{
		return this.forests.get(forestType);
	}
	
	/**
	 * Greatly increases detail on all maps in this terrain while still storing the original maps for easier manipulations.
	 * @param newMaxLevel 
	 * @param seed2 
	 */
	public void tesselate(int newMaxLevel, final int seed)
	{
		this.heights.subdivide(seed, newMaxLevel, heightSubdivisionAlgorithm );
	}
}
