/**
 * 
 */
package worldgenerator.geometry;

/**
 * @author Felix Dietrich
 *
 */
public class Point3D implements Comparable<Point3D> {
	private static final double DOUBLE_EPS = 1e-8;
	public final double x;
	public final double y;
	public final double z;
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static Point3D lerp(Point3D p1,
			Point3D p2, double f) {
		return new Point3D((1-f)*p1.x+f*p2.x, (1-f)*p1.y+f*p2.y, (1-f)*p1.z+f*p2.z);
	}

	public double distTo(Point3D p) {
		return Math.sqrt((this.x-p.x)*(this.x-p.x) + (this.y-p.y)*(this.y-p.y) + (this.z-p.z)*(this.z-p.z));
	}

	public Point3D add(Point3D toAdd) {
		return new Point3D(this.x + toAdd.x, this.y + toAdd.y, this.z + toAdd.z);
	}

	public Point3D sub(Point3D toSub) {
		return add(new Point3D(-toSub.x, -toSub.y, -toSub.z));
	}

	public Point3D cross(Point3D p2) {
		return new Point3D(this.y*p2.z-this.z*p2.y, this.z*p2.x-this.x*p2.z, this.x*p2.y-this.y*p2.x);
	}
	
	@Override
	public String toString() {
		return String.format("%f:%f:%f", x,y,z);
	}

	@Override
	public int compareTo(Point3D o) {
		if((this.x - o.x) > Point3D.DOUBLE_EPS)
		{
			return 1;
		}
		else if(Math.abs(this.x - o.x) < Point3D.DOUBLE_EPS
			&&  (this.y - o.y) > Point3D.DOUBLE_EPS)
		{
			return 1;
		}
		else if(Math.abs(this.x - o.x) < Point3D.DOUBLE_EPS
				&& Math.abs(this.y - o.y) < Point3D.DOUBLE_EPS
				&& (this.z - o.z) > Point3D.DOUBLE_EPS)
			{
				return 1;
			}
		else if(Math.abs(this.x - o.x) < Point3D.DOUBLE_EPS
				&& Math.abs(this.y - o.y) < Point3D.DOUBLE_EPS
				&& Math.abs(this.z - o.z) < Point3D.DOUBLE_EPS)
			{
				return 0;
			}
		
		return -1;
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

	public Point3D normalize(double fac) {
		return this.normalize().mult(fac);
	}
	
	public Point3D mult(double fac)
	{
		return new Point3D(this.x*fac, this.y*fac, this.z*fac);
	}

	private double length() {
		return Math.sqrt(this.x*this.x+this.y*this.y+this.z*this.z);
	}
}
