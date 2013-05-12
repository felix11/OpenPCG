/**
 * 
 */
package geometry;

/**
 * A point in 3D.
 * Similar to {@link Point}.
 * 
 * @author Felix Dietrich
 *
 */
public class Point3D implements Comparable<Point3D> {

	public final double x;
	public final double y;
	public final double z;
	public final int index;
	
	public static final Point3D ZERO = new Point3D(0,0,0);
	
	/**
	 * Constant for comparison of double values. Everything below this is considered equal.
	 */
	public static final double DOUBLE_EPS = 1e-8;

	public static Point3D lerp(Point3D p1,
			Point3D p2, double f) {
		return new Point3D((1-f)*p1.x+f*p2.x, (1-f)*p1.y+f*p2.y, (1-f)*p1.z+f*p2.z);
	}

	/**
	 * Normalizes the given vector to one. If it is the zero vector, it is returned unchanged.
	 * @return
	 */
	public Point3D normalize() {
		double length = this.length();
		if(length < Point3D.DOUBLE_EPS)
			return this;
		return new Point3D(this.x/length, this.y/length, this.z/length);
	}
	
	public Point3D mult(double fac)
	{
		return new Point3D(this.x*fac, this.y*fac, this.z*fac);
	}

	private double length() {
		return Math.sqrt(this.x*this.x+this.y*this.y+this.z*this.z);
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public Point3D(double x, double y, double z, int index) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.index = index;
	}
	
	public Point3D(double x, double y, double z) {
		this(x,y,z,-1);
	}
	
	public Point3D add(Point3D c)
	{
		return new Point3D(this.x + c.x, this.y + c.y, this.z + c.z, -1);
	}

	public Point3D sub(Point3D p) {
		return add(new Point3D(-p.x, -p.y, -p.z, -1));
	}
	
	/**
	 * Normalizes the given point (now considered a vector) to the given length.
	 * If it is the zero vector, it is returned unchanged.
	 * @param length
	 * @return
	 */
	public Point3D normalize(double length)
	{
		if(this.equals(ZERO))
		{
			return ZERO;
		}
		return new Point3D(x/distTo(ZERO)*length, y/distTo(ZERO)*length, z/distTo(ZERO)*length);
	}
	
	/**
	 * Euclidian distance to a given point.
	 * @param p
	 * @return
	 */
	public double distTo(Point3D p)
	{
		double x = this.x - p.x;
		double y = this.y - p.y;
		double z = this.z - p.z;
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	/**
	 * Interpolates a point between this to a given target with the given factor.<br>
	 * Formula: result = this + factor*(target-this)
	 * 
	 * @param target
	 * @param factor
	 * @return
	 */
	public Point3D interpolate(Point3D target, double factor)
	{
		Point3D result = new Point3D(x+factor*(target.x-x), y+factor*(target.y-y),z+factor*(target.z-z));
		return result;
	}

	/**
	 * Checks whether the given point is greater than the current point with respect to:<br>
	 * 1. x-coordinate
	 * -> 2. y-coordinate
	 *   -> 3. z-coordinate
	 * 
	 * @param p point to compare with
	 * @return true if the current point is greater than p, false otherwise.
	 */
	public boolean isGreaterThan(Point3D p) {
		if(this.x > p.x + DOUBLE_EPS)
			return true;
		if(Math.abs(this.x - p.x) < DOUBLE_EPS && this.y > p.y + DOUBLE_EPS)
			return true;
		if(Math.abs(this.x - p.x) < DOUBLE_EPS && Math.abs(this.y - p.y) < DOUBLE_EPS && this.z > p.z + DOUBLE_EPS)
			return true;
		return false;
	}

	/**
	 * Checks wether the given point is greater than the current point with respect to:<br>
	 * 1. x-coordinate
	 * -> 2. y-coordinate
	 * -> -> 3. z-coordinate
	 * 
	 * @param p point to compare with
	 * @return 1 if the current point is greater than p, -1 if smaller, 0 otherwise.
	 */
	@Override
	public int compareTo(Point3D p) {
		if(isGreaterThan(p))
			return 1;
		if(Math.abs(p.x-this.x) < DOUBLE_EPS && Math.abs(p.y-this.y) < DOUBLE_EPS && Math.abs(p.z-this.z) < DOUBLE_EPS)
			return 0;
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
		Point3D objP = (Point3D)obj;
		
		if(this.compareTo(objP) == 0)
			return true;
		return false;
	};
	
	@Override
	public int hashCode() {
		return (int)(this.x * 93563 + this.y * 17 + this.z);
	}
	
	public String toString() {
		return "("+this.x+","+this.y+","+this.z+")";
	}

	public Point3D multiply(double factor) {
		return new Point3D(this.x * factor, this.y * factor, this.z * factor);
	}

	/**
	 * Computes the cross product of two vectors and stores it in the cross vector.
	 * @param v1
	 * @param v2
	 * @param cross
	 */
	public Point3D cross(Point3D p2) {
		return new Point3D(this.y*p2.z-this.z*p2.y, this.z*p2.x-this.x*p2.z, this.x*p2.y-this.y*p2.x);
	}

	/**
	 * Computes a rotated point around the given center.
	 * @param angle
	 * @param center
	 * @return
	 */
	public Point3D rotate2D(double angle, Point3D center)
	{
		// rotate using polar coordinates
		double rad = this.distTo(center);
		double phi = this.angleTo2D(center) - Math.PI + angle;
		
		return new Point3D(center.x + rad * Math.cos(phi), center.y + rad * Math.sin(phi), this.z);
	}
	
	/**
	 * Computes the angle between the x-axis through the given point "center" and this.
	 * @param p2
	 * @return angle in radians, atan2(this.y-center.y, this.x-center.x)+pi.
	 */
	public double angleTo2D(Point3D center)
	{
		return (Math.atan2(this.y-center.y, this.x-center.x) + Math.PI);
	}
}
