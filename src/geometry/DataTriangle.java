/**
 * 
 */
package geometry;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * A triangle with additional double data at each point. 
 * @author Felix Dietrich
 *
 */
public class DataTriangle extends Triangle {

	/**
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param mp
	 */
	public DataTriangle(DataPoint p1, DataPoint p2, DataPoint p3) {
		super(p1, p2, p3);
		
		recalculateMeasurePoint();
	}
	
	/**
	 * Recalculates the measure point based on the data stored in the DataPoint s.
	 */
	private void recalculateMeasurePoint() {
		// set measure point for gradient computations
		TreeSet<DataPoint> points = new TreeSet<DataPoint>(DataPoint.getPointComparator());
		points.add((DataPoint)p1);
		points.add((DataPoint)p2);
		points.add((DataPoint)p3);
		
		// set lowest point as measure point
		this.setMeasurePoint(points.first());
	}

	/**
	 * Converts the given triangle in a DataTriangle.
	 * Note that if the triangle is already a DataTriangle, the data values are NOT copied but also set to 0.0.
	 * @param triangle
	 */
	public DataTriangle(Triangle triangle) {
		this(triangle, 0.0);
	}

	/**
	 * Converts the given triangle in a DataTriangle.
	 * Note that if the triangle is already a DataTriangle, the data values are NOT copied but also set to initialData.
	 * @param triangle the triangle to copy
	 * @param initialData data to set at each vertex
	 */
	public DataTriangle(Triangle triangle, double initialData) {
		this(new DataPoint(triangle.p1, initialData), new DataPoint(triangle.p2, initialData), new DataPoint(triangle.p3, initialData));
	}

	/**
	 * Get data at a specified point that must equal one of the triangles vertices.
	 * @param p p1,p2 or p3 specified in the constructor.
	 * @throws IllegalArgumentException if the point does not lie on any of the three vertices.
	 * @return the data value at that point
	 */
	public double getDataAt(Point p)
	{
		if(p == this.p1)
			return ((DataPoint)p1).getData();
		if(p == this.p2)
			return ((DataPoint)p2).getData();
		if(p == this.p3)
			return ((DataPoint)p3).getData();
		throw new IllegalArgumentException(p + " does not lie on any of the vertices of the given triangle.");
	}
	
	/**
	 * Set the data at the given point.
	 * @param p must be one of the vertices of this triangle. if p is not any of the vertices, nothing happens.
	 * @param data new data at p
	 */
	public void setDataAt(Point p, double data)
	{
		if(((Point)p).equals((Point)this.p1))
			((DataPoint)p1).setData(data);
		else if(((Point)p).equals((Point)this.p2))
			((DataPoint)p2).setData(data);
		else if(((Point)p).equals((Point)this.p3))
			((DataPoint)p3).setData(data);
		
		recalculateMeasurePoint();
		//else
		//	throw new IllegalArgumentException(p + " does not lie on any of the vertices of the given triangle.");
	}
	
	@Override
	public Double evaluateAt(Point toEval) {
		// plane spanned by v1 and v2
		Point v1 = p2.sub(p1);
		Point v2 = p3.sub(p1);
		
		double[] v1d = new double[]{v1.x, v1.y, ((DataPoint)p1).getData()-((DataPoint)p2).getData()};
		double[] v2d = new double[]{v2.x, v2.y, ((DataPoint)p1).getData()-((DataPoint)p3).getData()};
		
		double[] cross = new double[3];
		Point.cross(v1d,v2d, cross);
		double k = cross[2]*((DataPoint)p1).getData();
		
		return 1/cross[2]*(k-cross[0]*p1.sub(toEval).x-cross[1]*p1.sub(toEval).y);
	}

	public Collection<? extends DataPoint> getDataPoints() {
		List<DataPoint> dataPoints = new LinkedList<DataPoint>();
		dataPoints.add((DataPoint)p1);
		dataPoints.add((DataPoint)p2);
		dataPoints.add((DataPoint)p3);
		
		return dataPoints;
	}
}
