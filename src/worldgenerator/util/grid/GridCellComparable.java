/**
 * 
 */
package worldgenerator.util.grid;

/**
 * A comparable version of a grid cell.
 * @author Felix Dietrich
 */
public abstract class GridCellComparable<T extends Comparable<T>> extends GridCell<T> implements Comparable<GridCellComparable<T>> {

	public GridCellComparable(T data)
	{
		super(data);
	}

	public int compareTo(GridCellComparable<T> newData)
	{
		return this.data.compareTo(newData.data);
	}
}
