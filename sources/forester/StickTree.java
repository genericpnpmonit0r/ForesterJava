package forester;

/**
 * Set up the trunk for trees with a trunk width of 1 and simple geometry.<br>
 * Designed for sub-classing.  Only makes the trunk.
 */
public abstract class StickTree extends Tree {
	public StickTree() {
	}

	@Override
	public void makeTrunk() {
		int x = this.pos[0];
		int y = this.pos[1];
		int z = this.pos[2];
		int i = 0;
		while (i < this.height) {
			this.assignValue(x, y, z, this.treeWoodBlock, this.treeWoodMetadata);
			y++; i++;
		}
	}
}
