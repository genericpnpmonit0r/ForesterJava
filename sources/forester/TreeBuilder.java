package forester;

/**
 * Use this Builder class to create static instances of trees for future use
 */
public class TreeBuilder {
	private final Tree t;
	
	public TreeBuilder(Class<? extends Tree> tc) {
		try {
			t = tc.getConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Cannot instantiate tree class "+tc.getSimpleName(), e);
		}
	}
	
	public TreeBuilder withFixedHeight(int fixedH) {
		t.height = fixedH;
		return this;
	}

	public TreeBuilder withHeightVariance(int heightV) {
		t.randomHeightVariance = heightV;
		return this;
	}

	public TreeBuilder withLogRot(boolean useLogRot) {
		t.useLogRot = useLogRot;
		return this;
	}

	public TreeBuilder withLogRotXMeta(int xmeta) {
		t.logRotXMetadata = xmeta;
		return this;
	}

	public TreeBuilder withLogRotZMeta(int zmeta) {
		t.logRotZMetadata = zmeta;
		return this;
	}

	public TreeBuilder withBlockUpdates(boolean blockUpdates) {
		t.causeBlockUpdates = blockUpdates;
		return this;
	}

	public TreeBuilder withEdgeHeight(int edgeHeight) {
		t.treeEdgeHeight = edgeHeight;
		return this;
	}

	public TreeBuilder withWood(boolean wood) {
		t.treeWood = wood;
		return this;
	}

	public TreeBuilder withTrunkThickness(double thickness) {
		t.treeTrunkThickness = thickness;
		return this;
	}

	public TreeBuilder withTrunkHeight(double height) {
		t.treeTrunkHeight = height;
		return this;
	}

	public TreeBuilder withHollowTrunk(boolean hollow) {
		t.treeHollowTrunk = hollow;
		return this;
	}

	public TreeBuilder withBrokenTrunk(boolean broken) {
		t.treeBrokenTrunk = broken;
		return this;
	}

	public TreeBuilder withBranchDensity(double density) {
		t.treeBranchDensity = density;
		return this;
	}

	public TreeBuilder withRoots(TreeRoots roots) {
		t.treeRoots = roots;
		return this;
	}

	public TreeBuilder withRootButtresses(boolean buttresses) {
		t.treeRootButtresses = buttresses;
		return this;
	}

	public TreeBuilder withFoliage(boolean foliage) {
		t.treeFoliage = foliage;
		return this;
	}

	public TreeBuilder withFoliageDensity(double density) {
		t.treeFoliageDensity = density;
		return this;
	}

	public TreeBuilder withLights(int lights) {
		t.treeLights = lights;
		return this;
	}

	public TreeBuilder withWoodBlock(int woodBlockId) {
		t.treeWoodBlock = woodBlockId;
		return this;
	}

	public TreeBuilder withWoodMeta(int woodBlockMeta) {
		t.treeWoodMetadata = woodBlockMeta;
		return this;
	}

	public TreeBuilder withLeafBlock(int leafBlockId) {
		t.treeLeafBlock = leafBlockId;
		return this;
	}

	public TreeBuilder withLeafMeta(int leafBlockMeta) {
		t.treeLeafMetadata = leafBlockMeta;
		return this;
	}

	public TreeBuilder withTreeLightBlock(int lightBlockId) {
		t.treeLightingBlock = lightBlockId;
		return this;
	}

	public TreeBuilder withTreeLightMeta(int lightBlockMeta) {
		t.treeLightingMetadata = lightBlockMeta;
		return this;
	}

	public TreeBuilder withTrunkFillerBlock(int fillerBlockId) {
		t.treeTrunkFillerBlock = fillerBlockId;
		return this;
	}

	public TreeBuilder withTrunkFillerMeta(int fillerBlockMeta) {
		t.treeTrunkFillerMetadata = fillerBlockMeta;
		return this;
	}

	public TreeBuilder withRootStoppingBlocks(int... rootStopBlocks) {
		t.treeRootStoppingBlocks = rootStopBlocks;
		return this;
	}

	public TreeBuilder withBranchStoppingBlocks(int... brStopBlocks) {
		t.treeBranchStoppingBlocks = brStopBlocks;
		return this;
	}
	
	public Tree build() {
		return t;
	}
}