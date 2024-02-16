package forester.extra;

import java.util.Random;

import forester.Tree;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

/**
 * A 'shim' class to adapt between what MCP calls 'WorldGenerator' and the forester Tree classes
 */
public class WorldGeneratorShim extends WorldGenerator {
	private Tree tree;
	
	public WorldGeneratorShim(Tree tree) {
		this.tree = tree;
		this.tree.causeBlockUpdates = false;
	}
	
	public WorldGeneratorShim(Tree tree, boolean update) {
		super(update);
		this.tree = tree;
		this.tree.causeBlockUpdates = update;
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		return this.tree.generate(world, random, x, y, z);
	}
}
