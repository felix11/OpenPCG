/**
 * 
 */
package worldgenerator.util.grid;

import java.nio.ByteBuffer;

import worldgenerator.util.grid.GridFactory.GridAttributes;

/**
 * @author Felix
 *
 */
public class ImageBasedGridSubdivision implements ISubdivisionAlgorithm<Double>
{
	private final int seed;
	
	public ImageBasedGridSubdivision(int seed)
	{
		this.seed = seed;
	}
	
	/**
     * Get the grayscale value, or override in your own sub-classes
     */
    protected float calculateHeight(float red, float green, float blue) {
        return (float) (0.299 * red + 0.587 * green + 0.114 * blue);
    }
    
    private float byte2float(byte b){
        return ((float)(b & 0xFF)) / 255f;
    }
    
    public byte[] toByteArray(double value, int precision) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value * Math.pow(10,precision));
        return bytes;
    }

	@Override
	public Grid2DLayer<Double> createNewLayer(final int seed, final Grid2DLayer<Double> oldLayer, final int subdivisionsPerLevel)
	{
		GridAttributes attributes = new GridAttributes(oldLayer.rows() * subdivisionsPerLevel, oldLayer.cols() * subdivisionsPerLevel, seed);
		Grid2DLayer<Double> result = null;
		
		/*GridFactory.create2D(GridType.DOUBLE_2D, attributes);
		
		final BufferedImage before = new BufferedImage(oldLayer.cols(), oldLayer.rows(), BufferedImage.TYPE_INT_ARGB);
		
		// store the old layer in the image, scale it afterwards
		oldLayer.iterate(new Grid2DIterator<Double>()
		{
			@Override
			public void step(int row, int col, GridCell<Double> gridCell, Grid2D<Double> grid2d)
			{
				byte[] buf = toByteArray(gridCell.getData(), 5);
				//grid2d.setDataAt(row, col, oldLayer.getDataAt(row / subdivisionsPerLevel, col / subdivisionsPerLevel));
				before.setRGB(col, row, new Color(byte2float(buf[0]), byte2float(buf[1]), byte2float(buf[2]), byte2float(buf[3])).getRGB());
			}
		});
		
		// scale it
		final BufferedImage after = new BufferedImage(result.cols(), result.rows(), BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(2.0, 2.0);
		AffineTransformOp scaleOp =  new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		scaleOp.filter(before, after);
		
		// store the scaled image data in the result
		result.iterate(new Grid2DIterator<Double>()
		{
			@Override
			public void step(int row, int col, GridCell<Double> gridCell, Grid2D<Double> grid2d)
			{
				Color c = new Color(after.getRGB(col, row));
				float[] rgb = c.getRGBComponents(null);
				grid2d.setDataAt(row, col, new GridCellDouble((double)calculateHeight(rgb[0], rgb[1], rgb[2])));
			}
		});*/
		
		return result;
	}
}
