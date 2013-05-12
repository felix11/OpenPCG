package geometry;

import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * A grid augmenting the position of generic objects, for faster access.
 * O(1) instead of O(n) for one fixed radius check. See {@link LinkedCellsGrid#getObjects(java.awt.geom.Point2D.Double, double)}.
 * 
 * @author Felix Dietrich
 *
 */
public class LinkedCellsGrid<T> extends Polygon implements Iterable<T>
{
	
	final private double left;
	final private double top;
	final private double width;
	final private double height;
	private GridCell<T>[][] grid;
	private Map<T,Point2D.Double> totalObjects = new HashMap<T,Point2D.Double>();
	private int gridSize;
	private double cellSize;
	
	/**
	 * One cell in the grid. It contains a mapping from points to objects.
	 * 
	 * @author Felix Dietrich
	 *
	 * @param <E> type of objects stored in this cell. 
	 */
	private class GridCell<E>
	{
		public Map<Point2D.Double, E> objects = new HashMap<Point2D.Double, E>();
	}
	
	/**
	 * Generates an empty grid of GridCell&lt;T&gt; objects.
	 * @param s grid side length
	 * @return A two-dimensional grid of size "s^2" containing GridCell&lt;T&gt; objects.
	 */
	@SuppressWarnings({"unchecked"})
	private GridCell<T>[][] generateGrid(int... s) {
	    // Use Array native method to create array of a type only known at run time
		this.grid = (GridCell<T>[][]) Array.newInstance(GridCell.class,s);
		
		for(int r=0; r<grid.length; r++)
		{
			for(int c=0; c<grid.length; c++)
			{
				grid[r][c] = new GridCell<T>();
			}	
		}
		
		return this.grid;
	}

	/**
	 * Generates a LinkedCellsGrid with given dimension, position and number of items on one side.
	 * @param left x-position of top left corner
	 * @param top y-position of top left corner
	 * @param width width of the grid, in world units (e.g. [m])
	 * @param height height of the grid, in world units (e.g. [m])
	 * @param sideLength number of items on one side. the total number of grid cells equals sideLength^2
	 */
	public LinkedCellsGrid(double left, double top, double width, double height, double sideLength)
	{
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		
		// create grid
		this.gridSize = (int)Math.max(1, Math.ceil(Math.max(width,height)/sideLength));
		this.cellSize = Math.max(width,height) / gridSize;
		this.grid = generateGrid(gridSize, gridSize);
	}
	
	/**
	 * Determines the discrete grid position (x,y) given a point with double coordinates.
	 * @param pos a given position with coordinate values of type double
	 * @return the position in the grid, from 0 to this.gridSize-1 in both coordinates.
	 */
	private int[] gridPos(Point2D.Double pos)
	{
		// compute position in the grid
		int iX = (int)Math.max(0,Math.min(gridSize-1, Math.floor((pos.x - left) / width * gridSize)));
		int iY = (int)Math.max(0,Math.min(gridSize-1, Math.floor((pos.y - top) / height * gridSize)));
		
		return new int[]{iX,iY};
	}
	
	/**
	 * Adds a given object to the grid at the given position.
	 * The position is discretized automatically to fit in the cells.
	 * 
	 * @param object object to add
	 * @param pos position in the grid
	 */
	public void addObject(final T object, final Point2D.Double pos)
	{
		int[] gridPos = gridPos(pos);
		
		// store object in the grid cell
		this.grid[gridPos[0]][gridPos[1]].objects.put(pos, object);
		totalObjects.put(object, pos);
	}
	
	/**
	 * Returns a set of objects in the ball around pos with given radius. 
	 * @param pos position of the center of the ball
	 * @param radius radius of the ball
	 * @return set of objects, or an empty set if no objects are present.
	 */
	public List<T> getObjects(final Point2D.Double pos, final double radius)
	{
		final List<T> result = new LinkedList<T>();

		int[] gridPos = gridPos(pos);
		int discreteRad = (int)Math.ceil(radius / cellSize)*6;		//TODO: *6 magic number?
		
		final int maxRow = Math.min(gridSize-1,gridPos[0]+discreteRad);
		final int maxCol =  Math.min(gridSize-1,gridPos[1]+discreteRad);
		
		for(int row=Math.max(0, gridPos[0]-discreteRad); row <= maxRow; row++)
		{
			for(int col=Math.max(0, gridPos[1]-discreteRad); col <= maxCol; col++)
			{
				for(Entry<Point2D.Double,T> entry : this.grid[row][col].objects.entrySet())
				{
					if(entry.getKey().distance(pos) < radius)
					{
						result.add(entry.getValue());
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Removes the object <T> from the grid regardless of its position.
	 * @param object
	 */
	public void removeObject(T object)
	{
		Point2D.Double pos = totalObjects.remove(object);
		if(pos != null)
		{
			int[] gridPos = gridPos(pos);
			this.grid[gridPos[0]][gridPos[1]].objects.remove(pos);
		}
	}

	/**
	 * Removes all objects.
	 */
	public void clear() 
	{
		totalObjects.clear();
		this.grid = generateGrid(gridSize, gridSize);
	}

	@Override
    public Iterator<T> iterator()
    {
	    return totalObjects.keySet().iterator();
    }
	
	/**
	 * Returns the size (number of different keys <T>) of List.
	 * @return the size (number of different keys <T>) of List
	 */
	public int size()
	{
		return totalObjects.keySet().size();
	}
	
	public boolean contains(final T element)
	{
		return totalObjects.containsKey(element);
	}
}
