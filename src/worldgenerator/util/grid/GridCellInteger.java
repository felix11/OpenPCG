/**
 * 
 */
package worldgenerator.util.grid;

/**
 * @author Felix Dietrich
 *
 */
public class GridCellInteger extends GridCell<Integer>
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
	protected GridCell<Integer> clone()
	{
		return new GridCellInteger(data);
	}

	@Override
	public GridCell<Integer> mult(GridCell<Integer> toMult)
	{
		return new GridCellInteger(this.data * toMult.data);
	}

	@Override
	public GridCell<Integer> add(GridCell<Integer> toAdd)
	{
		return new GridCellInteger(this.data + toAdd.data);
	}

}
