package geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A polygon consists of a list of absolute points.
 * The last point must _not_ be the same as the first point.
 * 
 * @author Felix Dietrich
 * 
 */
public class Polygon {

	private List<Polygon> innerPolygons;
	private List<Point> pointList;
	private boolean isPositive = true;
	
	private double height;
	private double width;
	private Point minXPoint;
	private Point maxXPoint;
	private Point minYPoint;
	private Point maxYPoint;
	private Point centerPoint;

	public Polygon() {
		this.pointList = new ArrayList<Point>();
		this.innerPolygons = new LinkedList<Polygon>();
		
		height = 0;
		width = 0;
		centerPoint = new Point(0,0);
	}

	/**
	 * Return the list of all points contained in this polygon.
	 * Inner polygons are not considered.
	 * @return
	 */
	public List<Point> getBoundaryPoints() {
		return new LinkedList<Point>(pointList);
	}

	/**
	 * Checks whether the given point is equal to one of the boundary points.
	 * @param p point that might be one of the polygons boundary points.
	 * @return true iff p is equal to one of the boundary points.
	 */
	public boolean hasBoundaryPoint(Point p) {
		return pointList.contains(p);
	}

	/**
	 * Checks if the given point lies in the polygon.
	 * InnerPolygons are not checked here.
	 * If the point lies on one of the polygons points, it is said to be NOT contained in the polygon.
	 * 
	 * @param c point to check
	 * @return true if the point lies within the borders, false otherwise.
	 */
	public boolean contains(Point c, boolean useEndpoints) {
		// empty polygons do not contain points
		if(pointList.isEmpty())
			return false;
		
		// draw a line from c to the center of the polygon. if this line crosses any of the lines making
		// up the polygon, c is outside.
		Line fixedLine = new Line(c, centerPoint);

		// if point lies on border points, polygon is said to NOT contain it if useEndpoints is false.
		if(pointList.contains(c))
		{
			return useEndpoints;
		}

		for (int i = 0; i < pointList.size(); i++) {
			Line polyLine;
			if(i < pointList.size()-1)
				polyLine = new Line(pointList.get(i), pointList.get(i + 1));
			else
				polyLine = new Line(pointList.get(i), pointList.get(0));

			// if point lies on any lines, the polygon is said to NOT contain it if useEndpoints is false.
			if(polyLine.contains(c))
			{
				return useEndpoints;
			}
			
			// useEndpoints is always on here since the case that c lies on any the line is checked already
			boolean intersects = fixedLine.intersects(polyLine, true);
			if (intersects)
				return false;
		}
		return true;
	}
	
	/**
	 * Checks if any of the given points are contained in this polygon.
	 * @param points
	 * @return true if any one or more of the points are contained, false if not.
	 */
	public boolean contains(Collection<Point> points) {
		for(Point p: points)
		{
			if(this.contains(p, false))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		return (this.pointList.size() == 0);
	}

	/**
	 * Adds an inner polygon to this one. Note that if the given polygon
	 * exceeds this polygon somewhere, the boundaries (width/height) are adjusted accordingly.
	 * @param polygon
	 */
	public void addInnerPolygon(Polygon polygon) {
		this.innerPolygons.add(polygon);
		
		for(Point p:polygon.getBoundaryPoints())
		{
			recalculateBoundaries(p);
		}
	}

	public List<Polygon> getInnerPolygons() {
		List<Polygon> result = new LinkedList<Polygon>(this.innerPolygons);
		for(Polygon poly : this.innerPolygons)
		{
			result.addAll(poly.getInnerPolygons());
		}
		return result;
	}

	public void setNegative() {
		isPositive = false;
	}

	public boolean isPositive() {
		return isPositive;
	}

	public String toString() {

		StringBuilder s = new StringBuilder("Path ");

		for (int i = 0; i < pointList.size(); i++) {
			s.append(pointList.get(i).toString());
		}

		return s.toString();
	}

	/**
	 * Add a point with absolute coordinates to the point list.
	 * Update width and height of the polygon.
	 *  
	 * @param p
	 */
	public void addAbsPoint(Point p) {
		this.pointList.add(p);

		recalculateBoundaries(p);
	}

	/**
	 * Recalculates boundary data of the given polygon:
	 * minXPoint, maxXPoint, minYPoint, maxYPoint, center, height, width.
	 * 
	 * @param p point that was added to the polygon
	 */
	protected void recalculateBoundaries(Point p) {
		if (minXPoint == null || minXPoint.x > p.x)
			minXPoint = p;
		if (maxXPoint == null || maxXPoint.x < p.x)
			maxXPoint = p;
		if (minYPoint == null || minYPoint.y > p.y)
			minYPoint = p;
		if (maxYPoint == null || maxYPoint.y < p.y)
			maxYPoint = p;
		
		this.height = maxYPoint.y - minYPoint.y;
		this.width = maxXPoint.x - minXPoint.x;
		
		recalculateCenter();
	}

	/**
	 * Recalculates the center of this polygon after a point is added.
	 */
	private void recalculateCenter() {
		double centerX = 0;
		double centerY = 0;
		for(Point p : this.pointList)
		{
			centerX += p.x;
			centerY += p.y;
		}
		this.centerPoint = new Point(centerX / pointList.size(), centerY / pointList.size());
	}

	/**
	 * Add a relative point to the path surrounding the polygon. The last
	 * absolute point added is used to calculate the absolute coordinate of the
	 * new point.
	 * 
	 * @param p new point with relative coordinates to the last one.
	 */
	public void addRelPoint(Point p) {
		addAbsPoint(p.add(this.pointList.get(this.pointList.size() - 1)));
	}

	/**
	 * Calculate and return the area of this polygon without regarding
	 * innerPolygons.
	 * 
	 * @return area of the polygon without regarding innerPolygons.
	 */
	public double getArea() {
		double result = 0;
		for (int i = 0; i < pointList.size() - 1; i++) {
			result += (pointList.get(i).y + pointList.get(i + 1).y)
					* (pointList.get(i).x - pointList.get(i + 1).x);
		}
		return Math.abs(result) / 2.0;
	}

	/**
	 * Returns precomputed height. O(1).
	 * @return
	 */
	public double getHeight() {
		return this.height;
	}

	/**
	 * Returns precomputed width. O(1).
	 * @return
	 */
	public double getWidth() {
		return this.width;
	}
	
	/**
	 * @return precomputed min x value. O(1).
	 */
	public double getMinX()
	{
		return this.minXPoint.x;
	}

	/**
	 * @return precomputed min y value. O(1).
	 */
	public double getMinY()
	{
		return this.minYPoint.y;
	}

	/**
	 * Checks intersection with a given polygon.
	 * The given polygon is said to intersect this polygon if any point of this lies in "polygon" or the other way round.
	 * This is checked by looping over all lines of this and intersecting them with the polygon.
	 * Then, it is checked if any point of this lies inside of polygon, then the other way round.
	 * This way, completely enclosing geometries are covered as well.
	 * 
	 * @param polygon
	 * @param createMidpoints if true, every line is split into two by inserting a midpoint.
	 * @param useEndpoints if true, uses end points of the lines to check as well
	 * @return
	 */
	public boolean intersects(Polygon polygon, boolean createMidpoints, boolean useEndpoints) {
		List<Point> points = new LinkedList<Point>(pointList);
		
		// if midpoints should be created, loop over the pointList and create them
		if(createMidpoints && !pointList.isEmpty())
		{
			for(int i=0; i<pointList.size()-1;i++)
			{
				points.add(pointList.get(i).interpolate(pointList.get(i+1), 0.5));
			}
			points.add(pointList.get(pointList.size()-1).interpolate(pointList.get(0), 0.5));
		}
		
		// add first point so we get a closed loop
		points.add(points.get(0));
		
		// create the geometry to check intersection with
		// TODO: maybe check with polygon only?
		Geometry geometry = new Geometry();
		geometry.addInnerPolygon(polygon);
		
		// loop over all lines and check intersection
		for(int i=0; i<points.size()-1; i++)
		{
			Line intersectingLine = new Line(points.get(i), points.get(i+1));
			if(intersectingLine.intersects(geometry, useEndpoints, useEndpoints, useEndpoints))
			{
				return true;
			}
		}
		
		// loop over this points
		for(Point p: pointList)
		{
			if(polygon.contains(p, useEndpoints))
			{
				return true;
			}
		}
		
		// loop over polygons points
		for(Point p: polygon.getBoundaryPoints())
		{
			if(this.contains(p, useEndpoints))
			{
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Check whether the given polygon intersects with the open ball around "center" with given radius.
	 * @param center
	 * @param radius
	 * @return true if any point of the polygon lies within the open ball.
	 */
	public boolean intersects(Point center, double radius) {
		// if the center is contained in the polygon, parts of the ball are contained as well
		if(this.contains(center, true))
		{
			return true;
		}
		
		// check whether the center is closer to the sides than the radius
		// loop over all lines and check intersection
		for(int i=0; i<pointList.size(); i++)
		{
			Line intersectingLine;
			// loop around
			if(i < pointList.size()-1)
			{
				intersectingLine = new Line(pointList.get(i), pointList.get(i+1));
			}
			else
			{
				intersectingLine = new Line(pointList.get(i), pointList.get(0));
			}
			
			// check distance of closest point on the line to the center of the ball
			if(intersectingLine.closestTo(center).distTo(center) < radius)
			{
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Move the polygon so that its center coincides with the given position.
	 * Note that this destroys the index values of the points but not the orientation (ccw / cw).
	 *  
	 * @param newCenter
	 */
	public void moveTo(Point newCenter) {
		Point toAdd = newCenter.sub(this.centerPoint);
		List<Point> points = new LinkedList<Point>(this.pointList);
		this.pointList.clear();
		for(Point oldP : points)
		{
			this.addAbsPoint(oldP.add(toAdd));
		}
	}

	/**
	 * Rotate the given polygon around its center, counterclockwise by the given angle.
	 * Note that this destroys the index values of the points but not the orientation (ccw / cw).
	 * 
	 * @param phi angle
	 */
	public void rotate(double phi) {
		List<Point> points = new LinkedList<Point>(this.pointList);
		// store the center point since it will be changed by adding points later
		Point oldCenter = this.centerPoint;
		// remove all points
		this.pointList.clear();
		// and add them again
		for(Point oldP : points)
		{
			this.addAbsPoint(oldP.rotate(oldCenter, phi));
		}
	}
	
	/**
	 * Performs a deep copy of the polygon.
	 * @return
	 */
	public Polygon copy()
	{
		Polygon copy = new Polygon();
		for(Point p : this.getBoundaryPoints())
		{
			// the points do not need to be copied since they are immutable
			copy.addAbsPoint(p);
		}
		for(Polygon pol : this.getInnerPolygons())
		{
			this.addInnerPolygon(pol.copy());
		}
		
		return copy;
	}
}
