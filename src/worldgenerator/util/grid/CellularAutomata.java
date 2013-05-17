/**
 * 
 */
package worldgenerator.util.grid;

/**
 * @author Felix Dietrich
 *
 */
public class CellularAutomata<T extends Comparable<T>> implements ICellularAutomata
{
	private ComparableGrid2D<T> grid;
	private int stepCounter;
	private CAStep<T> stepFunction;
	
	/**
	 * Represents a customizable step of a cellular automata.
	 * @author Felix Dietrich
	 *
	 * @param <R> data type of the grid cells used in the CA.
	 */
	public interface CAStep<R extends Comparable<R>>
	{
		public abstract ComparableGrid2D<R> work(ComparableGrid2D<R> grid);
	}
	
	/**
	 * Initializes the automata with a copy of the initial data.
	 * @param initialData
	 */
	public CellularAutomata(ComparableGrid2D<T> initialData, CAStep<T> step)
	{
		this.grid = initialData.clone();
		this.stepCounter = 0;
		this.stepFunction = step;
	}

	@Override
	public void step()
	{
		grid = stepFunction.work(grid);
		this.stepCounter++;
	}
	
	/**
	 * Progresses a given number of steps.
	 * @param steps
	 */
	public void step(int steps)
	{
		for(int i=0; i<steps; i++)
		{
			step();
		}
	}

	public ComparableGrid2D<T> result()
	{
		return grid;
	}

}
