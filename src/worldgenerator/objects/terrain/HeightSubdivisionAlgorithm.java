/**
 * 
 */
package worldgenerator.objects.terrain;

import java.util.Random;

import worldgenerator.util.grid.ComparableGrid2D;
import worldgenerator.util.grid.Grid2D;
import worldgenerator.util.grid.Grid2DLayer;
import worldgenerator.util.grid.GridCell;
import worldgenerator.util.grid.GridCellDouble;
import worldgenerator.util.grid.ISubdivisionAlgorithm;
import worldgenerator.util.grid.Grid2D.Grid2DIterator;
import worldgenerator.util.noise.PerlinNoiseMap;

/**
 * A subdivision algorithm for heightmaps.
 * Uses a perlin noise variant plus a modified diamond square algorithm.
 * 
 * @author Felix Dietrich
 *
 */
public class HeightSubdivisionAlgorithm implements ISubdivisionAlgorithm<Double>
{
	@Override
	public Grid2DLayer<Double> createNewLayer(final int seed, final Grid2DLayer<Double> oldLayer, final int subdivisionsPerLevel)
	{
		int newrows = subdivisionsPerLevel * oldLayer.rows();
		int newcols = subdivisionsPerLevel * oldLayer.cols();
		Grid2DLayer<Double> result = new Grid2DLayer<Double>(newrows, newcols, oldLayer.getBaseGrid());
		
		// this grid stores maximum and minimum values to rescale the layer later to the max and min values of the old layer
		final Grid2D<Double> maxmin = new ComparableGrid2D<Double>(1,2, new GridCellDouble(0.0));
		final int MAXIMUM = 0;
		final int MINIMUM = 1;
		
		// create random object for diamond square algorithm
		final Random rand = new Random(seed);
		
		// iterate over all grids in the layer, compute new values in the grid
		result.iterate(new Grid2DIterator<Grid2D<Double>>()
		{
			@Override
			public void step(final int layerRow, final int layerCol, final GridCell<Grid2D<Double>> currentLayerGridCell, Grid2D<Grid2D<Double>> grid2d)
			{
				// for each given grid, iterate over the values and compute new values by tesselation of the old values.
				// pertube by perlin noise dependent on individual height values so that mountains are pertubed higher than land close to water.
				final PerlinNoiseMap perlinMap = new PerlinNoiseMap(currentLayerGridCell.getData().rows(), seed);
				// select the correct grid from the upper layer
				final int oldLayerRow = layerRow / subdivisionsPerLevel;
				final int oldLayerCol = layerCol / subdivisionsPerLevel;
				final Grid2D<Double> oldLayerGrid = oldLayer.getDataAt(oldLayerRow, oldLayerCol).getData();
				
				currentLayerGridCell.getData().iterate(new Grid2DIterator<Double>()
				{
					private final int[][] dirs = new int[][]{
							new int[]{-1,-1}, new int[]{-1,0}, new int[]{-1,1}, 
							new int[]{0,-1}, new int[]{0,0}, new int[]{0,1},
							new int[]{1,-1},  new int[]{1,0},  new int[]{1,1}};
					
					@Override
					public void step(int row, int col, GridCell<Double> gridCell, Grid2D<Double> grid2d)
					{
						// select the correct data value of the grid in the upper layer
						int oldRow = row / subdivisionsPerLevel + (layerRow % subdivisionsPerLevel) * oldLayerGrid.rows() / subdivisionsPerLevel;
						int oldCol = col / subdivisionsPerLevel + (layerCol % subdivisionsPerLevel) * oldLayerGrid.cols() / subdivisionsPerLevel;
						double newVal = oldLayerGrid.getDataAt(oldRow, oldCol).getData();
						
						// diamond square algorithm
						int dir = rand.nextInt(dirs.length);
						int newrow = oldRow + dirs[dir][0];
						int newcol = oldCol + dirs[dir][1];
						
						if(!oldLayerGrid.invalid(newrow, newcol))
							newVal = (5*newVal + oldLayerGrid.getDataAt(newrow, newcol).getData()) / 6.0;
						
						// store height value in the perlin map to make pertubations lateron
						perlinMap.Heights[row][col] = (float)newVal;
						grid2d.setDataAt(row, col, newVal);
					}
				});
				
				// pertube based on height
				perlinMap.Perturb2(8.0f, 16.0f, 32.0f);
				perlinMap.Smoothen();
				perlinMap.Erode2(5.0f, 3.0f);
				// copy the data back to the grid, cube it in the process
				currentLayerGridCell.getData().iterate(new Grid2DIterator<Double>()
				{
					@Override
					public void step(int row, int col, GridCell<Double> gridCell, Grid2D<Double> grid2d)
					{
						double val = (double)(perlinMap.Heights[row][col]);
						double power = 2.0; // cube the data so that coastal areas are flattened, mountains are steepened.
						val = Math.signum(val) * Math.pow(val, power);
						val = (val + 2.0*grid2d.getDataAt(row, col).getData()) / 3.0;
						grid2d.setDataAt(row, col, val);
						
						// store maximum and minimum values for rescaling later
						if(val < maxmin.getDataAt(0, MINIMUM).getData())
						{
							maxmin.setDataAt(0, MINIMUM, val);
						}
						if(val > maxmin.getDataAt(0, MAXIMUM).getData())
						{
							maxmin.setDataAt(0, MAXIMUM, val);
						}
					}
				});
			}
		});
		
		// compute rescaling factors
		final GridCell<Double> newMinimumInverted = new GridCellDouble(-maxmin.getDataAt(0, MINIMUM).getData()); // 
		final GridCell<Double> oldMinimum = new GridCellDouble(getMinimum(oldLayer));
		double diffOld = (getMaximum(oldLayer)-oldMinimum.getData());
		double diffNew = (maxmin.getDataAt(0, MAXIMUM).getData() - maxmin.getDataAt(0, MINIMUM).getData());
		final GridCellDouble rescaleFactor = new GridCellDouble( diffOld / diffNew );
		
		// rescale the new grids to oldLayer.maximum and .minimum
		result.iterate(new Grid2DIterator<Grid2D<Double>>()
		{
			@Override
			public void step(int row, int col, GridCell<Grid2D<Double>> gridCell, Grid2D<Grid2D<Double>> grid2d)
			{
				// move to zero base
				gridCell.getData().add(newMinimumInverted);
				// scale to new max/min
				gridCell.getData().mult(rescaleFactor);
				// move to new minimum
				gridCell.getData().add(oldMinimum);
			}
		});
		
		return result;
	}

	private Double getMaximum(Grid2DLayer<Double> oldLayer)
	{
		double result = Double.MIN_VALUE;
		
		for(int row=0; row < oldLayer.rows(); row++)
		{
			for(int col=0; col < oldLayer.cols(); col++)
			{
				GridCell<Double> max = ((ComparableGrid2D<Double>)oldLayer.getDataAt(row, col).getData()).getMaximum();
				if(result < max.getData())
				{
					result = max.getData();
				}
			}
		}
		
		return result;
	}

	private Double getMinimum(Grid2DLayer<Double> oldLayer)
	{
		double result = Double.MAX_VALUE;
		
		for(int row=0; row < oldLayer.rows(); row++)
		{
			for(int col=0; col < oldLayer.cols(); col++)
			{
				GridCell<Double> min = ((ComparableGrid2D<Double>)oldLayer.getDataAt(row, col).getData()).getMinimum();
				if(result > min.getData())
				{
					result = min.getData();
				}
			}
		}
		
		return result;
	}

}
