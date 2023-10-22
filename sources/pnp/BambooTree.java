package pnp;

public class BambooTree extends StickTree {
	public BambooTree(MCWorldAccessor mcmap) {
		this.mcmap = mcmap;
	}

	@Override
	public void makefoliage() {
		int start = this.pos[1];
		int end = this.pos[1] + this.height + 1;
		
		for (int y = start; y < end; y++) {
			for (int i = 0; i < 1; i++) {
				int xoff = Forester.choice(-1, 1);
				int zoff = Forester.choice(-1, 1);
				int x = this.pos[0] + xoff;
				int z = this.pos[2] + zoff;
				Forester.assign_value(x, y, z, Forester.LEAFMAT, Forester.LEAFDATA, this.mcmap);
			}
		}
	}
}