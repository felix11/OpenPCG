package worldgenerator.util.grid;

/**
 * A generic algorithm to create a new subdivision layer of a given layer.
 * @author Felix Dietrich
 *
 */
public interface ISubdivisionAlgorithm<U extends Comparable<U>>
{
	public Grid2DLayer<U> createNewLayer(int seed, final Grid2DLayer<U> oldLayer, final int subdivisionsPerLevel);
}