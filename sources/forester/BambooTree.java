package forester;

public class BambooTree extends StickTree {
	public BambooTree(MCLevel mcmap) {
		this.mcmap = mcmap;
	}

	/**
	 * Set up the foliage for a bamboo tree.<br>
	 * Make foliage sparse and adjacent to the trunk.
	 */
	@Override
	protected void makeFoliage() {
		int start = this.pos[1];
		int end = this.pos[1] + this.height + 1;
		
		for (int y = start; y < end; y++) {
			for (int i = 0; i < 1; i++) {
				int xoff = Forester.choice(random, -1, 1);
				int zoff = Forester.choice(random, -1, 1);
				int x = this.pos[0] + xoff;
				int z = this.pos[2] + zoff;
				this.assignValue(x, y, z, this.treeLeafBlock, this.treeLeafMetadata);
			}
		}
	}
}
