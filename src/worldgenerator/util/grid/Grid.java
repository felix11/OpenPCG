package worldgenerator.util.grid;

public interface Grid<T> {
	public void fill(GridCell<T> template);
	public Grid<T> clone();
}
