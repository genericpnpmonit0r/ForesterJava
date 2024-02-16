package forester;

public class RainforestTree extends ProceduralTree {

	public RainforestTree() {
		super();
	}

	@Override
	protected void prepare() {
		this.foliageShape = new double[] { 3.4, 2.6 };
		this.branchSlope = 1.0;
		super.prepare();
		this.trunkRadius = this.trunkRadius * 0.382;
		this.trunkHeight = this.trunkHeight * .9;
	}

	@Override
	protected double shapeFunc(int y) {
		double twigs;
		if (y < this.height * 0.8) {
			if (this.treeEdgeHeight < this.height) {
				twigs = super.shapeFunc(y);
				if (!Double.isNaN(twigs) && random.nextDouble() < 0.07) {
					return twigs;
				}
			}
			return Double.NaN;
		} else {
			double width = this.height * .382;
			double topdist = (this.height - y) / (this.height * 0.2);
            return width * (0.618 + topdist) * (0.618 + random.nextDouble()) * 0.382;
		}
	}
}
