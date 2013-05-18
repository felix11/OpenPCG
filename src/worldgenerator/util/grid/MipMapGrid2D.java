/**
 * 
 */
package worldgenerator.util.grid;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a 2D grid with several subdivisions.
 * This way, a general map can be represented together with a multiple of subdivisions, that can be used as a level of detail.
 * 
 * @author Felix Dietrich
 *
 */
public class MipMapGrid2D<T extends Comparable<T>> extends Grid2D<T>
{
	private int subdivisionsPerLevel;
	private int maxLevel;
	private Map<Integer, Grid2DLayer<T>> grids;
	
	/**
	 * Creates a MipMapGrid2D with given dimensions, default value and number of levels.
	 * @param height height of the smallest mipmap
	 * @param width width of the smallest mipmap
	 * @param fillTemplate
	 * @param subdivisionsPerLevel number of subdivisions per new level. each subdivision creates subdivisionsPerLevel^2 subdivisions (given per side, hence ^2) of the larger stored map.
	 */
	public MipMapGrid2D(int height, int width, GridCell<T> fillTemplate, int subdivisionsPerLevel)
	{
		// creates the basis level of this Grid2D.
		super(height, width, fillTemplate);
		
		if(subdivisionsPerLevel < 0)
			throw new IllegalArgumentException("subdivisions per level must be >= 0.");
		
		//double divlog = Math.log(subdivisionsPerLevel) / Math.log(2);
		//if(Math.floor(divlog) != divlog )
		//	throw new IllegalArgumentException("subdivisions must be a power of 2");
		
		this.subdivisionsPerLevel = subdivisionsPerLevel;
		// the maximum level of detail is zero, i.e. only the base layer is present at the moment.
		this.maxLevel = 0;
		
		// put the base level in the grids
		grids = new HashMap<Integer, Grid2DLayer<T>>();
		grids.put(maxLevel, new Grid2DLayer<T>(new ComparableGrid2D<T>(height, width, fillTemplate)));
	}
	
	public MipMapGrid2D(ComparableGrid2D<T> basemap, int subdivisionsPerLevel)
	{
		this(basemap.rows(), basemap.cols(), basemap.fillTemplate, subdivisionsPerLevel);
		this.data = basemap.clone().data;
		this.setLayer(0, new Grid2DLayer<T>(basemap));
	}
	
	/**
	 * Creates a number of subdivisions of the base level, up to newMaxLevel.
	 * Each time the basis is subdivided n times, where n was given in the constructor as subdivisionsPerLevel.
	 * If the basis was subdivided before, only the levels that were not created yet are created.
	 * @param newMaxLevel
	 * @param newMaxLevel 
	 * @param subdivider the algorithm by which a layer should be subdivided.
	 */
	public void subdivide(int seed, int newMaxLevel, ISubdivisionAlgorithm<T> subdivider)
	{
		for(int level = maxLevel+1; level <= newMaxLevel; level++)
		{
			Grid2DLayer<T> lastLayer = grids.get(level-1);
			Grid2DLayer<T> newLayer = subdivider.createNewLayer(seed, lastLayer, subdivisionsPerLevel);
			grids.put(level, newLayer);
		}
		maxLevel = newMaxLevel;
	}

	private void setLayer(Integer level, Grid2DLayer<T> value)
	{
		this.grids.put(level, value);
	}

	public Grid2DLayer<T> getLayer(int layer)
	{
		return grids.get(layer);
	}

	@Override
	public MipMapGrid2D<T> clone()
	{
		MipMapGrid2D<T> result = new MipMapGrid2D<T>(this.rows(), this.cols(), fillTemplate, subdivisionsPerLevel);
		
		// clone the basis grid
		ComparableGrid2D<T> basis = getBasisGrid();
		result.data = basis.data;
		
		// clone the subdivisions
		for(Entry<Integer, Grid2DLayer<T>> subdivision : grids.entrySet())
		{
			result.setLayer(subdivision.getKey(), subdivision.getValue().clone());
		}
		
		return result;
	}

	private ComparableGrid2D<T> getBasisGrid()
	{
		return (ComparableGrid2D<T>) grids.get(0).getDataAt(0, 0).getData();
	}
}
