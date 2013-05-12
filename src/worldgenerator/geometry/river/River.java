/**
 * 
 */
package worldgenerator.geometry.river;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import worldgenerator.util.factory.WorldObject;

/**
 * @author Felix Dietrich
 *
 */
public class River extends WorldObject {
	/**
	 * Rivers that add water to this river by having sinks equal to one of this.vertices
	 */
	private Collection<River> connectedSources;
	/**
	 * The unique source of this river.
	 */
	private GridCellRiverVertex source;
	/**
	 * The unique sink of this river.
	 */
	private GridCellRiverVertex sink;
	/**
	 * A set of grid cells that this river passes through.
	 * Must contain source and sink, should contain sinks of all connectedSources.
	 */
	private List<GridCellRiverVertex> vertices;
	
	public River(GridCellRiverVertex source, GridCellRiverVertex sink) {
		this.sink = sink;
		this.source = source;
		this.vertices = new LinkedList<GridCellRiverVertex>();
		this.vertices.add(source);
		this.vertices.add(sink);
		
		this.connectedSources = new LinkedList<River>();
	}
	
	/**
	 * Adds a connected source to this river and adds the sink of sourceRiver to the vertex list.
	 * @param sourceRiver
	 */
	public void addConnectedSource(River sourceRiver)
	{
		this.connectedSources.add(sourceRiver);
		this.addVertex(sourceRiver.sink);
	}

	public GridCellRiverVertex getSink() {
		return this.sink;
	}
	
	public GridCellRiverVertex getSource()
	{
		return this.source;
	}

	public Collection<River> getConnectedRivers() {
		return connectedSources;
	}

	public List<GridCellRiverVertex> getVertices() {
		return vertices;
	}

	/**
	 * Adds a vertex right before the sink.
	 * @param newVertex
	 */
	public void addVertex(GridCellRiverVertex newVertex) {
		GridCellRiverVertex sink = this.vertices.remove(this.vertices.size()-1);
		this.vertices.add(newVertex);
		this.vertices.add(sink);
	}

	public void addVertex(GridCellRiverVertex p1, GridCellRiverVertex newVertex) {
		int ind = this.vertices.indexOf(p1);
		if(ind < 0)
		{
			throw new IllegalArgumentException("Vertex " + p1 + " is not part of this river.");
		}
		this.vertices.add(ind+1, newVertex);
	}
}
