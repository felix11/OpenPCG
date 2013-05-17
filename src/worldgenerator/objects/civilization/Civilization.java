/**
 * 
 */
package worldgenerator.objects.civilization;

import java.util.Collection;

import worldgenerator.util.factory.WorldObject;

/**
 * A civilization holds cities, villages, roads etc.
 * 
 * @author Felix Dietrich
 *
 */
public class Civilization extends WorldObject
{
	private final Collection<City> cities;
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
	public Civilization(Collection<City> cities, Collection<Road> roads)
	{
		this.cities = cities;
		this.roads = roads;
	}
}
