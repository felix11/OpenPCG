package worldgenerator.test.geometry.river;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import worldgenerator.geometry.Point3D;
import worldgenerator.geometry.river.GridCellRiverVertex;
import worldgenerator.geometry.river.River;
import worldgenerator.geometry.river.RiverFactory;
import worldgenerator.io.RiverPlotter;

public class TestRiverFactory {
	private static final GridCellRiverVertex source = new GridCellRiverVertex(new Point3D(0,0,0), 1);
	private static final GridCellRiverVertex sink = new GridCellRiverVertex(new Point3D(10,0,0), 1);
	private static RiverPlotter plotter;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCreateNoRivers() {
		River river = RiverFactory.create(source, sink, 0, 0);
		
		assertEquals("No additional rivers should have been created.", 0, river.getConnectedRivers().size());
	}

	@Test
	public void testCreateRivers() {
		River river = RiverFactory.create(source, sink, 2, 1);
		
		assertNotSame("Additional rivers should have been created.", 0, river.getConnectedRivers().size());
	}

}
