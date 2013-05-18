/**
 * 
 */
package worldgenerator.util.grid;

/**
 * @author Felix Dietrich
 *
 */
public class ComparableGrid2D<T extends Comparable<T>> extends Grid2D<T> {

	protected GridCellComparable<T> maximum;
	protected GridCellComparable<T> minimum;

	public ComparableGrid2D(int rows, int cols, GridCell<T> fillTemplate)
	{
		super(rows, cols, fillTemplate);
		
		maximum = null;
		minimum = null;
	}

	public GridCellComparable<T> getMaximum()
	{
		return this.maximum;
	}

	public GridCellComparable<T> getMinimum()
	{
		return this.minimum;
	}
	
	protected void checkRange(GridCellComparable<T> newData)
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
	
	@Override
	public void setDataAt(int row, int col, GridCell<T> data)
	{
		super.setDataAt(row, col, data);
		checkRange((GridCellComparable<T>) data);
	}
	
	@Override
	public void setDataAt(int row, int col, T data)
	{
		super.setDataAt(row, col, data);
		if(!invalid(row, col))
		{
			checkRange((GridCellComparable<T>) this.data[row][col]);
		}
	}
	
	@Override
	public void add(Grid2D<T> grid)
	{
		maximum = null;
		minimum = null;
		super.add(grid);
	}
	
	@Override
	public void add(GridCell<T> cell)
	{
		maximum = null;
		minimum = null;
		super.add(cell);
	}
	
	@Override
	public void mult(Grid2D<T> scatter)
	{
		maximum = null;
		minimum = null;
		super.mult(scatter);
	}
	
	@Override
	public void mult(GridCell<T> cell)
	{
		maximum = null;
		minimum = null;
		super.mult(cell);
	}
	
	@Override
	public GridCellComparable<T> getDataAt(int row, int col)
	{
		return (GridCellComparable<T>) super.getDataAt(row, col);
	}

	public void clamp(T min, T max)
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
					minimum = getDataAt(r, c);
				}
				if(data.compareTo(max) > 0)
				{
					data = max;
					maximum = getDataAt(r, c);
				}
				// clamp between min and max
				setDataAt(r, c, data);
			}	
		}
	}

	@Override
	public ComparableGrid2D<T> clone()
	{
		ComparableGrid2D<T> copy = new ComparableGrid2D<T>(this.rows(), this.cols(), (GridCellComparable<T>) this.getDataAt(0, 0));
		for(int r=0; r<rows(); r++)
		{
			for(int c=0; c<cols(); c++)
			{
				copy.setDataAt(r,c,getDataAt(r, c).clone());
			}
		}
		
		return copy;
	}

}
