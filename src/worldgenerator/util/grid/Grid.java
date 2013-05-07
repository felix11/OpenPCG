package worldgenerator.util.grid;

public abstract class Grid<T extends Comparable<T>> {
	
	protected GridCell<T> maximum;
	protected GridCell<T> minimum;

	public Grid() {
		// TODO Auto-generated constructor stub
	}

	public abstract void fill(GridCell<T> template);
	
	@Override
	protected abstract Grid2D<T> clone();

	public GridCell<T> getMaximum()
	{
		return this.maximum;
	}

	public GridCell<T> getMinimum()
	{
		return this.minimum;
	}

}
