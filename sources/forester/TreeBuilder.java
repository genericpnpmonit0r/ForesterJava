package forester;

/**
 * Use this Builder class to create static instances of trees for future use
 */
public class TreeBuilder {
	private final Tree tree;
	
	public TreeBuilder(Class<? extends Tree> tc) {
		try {
			tree = tc.getConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Cannot instantiate tree class "+tc.getSimpleName(), e);
		}
	}
	
	public TreeBuilder withFixedHeight(int fixedH) {
		tree.height = fixedH;
		return this;
	}

	public TreeBuilder withHeightVariance(int heightV) {
		tree.randomHeightVariance = heightV;
		return this;
	}

	public TreeBuilder withLogRot(boolean useLogRot) {
		tree.useLogRot = useLogRot;
		return this;
	}

	public TreeBuilder withLogRotXMeta(int xmeta) {
		tree.logRotXMetadata = xmeta;
		return this;
	}

	public TreeBuilder withLogRotZMeta(int zmeta) {
		tree.logRotZMetadata = zmeta;
		return this;
	}

	public TreeBuilder withBlockUpdates(boolean blockUpdates) {
		tree.causeBlockUpdates = blockUpdates;
		return this;
	}

	public TreeBuilder withEdgeHeight(int edgeHeight) {
		tree.treeEdgeHeight = edgeHeight;
		return this;
	}

	public TreeBuilder withWood(boolean wood) {
		tree.treeWood = wood;
		return this;
	}

	public TreeBuilder withTrunkThickness(double thickness) {
		tree.treeTrunkThickness = thickness;
		return this;
	}

	public TreeBuilder withTrunkHeight(double height) {
		tree.treeTrunkHeight = height;
		return this;
	}

	public TreeBuilder withHollowTrunk(boolean hollow) {
		tree.treeHollowTrunk = hollow;
		return this;
	}

	public TreeBuilder withBrokenTrunk(boolean broken) {
		tree.treeBrokenTrunk = broken;
		return this;
	}

	public TreeBuilder withBranchDensity(double density) {
		tree.treeBranchDensity = density;
		return this;
	}

	public TreeBuilder withRoots(TreeRoots roots) {
		tree.treeRoots = roots;
		return this;
	}

	public TreeBuilder withRootButtresses(boolean buttresses) {
		tree.treeRootButtresses = buttresses;
		return this;
	}

	public TreeBuilder withFoliage(boolean foliage) {
		tree.treeFoliage = foliage;
		return this;
	}

	public TreeBuilder withFoliageDensity(double density) {
		tree.treeFoliageDensity = density;
		return this;
	}

	public TreeBuilder withLights(int lights) {
		tree.treeLights = lights;
		return this;
	}

	public TreeBuilder withWoodBlock(int woodBlockId) {
		tree.treeWoodBlock = woodBlockId;
		return this;
	}

	public TreeBuilder withWoodMeta(int woodBlockMeta) {
		tree.treeWoodMetadata = woodBlockMeta;
		return this;
	}

	public TreeBuilder withLeafBlock(int leafBlockId) {
		tree.treeLeafBlock = leafBlockId;
		return this;
	}

	public TreeBuilder withLeafMeta(int leafBlockMeta) {
		tree.treeLeafMetadata = leafBlockMeta;
		return this;
	}

	public TreeBuilder withTreeLightBlock(int lightBlockId) {
		tree.treeLightingBlock = lightBlockId;
		return this;
	}

	public TreeBuilder withTreeLightMeta(int lightBlockMeta) {
		tree.treeLightingMetadata = lightBlockMeta;
		return this;
	}

	public TreeBuilder withTrunkFillerBlock(int fillerBlockId) {
		tree.treeTrunkFillerBlock = fillerBlockId;
		return this;
	}

	public TreeBuilder withTrunkFillerMeta(int fillerBlockMeta) {
		tree.treeTrunkFillerMetadata = fillerBlockMeta;
		return this;
	}

	public TreeBuilder withRootStoppingBlocks(int... rootStopBlocks) {
		tree.treeRootStoppingBlocks = rootStopBlocks;
		return this;
	}

	public TreeBuilder withBranchStoppingBlocks(int... brStopBlocks) {
		tree.treeBranchStoppingBlocks = brStopBlocks;
		return this;
	}
	
	public TreeBuilder withPlantOnBlocks(int... plantOnBlocks) {
		tree.plantOnBlocks = plantOnBlocks;
		return this;
	}
	
	public Tree build() {
		return tree;
	}
}