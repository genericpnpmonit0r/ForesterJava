package forester;

public class PalmTree extends StickTree {
	public PalmTree(MCLevel mcmap) {
		this.mcmap = mcmap;
	}

	/**
	 * Set up the foliage for a palm tree.<br>
	 * Make foliage stick out in four directions from the top of the trunk.
	 */
	@Override
	protected void makeFoliage() {
		int y = this.pos[1] + this.height;
		for (int xoff = -2; xoff < 3; xoff++) {
			for (int zoff = -2; zoff < 3; zoff++) {
				if (Math.abs(xoff) == Math.abs(zoff)) {
					int x = this.pos[0] + xoff;
					int z = this.pos[2] + zoff;
					this.assignValue(x, y, z, this.treeLeafBlock, this.treeLeafMetadata);
					
				}
			}
		}
	}
}
