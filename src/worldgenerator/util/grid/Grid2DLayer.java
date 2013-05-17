package worldgenerator.util.grid;


/**
 * Represents a layer in a MipMapGrid2D.
 * Stores several grids in a two dimensional array.
 * 
 * @author Felix Dietrich
 *
 */
public class Grid2DLayer<U> extends Grid2D<Grid2D<U>>
{
	public Grid2DLayer(int rows, int cols, Grid2D<U> fillTemplate)
	{
		this(rows, cols, new GridCellGrid2D<U>(fillTemplate));
	}
	
	public Grid2DLayer(Grid2D<U> grid)
	{
		this(1, 1, grid);
	}

	public Grid2DLayer(int rows, int cols, GridCellGrid2D<U> fillTemplate)
	{
		super(rows, cols, fillTemplate);
	}

	@Override
	public Grid2DLayer<U> clone()
	{
		Grid2DLayer<U> copy = new Grid2DLayer<U>(this.rows(), this.cols(), (GridCellGrid2D<U>) this.getDataAt(0, 0));
		for(int r=0; r<rows(); r++)
		{
			for(int c=0; c<cols(); c++)
			{
				copy.setDataAt(r,c,getDataAt(r, c).clone());
			}
		}
		
		return copy;
	}

	/**
	 * The grid at 0,0.
	 * @return returns getDataAt(0,0) of the current layer
	 */
	public Grid2D<U> getBaseGrid()
	{
		return this.getDataAt(0, 0).getData();
	}
}
