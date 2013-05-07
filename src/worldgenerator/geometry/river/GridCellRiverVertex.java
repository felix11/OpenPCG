/**
 * 
 */
package worldgenerator.geometry.river;

import java.util.Comparator;

import worldgenerator.geometry.Point3D;
import worldgenerator.util.grid.GridCell;

/**
 * Represents a vertex of a river.
 * This is both a 3D coordinate and a flow control.
 * 
 * @author Felix Dietrich
 */
public class GridCellRiverVertex extends GridCell<Point3D> {
	private Point3D position;
	private double flux;

	public GridCellRiverVertex(Point3D position, double flux) {
		super(position);
		this.flux = flux;
	}

	@Override
	protected GridCell<Point3D> clone() {
		return new GridCellRiverVertex(this.position, this.flux);
	}

	public double getFlux() {
		return this.flux;
	}

	public Point3D getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		return String.format("%s#%f", this.position.toString(), this.flux);
	}

	@Override
	public GridCell<Point3D> mult(GridCell<Point3D> toMult)
	{
		throw new UnsupportedOperationException("mult is not implemented for a GridCellRiverVertex.");
	}

	@Override
	public GridCell<Point3D> add(GridCell<Point3D> toAdd)
	{
		throw new UnsupportedOperationException("add is not implemented for a GridCellRiverVertex.");
	}
}
