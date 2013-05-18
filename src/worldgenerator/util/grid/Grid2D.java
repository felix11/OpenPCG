/**
 * 
 */
package worldgenerator.util.grid;

import java.lang.reflect.Array;

/**
 * A generic two-dimensional grid.
 * @author Felix Dietrich
 *
 */
public abstract class Grid2D<T> implements Grid<T>
{
	/**
	 * Generic iterator for the grid values. Used in the {@link Grid2D#iterate(Grid2DIterator)} method.
	 * @author Felix Dietrich
	 *
	 */
	public interface Grid2DIterator<C>
	{
		public void step(int row, int col, GridCell<C> gridCell, Grid2D<C> grid2d);
	}
	
	protected GridCell<T>[][] data;
	protected GridCell<T> fillTemplate;

	@SuppressWarnings("unchecked")
	public Grid2D(int height, int width, GridCell<T> fillTemplate) {
		if(height < 0)
			throw new IllegalArgumentException("Height must not be smaller than zero.");
		if(width < 0)
			throw new IllegalArgumentException("Width must not be smaller than zero.");
		
		this.fillTemplate = fillTemplate;
		
		// generate empty grid
		data = (GridCell<T>[][]) Array.newInstance(fillTemplate.getClass(), height, width);
		fill(fillTemplate);
	}

	public GridCell<T>[] getRow(int index) {
		return data[index];
	}

	/**
	 * Sets the given grid cell at the given position.
	 * If the indices are out of range, nothing happens.
	 * @param row
	 * @param col
	 * @param data
	 */
	public void setDataAt(int row, int col, GridCell<T> data)
	{
		if(invalid(row,col))
			return;
		
		this.data[row][col] = data;
	}
	
	/**
	 * Sets the given data at the given position.
	 * If the indices are out of range, nothing happens.
	 * @param row
	 * @param col
	 * @param data
	 */
	public void setDataAt(int row, int col, T data)
	{
		if(invalid(row,col))
			return;
		
		this.data[row][col].data = data;
	}

	public GridCell<T> getDataAt(int row, int col)
	{
		return data[row][col];
	}

	@Override
	public void fill(GridCell<T> template) {
		for(int r=0; r<rows(); r++)
		{
			for(int c=0; c<cols(); c++)
			{
				setDataAt(r, c, template.clone());
			}	
		}
	}

	public void mult(Grid2D<T> scatter)
	{
		for(int r=0; r<rows(); r++)
		{
			for(int c=0; c<cols(); c++)
			{
				this.setDataAt(r,c,getDataAt(r, c).mult(scatter.getDataAt(r, c)));
			}
		}
	}

	public void mult(GridCell<T> cell)
	{
		for(int r=0; r<rows(); r++)
		{
			for(int c=0; c<cols(); c++)
			{
				this.setDataAt(r,c,getDataAt(r, c).mult(cell));
			}
		}
	}

	public void add(GridCell<T> cell)
	{
		for(int r=0; r<rows(); r++)
		{
			for(int c=0; c<cols(); c++)
			{
				this.setDataAt(r,c,getDataAt(r, c).add(cell));
			}
		}
	}

	public void add(Grid2D<T> grid)
	{
		for(int r=0; r<rows(); r++)
		{
			for(int c=0; c<cols(); c++)
			{
				this.setDataAt(r,c,getDataAt(r, c).add(grid.getDataAt(r, c)));
			}
		}
	}

	/**
	 * Sums grid cells over an area of averageSoilKernelSize*averageSoilKernelSize cells.
	 * If any invalid position is encountered, the center value is added instead. This demands that the center is valid.
	 * @param row
	 * @param col
	 * @param averageSoilKernelSize
	 * @return the sum of all cells in the given square.
	 */
	public GridCell<T> sumDataOverArea(int row, int col, int averageSoilKernelSize)
	{
		GridCell<T> result = getDataAt(row, col);
		for(int lrow = row-averageSoilKernelSize/2; lrow < row+averageSoilKernelSize/2; lrow++)
		{
			for(int lcol = col-averageSoilKernelSize/2; lcol < col+averageSoilKernelSize/2; lcol++)
			{
				// if the data is invalid, add the center
				if(invalid(lrow, lcol))
					result = result.add(getDataAt(row, col));
				else
					result = result.add(getDataAt(lrow, lcol));
			}
		}
		return result;
	}

	public int rows() {
		return data.length;
	}

	public int cols() {
		if(data.length > 0)
			return data[0].length;
		else
			return 0;
	}

	/**
	 * Iterates over all values of this grid, calling the {@link Grid2DIterator#step(int, int, Grid2D)} method for each cell.
	 * @param iterator
	 */
	public void iterate(Grid2DIterator<T> iterator)
	{
		for(int r=0; r<rows(); r++)
		{
			for(int c=0; c<cols(); c++)
			{
				iterator.step(r, c, this.getDataAt(r, c), this);
			}
		}
	}

	public boolean invalid(int row, int col)
	{
		return (row < 0 || row >= rows() || col < 0 || col >= cols());
	}
	
	@Override
	public abstract Grid2D<T> clone();
}
