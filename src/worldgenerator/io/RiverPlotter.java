/**
 * 
 */
package worldgenerator.io;

import worldgenerator.geometry.river.GridCellRiverVertex;
import worldgenerator.geometry.river.River;

/**
 * Plots a river with subrivers.<br>
 * Format for a river: "GridCellRiverVertex","GridCellRiverVertex","GridCellRiverVertex",...\n<br>
 * Format for the file: mainriver\n subriver#1\n subriver #2\n ...<br>
 * @author Felix Dietrich
 *
 */
public class RiverPlotter extends APlotter {
	
	private River river;

	public RiverPlotter(River river) {
		this.river = river;
	}

	/* (non-Javadoc)
	 * @see worldgenerator.io.APlotter#plot2string()
	 */
	@Override
	public String plot2string() {
		StringBuilder sb = new StringBuilder();
		sb.append(plotRiver(this.river));
		sb.append(System.lineSeparator());
		
		for(River r : this.river.getConnectedRivers())
		{
			sb.append(plotRiver(r));
			sb.append(System.lineSeparator());
		}
		
		return sb.toString();
	}

	/**
	 * Plots the GridCellRiverVertex objects of a given river to a string.
	 * @param river
	 * @return
	 */
	private String plotRiver(River river) {
		StringBuilder sb = new StringBuilder();
		for(GridCellRiverVertex gcrv : river.getVertices())
		{
			sb.append(gcrv);
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

}
