/**
 * 
 */
package worldgenerator.util.grid;

/**
 * @author Felix Dietrich
 *
 */
public class GridCellGrid2D<T> extends GridCell<Grid2D<T>>
{

	public GridCellGrid2D(Grid2D<T> data)
	{
		super(data);
	}

	@Override
	protected GridCell<Grid2D<T>> clone()
	{
		return new GridCellGrid2D<T>(this.data.clone());
	}

	@Override
	public GridCell<Grid2D<T>> mult(GridCell<Grid2D<T>> toMult)
	{
		throw new IllegalAccessError("mult is not supported"); 
		//return this;
	}

	@Override
	public GridCell<Grid2D<T>> add(GridCell<Grid2D<T>> toAdd)
	{
		throw new IllegalAccessError("mult is not supported");
		//return this;
	}

}
