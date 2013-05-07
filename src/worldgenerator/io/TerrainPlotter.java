/**
 * 
 */
package worldgenerator.io;

import worldgenerator.geometry.river.River;
import worldgenerator.geometry.terrain.Terrain;

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
		sb.append("<!-- Heightmap -->");
		sb.append(System.lineSeparator());
		sb.append(new GridPlotter2D(terrain.getHeightMap()).plot2string());
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
