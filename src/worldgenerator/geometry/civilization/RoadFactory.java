/**
 * 
 */
package worldgenerator.geometry.civilization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

import vadereutils.geometry.Line;
import vadereutils.geometry.Point;
import worldgenerator.geometry.Point3D;
import worldgenerator.util.factory.IWorldObjectFactory;
import worldgenerator.util.grid.Grid2D;
import worldgenerator.util.grid.GridCellDouble;
import worldgenerator.util.grid.GridFactory;
import worldgenerator.util.grid.GridType;
import worldgenerator.util.grid.GridFactory.GridAttributes;

/**
 * @author Felix Dietrich
 *
 */
public class RoadFactory implements IWorldObjectFactory<Road>
{
	public static class RoadAttributes
	{
		/** maximum number of roads to create. */
		public final int maxRoads;
		/** maximum distance possible between two road vertices. */
		public final double maxDistance;
		/** minimum distance possible between two road vertices. */
		public final double minDistance;
		/** search angle for next road vertex. */
		public final double searchAngle;
		/** used seed. */
		public final int seed;

		/**
		 * @param maxRoads
		 * @param height
		 * @param width
		 * @param seed
		 */
		public RoadAttributes(int maxRoads, double maxDistance, double minDistance, double searchAngle, int seed)
		{
			this.maxRoads = maxRoads;
			this.maxDistance = maxDistance;
			this.minDistance = minDistance;
			this.searchAngle = searchAngle;
			this.seed = seed;
		}

		public RoadAttributes(RoadAttributes copyAttributes)
		{
			this(copyAttributes.maxRoads, copyAttributes.maxDistance, copyAttributes.minDistance, copyAttributes.searchAngle, copyAttributes.seed);
		}
	}
	
	public static Collection<Road> createRoadNetwork(final Collection<City> population, final Grid2D<Double> heightmap, RoadAttributes attributes)
	{
		Collection<Road> result = new LinkedList<Road>();
		Collection<Line> lines = new LinkedList<Line>();
		
		final Grid2D<Double> multHeightmap = heightmap.clone();
		multHeightmap.mult(new GridCellDouble(10.0));
		
		// create link set ordered by distance
		TreeSet<CityLink> links = new TreeSet<CityLink>(new Comparator<CityLink>()
		{
			@Override
			public int compare(CityLink cl1, CityLink cl2)
			{
				// TODO: find better way than using heightmap twice to simulate high altitudes
				double q1 = computeRayQuality(cl1.c1.getPosition(), cl1.c2.getPosition(), multHeightmap, heightmap);
				double q2 = computeRayQuality(cl2.c1.getPosition(), cl2.c2.getPosition(), multHeightmap, heightmap);
				return Double.compare(cl1.getDistance()+q1, cl2.getDistance()+q2);
			}
		});
		
		// fill with city links
		for(City city : population)
		{
			for(CityLink cityLink : city.getLinks().values())
			{
				if(cityLink.getDistance() <= attributes.maxDistance && cityLink.getDistance() >= attributes.minDistance)
				{
					links.add(cityLink);
				}
			}
		}
		
		for(CityLink cityLink: links)
		{
			// TODO: remove ugly interopability hack
			Point p1 = new Point(cityLink.c1.getPosition().x, cityLink.c1.getPosition().y);
			Point p2 = new Point(cityLink.c2.getPosition().x, cityLink.c2.getPosition().y);
			Line newline = new Line(p1, p2);
			boolean intersects = false;
			
			for(Line line : lines)
			{
				if(newline.intersects(line, false))
				{
					intersects = true;
					break;
				}
			}
			// if no intersection was found, create a road
			if(!intersects)
			{
				lines.add(newline);
				LinkedList<Point3D> vertices = new LinkedList<Point3D>();
				vertices.add(cityLink.c1.getPosition());
				vertices.add(cityLink.c2.getPosition());
				result.add(new Road(vertices));
			}
		}
		
		return result;
	}

	private static final int RAYS_PER_SEARCH = 25;
	private static final int SAMPLES_PER_RAY = 10;
	private static final int SEARCHES_PER_ROAD = 5;
	
	/**
	 * Create a road network based on the ideas in 
	 * http://people.ee.ethz.ch/~pascmu/documents/procedural_modeling_of_cities__siggraph2001.pdf
	 * 
	 * @param populationDensity
	 * @param attributes
	 * @return
	 */
	public static Collection<Road> createRoadNetwork(Grid2D<Double> populationDensity, Grid2D<Double> heightmap, RoadAttributes attributes)
	{
		Collection<Road> result = new LinkedList<Road>();
		
		// compute the row and col of the maximum density
		Point3D maxPoint = null;
		for(int row=0; row < populationDensity.rows(); row++)
		{
			for(int col=0; col < populationDensity.cols(); col++)
			{
				if(populationDensity.getDataAt(row, col).compareTo(populationDensity.getMaximum()) == 0)
				{
					maxPoint = new Point3D(col,row, heightmap.getDataAt(row, col).getData());
					break;
				}
			}
			if(maxPoint != null)
			{
				break;
			}
		}
		
		// start the first road at the highest density
		LinkedList<Point3D> vertices = new LinkedList<Point3D>();
		vertices.add(maxPoint);
		ArrayList<Point3D> totalvertices = new ArrayList<Point3D>(100);
		
		Random rand = new Random(attributes.seed);
		Point3D next = null;
		Point3D bestRayEnd = null;
		
		LinkedList<Line> roadLines = new LinkedList<Line>();
		
		// create a given number of roads, maybe less if one is deleted in the process
		for(int roadInd=0; roadInd < attributes.maxRoads; roadInd++)
		{
			// direction of the road
			Point3D direction = new Point3D(rand.nextDouble()-0.5, rand.nextDouble()-0.5, 0);
			Point3D last = vertices.getLast();
			
			for(int search=1; search < SEARCHES_PER_ROAD; search++)
			{
				double bestRayQuality = 0.0;
				for(int ray=0; ray < RAYS_PER_SEARCH; ray++)
				{
					Point3D ldir = direction.rotate2D((ray/(double)RAYS_PER_SEARCH-0.5) * attributes.searchAngle, last);
					ldir = ldir.normalize();
					
					// length of the road
					double length = attributes.minDistance + (rand.nextDouble()) * (attributes.maxDistance - attributes.minDistance);
					
					// compute next vertex
					next = vertices.getLast().add(ldir.mult(length));
					double rayQuality = computeRayQuality(vertices.getLast(), next, populationDensity, heightmap);
					if(bestRayEnd == null || rayQuality > bestRayQuality)
					{
						bestRayEnd = next;
						bestRayQuality = rayQuality;
					}
				}
				next = bestRayEnd;
				// TODO: remove ugly interopability hack
				Line newline = new Line(new Point(vertices.getLast().x, vertices.getLast().y), new Point(next.x, next.y));
				
				boolean intersection = false;
				for(Line l: roadLines)
				{
					if(l.intersects(newline, false))
					{
						intersection = true;
						break;
					}
				}
				
				// if the new road intersects a given one, dont use the new road
				if(intersection)
				{
					continue;
				}
				
				roadLines.add(newline);
				vertices.add(next);
			}
			
			// if no road was found, continue
			if(vertices.size() < 2)
			{
				continue;
			}
			
			// new road
			Road road = new Road(vertices);
			result.add(road);
			
			// save vertices, clear vertices for new road, get next starting vertex
			totalvertices.addAll(vertices);
			vertices.clear();
			int nextStartInd = rand.nextInt(totalvertices.size());
			vertices.add(totalvertices.get(nextStartInd));
		}
		return result;
	}
	
	/**
	 * Compute the ray quality based on the formula in 
	 * http://people.ee.ethz.ch/~pascmu/documents/procedural_modeling_of_cities__siggraph2001.pdf
	 * section 3.2.1, population density.
	 * "The population at every sample point on the ray is weighted with the inverse distance to the roadend and summed up".
	 * @param last
	 * @param next
	 * @param population
	 * @param heightmap 
	 * @return
	 */
	private static double computeRayQuality(Point3D last, Point3D next, Grid2D<Double> population, Grid2D<Double> heightmap)
	{
		double quality = 0.0;
		for(int sample = 0; sample < SAMPLES_PER_RAY; sample++)
		{
			Point3D samplePoint = Point3D.lerp(last, next, sample / (double)SAMPLES_PER_RAY);
			int row = (int)samplePoint.y;
			int col = (int)samplePoint.x;
			
			double localPopulation;
			double localHeight;
			if(population.invalid(row, col))
			{
				localPopulation = 0.0;
				localHeight = 0.0;
			}
			else
			{
				localPopulation = population.getDataAt(row,col).getData();
				// TODO: remove arbitrary formula by paper formula
				localHeight = heightmap.getDataAt(row,col).getData()*population.getMaximum().getData()/10.0;
			}
			// if the road goes through a lake, do not count it
			if(localHeight < 0)
			{
				return 0;
			}
			quality += (localPopulation-localHeight) / last.distTo(next);
		}
		return quality;
	}

	/**
	 * Create a 2D grid showing the positions of the given roads.
	 * @param roads
	 * @param attributes
	 * @return
	 */
	public static Grid2D<Double> createGrid(Collection<Road> roads, GridAttributes attributes)
	{
		Grid2D<Double> result = GridFactory.create2D(GridType.DOUBLE_2D, attributes);
		
		for(Road road: roads)
		{
			for(Point3D vertex : road.getVertices(50))
			{
				result.setDataAt((int)vertex.y, (int)vertex.x, vertex.z);
			}
		}
		
		return result;
	}
}
