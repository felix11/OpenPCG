package worldgenerator.test.geometry.river;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import geometry.Point3D;

import org.junit.Before;
import org.junit.Test;

import worldgenerator.io.RiverPlotter;
import worldgenerator.objects.river.GridCellRiverVertex;
import worldgenerator.objects.river.River;
import worldgenerator.objects.river.RiverFactory;

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
