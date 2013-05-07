package worldgenerator.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public abstract class APlotter implements IPlotter {

	@Override
	public abstract String plot2string();
	
	@Override
	public void plot2file(String filename) throws IOException {
		List<String> lines = new LinkedList<String>();
		lines.add(plot2string());
		Files.write(Paths.get(filename), lines, StandardCharsets.UTF_8);
	}

}
