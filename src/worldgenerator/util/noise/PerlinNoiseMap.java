package worldgenerator.util.noise;


/**
 * From:
 * http://www.float4x4.net/index.php/2010/06/generating-realistic-and-playable-terrain-height-maps/
 *
 */
public class PerlinNoiseMap {
	public float[][] Heights;
	private PerlinNoiseGenerator Perlin;
	public int Size;

	public PerlinNoiseMap(int size, int seed) {
		Size = size;
		Heights = new float[Size][Size];
		Perlin = new PerlinNoiseGenerator(seed);
	}

	public void AddPerlinNoise(float f) {
		for (int i = 0; i < Size; i++) {
			for (int j = 0; j < Size; j++) {
				Heights[i][j] += Perlin.Noise(f * i / (float) Size, f * j
						/ (float) Size, 0);
			}
		}
	}

	public void Perturb(float f, float d) {
		int u, v;
		float[][] temp = new float[Size][Size];
		for (int i = 0; i < Size; ++i) {
			for (int j = 0; j < Size; ++j) {
				u = i
						+ (int) (Perlin.Noise(f * i / (float) Size, f * j
								/ (float) Size, 0) * d);
				v = j
						+ (int) (Perlin.Noise(f * i / (float) Size, f * j
								/ (float) Size, 1) * d);
				if (u < 0)
					u = 0;
				if (u >= Size)
					u = Size - 1;
				if (v < 0)
					v = 0;
				if (v >= Size)
					v = Size - 1;
				temp[i][j] = Heights[u][v];
			}
		}
		Heights = temp;
	}
	
	/**
	 * Variance dependent pertubation.
	 * @author Felix Dietrich
	 * 
	 * @param f
	 * @param d
	 * @param s in [0,inf] dependence on variance
	 */
	public void Perturb2(float f, float d, float s) {
		int u, v;
		double variance = 0;
		double mean = 0;//mean(Heights);
		float[][] temp = new float[Size][Size];
		for (int i = 0; i < Size; ++i) {
			for (int j = 0; j < Size; ++j) {
				variance = (Heights[i][j]-mean)*(Heights[i][j]-mean);
				
				u = i
						+ (int) (Perlin.Noise(f * i / (float) Size, f * j
								/ (float) Size, 0) * d * (s * variance+0.1f));
				v = j
						+ (int) (Perlin.Noise(f * i / (float) Size, f * j
								/ (float) Size, 1) * d * (s * variance+0.1f));
				if (u < 0)
					u = 0;
				if (u >= Size)
					u = Size - 1;
				if (v < 0)
					v = 0;
				if (v >= Size)
					v = Size - 1;
				temp[i][j] = Heights[u][v];
			}
		}
		Heights = temp;
	}

	private double mean(float[][] heights2) {
		double result = 0.0;
		for (int i = 0; i < Size; ++i) {
			for (int j = 0; j < Size; ++j) {
				result += Heights[i][j];
			}
		}
		return result / (Size*Size);
	}

	public void Erode(float smoothness) {
		for (int i = 1; i < Size - 1; i++) {
			for (int j = 1; j < Size - 1; j++) {
				float d_max = 0.0f;
				int[] match = { 0, 0 };

				for (int u = -1; u <= 1; u++) {
					for (int v = -1; v <= 1; v++) {
						if (Math.abs(u) + Math.abs(v) > 0) {
							float d_i = Heights[i][j] - Heights[i + u][j + v];
							if (d_i > d_max) {
								d_max = d_i;
								match[0] = u;
								match[1] = v;
							}
						}
					}
				}

				if (0 < d_max && d_max <= (smoothness / (float) Size)) {
					float d_h = 0.5f * d_max;
					Heights[i][j] -= d_h;
					Heights[i + match[0]][j + match[1]] += d_h;
				}
			}
		}
	}

	/**
	 * Variance dependent erosion.
	 * @author Felix Dietrich
	 * 
	 * @param smoothness
	 * @param s in [0,inf] dependence on variance
	 */
	public void Erode2(float smoothness, float s) {
		double variance = 0;
		double mean = 0;//mean(Heights);
				
		for (int i = 1; i < Size - 1; i++) {
			for (int j = 1; j < Size - 1; j++) {
				variance = Math.abs(Heights[i][j]-mean);
				float d_max = 0.0f;
				int[] match = { 0, 0 };

				for (int u = -1; u <= 1; u++) {
					for (int v = -1; v <= 1; v++) {
						if (Math.abs(u) + Math.abs(v) > 0) {
							float d_i = Heights[i][j] - Heights[i + u][j + v];
							if (d_i > d_max) {
								d_max = d_i;
								match[0] = u;
								match[1] = v;
							}
						}
					}
				}

				if (0 < d_max && d_max <= (smoothness / (float) Size)) {
					double d_h = 0.5f * d_max * (variance*s+0.1f);
					Heights[i][j] -= d_h;
					Heights[i + match[0]][j + match[1]] += d_h;
				}
			}
		}
	}

	public void Smoothen() {
		for (int i = 1; i < Size - 1; ++i) {
			for (int j = 1; j < Size - 1; ++j) {
				float total = 0.0f;
				for (int u = -1; u <= 1; u++) {
					for (int v = -1; v <= 1; v++) {
						total += Heights[i + u][j + v];
					}
				}

				Heights[i][j] = total / 9.0f;
			}
		}
	}
}