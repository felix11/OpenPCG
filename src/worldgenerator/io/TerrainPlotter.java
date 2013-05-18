/**
 * 
 */
package worldgenerator.io;

import worldgenerator.objects.river.River;
import worldgenerator.objects.terrain.Terrain;

/**
 * @author Felix Dietrich
 *
 */
public class TerrainPlotter extends APlotter {
	
	private Terrain terrain;

	public TerrainPlotter(Terrain terrain)
	{
		this.terrain = terrain;
	}

	/* (non-Javadoc)
	 * @see worldgenerator.io.APlotter#plot2string()
	 */
	@Override
	public String plot2string() {
		StringBuilder sb = new StringBuilder("<!-- Terrain file -->");
		
		// heightmap
		sb.append(System.lineSeparator());
		sb.append("<!-- Heightmap level 0 -->");
		sb.append(System.lineSeparator());
		sb.append(new Grid2DPlotter(terrain.getHeightMap(0)).plot2string());
		sb.append(System.lineSeparator());
		
		// rivers
		sb.append("<!-- Rivers -->");
		for(River r : terrain.getRivers())
		{
			sb.append(new RiverPlotter(r).plot2string());
		}
		sb.append(System.lineSeparator());
		
		return sb.toString();
	}

}
