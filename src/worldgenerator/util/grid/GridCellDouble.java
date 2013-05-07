/**
 * 
 */
package worldgenerator.util.grid;


/**
 * @author Felix Dietrich
 *
 */
public class GridCellDouble extends GridCell<Double> {

	private int precision = 4;
	
	public GridCellDouble(Double data)
	{
		super(data);
	}

	public GridCellDouble(double data, int precision) {
		super(data);
		this.precision = precision;
	}
	
	@Override
	public String toString() {
		return String.format("%.0" + precision + "f", this.data);
	}
	
	@Override
	protected GridCell<Double> clone() {
		return new GridCellDouble(this.data, this.precision);
	}

	@Override
	public GridCell<Double> mult(GridCell<Double> toMult)
	{
		return new GridCellDouble(data * toMult.data);
	}

	@Override
	public GridCell<Double> add(GridCell<Double> toAdd)
	{
		return new GridCellDouble(data + toAdd.data);
	}
}
