package forester;

public class PalmTree extends StickTree {
	public PalmTree(MCWorldAccessor mcmap) {
		this.mcmap = mcmap;
	}

	/**
	 * Set up the foliage for a palm tree.<br>
	 * Make foliage stick out in four directions from the top of the trunk.
	 */
	@Override
	public void makefoliage() {
		int y = this.pos[1] + this.height;
		for (int xoff = -2; xoff < 3; xoff++) {
			for (int zoff = -2; zoff < 3; zoff++) {
				if (Math.abs(xoff) == Math.abs(zoff)) {
					int x = this.pos[0] + xoff;
					int z = this.pos[2] + zoff;
					Forester.assign_value(x, y, z, this.tree_LEAFMAT, this.tree_LEAFDATA, this.mcmap);
				}
			}
		}
	}
}