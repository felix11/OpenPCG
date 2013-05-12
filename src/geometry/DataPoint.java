package geometry;

import java.util.Comparator;

/**
 * Point class representing one {@link Point} in 2D space with an additional double-valued data store.
 * 
 * An order can be applied with the isGreaterThan method.
 */
public class DataPoint extends Point implements Comparable<Point> {

	private double data;
	private final static double EPS_COMPARE = 1e-8;

	public DataPoint(double x, double y, int index, double data) {
		super(x,y,index);
		this.data = data;
	}
	
	public DataPoint(double x, double y, int index) {
		this(x,y,index,0);
	}
	
	public DataPoint(double x, double y) {
		this(x,y,0,0);
	}
	
	public DataPoint(Point p, double data) {
		this(p.x, p.y, p.index, data);
	}

	public DataPoint(Point p1) {
		super(p1.x, p1.y, p1.index);
		if(p1.getClass().equals(DataPoint.class))
		{
			this.data = ((DataPoint)p1).data;
		}
		else
		{
			this.data = 0.0;
		}
	}
	
	/**
	 * @return the data
	 */
	public double getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(double data) {
		this.data = data;
	}

	public String toString() {
		return "("+this.getX()+","+this.getY()+"&"+this.data+")";
	}

	/**
	 * Creates a comparator for the data values.
	 * @return
	 */
	public static Comparator<DataPoint> getComparator() {
		return new Comparator<DataPoint>()
		{

			@Override
			public int compare(DataPoint d1, DataPoint d2) {
				if (Math.abs(d1.data - d2.data) < EPS_COMPARE )
				{
					return 0;// do not compare coordinates
				}
				else if (d1.data < d2.data)
				{
					return -1;
				}
				return 1;
			}
		};
	}

	/**
	 * Creates a comparator for the data values.
	 * If the values agree, the Point.compareTo is called to compare x,y values.
	 * @return
	 */
	public static Comparator<? super DataPoint> getPointComparator() {
		return new Comparator<DataPoint>()
		{
			@Override
			public int compare(DataPoint d1, DataPoint d2) {
				if (Math.abs(d1.data - d2.data) < EPS_COMPARE )
				{
					// compare coordinates
					return d1.compareTo(d2);
				}
				else if (d1.data < d2.data)
				{
					return -1;
				}
				return 1;
			}
		};
	}
}
