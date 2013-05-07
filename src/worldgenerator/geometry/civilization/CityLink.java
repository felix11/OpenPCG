/**
 * 
 */
package worldgenerator.geometry.civilization;

/**
 * Represents a link between two cities.
 * @author Felix Dietrich
 *
 */
public class CityLink
{
	private final City c1;
	private final City c2;
	private double distance = 0.0;
	
	/**
	 * @param c1
	 * @param c2
	 * @param distance
	 */
	public CityLink(City c1, City c2, double distance)
	{
		this.c1 = c1;
		this.c2 = c2;
		this.distance = distance;
	}

	public double getDistance()
	{
		return distance;
	}
	
	
}
