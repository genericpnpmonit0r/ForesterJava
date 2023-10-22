package pnp;

public class ConeTree extends ProceduralTree {
	public ConeTree(MCWorldAccessor mcmap) {
		super(mcmap);
	}

	@Override
	public void prepare() {
		this.branchslope = 0.15;
		super.prepare();
		this.foliage_shape = new double[] { 3, 2.6, 2, 1 };
		this.trunkradius = this.trunkradius * 0.5;
	}

	@Override
	protected Double shapefunc(int y) {
		Double twigs = super.shapefunc(y);
		if (twigs != null) {
			return twigs;
		}
		if (y < this.height * (.25 + .05 * Math.sqrt(Math.random()))) {
			return null;
		}
		double radius = (this.height - y) * 0.382;
		if (radius < 0) {
			radius = 0;
		}
		return Double.valueOf(radius);
	}
}