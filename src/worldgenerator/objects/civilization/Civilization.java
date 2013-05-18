/**
 * 
 */
package worldgenerator.objects.civilization;

import java.util.Collection;

import worldgenerator.objects.terrain.HeightSubdivisionAlgorithm;
import worldgenerator.util.factory.WorldObject;
import worldgenerator.util.grid.ComparableGrid2D;
import worldgenerator.util.grid.Grid2DLayer;
import worldgenerator.util.grid.MipMapGrid2D;

/**
 * A civilization holds cities, villages, roads etc.
 * 
 * @author Felix Dietrich
 *
 */
public class Civilization extends WorldObject
{
	private final Collection<City> cities;
	private final MipMapGrid2D<Double> populationDensity;
	private final Collection<Road> roads;
	
	/**
	 * @return the cities
	 */
	public Collection<City> getCities()
	{
		return cities;
	}
	
	/**
	 * @return the roads
	 */
	public Collection<Road> getRoads()
	{
		return roads;
	}
	
	/**
	 * @param cities
	 * @param villages
	 * @param populationDensity
	 * @param roads
	 */
	public Civilization(Collection<City> cities, ComparableGrid2D<Double> populationDensity, Collection<Road> roads)
	{
		this.cities = cities;
		this.populationDensity = new MipMapGrid2D<Double>(populationDensity, 2);
		this.roads = roads;
	}

	/**
	 * @return the population density map
	 */
	public Grid2DLayer<Double> getPopulationDensity(int layer)
	{
		return populationDensity.getLayer(layer);
	}
	
	public void tesselate(int newMaxLevel, final int seed)
	{
		this.populationDensity.subdivide(seed, newMaxLevel, new PopulationDensitySubdivisionAlgorithm());
	}
}
