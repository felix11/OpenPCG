package worldgenerator.io;

import java.io.IOException;

public interface IPlotter {
	String plot2string();
	void plot2file(String filename) throws IOException;
}
