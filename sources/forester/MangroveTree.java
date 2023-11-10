package forester;

/**
 * This kind of tree is designed to resemble a mangrove tree.
 */
public class MangroveTree extends RoundTree {
	public MangroveTree(MCWorldAccessor mcmap) {
		super(mcmap);
	}

	@Override
	public void prepare() {
		this.branchSlope = 1.0;
		super.prepare();
		this.trunkRadius = this.trunkRadius * 0.618;
	}

	@Override
	protected double shapeFunc(int y) {
		double val = super.shapeFunc(y);

		if (Double.isNaN(val))
			return val;

		val *= 1.618;
		
		return val;
	}
}
