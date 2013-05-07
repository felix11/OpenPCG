/**
 * 
 */
package worldgenerator.util.grid;

/**
 * @author Felix Dietrich
 *
 */
public interface ICellularAutomata
{
	/**
	 * Progresses the automata one step, applying the automata function on each cell.
	 */
	public void step();
}
