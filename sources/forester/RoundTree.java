package forester;

public class RoundTree extends ProceduralTree {
	public RoundTree(MCWorldAccessor mcmap) {
		super(mcmap);
	}

	@Override
	public void prepare() {
		this.branchSlope = 0.382;
		super.prepare();
		this.foliageShape = new double[] { 2, 3, 3, 2.5, 1.6 };
		this.trunkRadius = this.trunkRadius * 0.8;
		this.trunkHeight = this.treeTrunkHeight * this.trunkHeight;
	}

	@Override
	protected double shapeFunc(int y) {
		double twigs = super.shapeFunc(y);
		if (!Double.isNaN(twigs)) {
			return twigs;
		}
		if (y < this.height * (.282 + .1 * Math.sqrt(random.nextDouble()))) {
			return Double.NaN;
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
