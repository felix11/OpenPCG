/**
 * 
 */
package worldgenerator.util.grid;

/**
 * An abstract version of a grid cell.
 * @author Felix Dietrich
 *
 */
public abstract class GridCell<T>
{
	protected T data;
	
	public GridCell(T data)
	{
		this.data = data;
	}
	
	@Override
	protected abstract GridCell<T> clone();

	public abstract GridCell<T> mult(GridCell<T> toMult);
	public abstract GridCell<T> add(GridCell<T> toAdd);

	public T getData()
	{
		return data;
	}
}
