package forester;

public class RoundTree extends ProceduralTree {
	public RoundTree(MCWorldAccessor mcmap) {
		super(mcmap);
	}

	@Override
	public void prepare() {
		this.branchslope = 0.382;
		super.prepare();
		this.foliage_shape = new double[] { 2, 3, 3, 2.5, 1.6 };
		this.trunkradius = this.trunkradius * 0.8;
		this.trunkheight = this.tree_TRUNKHEIGHT * this.trunkheight;
	}

	@Override
	protected Double shapefunc(int y) {
		Double twigs = super.shapefunc(y);
		if (twigs != null) {
			return twigs;
		}
		if (y < this.height * (.282 + .1 * Math.sqrt(Math.random()))) {
			return null;
		}
		double radius = this.height / 2.;
		double adj = this.height / 2. - y;
		double dist;
		if (adj == 0) {
			dist = radius;
		} else if (Math.abs(adj) > radius) {
			dist = 0;
		} else {
			dist = Math.sqrt((Math.pow(radius, 2) - Math.pow(adj, 2)));
			dist = dist * .618;
		}
		return dist;
	}
}
