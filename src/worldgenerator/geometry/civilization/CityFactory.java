/**
 * 
 */
package worldgenerator.geometry.civilization;

import java.util.Collection;
import java.util.LinkedList;

import worldgenerator.geometry.Point3D;
import worldgenerator.geometry.terrain.Terrain;
import worldgenerator.util.grid.Grid2D;
import worldgenerator.util.grid.GridCellInteger;
import worldgenerator.util.grid.GridFactory.GridAttributes;
import worldgenerator.util.noise.RandomSource;

/**
 * @author Felix Dietrich
 *
 */
public class CityFactory
{
	public static class CityAttributes
	{
		public final int maxPopulation;
		public final int minPopulation;
		public final double idealDistance;
		public final double density;
		public final int seed;

		/**
		 * @param height
		 * @param width
		 * @param seed
		 */
		public CityAttributes(double density, int minPop, int maxPop, double idealDistance, int seed)
		{
			this.density = density;
			this.idealDistance = idealDistance;
			this.minPopulation = minPop;
			this.maxPopulation = maxPop;
			this.seed = seed;
		}

		public CityAttributes(CityAttributes copyAttributes)
		{
			this(copyAttributes.density, copyAttributes.minPopulation, copyAttributes.maxPopulation, copyAttributes.idealDistance, copyAttributes.seed);
		}
	}
	
	public static Collection<City> create(Terrain terrain, CityAttributes attributes)
	{
		Grid2D<Double> heightmap = terrain.getHeightMap();
		Grid2D<Double> soilQuality = terrain.getSoilQualityMap();
		Collection<City> cities = new LinkedList<City>();
		
		int ID = 0;
		
		// create cities, store soil quality at their position
		for(int row=0; row<heightmap.rows(); row++)
		{
			for(int col=0; col<heightmap.cols(); col++)
			{
				double rval = RandomSource.rand.nextDouble();
				double h = heightmap.getDataAt(row, col).getData();
				double soil = soilQuality.getDataAt(row, col).getData();
				if(h > 0 && soil * attributes.density > rval)
				{
					City newCity = new City(new GridCellInteger(ID), row, col, heightmap.getDataAt(row, col));
					newCity.setSoilQuality(soil);
					cities.add(newCity);
				}
			}
		}
		
		// link cities
		for(City c1 : cities)
		{
			Point3D c1pos = new Point3D(c1.getCol(), c1.getRow(), c1.getHeight().getData());
			for(City c2 : cities)
			{
				Point3D c2pos = new Point3D(c2.getCol(), c2.getRow(), c2.getHeight().getData());
				double dist = c1pos.distTo(c2pos);
				
				c1.addLink(c2, dist);
			}
		}
		
		// compute city sizes
		for(City city : cities)
		{
			computeInitialSize(city, attributes);
		}
		
		return cities;
	}
	
	/**
	 * Computes the initial size of the given city based on soil quality and connections to other cities.
	 * @param attributse min pop, max pop, ideal distance between two cities so that the connection can be fully utilised. anything closer results also in full utilisation.
	 */
	public static void computeInitialSize(City c, CityAttributes attributes)
	{
		double influenceSum = 0.0;
		for(CityLink link : c.getLinks().values())
		{
			double distance = link.getDistance();
			double influence = attributes.idealDistance / distance;
			
			influenceSum += influence;
		}
		
		// TODO: modify arbitrary formula to weight influence
		double influence = 1 - Math.exp(-influenceSum);
		
		// population factor, both influence and soil quality have an impact (multiplicative)
		// TODO: modify arbitrary formula to weight influence
		double populationFactor = (influence * c.getSoilQuality());
		
		int min = attributes.minPopulation;
		int max = attributes.maxPopulation;
		c.setPopulationSize((int)(min + populationFactor * (max-min)));
	}

	/**
	 * Create a 2D grid showing the positions of the given cities.
	 * @param cities
	 * @param attributes
	 * @return
	 */
	public static Grid2D<Integer> createGrid(Collection<City> cities, GridAttributes attributes)
	{
		Grid2D<Integer> result = new Grid2D<Integer>(attributes.height, attributes.width, new GridCellInteger(-1));
		
		for(City city : cities)
		{
			result.setDataAt(city.getRow(), city.getCol(), city.getPopulation());
		}
		
		return result;
	}
}
