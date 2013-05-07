/**
 * 
 */
package worldgenerator.geometry.civilization;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import worldgenerator.geometry.WorldObject;
import worldgenerator.util.grid.GridCell;

/**
 * Represents a city.
 * @author Felix Dietrich
 *
 */
public class City extends WorldObject
{

	/**
	 * @return the height
	 */
	public GridCell<Double> getHeight()
	{
		return height;
	}

	/**
	 * @return the row
	 */
	public int getRow()
	{
		return row;
	}

	/**
	 * @return the col
	 */
	public int getCol()
	{
		return col;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the soil quality
	 */
	public double getSoilQuality()
	{
		return soilQuality;
	}

	public void setSoilQuality(double soil)
	{
		this.soilQuality = soil;
	}

	/**
	 * @return the population
	 */
	public int getPopulation()
	{
		return population;
	}

	private final GridCell<Double> height;
	private final int row;
	private final int col;
	private final GridCell<Integer> ID;
	private final String name;
	private final Map<City, CityLink> links;
	private double soilQuality;
	private int population;

	public City(GridCell<Integer> ID, int row, int col, GridCell<Double> height)
	{
		this.ID = ID;
		this.name = "";
		this.row = row;
		this.col = col;
		this.height = height;
		this.links = new HashMap<City, CityLink>();
		this.soilQuality = 0.0;
		this.population = 0;
	}
	
	public void addLink(City c, double strength)
	{
		this.links.put(c, new CityLink(this, c, strength));
	}

	public GridCell<Integer> getID()
	{
		return this.ID;
	}

	@Override
	public int hashCode()
	{
		return ID.getData().hashCode();
	}

	public Map<City, CityLink> getLinks()
	{
		return links;
	}

	public void setPopulationSize(int population)
	{
		this.population = population;
	}
}