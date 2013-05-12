package worldgenerator.test.io;

import geometry.Point3D;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import worldgenerator.geometry.river.GridCellRiverVertex;
import worldgenerator.geometry.river.River;
import worldgenerator.geometry.river.RiverFactory;
import worldgenerator.io.RiverPlotter;

public class TestRiverPlotter {
	private static final GridCellRiverVertex source = new GridCellRiverVertex(new Point3D(0,0,0), 1);
	private static final GridCellRiverVertex sink = new GridCellRiverVertex(new Point3D(10,0,0), 2);
	private static RiverPlotter plotter;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testPlot2string() throws IOException {
		
		River river = RiverFactory.createSingle(source, sink, 25, 0.5);
		plotter = new RiverPlotter(river);
		
		String filename = "test_plotriver.txt";
		plotter.plot2file(filename );
	}

}
