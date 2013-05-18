/**
 * 
 */
package worldgenerator.objects.soil;

import worldgenerator.util.grid.ComparableGrid2D;
import worldgenerator.util.grid.GridFactory;
import worldgenerator.util.grid.GridType;
import worldgenerator.util.grid.GridFactory.GridAttributes;

/**
 * @author Felix Dietrich
 *
 */
public class SoilFactory
{
	/**
	 * Computes a soil quality map from given height and water data.
	 * @param heightmap
	 * @param watersheds
	 * @return
	 */
	public static ComparableGrid2D<Double> computeQualityMap(ComparableGrid2D<Double> heightmap, ComparableGrid2D<Integer> watersheds, GridAttributes attributes)
	{
		ComparableGrid2D<Double> qualityMap = GridFactory.create2D(GridType.DOUBLE_2D, attributes);
		
		for(int r=0; r < qualityMap.rows(); r++)
		{
			for(int c=0; c < qualityMap.cols(); c++)
			{
				double h = heightmap.getDataAt(r, c).getData();
				// TODO: change arbitrary formula for soil quality
				double val = Math.max(-Math.sqrt(Math.abs(h)) + 1, 0);
				qualityMap.setDataAt(r, c, val);
			}
		}
		
		return qualityMap;
	}
}
