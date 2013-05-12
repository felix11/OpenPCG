package geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A generic geometry.
 * Represented by a polygon (borders) with inner polygons (obstacles), if any.
 * 
 * @author Felix Dietrich
 *
 */
public class Geometry extends Polygon {
	
	/**
	 * Returns a list of all points of this geometry.
	 * @return A list of points.
	 */
	public List<Point> getPoints()
	{
		List<Point> resultList = new LinkedList<Point>();
		
		// add points of boundary
		resultList.addAll(this.getBoundaryPoints());
		// add obstacles
		for(Polygon p: this.getInnerPolygons())
		{
			resultList.addAll(p.getBoundaryPoints());
		}
		
		return resultList;
	}
	
	/**
	 * Orders a given list angular relative to a given point, starting with angle 0.
	 * @param allPoints
	 * @param center
	 * @return an ordered DataPoint list with the angle of the point as data and the original index set.
	 */
	public static List<DataPoint> orderByAngle(List<Point> allPoints, Point center)
	{
		List<DataPoint> orderedList = new ArrayList<DataPoint>();
		
		for(int i=0; i<allPoints.size(); i++)
		{
			Point p = allPoints.get(i);
			orderedList.add(new DataPoint(p.x, p.y, i, p.angleTo(center)));
		}
		// sort by angle
		Collections.sort(orderedList, DataPoint.getComparator());
		
		return orderedList;
	}
	
	/**
	 * Returns the list of all polygons of this geometry, including the boundary.
	 * @return
	 */
	public List<Polygon> getPolygons()
	{
		List<Polygon> polygons = new LinkedList<Polygon>();
		
		// if the geometry has a boundary, add it
		if(this.getBoundaryPoints().size() > 0)
		{
			polygons.add((Polygon)this);
		}
		// also add all inner polygons
		polygons.addAll(this.getInnerPolygons());
		
		return polygons;
	}
	
	/**
	 * Checks if a given point is in the geometry, including inner polygons.
	 * @param toCheck Point to check
	 */
	@Override
	public boolean contains(Point toCheck, boolean useEndpoints)
	{
		List<Polygon> polygons = getInnerPolygons();
		for(Polygon p:polygons)
		{
			if(p.contains(toCheck, useEndpoints))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines whether a point lies on the boundary of this geometry.
	 * @param p
	 * @return
	 */
	public boolean onBoundary(Point p) {
		List<Point> points = this.getBoundaryPoints();
		if(points.contains(p))
		{
			return true;
		}
		
		points.add(points.get(0));
		for(int i=0; i<points.size()-1; i++)
		{
			if(new Line(points.get(i), points.get(i+1)).contains(p))
			{
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Checks intersection with a given polygon
	 * @param polygon
	 * @param createMidpoints if true, every line is split into two by inserting a midpoint.
	 * @return
	 */
	public boolean intersects(Polygon polygon, boolean createMidpoints, boolean useEndpoints) {

		List<Polygon> allPolys = this.getInnerPolygons();
		for(Polygon p:allPolys)
		{
			if(p.intersects(polygon, createMidpoints, useEndpoints))
			{
				return true;
			}
		}
		
		return false;
	}
}
