package forester;

import java.util.Random;

/**
 * WorldGeneratorAdaptor<BR>
 * this was originally going to be the 'shim' interface between what MCP calls 
 * 'WorldGenerator' and the forester Tree class
 * but unfortunately java does not have multiple inheritance
 */
public interface FeatureAdaptor {
	boolean generate(MCLevel level, Random random, int x, int y, int z);
	void setCausesBlockUpdates(boolean b);
}
