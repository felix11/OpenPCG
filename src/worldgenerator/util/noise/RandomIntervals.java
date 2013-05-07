package worldgenerator.util.noise;

/**
 * Represents the interval [0,1] divided randomly in a given number of subintervals.
 * All of them are disjoint, ordered and yield [0,1] in union.
 * 
 * @author Felix Dietrich
 */
public class RandomIntervals {
	public final double[][] intervals;
	
	public RandomIntervals(int size) {
		if(size < 1)
		{
			throw new IllegalArgumentException("Size must be greater than zero.");
		}
		
		this.intervals = new double[size][2];
		
		generateIntervals();
	}
	
	private void generateIntervals() {
		this.intervals[0][0] = 0;
		this.intervals[0][1] = RandomSource.rand.nextDouble();
		double len = this.intervals[0][1];
		
		// build up the intervals
		for(int i=1; i<this.size(); i++)
		{
			this.intervals[i][0] = this.intervals[i-1][1];
			double newLen = RandomSource.rand.nextDouble();
			this.intervals[i][1] = this.intervals[i][0] + newLen;
			len += newLen;
		}
		
		// normalize the interval in total to [0,1]
		for(int i=0; i<this.size(); i++)
		{
			this.intervals[i][0] /= len;
			this.intervals[i][1] /= len;
		}
	}

	public int size()
	{
		return this.intervals.length;
	}
}
