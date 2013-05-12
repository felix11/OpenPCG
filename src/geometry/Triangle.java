package geometry;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A triangle.
 * Points must be given in counter clockwise manner to get correct inward facing normals.
 * 
 * @author Felix Dietrich
 */
public class Triangle extends Polygon {
	
	public final Point p1;
	public final Point p2;
	public final Point p3;
	
	/**
	 * Neighboring triangles of point 1
	 */
	public final List<Triangle> neighbors1;
	/**
	 * Neighboring triangles of point 2
	 */
	public final List<Triangle> neighbors2;
	/**
	 * Neighboring triangles of point 3
	 */
	public final List<Triangle> neighbors3;
	
	/**
	 * The point where the distance is measured from.
	 */
	public DataPoint measurePoint;

	/**
	 * Creates a triangle.
	 * Points must be given in ccw order.
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param mp datapoint, holding the data of this triangle
	 */
	public Triangle(Point p1, Point p2, Point p3, DataPoint mp) {
		this.addAbsPoint(p1);
		this.addAbsPoint(p2);
		this.addAbsPoint(p3);
		
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.measurePoint = mp;
		
		this.neighbors1 = new LinkedList<Triangle>();
		this.neighbors2 = new LinkedList<Triangle>();
		this.neighbors3 = new LinkedList<Triangle>();
	}

	/**
	 * Creates a triangle.
	 * Points must be given in ccw order.
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	public Triangle(Point p1, Point p2, Point p3) {
		this(p1,p2,p3,new DataPoint(p1, 0.0));
	}

	public Point midPoint() {
		return new Point((p1.x + p2.x + p3.x)/3.0, (p1.y + p2.y + p3.y)/3.0);
	}

	public boolean isLine() {
		Line l1 = new Line(p1,p2);
		Line l2 = new Line(p1,p3);
		Line l3 = new Line(p2,p3);
		
		return l1.contains(p3) || l2.contains(p2) || l3.contains(p1);
	}

	/**
	 * Sets the measure point.
	 * @param newMeasurePoint
	 */
	public void setMeasurePoint(DataPoint newMeasurePoint) {
		this.measurePoint = newMeasurePoint;
	}

	public Double evaluateAt(Point toEval) {
		return this.measurePoint.getData() + this.measurePoint.distTo(toEval);
	}
	
	/**
	 * Computes the inward facing normal vector for the given points of the triangle.
	 * @param p1
	 * @param p2
	 * @return inward facing normal vector
	 */
	public Point getNormal(Point p1, Point p2)
	{
		Point normal = new Point(p2.y-p1.y, -(p2.x-p1.x));
		// if the normal is already inward facing, return it
		if(Line.ccw(p1, p2, normal) == Line.ccw(p1, p2, this.midPoint()))
		{
			return normal;
		}
		// otherwise, reflect it
		else
		{
			return normal.multiply(-1);
		}
	}
}
