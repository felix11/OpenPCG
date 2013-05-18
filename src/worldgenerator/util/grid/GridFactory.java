package worldgenerator.util.grid;

import worldgenerator.util.noise.PerlinNoiseMap;

public class GridFactory {
	
	public static class GridAttributes
	{

		public final int height;
		public final int width;
		public final int seed;
		
		public double factor = 1.0;
		public double scale = 1.0;

		/**
		 * @param height
		 * @param width
		 * @param seed
		 */
		public GridAttributes(int height, int width, int seed)
		{
			this.height = height;
			this.width = width;
			this.seed = seed;
		}

		/**
		 * @param height
		 * @param width
		 * @param seed
		 * @param factor
		 */
		public GridAttributes(int height, int width, int seed, double factor)
		{
			this(height, width, seed);
			this.factor = factor;
		}

		public GridAttributes(GridAttributes copyAttributes)
		{
			this(copyAttributes.height, copyAttributes.width, copyAttributes.seed, copyAttributes.factor);
		}
	}
	
	public static ComparableGrid2D<Double> create2D(GridType gridType, GridAttributes attributes)
	{
		ComparableGrid2D<Double> result = null;
		double max = 0;
		double min = 0;
		
		switch(gridType)
		{
		case DOUBLE_2D:
			result = new ComparableGrid2D<Double>(attributes.height, attributes.width, new GridCellDouble(0.0));
			break;
		case PERLIN_NOISE_2D:
			result = new ComparableGrid2D<Double>(attributes.height, attributes.width, new GridCellDouble(0.0));
			generatePerlinNoiseGrid2D(result, attributes.seed);
			max = result.getMaximum().getData();
			min = result.getMinimum().getData();
			rescaleGrid2D(result, min, max);
			result.mult(new GridCellDouble(2.0));
			result.add(new GridCellDouble(-1.0));
			break;
		case SPARSE_PERLIN_NOISE_2D:
			result = create2D(GridType.PERLIN_NOISE_2D, attributes);
			result.clamp(result.getMaximum().getData() * (1.0-attributes.factor), result.getMaximum().getData());

			// rescale to 0...1
			max = result.getMaximum().getData();
			min = result.getMinimum().getData();
			result.add(new GridCellDouble(-min));
			result.mult(new GridCellDouble(1.0/(max-min)));
		default:
			break;
		}
		return result;
	}

	private static void rescaleGrid2D(ComparableGrid2D<Double> result, double min, double max)
	{
		double rows = result.rows();
		double cols = result.cols();
		
		for(int r=0; r<rows; r++)
		{
			for(int c=0; c<cols; c++)
			{
				// get data
				double data = result.getDataAt(r, c).getData();
				// scale data
				data = (data-min)/(max-min);
				result.setDataAt(r, c, new GridCellDouble(data));
			}
		}
	}

	/**
	 * Generate a 2d grid filled with perlin noise.
	 * 
	 * @param result
	 */
	private static void generatePerlinNoiseGrid2D(ComparableGrid2D<Double> result, int seed) {
		double rows = result.rows();
		double cols = result.cols();
		
		float factor = (float) (Math.max(result.rows(), result.cols()) / 128.0);
		
		PerlinNoiseMap h = new PerlinNoiseMap(Math.max(result.rows(), result.cols()), seed);
		h.AddPerlinNoise(3.0f * factor);
		h.Perturb(64.0f * factor, 6.0f * factor);
		for (int i = 0; i < 10; i++ )
			h.Erode(10.0f);
		h.Smoothen();
		for (int i = 0; i < 2; i++ )
			h.Erode2(7.0f,3.0f);
		h.Perturb2(16.0f * factor, 32.0f * factor, 6.0f);
		
		for(int r=0; r<rows; r++)
		{
			for(int c=0; c<cols; c++)
			{
				double noise = h.Heights[r][c];
				result.setDataAt(r, c, new GridCellDouble(noise,6));
			}
		}
	}
}
