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
	private Map<Integer, Grid2D<T>> grids;
	private int subdivisionsPerLevel;
	private int maxLevel;
	
	/**
	 * Creates a MipMapGrid2D with given dimensions, default value and number of levels.
	 * @param height height of the smallest mipmap
	 * @param width width of the smallest mipmap
	 * @param fillTemplate
	 * @param subdivisionsPerLevel number of subdivisions per new level. each subdivision creates subdivisionsPerLevel*2 subdivisions (given per side, hence *2) of the largest stored map. must be a power of 2.
	 */
	public MipMapGrid2D(int height, int width, GridCell<T> fillTemplate, int subdivisionsPerLevel)
	{
		// creates the basis level of this Grid2D.
		super(height, width, fillTemplate);
		
		if(subdivisionsPerLevel < 0)
			throw new IllegalArgumentException("subdivisions per level must be >= 0.");
		
		double divlog = Math.log(subdivisionsPerLevel) / Math.log(2);
		if(Math.floor(divlog) != divlog )
			throw new IllegalArgumentException("subdivisions must be a power of 2");
		
		this.subdivisionsPerLevel = subdivisionsPerLevel;
		// the maximum level of detail is zero, i.e. only the base layer is present at the moment.
		this.maxLevel = 0;
		// put the base level also in the grids
		this.grids = new HashMap<Integer, Grid2D<T>>();
		this.grids.put(0, this);
	}
	
	public MipMapGrid2D(Grid2D<T> basemap, int subdivisionsPerLevel)
	{
		this(basemap.rows(), basemap.cols(), basemap.fillTemplate, subdivisionsPerLevel);
		this.data = basemap.clone().data;
		this.setLayer(0, basemap);
	}

	public void setDataAtLayer(int row, int col, int layer, GridCell<T> data)
	{
		if(layer > maxLevel || layer < 0)
			throw new IllegalArgumentException("layer " + layer + "is not supported by this mipmap grid.");
		grids.get(layer).setDataAt(row, col, data);
	}
	
	public GridCell<T> getDataAtLayer(int row, int col, int layer)
	{
		if(layer > maxLevel || layer < 0)
			throw new IllegalArgumentException("layer " + layer + "is not supported by this mipmap grid.");
		return grids.get(layer).getDataAt(row, col);
	}
	
	/**
	 * Creates a number of subdivisions of the base level, up to newMaxLevel.
	 * Each time the basis is subdivided n times, where n was given in the constructor as subdivisionsPerLevel.
	 * If the basis was subdivided before, only the levels that were not created yet are created.
	 * @param newMaxLevel
	 * @param subdivider the algorithm by which a layer should be subdivided.
	 */
	public void subdivide(int newMaxLevel, ISubdivisionAlgorithm<T> subdivider)
	{
		for(int level = maxLevel+1; level <= newMaxLevel; level++)
		{
			Grid2D<T> lastLayer = grids.get(level-1);
			Grid2D<T> newLayer = subdivider.createNewLayer(lastLayer, subdivisionsPerLevel);
			grids.put(level, newLayer);
		}
		maxLevel = newMaxLevel;
	}

	private void setLayer(Integer level, Grid2D<T> value)
	{
		this.grids.put(level, value);
	}

	public Grid2D<T> getLayer(int layer)
	{
		return grids.get(layer);
	}

	@Override
	protected MipMapGrid2D<T> clone()
	{
		MipMapGrid2D<T> result = new MipMapGrid2D<T>(this.rows(), this.cols(), fillTemplate, subdivisionsPerLevel);
		
		// clone the basis grid
		Grid2D<T> basis = super.clone();
		result.data = basis.data;
		
		// clone the subdivisions
		for(Entry<Integer, Grid2D<T>> subdivision : grids.entrySet())
		{
			result.setLayer(subdivision.getKey(), subdivision.getValue().clone());
		}
		
		return result;
	}
}
