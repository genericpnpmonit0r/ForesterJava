package forester;

/**
 * Set up the foliage for a 'normal' tree.<br>
 * This tree will be a single bulb of foliage above a single width trunk.<br>
 * This shape is very similar to the default Minecraft tree.
 */
public class NormalTree extends StickTree {
	public NormalTree(MCLevel mcmap) {
		this.mcmap = mcmap;
	}

	/**
	 * note, foliage will disintegrate if there is no foliage below, or<br>
	 * if there is no "log" block within range 2 (square) at the same level or one level below
	 */
	@Override
	protected void makeFoliage() {
		int topy = this.pos[1] + this.height - 1;
		int start = topy - 2;
		int end = topy + 2;
		int rad;
		for (int y = start; y < end; y++) {
			if (y > start + 1) {
				rad = 1;
			} else {
				rad = 2;
			}
			int x, z;
			for (int xoff = -rad; xoff < rad + 1; xoff++) {
				for (int zoff = -rad; zoff < rad + 1; zoff++) {
					if (random.nextDouble() > 0.618D && Math.abs(xoff) == Math.abs(zoff) && Math.abs(xoff) == rad) {
						continue;
					}
					x = this.pos[0] + xoff;
					z = this.pos[2] + zoff;
					this.assignValue(x, y, z, this.treeLeafBlock, this.treeLeafMetadata);
				}
			}
		}
	}
}
