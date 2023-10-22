package pnp;

public class NormalTree extends StickTree {
	public NormalTree(MCWorldAccessor mcmap) {
		this.mcmap = mcmap;
	}

	@Override
	public void makefoliage() {
		int topy = this.pos[1] + this.height - 1;
		int start = topy - 2;
		int end = topy + 2;
		int rad = 0;
		for (int y = start; y < end; y++) {
			if (y > start + 1) {
				rad = 1;
			} else {
				rad = 2;
			}
			int x, z;
			for (int xoff = -rad; xoff < rad + 1; xoff++) { // hope this is how range() works i have no fucking clue
				for (int zoff = -rad; zoff < rad + 1; zoff++) {
					if (Math.random() > 0.618D && Math.abs(xoff) == Math.abs(zoff) && Math.abs(xoff) == rad) {
						continue;
					}
					x = this.pos[0] + xoff;
					z = this.pos[2] + zoff;
					Forester.assign_value(x, y, z, Forester.LEAFMAT, Forester.LEAFDATA, this.mcmap);
				}
			}
		}
	}
}