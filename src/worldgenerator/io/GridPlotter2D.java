/**
 * 
 */
package worldgenerator.io;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import worldgenerator.util.grid.Grid2D;
import worldgenerator.util.grid.GridCell;
import worldgenerator.util.grid.GridCellDouble;
import worldgenerator.util.visualization.IOUtils;

/**
 * @author Felix Dietrich
 *
 */
public class GridPlotter2D<T extends Comparable<T>> extends APlotter {

	private static final Color TRANSPARENT = new Color(0,0,0,0);
	private Grid2D<T> grid;

	/**
	 * 
	 */
	public GridPlotter2D(Grid2D<T> grid) {
		this.grid = grid;
	}

	/* (non-Javadoc)
	 * @see earthgenerator.visualization.IPlotter#plot()
	 */
	@Override
	public String plot2string() {
		if(grid == null)
		{
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		for(int rowInd=0; rowInd < grid.rows(); rowInd++)
		{
			GridCell<T>[] row = grid.getRow(rowInd);
			
			for(GridCell<T> cell : row)
			{
				sb.append(cell.toString());
				sb.append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(System.lineSeparator());
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	public void plot2image(BufferedImage image) throws IOException
	{
		if(grid == null)
		{
			return;
		}
		
		int height = grid.rows();
		int width = grid.cols();
		int[] rgbArray = new int[height * width];
		
		Graphics g = image.getGraphics();
		
		for(int row=0; row < grid.rows(); row++)
		{
			for(int col=0; col < grid.cols(); col++)
			{
				GridCell<T> cell = grid.getDataAt(row, col);
				float d = (float)Math.max(0, Math.min(1.0, Double.parseDouble(cell.toString())));
				rgbArray[row * grid.cols() + col] = new Color(d,d,d).getRGB();
			}
		}

		image.setRGB(0, 0, width, height, rgbArray, 0, width);
		
		//g.setColor(Color.WHITE);
		//g.fillRect(0, 0, width, height);
		//g.drawImage(image, 0, 0, TRANSPARENT, null);
	}

}
