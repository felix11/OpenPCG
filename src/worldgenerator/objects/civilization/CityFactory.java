/**
 * 
 */
package worldgenerator.objects.civilization;

import geometry.Point3D;

import java.util.Collection;
import java.util.LinkedList;

import worldgenerator.objects.terrain.Terrain;
import worldgenerator.util.factory.IWorldObjectFactory;
import worldgenerator.util.grid.ComparableGrid2D;
import worldgenerator.util.grid.Grid2D;
import worldgenerator.util.grid.Grid2D.Grid2DIterator;
import worldgenerator.util.grid.GridCell;
import worldgenerator.util.grid.GridCellComparable;
import worldgenerator.util.grid.GridCellInteger;
import worldgenerator.util.grid.GridFactory.GridAttributes;
import worldgenerator.util.grid.GridUtils;
import worldgenerator.util.noise.RandomSource;

/**
 * @author Felix Dietrich
 *
 */
public class CityFactory implements IWorldObjectFactory<City>
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
	
	/**
	 * Creates cities based on the height map, soil quality and given city attributes.
	 * @param terrain
	 * @param attributes
	 * @return
	 */
	public static Collection<City> create(Terrain terrain, CityAttributes attributes)
	{
		ComparableGrid2D<Double> heightmap = terrain.getHeightMap(0);
		ComparableGrid2D<Double> soilQuality = terrain.getSoilQualityMap();
		Collection<City> cities = new LinkedList<City>();
		
		RandomSource.rand.setSeed(attributes.seed);
		
		int ID = 0;
		
		// create cities, store soil quality at their position
		for(int row=0; row<heightmap.rows(); row++)
		{
			for(int col=0; col<heightmap.cols(); col++)
			{
				double rval = RandomSource.rand.nextDouble();
				double h = heightmap.getDataAt(row, col).getData();
				int averageSoilKernelSize = 5;
				double soil = soilQuality.sumDataOverArea(row, col, averageSoilKernelSize ).getData() / (averageSoilKernelSize*averageSoilKernelSize);
				if(h > 0 && soil * attributes.density > rval)
				{
					City newCity = new City(new GridCellInteger(ID), row, col, heightmap.getDataAt(row, col));
					newCity.setSoilQuality(soil);
					cities.add(newCity);
				}
			}
		}
		
		// link cities
		CityFactory.link(cities, new LinkedList<City>());
		
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
		int population = (int)(min + populationFactor * (max-min));

		// correction factor so that the overall population density is exponentially distributed, assuming that "population" is uniformly distributed
		// TODO: calculate correction formula
		double correction = 1;
		
		c.setPopulationSize((int)(population * correction));
	}

	/**
	 * Create a 2D grid showing the positions of the given cities.
	 * @param cities
	 * @param attributes
	 * @return
	 */
	public static ComparableGrid2D<Integer> createGrid(Collection<City> cities, GridAttributes attributes)
	{
		ComparableGrid2D<Integer> result = new ComparableGrid2D<Integer>(attributes.height, attributes.width, new GridCellInteger(0));
		
		for(City city : cities)
		{
			result.setDataAt((int)city.getPosition().y, (int)city.getPosition().x, city.getPopulation());
		}
		
		return result;
	}

	/**
	 * Create a 2D grid storing the population density imposed by the given cities.
	 * @param heightmap 
	 * @param cities
	 * @param attributes
	 * @return
	 */
	public static ComparableGrid2D<Double> createPopulationDensityGrid(final ComparableGrid2D<Double> heightmap, Collection<City> cities, GridAttributes attributes)
	{
		if(attributes.factor <= 0)
			throw new IllegalArgumentException("CityAttributes.density must set the kernel size and must thus be > 0.");
		
		ComparableGrid2D<Double> result = GridUtils.int2double(createGrid(cities, attributes));
		GridUtils.ApplyGaussianFilter(result, (int)attributes.factor);
		
		result.iterate(new Grid2DIterator<Double>()
		{
			@Override
			public void step(int row, int col, GridCell<Double> gridCell, Grid2D<Double> grid2d)
			{
				if(heightmap.getDataAt(row, col).getData() < 0)
				{
					// TODO: add the removed data somewhere else, so that the population count stays the same
					grid2d.setDataAt(row, col, 0.0);
				}
			}
		});
		
		return result;
	}

	/**
	 * Links all cities given in the collections together, removing old links already stored in the cities.
	 * @param cityCollection1
	 * @param cityCollection2
	 */
	public static void link(Collection<City> cityCollection1, Collection<City> cityCollection2)
	{
		Collection<City> allcities = new LinkedList<City>();
		
		// unify all cities in one array
		allcities.addAll(cityCollection1);
		allcities.addAll(cityCollection2);
		
		// loop over all cities and link them
		for(City c1 : allcities)
		{
			// remove previous links
			c1.getLinks().clear();
			// add new links
			for(City c2 : allcities)
			{
				// if this is not exactly the same city, link the two
				if(c1 != c2)
				{
					c1.addLink(c2, c1.getPosition().distTo(c2.getPosition()));
				}
			}
		}
	}
}
