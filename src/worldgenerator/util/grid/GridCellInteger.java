/**
 * 
 */
package worldgenerator.util.grid;

/**
 * @author Felix Dietrich
 *
 */
public class GridCellInteger extends GridCellComparable<Integer>
{

	public GridCellInteger(Integer data)
	{
		super(data);
	}

	/* (non-Javadoc)
	 * @see worldgenerator.util.grid.GridCell#toString()
	 */
	@Override
	public String toString()
	{
		return this.data.toString();
	}

	/* (non-Javadoc)
	 * @see worldgenerator.util.grid.GridCell#clone()
	 */
	@Override
	protected GridCellComparable<Integer> clone()
	{
		return new GridCellInteger(data);
	}

	@Override
	public GridCellComparable<Integer> mult(GridCell<Integer> toMult)
	{
		return new GridCellInteger(this.data * toMult.data);
	}

	@Override
	public GridCellComparable<Integer> add(GridCell<Integer> toAdd)
	{
		return new GridCellInteger(this.data + toAdd.data);
	}

}
