/**
 * 
 */
package worldgenerator.util.grid;

/**
 * @author Felix Dietrich
 * 
 */
public abstract class GridCell<T extends Comparable<T>> implements Comparable<GridCell<T>> {
	
	protected T data;
	
	public GridCell(T data)
	{
		this.data = data;
	}
	
	@Override
	public abstract String toString();
	
	@Override
	protected abstract GridCell<T> clone();

	public abstract GridCell<T> mult(GridCell<T> toMult);
	public abstract GridCell<T> add(GridCell<T> toAdd);

	public T getData()
	{
		return data;
	}

	public int compareTo(GridCell<T> newData)
	{
		return this.data.compareTo(newData.data);
	}
}
