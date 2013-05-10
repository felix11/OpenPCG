/**
 * 
 */
package worldgenerator.geometry.terrain;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

import worldgenerator.geometry.WorldObject;
import worldgenerator.geometry.resource.Resources;
import worldgenerator.geometry.river.River;
import worldgenerator.util.grid.Grid2D;
import worldgenerator.util.grid.Grid2D.Grid2DIterator;
import worldgenerator.util.grid.GridCell;
import worldgenerator.util.grid.GridFactory;
import worldgenerator.util.grid.GridType;
import worldgenerator.util.grid.ISubdivisionAlgorithm;
import worldgenerator.util.grid.MipMapGrid2D;
import worldgenerator.util.grid.GridFactory.GridAttributes;

/**
 * @author Felix Dietrich
 */
public class Terrain extends WorldObject
{
	private final MipMapGrid2D<Double> heights;
	private final Grid2D<Integer> watersheds;
	private final Grid2D<Double> soilQuality;
	private final Map<Resources, Grid2D<Double>> resources;
	private final Collection<River> rivers;
	private Map<Integer, Grid2D<Double>> forests;
	
	public Terrain(Grid2D<Double> heightmap, Map<Resources, Grid2D<Double>> resources, Grid2D<Integer> watersheds, Collection<River> rivers, Grid2D<Double> soilQuality, Map<Integer, Grid2D<Double>> forests) {
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
	
	public Grid2D<Double> getHeightMap(int layer)
	{
		return this.heights.getLayer(layer);
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

	/**
	 * Greatly increases detail on all maps in this terrain while still storing the original maps for easier manipulations.
	 * @param newMaxLevel 
	 */
	public void tesselate(int newMaxLevel, final int seed)
	{
		this.heights.subdivide(newMaxLevel, new ISubdivisionAlgorithm<Double>()
		{
			@Override
			public Grid2D<Double> createNewLayer(final Grid2D<Double> oldLayer, final int subdivisionsPerLevel)
			{
				GridAttributes attributes = new GridAttributes(oldLayer.rows() * subdivisionsPerLevel, oldLayer.cols() * subdivisionsPerLevel, seed);
				Grid2D<Double> result = GridFactory.create2D(GridType.DOUBLE_2D, attributes);
				
				// create random object for diamond square algorithm
				final Random rand = new Random(seed);
				
				// store the old layer in the image, scale it afterwards
				result.iterate(new Grid2DIterator<Double>()
				{
					private final int[][] dirs = new int[][]{
									new int[]{-1,-1}, new int[]{-1,0}, new int[]{-1,1}, 
									new int[]{0,-1},				   new int[]{0,1},
									new int[]{1,-1},  new int[]{1,0},  new int[]{1,1}};
					
					@Override
					public void step(int row, int col, GridCell<Double> gridCell, Grid2D<Double> grid2d)
					{
						int oldRow = row / subdivisionsPerLevel;
						int oldCol = col / subdivisionsPerLevel;
						double newVal = oldLayer.getDataAt(oldRow, oldCol).getData();
						
						// diamond square algorithm
						int dir = rand.nextInt(dirs.length);
						int newrow = oldRow + dirs [dir][0];
						int newcol = oldCol + dirs[dir][1];
						
						if(!oldLayer.invalid(newrow, newcol))
							newVal = (newVal*1 + oldLayer.getDataAt(newrow, newcol).getData()) / 2.0;
						
						grid2d.setDataAt(row, col, newVal);
					}
				});
				
				return result;
			}
		});
	}
}
