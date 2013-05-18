package worldgenerator.test.io;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import worldgenerator.io.Grid2DPlotter;
import worldgenerator.util.grid.ComparableGrid2D;
import worldgenerator.util.grid.GridCellDouble;


public class TestGridPlotter {
	
	private static Grid2DPlotter plotter;
	private static int width = 2;
	private static int height = 2;
	private ComparableGrid2D<Double> grid;

	@Before
	public void setUp() throws Exception {
		grid = new ComparableGrid2D<Double>(height, width, new GridCellDouble(0.0));
		plotter = new Grid2DPlotter(grid);
	}

	@Test
	public void testGridPlotterPlot() {
		String str = plotter.plot2string();
		assertEquals("Plotted grids do not match.", "<empty>,<empty>;<empty>,<empty>", str);

		grid.setDataAt(0,0,new GridCellDouble(1.0,1));
		grid.setDataAt(0,1,new GridCellDouble(1.0,1));
		grid.setDataAt(1,0,new GridCellDouble(0.0,1));
		grid.setDataAt(1,1,new GridCellDouble(0.02,2));
		String str2 = plotter.plot2string();
		assertEquals("Plotted grids do not match.", "1.0,1.0;0.0,0.02", str2);
	}
}
