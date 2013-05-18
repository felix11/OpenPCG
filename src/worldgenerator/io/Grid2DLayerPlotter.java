/**
 * 
 */
package worldgenerator.io;

import geometry.Point;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import worldgenerator.util.grid.Grid2D;
import worldgenerator.util.grid.Grid2D.Grid2DIterator;
import worldgenerator.util.grid.Grid2DLayer;
import worldgenerator.util.grid.GridCell;

/**
 * @author Felix Dietrich
 *
 */
public class Grid2DLayerPlotter<U> extends APlotter
{
	private Grid2DLayer<U> grid2DLayer;
	
	/**
	 * @param grid2dLayer the layer to plot
	 */
	public Grid2DLayerPlotter(Grid2DLayer<U> grid2dLayer)
	{
		grid2DLayer = grid2dLayer;
	}

	/**
	 * Plots the whole grid into one string.
	 * This generates a mipmapped object representation that can be stored in one file only.
	 * For separate files, use plot2strings().
	 * @see worldgenerator.io.APlotter#plot2string()
	 */
	@Override
	public String plot2string()
	{
		Map<Point,String> gridStrings = plot2strings();
		StringBuilder sb = new StringBuilder("<Grid2DLayer_Full>");
		for(Entry<Point,String> entry : gridStrings.entrySet())
		{
			sb.append(entry.getKey().toString());
			sb.append(System.lineSeparator());
			sb.append(entry.getValue());
			sb.append(System.lineSeparator());
		}
		sb.append("</Grid2DLayer_Full>");
		
		return sb.toString();
	}
	
	/**
	 * Plots each grid to a string and stores them in a map with key = (row,col), value = (string representation of grid).
	 * @return the map of (point, string) representing the individual grids of this layer. the point has format point(row, col).
	 */
	public Map<Point,String> plot2strings()
	{
		final Map<Point,String> gridStrings = new HashMap<Point,String>();
		
		// iterate over all grids in this layer, adding their string representation to the result list.
		grid2DLayer.iterate(new Grid2DIterator<Grid2D<U>>()
		{
			@Override
			public void step(int row, int col, GridCell<Grid2D<U>> gridCell, Grid2D<Grid2D<U>> grid2d)
			{
				Grid2DPlotter<U> gridPlotter = new Grid2DPlotter<U>(gridCell.getData());
				gridStrings.put(new Point(row, col), gridPlotter.plot2string());
			}
		});
		
		return gridStrings;
	}
	
	/**
	 * Plots the grids of this layer to individual files.
	 * The filename represents the position of the grid: &lt;filename_base&gt;_&lt;row&gt;_&lt;col&gt;.txt
	 * @param filename_base
	 * @throws IOException
	 */
	public void plot2files(final String filename_base) throws IOException
	{
		// iterate over all grids in this layer, adding their string representation to the result list.
		grid2DLayer.iterate(new Grid2DIterator<Grid2D<U>>()
		{
			@Override
			public void step(int row, int col, GridCell<Grid2D<U>> gridCell, Grid2D<Grid2D<U>> grid2d)
			{
				Grid2DPlotter<U> gridPlotter = new Grid2DPlotter<U>(gridCell.getData());
				String filename = String.format("%s_%d_%d.txt", filename_base, row, col);
				try
				{
					gridPlotter.plot2file(filename);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
