/**
 * 
 */
package worldgenerator.geometry.forest;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import worldgenerator.util.grid.CellularAutomata;
import worldgenerator.util.grid.CellularAutomata.CAStep;
import worldgenerator.util.grid.Grid2D;
import worldgenerator.util.grid.Grid2D.Grid2DIterator;
import worldgenerator.util.grid.GridCell;
import worldgenerator.util.grid.GridCellDouble;
import worldgenerator.util.grid.GridFactory;
import worldgenerator.util.grid.GridFactory.GridAttributes;
import worldgenerator.util.grid.GridType;


/**
 * @author Felix Dietrich
 *
 */
public class ForestFactory
{
	public static class ForestLevels
	{
		private double density;
		public double lower;
		public double upper;
		
		public ForestLevels(double lower, double upper, double density)
		{
			this.lower = lower;
			this.upper = upper;
			this.density = density;
		}
		
	}

	public static class ForestAttributes
	{
		public final Map<Integer, ForestLevels> levels;
		public final int seed;
		public final double density;

		/**
		 * @param height
		 * @param width
		 * @param seed
		 */
		public ForestAttributes(Map<Integer, ForestLevels> maxPop, double density, int seed)
		{
			this.levels = maxPop;
			this.density = density;
			this.seed = seed;
		}

		public ForestAttributes(ForestAttributes copyAttributes)
		{
			this(copyAttributes.levels, copyAttributes.density, copyAttributes.seed);
		}
	}
	
	/**
	 * Create forest tree types based on heightmap, soilmap and given lower and upper levels.
	 * First, forest seeds are placed on separate Grid2D s separately.
	 * Second, the seeds are grown with a cellular automata.
	 * 
	 * @param heightmap
	 * @param soilmap
	 * @param attributes
	 * @return
	 */
	public static Map<Integer,Grid2D<Double>> create(final Grid2D<Double> heightmap, final Grid2D<Double> soilmap, final ForestAttributes attributes)
	{
		final Map<Integer,Grid2D<Double>> result = new HashMap<Integer,Grid2D<Double>>();
		final Random rand = new Random(attributes.seed);
		
		// generate a 2D grid for each level, initialised with 0.0
		for(Entry<Integer, ForestLevels> level : attributes.levels.entrySet())
		{
			GridAttributes gridAttributes = new GridAttributes(heightmap.rows(), heightmap.cols(), attributes.seed);
			result.put(level.getKey(), GridFactory.create2D(GridType.DOUBLE_2D, gridAttributes));
		}
		
		// place forest seeds for each level
		heightmap.iterate(new Grid2DIterator<Double>()
		{
			@Override
			public void step(int row, int col, GridCell<Double> gridCell, Grid2D<Double> localgrid)
			{
				double height = heightmap.getDataAt(row, col).getData();
				for(Entry<Integer, ForestLevels> level : attributes.levels.entrySet())
				{
					if(level.getValue().lower <= height && level.getValue().upper >= height)
					{
						Grid2D<Double> grid = result.get(level.getKey());

						// TODO: modify tree probability at this position, currently based on level.
						double heightprob = (height-level.getValue().lower)/(level.getValue().upper - level.getValue().lower);
						double probability = -Math.abs(heightprob-0.5)+1;
						
						// TODO: modify forest seeding probability.
						// currently, prob * overall density * level density
						if(rand.nextDouble() < probability * attributes.density * level.getValue().density)
						{
							grid.setDataAt(row, col, probability);
						}
					}
				}
			}
		});
		
		// grow forests for some steps
		for(Entry<Integer, ForestLevels> level : attributes.levels.entrySet())
		{
			Grid2D<Double> initialGrid = result.get(level.getKey());
			CellularAutomata<Double> automata = new CellularAutomata<Double>(initialGrid, new CAStep<Double>() {

				@Override
				public Grid2D<Double> work(Grid2D<Double> grid)
				{
					// iterate over the forest probabilities and grow
					grid.iterate(new Grid2DIterator<Double>()
					{
						private final int[][] dirs = new int[][]{ new int[]{-1,0},  new int[]{1,0},  new int[]{0,-1},  new int[]{0,1}};

						@Override
						public void step(int row, int col, GridCell<Double> gridCell, Grid2D<Double> localgrid)
						{
							if(gridCell.getData() > 0)
							{
								int dir = rand.nextInt(4);
								int newrow = row + dirs [dir][0];
								int newcol = col + dirs[dir][1];
								if(heightmap.getDataAt(newrow, newcol).getData() > 0)
								{
									localgrid.setDataAt(newrow, newcol, gridCell.getData());
								}
							}
						}
					});
					return grid;
				}
			});
			
			// some growing steps for the forest
			automata.step(3);
			// store the modified grid back in the result map
			result.put(level.getKey(), automata.result());
		}
		
		return result;
	}
}
