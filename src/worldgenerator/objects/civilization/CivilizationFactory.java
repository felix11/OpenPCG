/**
 * 
 */
package worldgenerator.objects.civilization;

import java.util.Collection;

import worldgenerator.objects.civilization.CityFactory.CityAttributes;
import worldgenerator.objects.civilization.RoadFactory.RoadAttributes;
import worldgenerator.objects.terrain.Terrain;
import worldgenerator.util.factory.IWorldObjectFactory;

/**
 * @author Felix Dietrich
 */
public class CivilizationFactory implements IWorldObjectFactory<Civilization>
{

	public static Civilization create(Terrain terrain, CityAttributes cAttributes, RoadAttributes rAttributes)
	{
		// create cities
		Collection<City> cities = CityFactory.create(terrain, cAttributes);
		
		// create road network
		Collection<Road> roads = RoadFactory.createRoadNetwork(cities, terrain.getHeightMap(0), rAttributes);
		
		return new Civilization(cities, roads);
	}

}
