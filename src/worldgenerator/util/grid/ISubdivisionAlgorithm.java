package worldgenerator.util.grid;

/**
 * A generic algorithm to create a new subdivision layer of a given layer.
 * @author Felix Dietrich
 *
 */
public interface ISubdivisionAlgorithm<U extends Comparable<U>>
{
	public Grid2D<U> createNewLayer(final Grid2D<U> oldLayer, final int subdivisionsPerLevel);
}