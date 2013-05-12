package worldgenerator.util.noise;

import geometry.Point3D;

import java.util.Random;

public class RandomSource {
	public static final Random rand = new Random();
	
	static {
		rand.setSeed(0);
	}
	
	public static final Point3D getRandomPoint3D(double radius)
	{
		double phi = rand.nextDouble() * 2 * Math.PI;
		double theta = rand.nextDouble() * 2 * Math.PI;
		
		return new Point3D(radius * Math.sin(theta) * Math.cos(phi), radius * Math.sin(theta) * Math.sin(phi), radius * Math.cos(theta));
	}
}
