/**
 * 
 */
package worldgenerator.geometry.civilization;

import geometry.Point3D;

import java.util.LinkedList;
import java.util.Random;

import worldgenerator.util.factory.WorldObject;

/**
 * A road is composed of a list of Point3D objects.
 * @author Felix Dietrich
 *
 */
public class Road extends WorldObject
{
	private LinkedList<Point3D> vertices;

	/**
	 * @param vertices
	 */
	public Road(LinkedList<Point3D> vertices)
	{
		this.vertices = new LinkedList<Point3D>(vertices);
	}

	/**
	 * Returns the list containing the road vertices.
	 * @return
	 */
	public LinkedList<Point3D> getVertices()
	{
		return this.vertices;
	}

	public void addVertex(Point3D next)
	{
		this.vertices.add(next);
	}

	/**
	 * Returns a list of n vertices sampled between all road vertices.
	 * Can be used to draw the road.
	 * @param n
	 * @return
	 */
	public LinkedList<Point3D> getVertices(int n)
	{
		LinkedList<Point3D> result = new LinkedList<Point3D>();
		
		Random r = new Random(0);
		
		for(int i=0; i<vertices.size(); i++)
		{/*
			double factor = i/(double)n;
			int last = (int)Math.floor(factor);
			int next = last+1;
			double lastF = last / (double)vertices.size();
			double nextF = next / (double)vertices.size();
			double localFactor = (factor-lastF)/(nextF-lastF);
			result.add(Point3D.lerp(vertices.get(last), vertices.get(next), localFactor));*/
			double factor = i/(double)vertices.size();
			for(int k=0; k<n; k++)
			{
				int last = (int)Math.floor(factor*vertices.size());
				int next = Math.min(vertices.size()-1,last+1);
				double lfactor = k/(double)n;
				result.add(Point3D.lerp(vertices.get(last), vertices.get(next), lfactor));
			}
		}
		
		return result;
	}
}
