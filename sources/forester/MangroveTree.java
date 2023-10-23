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
		this.branchslope = 1.0;
		super.prepare();
		this.trunkradius = this.trunkradius * 0.618;
	}

	@Override
	protected Double shapefunc(int y) {
		Double val = super.shapefunc(y);

		if (val == null)
			return val;

		double val2 = val.doubleValue();
		val2 = val2 * 1.618;
		
		return val2;
	}
}
