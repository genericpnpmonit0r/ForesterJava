package forester;

/**
 * This kind of tree is designed to resemble a conifer tree.
 */
public class ConeTree extends ProceduralTree {
	public ConeTree(MCWorldAccessor mcmap) {
		super(mcmap);
	}

	@Override
	public void prepare() {
		this.branchSlope = 0.15;
		super.prepare();
		this.foliageShape = new double[] { 3, 2.6, 2, 1 };
		this.trunkRadius = this.trunkRadius * 0.5;
	}

	@Override
	protected double shapeFunc(int y) {
		double twigs = super.shapeFunc(y);
		if (!Double.isNaN(twigs)) {
			return twigs;
		}
		if (y < this.height * (.25 + .05 * Math.sqrt(random.nextDouble()))) {
			return Double.NaN;
		}
		double radius = (this.height - y) * 0.382;
		if (radius < 0) {
			radius = 0;
		}
		return radius;
	}
}
