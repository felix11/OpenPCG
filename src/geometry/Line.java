package geometry;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * Line class.
 * Represents a line in 2d space given as two points p1 and p2.
 * Provides lots of functionality regarding lines, intersections etc.
 * 
 * @author Felix Dietrich
 */
public class Line {

	public final Point p1, p2;
	public final double slope;
	public final double yintercept;
	public Point lastInterceptionPoint;

	public Line(Point p1, Point p2)
	{
		this.p1 = p1;
		this.p2 = p2;

		if (p1.x < p2.x)
		{
			this.slope = (p2.y - p1.y) / (p2.x - p1.x);
		} else
		{
			this.slope = (p1.y - p2.y) / (p1.x - p2.x);
		}

		this.yintercept = p1.y - this.slope * p1.x;
	}

	/**
	 * checks whether the line is just a single point or not.
	 * @return true if the lines points p1 and p2 are equal, false otherwise.
	 */
	public boolean isPoint()
	{
		return p1.x == p2.x && p1.y == p2.y;
	}

	/**
	 * Evaluate the line at the given point x using the y=mx+t formula.
	 * Start and end points are not taken into account.
	 * @param x value the line should be evaluated
	 * @return y value at x
	 */
	public double evaluateAt(double x)
	{
		return this.slope * x + this.yintercept;
	}

	/**
	 * Checks whether otherLine intersects with this line.
	 * If the otherLine has more than one intersection point with this line (collinearity),
	 * <b>false</b> is returned!
	 * 
	 * @param otherLine the line that could intersect with this
	 * @return true if an intersection point was found, false otherwise.
	 */
	public boolean intersects(Line otherLine, boolean useEndPoints)
	{
		double p1x = this.p1.x;
		double p1y = this.p1.y;
		double p2x = this.p2.x;
		double p2y = this.p2.y;

		double q1x = otherLine.p1.x;
		double q1y = otherLine.p1.y;
		double q2x = otherLine.p2.x;
		double q2y = otherLine.p2.y;

		if (otherLine.isPoint() && this.isPoint())
		{
			if (this.p1.x == otherLine.p1.x && this.p1.y == otherLine.p1.y)
			{
				lastInterceptionPoint = new DataPoint(p1x, p1y);
				return true;
			}
		} else if (otherLine.isPoint() && !this.isPoint())
		{
			if (ccw(this.p1, this.p2, otherLine.p1) == 0)
			{
				// construct a point which is not on any of the lines
				DataPoint nol = new DataPoint(this.p1.x + this.p2.y - this.p1.y,
						this.p1.y - (this.p2.x - this.p1.x));

				if (Math.signum(ccw(nol, this.p1, otherLine.p1)) != Math.signum(ccw(nol, this.p2, otherLine.p1)))
				{
					lastInterceptionPoint = new DataPoint(otherLine.p1.x, otherLine.p1.y);
					return true;
				}
			}
		} else if (!otherLine.isPoint() && this.isPoint())
		{
			if (ccw(otherLine.p1, otherLine.p2, this.p1) == 0)
			{
				// construct a point which is not on any of the lines
				DataPoint nol = new DataPoint(otherLine.p1.x + otherLine.p2.y
						- otherLine.p1.y, otherLine.p1.y
						- (otherLine.p2.x - otherLine.p1.x));

				// test if the point this.p1 is on the line
				if (Math.signum(ccw(nol, otherLine.p1, this.p1)) != Math.signum(ccw(nol, otherLine.p2, this.p1)))
				{
					lastInterceptionPoint = new DataPoint(this.p1.x, this.p1.y);
					return true;
				}
			}
		} else if (!otherLine.isPoint() && !this.isPoint())
		{
			// construct a point which is not on any of the lines
			DataPoint nol = new DataPoint(otherLine.p1.x + otherLine.p2.y
					- otherLine.p1.y, otherLine.p1.y
					- (otherLine.p2.x - otherLine.p1.x));

			// test for collinearity
			if (Math.abs(ccw(this.p1, this.p2, otherLine.p1)) < Point.DOUBLE_EPS
					&& Math.abs(ccw(this.p1, this.p2, otherLine.p2)) < Point.DOUBLE_EPS)
			{
				if (ccw(nol, this.p1, otherLine.p1)
						* ccw(nol, this.p1, otherLine.p2) <= 0
						|| ccw(nol, otherLine.p1, this.p1)
						* ccw(nol, otherLine.p1, this.p2) <= 0)
				{
					// attention: might be different (true) for other definitions
					// with false, collinear lines with more than one intersection
					// point do not intersect
					return false;
				}
			} // not collinear, use lambda and mu
			else
			{
				// calculated by solving the line equation p1+lambda(p2-p1)=...
				double lambda = -((-p1y * q1x + p1x * q1y + p1y * q2x - q1y
						* q2x - p1x * q2y + q1x * q2y) / (p1y * q1x - p2y * q1x
						- p1x * q1y + p2x * q1y - p1y * q2x + p2y * q2x + p1x
						* q2y - p2x * q2y));
				double mu = -((-p1y * p2x + p1x * p2y + p1y * q1x - p2y * q1x
						- p1x * q1y + p2x * q1y) / (-p1y * q1x + p2y * q1x
						+ p1x * q1y - p2x * q1y + p1y * q2x - p2y * q2x - p1x
						* q2y + p2x * q2y));

				// use <= and >= to check for end points as well
				if(useEndPoints)
				{
					if ((lambda < 1+Point.DOUBLE_EPS && lambda > -Point.DOUBLE_EPS) && (mu < 1+Point.DOUBLE_EPS && mu > -Point.DOUBLE_EPS))
					{
						lastInterceptionPoint = new DataPoint(p1x+(p2x-p1x)*lambda, p1y+(p2y-p1y)*lambda);
						return true;
					}
				}
				else
				{
					if ((lambda < 1-Point.DOUBLE_EPS && lambda > Point.DOUBLE_EPS) && (mu < 1-Point.DOUBLE_EPS && mu > Point.DOUBLE_EPS))
					{
						lastInterceptionPoint = new DataPoint(p1x+(p2x-p1x)*lambda, p1y+(p2y-p1y)*lambda);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * return the last interception point, found by intercepts(...).
	 * @return 
	 */
	public Point lastInterceptionPoint()
	{
		return lastInterceptionPoint;
	}
	
	/**
	 * Computes the point on the line that is closest to the given point p.
	 * from: http://stackoverflow.com/questions/3120357/get-closest-point-to-a-line
	 * @param p the point that is usually not on the line (but can be)
	 * @return the point on the line that is closest to p
	 */
	public Point closestTo(Point p)
	{
		// check only pathological example: A == P
		if(p1.equals(p))
		{
			return p1;
		}
		
		Point a2p = p.sub(p1);
		Point a2b = p2.sub(p1);
		double distAB = a2b.x*a2b.x + a2b.y*a2b.y;
		double a2p_dot_a2b = a2p.x*a2b.x + a2p.y*a2b.y;
		double t = Math.min(0, Math.max(1, a2p_dot_a2b / distAB)); // normalize t to [0,1] to stay on the line segment
		
		return new Point(p1.x + a2b.x * t, p1.y + a2b.y*t);
	}

	/**
	 * Calculate the counter clockwise result for the three given points.<br>
	 * ccw(p1,p2,p3) < 0 if p3 is left of Line(p1,p2)<br>
	 * ccw(p1,p2,p3) = 0 if p3 lies on Line(p1,p2)<br>
	 * ccw(p1,p2,p3) > 0 if p3 is right of Line(p1,p2)<br>
	 * 
	 * @param p1 first point
	 * @param p2 second point
	 * @param p3 third point
	 * @return ccw(p1 p2 p3)
	 */
	public static double ccw(Point p1, Point p2, Point p3)
	{
		// return p.x * (q.x - r.y) - p.y * (q.x - r.x) + q.x * (r.y - r.x);
		return (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x);
	}
	
	@Override
	public String toString()
	{
		return "l[" + p1.toString() + "," + p2.toString() + "]";
	}

	/**
	 * Check if this line intersects anywhere with the given geometry.
	 * @param geometry
	 * @param useEndPoints
	 * @param includeEdges not used yet.
	 * @return True if intersection point was found, false if not.
	 */
	public boolean intersects(Geometry geometry, boolean useEndPoints, boolean includeEdges, boolean considerTouching) {
		
		// loop over all polygons and check if any line intersects with this line.
		for(Polygon p:geometry.getInnerPolygons())
		{
			// create closed loop
			List<Point> pointList = p.getBoundaryPoints();
			if(pointList.isEmpty())
				continue;
			
			// add points to be able to perform the double circle
			pointList.add(pointList.get(0));
			//pointList.add(pointList.get(1));
			
			int xxx = 1;
			
			// loop over the points, skipping some in the process. this implies that the polygons are convex.
			for(int skipSize = 1; skipSize<=1; skipSize++)
			{
				for (int i = 0; i < pointList.size() - (skipSize); i++) {
					Line polyLine = new Line(pointList.get(i), pointList.get(i + skipSize));
					if (this.intersects(polyLine, useEndPoints))
					{
						// if we found a match using a higher skipsize, the interception point most likely 
						// lies inside the polygon. try again with endpoints enabled.
						if(skipSize > 1)
						{
							return intersects(geometry, true, includeEdges, considerTouching);
						}
						else
						{
							// if the point is on the boundary, it might be an edge
							int[] edge_indices = new int[3];
							Point toCheck = this.lastInterceptionPoint;
							if(useEndPoints || isEdge(pointList, pointList.size()-2, toCheck, edge_indices))
							{
								return true;
							}
						}
					}
				}
			}
		}
		
		
		// loop over all inner polygons and check if the line is in some of them
		Point midPoint = p1.interpolate(p2, 0.5);
		for(Polygon p:geometry.getInnerPolygons())
		{
			if(p.contains(midPoint, false))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks whether a given point is an edge in a polygon given by a point list.
	 * @param pointList
	 * @param toCheck
	 * @param size size of the given list, if not all points should be considered
	 * @param edge_indices will be set to the indices of the points between the previous and next point of toCheck, if any.
	 * @return
	 */
	private boolean isEdge(List<Point> pointList, int size, Point toCheck, int[] edge_indices) {
		if(pointList.contains(toCheck))
		{	
			int iPindex = pointList.indexOf(toCheck);
			int iLast = ((iPindex-1)+size) % size;
			int iNext = (iPindex+1) % size;
			
			edge_indices[0] = iLast;
			edge_indices[1] = iPindex;
			edge_indices[2] = iNext;
			
			Point otherLinePoint = this.p1;
			if(otherLinePoint.equals(toCheck))
			{
				otherLinePoint = this.p2;
			}
			
			double ccwLast = ccw(this.p1, this.p2, pointList.get(iLast));
			double ccwNext = ccw(this.p1, this.p2, pointList.get(iNext));
			
			// if the two points lie on different sides of this line, the line intersects.
			if(Math.signum(ccwLast) != Math.signum(ccwNext)
			&& Math.abs(ccwLast) > Point.DOUBLE_EPS
			&& Math.abs(ccwNext) > Point.DOUBLE_EPS)
			{
				return false;
			}
			else // edge found
			{
				return true;
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * Intersect this line with the given geometry and return all intersection points, if any.
	 * @param geometry
	 * @param useEndPoints
	 * @param includeEdges if useEndpoints is true, this flag states if a line crossing edges should said
	 * to intersect with them as well. careful: edges where the line ENTERS a polygon are always considered intersection
	 * points, regardless of includeedges! 
	 * @return
	 */
	public TreeSet<Point> intersect(Geometry geometry, boolean useEndPoints, boolean includeEdges, boolean considerTouching)
	{
		TreeSet<Point> intersections = new TreeSet<Point>();
		
		// if boundary present, also intersect with it
		if(geometry.getBoundaryPoints().size() > 0)
		{
			Geometry container = new Geometry();
			Polygon boundary = new Polygon();
			for(Point p:geometry.getBoundaryPoints())
			{
				boundary.addAbsPoint(p);
			}
			container.addInnerPolygon(boundary);
			intersections = intersect(container, true, false, considerTouching);
		}
		
		// loop over all polygons and check if any line intersects with this line.
		for(Polygon p:geometry.getInnerPolygons())
		{
			// create closed loop
			List<Point> pointList = p.getBoundaryPoints();
			if(pointList.isEmpty())
				continue;
			
			// add points to be able to perform the double circle
			pointList.add(pointList.get(0));
			pointList.add(pointList.get(1));
			
			// loop over the points, skipping some in the process. this implies that the polygons are convex.
			int maxSkipSize = (includeEdges ? 2 : 1);
			for(int skipSize = 1; skipSize <= maxSkipSize; skipSize++)
			{
				for (int i = 0; i < pointList.size() - (skipSize); i++) {
					Line polyLine = new Line(pointList.get(i), pointList.get(i + skipSize));
					if (this.intersects(polyLine, useEndPoints))
					{
						// if we found a match using a higher skipsize, the interception point most likely 
						// lies inside the polygon. try again with endpoints enabled.
						if(skipSize > 1)
						{
							return intersect(geometry, true, includeEdges, considerTouching);
						}
						else
						{
							// if the point is on the boundary, it might be an edge
							Point toCheck = this.lastInterceptionPoint;
							int[] edge_indices = new int[3]; // indexes of the previous, toCheck and next point.
							boolean isEdge = isEdge(pointList, pointList.size()-2, toCheck, edge_indices);
							Point last = pointList.get(edge_indices[0]);
							Point check = pointList.get(edge_indices[1]);
							Point next = pointList.get(edge_indices[2]);
							Triangle edge = new Triangle(last, check, next, null);
							
							// TODO: this is kind of a cruel method. is it possible to come up with a better one
							// for solving the problem of touching polygons?
							Point lastToCheck = check.sub(last).normalize(1);
							Point nextToCheck = check.sub(next).normalize(1);
							
							// if edges should be included
							if(includeEdges
							// or the point is no edge
							|| !isEdge
							// or the line points away from the edge and does not intersect it
							|| (isEdge && !this.p1.equals(toCheck) && !this.p2.equals(toCheck) && this.intersects(edge, false, true, true)))
							{
								// disable touching checks for now
								if(considerTouching)
								{
									//or the line runs through two polygons without intersecting any of them, but they are touching
									if((Math.abs(ccw(p1, next, check)) < Point.DOUBLE_EPS && geometry.contains(check.add(lastToCheck), false))
									|| (Math.abs(ccw(p1, last, check)) < Point.DOUBLE_EPS && geometry.contains(check.add(nextToCheck), false)))
									{
										intersections.add(toCheck);
									}
								}
								else
								{
									intersections.add(toCheck);
								}
							}
						}
					}
				}
			}
		}
		return intersections;
	}

	/**
	 * checks for intersection with a polygon.
	 * @param polygon
	 * @param useEndPoints
	 * @return
	 */
	private boolean intersects(Polygon polygon, boolean useEndPoints, boolean includeEdges, boolean considerTouching) {
		Geometry geometry = new Geometry();
		geometry.addInnerPolygon(polygon);
		return intersects(geometry, useEndPoints, includeEdges, considerTouching);
	}

	/**
	 * Returns if the given point lies on this line.
	 * @param p
	 * @return true if the point lies on this line (including the end points), false if not.
	 */
	public boolean contains(Point p) {
		if(p1.equals(p) || p2.equals(p))
		{
			return true;
		}
		
		// colinear and between p1 and p2
		if(Math.abs(ccw(p1, p2, p)) < Point.DOUBLE_EPS && Math.abs(p1.distTo(p) + p2.distTo(p) - p1.distTo(p2)) < Point.DOUBLE_EPS)
		{
			return true;
		}
		
		return false;
	}

	/**
	 * Checks whether this line contains any of the given points. Endpoints are checked if useEndpoints is true.
	 * @param points
	 * @return
	 */
	public boolean contains(Collection<Point> points, boolean useEndpoints) {
		for(Point p:points)
		{
			if(useEndpoints && (p.equals(p1) || p.equals(p2)))
				return true;
			else
			{
				// colinear and between p1 and p2
				if(ccw(p1, p2, p) == 0 && p1.distTo(p) > 0 && p2.distTo(p) > 0 && Math.abs(p1.distTo(p) + p2.distTo(p) - p1.distTo(p2)) < 1e-10)
				{
					return true;
				}
			}
		}
		return false;
	}
}