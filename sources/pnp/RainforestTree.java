package pnp;

public class RainforestTree extends ProceduralTree {

	public RainforestTree(MCWorldAccessor mcmap) {
		super(mcmap);
	}

	@Override
	public void prepare() {
		this.foliage_shape = new double[] { 3.4, 2.6 };
		this.branchslope = 1.0;
		super.prepare();
		this.trunkradius = this.trunkradius * 0.382;
		this.trunkheight = this.trunkheight * .9;
	}

	@Override
	protected Double shapefunc(int y) {
		Double twigs;
		if (y < this.height * 0.8) {
			if (Forester.EDGEHEIGHT < this.height) {
				twigs = super.shapefunc(y);
				if (twigs != null && Math.random() < 0.07) {
					return twigs;
				}
			}
			return null;
		} else {
			double width = this.height * .382;
			double topdist = (this.height - y) / (this.height * 0.2);
			double dist = width * (0.618 + topdist) * (0.618 + Math.random()) * 0.382;
			return Double.valueOf(dist);
		}
	}
}