/**
 * 
 */
package worldgenerator.objects.civilization;

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
 * A subdivision algorithm for population density maps.
 * Uses a perlin noise variant plus a modified diamond square algorithm.
 * 
 * @author Felix Dietrich
 *
 */
public class PopulationDensitySubdivisionAlgorithm implements ISubdivisionAlgorithm<Double>
{
	@Override
	public Grid2DLayer<Double> createNewLayer(final int seed, final Grid2DLayer<Double> oldLayer, final int subdivisionsPerLevel)
	{
		int newrows = subdivisionsPerLevel * oldLayer.rows();
		int newcols = subdivisionsPerLevel * oldLayer.cols();
		Grid2DLayer<Double> result = new Grid2DLayer<Double>(newrows, newcols, oldLayer.getBaseGrid());
		
		// this grid stores maximum and minimum values to rescale the layer later to the max and min values of the old layer
		final Grid2D<Double> maxmin = new ComparableGrid2D<Double>(1,2, new GridCellDouble(0.0));
		final int MAX_INDEX = 0;
		final int MIN_INDEX = 1;
		
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
						grid2d.setDataAt(row, col, newVal);
						
						// mean value
						int meanKernelSize = 5;
						double valMean = oldLayerGrid.sumDataOverArea(oldRow, oldCol, meanKernelSize).getData();
						valMean /= meanKernelSize * meanKernelSize;
						
						newVal = valMean;
						
						// store height value in the perlin map to make pertubations lateron
						perlinMap.Heights[row][col] = (float)newVal;
					}
				});
				
				// pertube based on height
				perlinMap.Perturb2(8.0f, 2.0f, 8.0f);
				perlinMap.Smoothen();
				
				// copy the data back to the grid, cube it in the process
				currentLayerGridCell.getData().iterate(new Grid2DIterator<Double>()
				{
					@Override
					public void step(int row, int col, GridCell<Double> gridCell, Grid2D<Double> grid2d)
					{
						double val = (double)perlinMap.Heights[row][col];
						val = Math.max(0.0, (val*2.0 + 1.0*gridCell.getData()) / 3.0);
						grid2d.setDataAt(row, col, val);
						
						// store maximum and minimum values for rescaling later
						if(val < maxmin.getDataAt(0, MIN_INDEX).getData())
						{
							maxmin.setDataAt(0, MIN_INDEX, val);
						}
						if(val > maxmin.getDataAt(0, MAX_INDEX).getData())
						{
							maxmin.setDataAt(0, MAX_INDEX, val);
						}
					}
				});
			}
		});
		/*
		// compute rescaling factors
		final GridCell<Double> newMinimumInverted = new GridCellDouble(-maxmin.getDataAt(0, MIN_INDEX).getData()); // 
		final GridCell<Double> oldMaximum = new GridCellDouble(getMaximum(oldLayer));
		final GridCell<Double> oldMinimum = new GridCellDouble(getMinimum(oldLayer));
		double diffOld = (oldMaximum.getData()-oldMinimum.getData());
		double diffNew = (maxmin.getDataAt(0, MAX_INDEX).getData() - maxmin.getDataAt(0, MIN_INDEX).getData());
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
			}
		});*/
		
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
