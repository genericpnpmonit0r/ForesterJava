package forester.externs;

import java.util.Random;

import forester.FeatureAdaptor;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

/**
 * A 'shim' class to adapt between what MCP calls 'WorldGenerator' and the forester Tree classes
 */
public class WorldGeneratorShim extends WorldGenerator {
	private FeatureAdaptor tree;
	
	public WorldGeneratorShim(FeatureAdaptor fa) {
		this.tree = fa;
		this.tree.setCausesBlockUpdates(false);
	}
	
	public WorldGeneratorShim(FeatureAdaptor fa, boolean update) {
		super(update);
		this.tree = fa;
		this.tree.setCausesBlockUpdates(update);
	}

	@Override
	public boolean generate(World world1, Random random2, int i3, int i4, int i5) {
		return this.tree.generate(world1, random2, i3, i4, i5);
	}

}
