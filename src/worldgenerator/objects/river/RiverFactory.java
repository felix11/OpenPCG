package worldgenerator.objects.river;

import geometry.Point3D;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import worldgenerator.util.factory.IWorldObjectFactory;
import worldgenerator.util.grid.CellularAutomata;
import worldgenerator.util.grid.CellularAutomata.CAStep;
import worldgenerator.util.grid.ComparableGrid2D;
import worldgenerator.util.grid.GridCellComparable;
import worldgenerator.util.grid.GridCellDouble;
import worldgenerator.util.grid.GridCellInteger;
import worldgenerator.util.grid.GridFactory;
import worldgenerator.util.grid.GridFactory.GridAttributes;
import worldgenerator.util.grid.GridType;
import worldgenerator.util.noise.RandomIntervals;
import worldgenerator.util.noise.RandomSource;

public class RiverFactory implements IWorldObjectFactory<River> {
	/**
	 * Creates a river with given source and sink.
	 * The sparsity defines how many new source rivers are created in the process. 
	 * @param source
	 * @param sink
	 * @param newRivers in [0...inf). 0: no new rivers, x: x rivers per segment from source to sinks, is repeated n times until sink.flow matches (x^n)*source.flow
	 * @param minDist the minimum distance between source and sink for newly created side rivers.
	 * @return a new river with connected side rivers
	 */
	public static River create(GridCellRiverVertex source, GridCellRiverVertex sink, int newRivers, double minDist)
	{
		if(newRivers < 0)
			throw new IllegalArgumentException("newRivers must be between 0 and Integer.MAX_VALUE.");
		
		River result = new River(source, sink);
		
		// if new rivers should be created, create them here.
		if(newRivers > 0)
		{
			// generate random interval for subrivers position and flux
			RandomIntervals subRiverSinks = new RandomIntervals(newRivers);
			RandomIntervals subRiverFluxes = new RandomIntervals(newRivers);
			
			// compute missing flux and create new rivers to match it
			double missingFlux = sink.getFlux() - source.getFlux();
			if(missingFlux > 0)
			{
				for(int i=0; i<newRivers; i++)
				{
					// get new flux and position factor from intervals
					double newFlux = (subRiverFluxes.intervals[i][1] - subRiverFluxes.intervals[i][0]) * missingFlux;
					double newPos = (subRiverSinks.intervals[i][1]);
					
					// create new source and sink positions.
					// TODO: change position generation to a more elaborate procedure
					double dist = source.getPosition().distTo(sink.getPosition()) / 5;
					
					if(dist > minDist)
					{
						Point3D newSinkPos = Point3D.lerp(source.getPosition(), sink.getPosition(), newPos);
						newSinkPos = newSinkPos.add(RandomSource.getRandomPoint3D(dist));
						GridCellRiverVertex newSink = new GridCellRiverVertex(newSinkPos, newFlux);
						
						Point3D newSourcePos = Point3D.lerp(source.getPosition(), sink.getPosition(), newPos);
						newSourcePos = newSourcePos.add(RandomSource.getRandomPoint3D(dist));
						// changes the flux of the source of the new river randomly so that even more rivers will be created lateron
						GridCellRiverVertex newSource = new GridCellRiverVertex(newSourcePos, Math.max(0, newFlux * (1+1*RandomSource.rand.nextGaussian())));
						
						River newRiver = RiverFactory.create(newSource, newSink, newRivers, minDist);
						result.addConnectedSource(newRiver);
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Creates a single river with bends.
	 * 
	 * @param source
	 * @param sink
	 * @param newVertices defines how many new vertices (and hence, bends) should be created
	 * @return
	 */
	public static River createSingle(GridCellRiverVertex source, GridCellRiverVertex sink, int newVertices, double stretch)
	{
		River result = new River(source, sink);
		
		addRandomVertices(result, source, sink, newVertices, stretch);
		
		int mapsize = 512;
		ComparableGrid2D<Point3D> rivermap = new ComparableGrid2D<Point3D>(mapsize, mapsize, new GridCellRiverVertex(new Point3D(0.0, 0.0, 0.0), 0.0));
		// go on creating other rivers
		
		return result;
	}

	private static void addRandomVertices(River result, GridCellRiverVertex gcp1, GridCellRiverVertex gcp2, int newVertices,
			double stretch) {
		if(newVertices < 1)
		{
			return;
		}
		
		Point3D p1 = gcp1.getPosition();
		Point3D p2 = gcp2.getPosition();

		// center point between two end points
		Point3D center = Point3D.lerp(p1, p2, 0.5);
		// normal to the line from p1 to p2
		Point3D normal = p2.sub(p1).cross(new Point3D(p1.x, p1.y, p1.z+10)).normalize();
		// stretch factor
		double stretchFactor = stretch * p1.distTo(p2) * (RandomSource.rand.nextDouble() * 2 - 1);
		// new vertex
		GridCellRiverVertex newVertex = new GridCellRiverVertex(center.add(normal.normalize(stretchFactor)), gcp1.getFlux());
		
		result.addVertex(gcp1, newVertex);
		
		// continue generation of vertices until newVertices is reached
		addRandomVertices(result, gcp1, newVertex, (int)Math.ceil((newVertices-1)/2.0), stretch/1.5);
		addRandomVertices(result, newVertex, gcp2, (int)Math.ceil((newVertices-1)/2.0), stretch/1.5);
	}

	/**
	 * Creates multiple rivers based on a heightmap.
	 * @param heightmap
	 * @return
	 */
	public static Collection<River> createMultiple(ComparableGrid2D<Double> heightmap, int Nrivers) {
		Collection<River> rivers = new LinkedList<River>();
		
		return rivers;
	}

	/**
	 * Uses a heightmap to create a watershades map with the given attributes.
	 * Watersheds are created by dropping a random map of virtual water onto the heightmap and let it flow downwards using a cellular automata.
	 * @param heightmap
	 * @param attributes
	 * @return
	 */
	public static ComparableGrid2D<Integer> createWatersheds(final ComparableGrid2D<Double> heightmap, GridAttributes attributes)
	{
		// cutoff value below which no watersheds form
		final double waterShedCutoff = 0.10;
		
		ComparableGrid2D<Integer> initialData = new ComparableGrid2D<Integer>(attributes.height, attributes.width, new GridCellInteger(-999));
		
		// store the indices in the initial data to be able to assign different numbers to the watersheds lateron
		/*for(int r=0; r < attributes.height; r++)
		{
			for(int c=0; c < attributes.width; c++)
			{
				initialData.setDataAt(r, c, new GridCellInteger(r * attributes.width + c));
			}
		}*/
		
		// create the cellular automata that computes the initial watersheds
		CellularAutomata<Integer> automata = new CellularAutomata<Integer>(initialData, new CAStep<Integer>()
		{
			@Override
			public ComparableGrid2D<Integer> work(ComparableGrid2D<Integer> grid)
			{
				for(int r=0; r < grid.rows(); r++)
				{
					for(int c=0; c < grid.cols(); c++)
					{
						// if the threshold was crossed, progress the watershed
						double dval = heightmap.getDataAt(r, c).getData();
						if(dval > waterShedCutoff)
						{
							int val = 1;
							grid.setDataAt(r-1, c, val);
							grid.setDataAt(r+1, c, val);
							grid.setDataAt(r, c, val);
							grid.setDataAt(r, c-1, val);
							grid.setDataAt(r, c+1, val);
						}
						else
						{
							grid.setDataAt(r, c, -1);
						}
					}
				}
				return grid;
			}
		});
		
		// progress some steps so that the watersheds have time to form
		int steps = 10;
		automata.step(steps);
		
		// store the result in the initial data for the next automata
		initialData = automata.result();
		
		// create the cellular automata that computes the initial watersheds
		CellularAutomata<Integer> automataExpand = new CellularAutomata<Integer>(initialData, new CAStep<Integer>()
		{
			@Override
			public ComparableGrid2D<Integer> work(ComparableGrid2D<Integer> grid)
			{
				for(int r=0; r < grid.rows(); r++)
				{
					for(int c=0; c < grid.cols(); c++)
					{
						int val = grid.getDataAt(r, c).getData();
						double cheight = heightmap.getDataAt(r, c).getData();
						// if a watershed is present, extend it in an appropriate direction
						if(val > -100 && cheight > -0.1)
						{
							for(int hr=-1; hr<=1; hr++)
							{
								for(int hc=-1; hc<=1; hc++)
								{
									// if the height is decreasing, extend the watershed
									if(!heightmap.invalid(r+hr, c+hc))
									{
										double h = heightmap.getDataAt(r+hr, c+hc).getData();
										if(h < cheight)
										{
											grid.setDataAt(r+hr, c+hc, (int)(h*100));
										}
									}
								}
							}
						}
					}
				}
				return grid;
			}
		});
		
		// progress some steps so that the watersheds have time to form
		steps = 15;
		automataExpand.step(steps);
		
		return automataExpand.result();
	}
}
