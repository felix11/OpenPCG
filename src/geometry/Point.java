package geometry;

/**
 * Point class representing one point in 2D space.
 * An order can be applied with the isGreaterThan method.
 */
public class Point implements Comparable<Point>  {

	public final double x;
	public final double y;
	public final int index;
	
	public static final Point ZERO = new Point(0,0);
	
	/**
	 * Constant for comparison of double values. Everything below this is considered equal.
	 */
	public static final double DOUBLE_EPS = 1e-8;
	
	public Point(double x, double y, int index) {
		this.x = x;
		this.y = y;
		this.index = index;
	}
	
	public Point(double x, double y) {
		this(x,y,-1);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public Point add(Point c)
	{
		return new Point(this.x + c.getX(), this.y + c.getY());
	}

	public Point sub(Point p) {
		return add(new Point(-p.x, -p.y));
	}
	
	/**
	 * Normalizes the given point (now considered a vector) to the given length.
	 * @param length
	 * @return
	 */
	public Point normalize(double length)
	{
		return new Point(x/distTo(ZERO)*length, y/distTo(ZERO)*length);
	}
	
	/**
	 * Euclidian distance to a given point.
	 * @param p
	 * @return
	 */
	public double distTo(Point p)
	{
		double x = this.x - p.x;
		double y = this.y - p.y;
		return Math.sqrt(x*x+y*y);
	}
	
	/**
	 * Computes the angle between the x-axis through the given point "center" and this.
	 * @param p2
	 * @return angle in radians, atan2(this.y-center.y, this.x-center.x)+pi.
	 */
	public double angleTo(Point center)
	{
		return (Math.atan2(this.y-center.y, this.x-center.x) + Math.PI);
	}
	
	/**
	 * Interpolates a point between this to a given target with the given factor.<br>
	 * Formula: result = this + factor*(target-this)
	 * 
	 * @param target
	 * @param factor
	 * @return
	 */
	public Point interpolate(Point target, double factor)
	{
		Point result = new Point(x+factor*(target.x-x), y+factor*(target.y-y));
		return result;
	}

	/**
	 * Checks wether the given point is greater than the current point with respect to:<br>
	 * 1. x-coordinate
	 * -> 2. y-coordinate
	 * 
	 * @param p point to compare with
	 * @return true if the current point is greater than p, false otherwise.
	 */
	public boolean isGreaterThan(Point p) {
		if(this.x > p.x)
			return true;
		if(Math.abs(this.x - p.x) < DOUBLE_EPS)
			if(this.y > p.y)
				return true;
		return false;
	}

	/**
	 * Checks wether the given point is greater than the current point with respect to:<br>
	 * 1. x-coordinate
	 * -> 2. y-coordinate
	 * 
	 * @param p point to compare with
	 * @return 1 if the current point is greater than p, -1 if smaller, 0 otherwise.
	 */
	@Override
	public int compareTo(Point p) {
		if(Math.abs(this.x - p.x) < DOUBLE_EPS)
		{
			if(Math.abs(this.y - p.y) < DOUBLE_EPS)
			{
				return 0;
			}
			else
			{
				if(this.y > p.y)
				{
					return 1;
				}
			}
		}
		else
		{
			if(this.x > p.x)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
		return -1;
	}
	
	/**
	 * Uses compareTo to implement the object.equals method.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		Point objP = (Point)obj;
		
		if(this.compareTo(objP) == 0)
			return true;
		return false;
	};
	
	@Override
	public int hashCode() {
		return (int)(this.x * 93563 + this.y);
	}
	
	public String toString() {
		return "("+this.getX()+","+this.getY()+")";
	}

	public Point multiply(double factor) {
		return new Point(this.x * factor, this.y * factor);
	}

	/**
	 * The (smallest possible) angle at C from the triangle ACB.
	 * @param A
	 * @param C
	 * @param B
	 * @return
	 */
	public static double angle(Point A, Point C, Point B) {
		double phi1 = A.angleTo(C);
		double phi2 = B.angleTo(C);
		double phi = Math.abs(phi1-phi2);
		return Math.min(phi, 2*Math.PI - phi);
	}

	/**
	 * Computes the cross product of two vectors and stores it in the cross vector.
	 * @param v1
	 * @param v2
	 * @param cross
	 */
	public static void cross(double[] v1, double[] v2, double[] cross) {
		cross[0] = v1[1]*v2[2]-v1[2]*v2[1];
		cross[1] = v1[2]*v2[0]-v1[0]*v2[2];
		cross[2] = v1[0]*v2[1]-v1[1]*v2[0];
	}

	/**
	 * Returns a new point, wich is the given point rotated around the center by the angle phi.
	 * 
	 * @param center
	 * @param phi
	 * @return
	 */
	public Point rotate(Point center, double phi) {
		// rotate using polar coordinates
		double rad = this.distTo(center);
		double angle = this.angleTo(center) - Math.PI + phi;
		
		return new Point(center.x + rad * Math.cos(angle), center.y + rad * Math.sin(angle));
	}
}
