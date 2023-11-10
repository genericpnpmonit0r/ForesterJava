package forester.customtrees;

import forester.MCWorldAccessor;
import forester.RoundTree;

/**
 * An example on how the tree types can be expanded on, this implements the notchian foliage cluster shape
 */
public class CustomRoundTree extends RoundTree {

	public CustomRoundTree(MCWorldAccessor mcmap) {
		super(mcmap);
	}
	
	@Override
	public void prepare() {
		super.prepare();
		this.foliageShape = new double[] {2, 3,3,3, 2};
		this.branchSlope = 0.381D;
	}

}
