/**
 * 
 */
package worldgenerator.util.grid;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Felix Dietrich
 *
 */
public class Grid2D<T extends Comparable<T>> extends Grid<T> {
	
	/**
	 * Generic iterator for the grid values. Used in the {@link Grid2D#iterate(Grid2DIterator)} method.
	 * @author Felix Dietrich
	 *
	 */
	public interface Grid2DIterator<C extends Comparable<C>>
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
		
		maximum = null;
		minimum = null;
		
		this.fillTemplate = fillTemplate;
		
		// generate empty grid
		data = (GridCell<T>[][]) Array.newInstance(fillTemplate.getClass(), height, width);
		fill(fillTemplate);
	}

	public GridCell<T>[] getRow(int index) {
		return data[index];
	}
	
	public void setDataAt(int row, int col, GridCell<T> data)
	{
		if(invalid(row,col))
			return;
		
		this.data[row][col] = data;
		checkRange(data);
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
		checkRange(this.data[row][col]);
	}
	
	private void checkRange(GridCell<T> newData)
	{
		if(maximum == null || maximum.compareTo(newData) <= 0)
		{
			maximum = newData;
		}
		if(minimum == null || minimum.compareTo(newData) >= 0)
		{
			minimum = newData;
		}
	}
	
	public boolean invalid(int row, int col)
	{
		return (row < 0 || row >= rows() || col < 0 || col >= cols());
	}

	public GridCell<T> getDataAt(int row, int col)
	{
		return data[row][col];
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

	public void clamp(Grid2D<T> result, T min, T max)
	{
		if(this.maximum == this.minimum)
			return;
		if(max.compareTo(min) < 0)
			throw new IllegalArgumentException("maximum should be greater than minimum when scaling Grid2D. max: " + max + ", min: " + min);
		
		for(int r=0; r<rows(); r++)
		{
			for(int c=0; c<cols(); c++)
			{
				// find data value
				T data = (getDataAt(r,c)).getData();
				// cut off values lower than the cutoff value
				if(data.compareTo(min) < 0)
				{
					data = min;
				}
				if(data.compareTo(max) > 0)
				{
					data = max;
				}
				// scale between 0 and 1
				setDataAt(r, c, data);
			}	
		}
	}

	/**
	 * Generates an ArrayList containing all elements in this grid.
	 * O(N) complexity, copies all element pointers in the array.
	 * @return an ArrayList containing pointers to all elements in the grid.
	 */
	public Collection<GridCell<T>> cells()
	{
		if(data == null)
			return new ArrayList<GridCell<T>>();
			
		Collection<GridCell<T>> result = new ArrayList<GridCell<T>>(data.length * data[0].length);

		for(int r=0; r<rows(); r++)
		{
			for(int c=0; c<cols(); c++)
			{
				result.add(getDataAt(r, c));
			}
		}
		
		return result;
	}

	@Override
	protected Grid2D<T> clone()
	{
		Grid2D<T> copy = new Grid2D<T>(this.rows(), this.cols(), this.getDataAt(0, 0));
		for(int r=0; r<rows(); r++)
		{
			for(int c=0; c<cols(); c++)
			{
				copy.setDataAt(r,c,getDataAt(r, c).clone());
			}
		}
		
		return copy;
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

}
