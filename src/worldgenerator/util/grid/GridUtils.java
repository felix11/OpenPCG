/**
 * 
 */
package worldgenerator.util.grid;

import worldgenerator.util.grid.Grid2D.Grid2DIterator;

/**
 * @author Felix
 * 
 */
public class GridUtils
{
	/**
	 * From:
	 * http://stackoverflow.com/questions/10150739/gaussian-filter-without-
	 * using-convolveop
	 * 
	 * @param grid
	 * @param kernel_size
	 */
	public static void ApplyGaussianFilter(ComparableGrid2D<Double> grid, int kernel_size)
	{
		int kernel_rows = kernel_size;
		int kernel_cols = kernel_size;

		// define kernel here (double loop), these are the 1/16, 2/16, etc...
		// values that you're multiplying the image pixels by
		double[][] kernel = new double[kernel_rows][kernel_cols];
		double integral = 0.0;
		double sigmaRow = kernel_rows / 10.0;
		double sigmaCol = kernel_cols / 10.0;
		
		for(int krow = 0; krow < kernel_rows; krow++)
		{
			for(int kcol = 0; kcol < kernel_cols; kcol++)
			{
				double distSqr = (krow - kernel_rows / 2.0)*(krow - kernel_rows / 2.0)/sigmaRow/sigmaRow + (kcol - kernel_cols / 2.0)*(kcol - kernel_cols / 2.0)/sigmaCol/sigmaCol;
				double kernelVal = Math.exp(-0.5*distSqr); 
				kernel[krow][kcol] = kernelVal;
				integral += kernelVal;
			}
		}
		
		// scale down to integral = 1.0
		for(int krow = 0; krow < kernel_rows; krow++)
		{
			for(int kcol = 0; kcol < kernel_cols; kcol++)
			{
				kernel[krow][kcol] /= integral;
			}
		}
		
		ComparableGrid2D<Double> copy = grid.clone();

		// iterate over each pixel in the image
		for (int row = 0; row < grid.rows(); row++)
		{
			for (int col = 0; col < grid.cols(); col++)
			{
				double newdata = 0;

				// iterate over each pixel in the kernel
				for (int row_offset = 0; row_offset < kernel_rows; row_offset++)
				{
					for (int col_offset = 0; col_offset < kernel_cols; col_offset++)
					{
						// subtract by half the kernel size to center the kernel
						// on the pixel in question
						int row_index = row + row_offset - kernel_rows / 2;
						int col_index = col + col_offset - kernel_cols / 2;

						// check invalid positions and copy data from the grid copy so that we dont use already updated grid points
						if(!copy.invalid(row_index, col_index))
						{
							newdata += copy.getDataAt(row_index, col_index).getData() * kernel[row_offset][col_offset];
						}
					}
				}

				grid.setDataAt(row, col, newdata);
			}
		}
	}

	/**
	 * Creates a Double grid2d from a given Integer grid2d.
	 * @param intgrid
	 * @return
	 */
	public static ComparableGrid2D<Double> int2double(final ComparableGrid2D<Integer> intgrid)
	{
		ComparableGrid2D<Double> doublegrid = new ComparableGrid2D<Double>(intgrid.rows(), intgrid.cols(), new GridCellDouble((double)intgrid.fillTemplate.getData()));
		doublegrid.iterate(new Grid2DIterator<Double>()
		{

			@Override
			public void step(int row, int col, GridCell<Double> gridCell, Grid2D<Double> grid2d)
			{
				grid2d.setDataAt(row, col, (double)intgrid.getDataAt(row, col).getData());
			}
		});
		return doublegrid;
	}
}
