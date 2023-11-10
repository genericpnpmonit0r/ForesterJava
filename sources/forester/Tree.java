package forester;

import java.util.Random;

/** The base class for all Forester trees. */
public abstract class Tree {
	/** Random number generator, this is used instead of Math.random() to be able to set the seed used by the RNG */
	protected final Random random = new Random();
	/** MC world equivalent */
	protected MCWorldAccessor mcmap;
	/** Contains tree x y z coordinates [0] = x, [1] = y, [2] = z*/
	public int[] pos;
	/** Tree height*/
	public int height;
	
	/*Static options for tree block rotation metadata on procedural trees*/
	/** Set this to true for later MC versions like release 1.3*/
	protected static final boolean USE_LOG_ROTATION = false;
	protected static final int LOG_ROT_X_METADATA = 2;
	protected static final int LOG_ROT_Z_METADATA = 3;
	
	/** Setting this to true will call the method to set blocks with notify which causes block updates */
	public boolean causeBlockUpdates = true;
	
	/**
	 * Which shapes would you like the trees to be? these first three are best suited for small heights, from 5 - 10<br>
	 * <br>
	 * NORMAL is the normal minecraft shape, it only gets taller and shorter<br>
	 * BAMBOO a trunk with foliage, it only gets taller and shorter<br>
	 * PALM a trunk with a fan at the top, only gets taller and shorter, these last four are best suited for very large trees, heights greater than 8<br>
	 * ROUND procedural spherical shaped tree, can scale up to immense size<br>
	 * CONE procedural, like a pine tree, also can scale up to immense size<br>
	 * RAINFOREST many slender trees, most at the lower range of the height, with a few at the upper end.<br>
	 * MANGROVE makes mangrove trees.<br>
	 */
	protected TreeShape treeShape = TreeShape.ROUND; //protected cause automatically set by getTree()
	
	/**
	 * EDGEHEIGHT is the height at the trees at the edge of the area.<br>
	 * ie, when radius = RADIUS
	 */
	public int treeEdgeHeight = 25;
	
	/** 
	 * Do you want branches, trunk, and roots?<br>
	 * <br>
	 * true makes all of that<br>
	 * false does not create the trunk and branches, or the roots (even if they are enabled further down)<br>
	 */
	public boolean treeWood = true;
	
	/**
	 * Trunk thickness multiplier<br>
	 * from zero (super thin trunk) to whatever huge number you can think of.<br>
	 * Only works if SHAPE is not a "stickly" sub-type<br>
	 * <br>
	 * Example:<br>
	 * 1.0 is the default, it makes decently normal sized trunks<br>
	 * 0.3 makes very thin trunks<br>
	 * 4.0 makes a thick trunk (good for HOLLOWTRUNK).<br>
	 * 10.5 will make a huge thick trunk.  Not even kidding. Makes spacious<br>
	 * hollow trunks though!<br>
	 */
	public double treeTrunkThickness = 1.0D;
	
	
	/**
	 * Trunk height, as a fraction of the tree<br>
	 * Only works on "round" shaped trees<br>
	 * Sets the height of the crown, where the trunk ends and splits<br>
	 * <br>
	 * Examples:<br>
	 * 0.7 the default value, a bit more than half of the height<br>
	 * 0.3 good for a fan-like tree<br>
	 * 1.0 the trunk will extend to the top of the tree, and there will be no crown<br>
	 * 2.0 the trunk will extend out the top of the foliage, making the tree appear<br>
	 * like a cluster of green grapes impaled on a spike.<br>
	 */
	public double treeTrunkHeight = 0.7D;
	
	
	/**
	 * Do you want the trunk and tree broken off at the top?<br>
	 * removes about half of the top of the trunk, and any foliage and branches that would attach above it.<br>
	 * Only works if SHAPE is not a "stickly" sub-type This results in trees that are shorter than the height settings<br>
	 * <br>
	 * true does that stuff<br>
	 * false makes a normal tree (default)<br>
	 * Note, this works well with HOLLOWTRUNK (below) turned on as well.<br>
	 */
	public boolean treeBrokenTrunk = false;
	
	/**
	 * Do you want the trunk to be hollow (or filled) inside?<br>
	 * <br>
	 * Only works with larger sized trunks.<br>
	 * Only works if SHAPE is not a "stickly" sub-type<br>
	 * true makes the trunk hollow (or filled with other stuff)<br>
	 * false makes a solid trunk (default)<br>
	 */
	public boolean treeHollowTrunk = false;
	
	
	/** 
	 * How many branches should there be? General multiplier for the number of branches However,<br>
	 * it will not make more branches than foliage clusters so to guarantee a branch to every foliage cluster,<br>
	 * set it very high, like 10000 this also affects the number of roots, if they are enabled.<br>
	 * <br>
	 * Examples:<br>
	 * 1.0 is normal<br>
	 * 0.5 will make half as many branches<br>
	 * 2.0 will make twice as many branches<br>
	 * 10000 will make a branch to every foliage cluster (I'm pretty sure)<br>
	 */
	public double treeBranchDensity = 1.0D;
	
	/** 
	 * Do you want roots from the bottom of the tree?<br>
	 * Only works if SHAPE is ROUND or CONE
	 * <br>
	 * YES roots will penetrate anything, and may enter underground caves.<br>
	 * TOSTONE roots will be stopped by stone (default see STOPSROOTS below). There may be some penetration.<br>
	 * HANGING will hang downward in air. Good for "floating" type maps (I really miss "floating" terrain as a default option)<br>
	 * NO roots will not be generated<br>
	 */
	public TreeRoots treeRoots = TreeRoots.YES;
	
	/**
	 * Do you want root buttresses?<br>
	 * These make the trunk not-round at the base, seen in tropical or old trees.<br>
	 * This option generally makes the trunk larger.<br>
	 * Only works if SHAPE is ROUND or CONE<br>
	 * <br>
	 * Options:<br>
	 * true makes root buttresses<br>
	 * false leaves them out<br>
	 */
	public boolean treeRootButtresses = false;
	
	/**
	 * Do you want leaves on the trees?<br>
	 * <br>
	 * true there will be leaves<br>
	 * false there will be no leaves<br>
	 */
	public boolean treeFoliage = true;
	
	/**
	 * How thick should the foliage be?<br>
	 * General multiplier for the number of foliage clusters<br>
	 * Examples:<br>
	 * <br>
	 * 1.0 is normal<br>
	 * 0.3 will make very sparse spotty trees, half as many foliage clusters<br>
	 * 2.0 will make dense foliage, better for the RAINFOREST SHAPE<br>
	 */
	public double treeFoliageDensity = 1.0D;
		
	/**
	 * Add lights in the middle of foliage clusters for those huge trees that get so dark underneath<br>
	 * or for enchanted forests that should glow and stuff<br>
	 * Only works if SHAPE is ROUND or CONE<br>
	 * <br>
	 * 0 makes just normal trees<br>
	 * 1 adds one light inside the foliage clusters for a bit of light<br>
	 * 2 adds two lights around the base of each cluster, for more light<br>
	 * 4 adds lights all around the base of each cluster for lots of light<br> 
	 */
	public int treeLights = 0;
	
	/** World max height, change to 255 in MC 1.2.5 or above */
	public int worldMaxHeight = 127;
	
	/** What block id should the trunk be made out of */
	public int treeWoodBlock = 17;
	
	/** What block metadata should the trunk be made out of */
	public int treeWoodMetadata = 0;
	
	/** What block id should the leaves be made out of */
	public int treeLeafBlock = 18;
	
	/** What block metadata should the leaves be made out of */
	public int treeLeafMetadata = 0;
	
	/** What block id should the lights be made out of */
	public int treeLightingBlock = 50;
	
	/** What block metadata should the lights be made out of */
	public int treeLightingMetadata = 0;
	
	/** What block id should the trunk filler be made out of, set to 0 for air */
	public int treeTrunkFillerBlock = 0;
	
	/** What block metadata should the trunk filler be made out of */
	public int treeTrunkFillerMetadata = 0;
	
	/** What kind of blocks should stop roots*/
	public int[] treeRootStoppingBlocks = new int[]{ 1,7 };
	/** What kind of blocks should stop branches (leave empty to skip check)*/
	public int[] treeBranchStoppingBlocks = new int[]{}; //currently broken.
	
	public Tree() {
		this.pos = new int[] { 0, 0, 0 };
		this.height = 1;
	}
	
	/** Make sure the parameters are not out of range to prevent out of bounds stuff, should be called before anything below */
	private void checkParameters() {
		if (treeShape == null) treeShape = TreeShape.ROUND;
		if (treeEdgeHeight < 1) treeEdgeHeight = 1;
		if (treeTrunkThickness < 0.0D) treeTrunkThickness = 0.0D;
		if (treeTrunkHeight < 0.0D) treeTrunkHeight = 0.0D;
		if (treeRoots == null) treeRoots = TreeRoots.NO;
		if (treeFoliageDensity < 0.0D) treeFoliageDensity = 0.0D;
		if (treeBranchDensity < 0.0D) treeBranchDensity = 0.0D;
		if (treeLights < 0 || treeLights > 4) treeLights = 0;
	}

	/**
	 * Sets a block id and metadata at the specified coordinates, note that this.mcmap must be set and not null!
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @param id block ID
	 * @param metadata block metadata
	 */
	protected final void assignValue(int x, int y, int z, int id, int metadata) {
		if(this.causeBlockUpdates) {
			this.mcmap.setBlockAndMetadataWithNotify(x, y, z, id, metadata);
		} else {
			this.mcmap.setBlockAndMetadata(x, y, z, id, metadata);
		}
		
	}
	
	/**
	 * Initialize the internal values for the Tree object.
	 */
	public void prepare() {}

	/**
	 * Generate the trunk and enter it in mcmap.
	 */
	public abstract void makeTrunk();

	/**
	 * Generate the foliage and enter it in mcmap. Note, foliage will disintegrate if there is no log nearby
	 */
	public abstract void makeFoliage();

	@Override
	public String toString() {
		return String.format("Tree{pos=(%d,%d,%d),height=%d,type=%s}", 
				this.pos[0], this.pos[1], this.pos[2], this.height, this.getClass().getSimpleName());
	}
	
	public void setTreeSeed(long seed) {
		this.random.setSeed(seed);
	}
	
	/**
	 * Sets the tree shape and returns it
	 * @param mcmap world
	 * @param shape tree shape
	 * @return the tree object
	 */
	public static Tree getTree(MCWorldAccessor mcmap, TreeShape shape) {
		Tree tree = null;
		switch (shape) {
			case NORMAL: tree = new NormalTree(mcmap); break;
			case BAMBOO: tree =  new BambooTree(mcmap); break;
			case PALM: tree = new PalmTree(mcmap); break;
			case ROUND: tree = new RoundTree(mcmap); break;
			case CONE: tree =  new ConeTree(mcmap); break;
			case RAINFOREST: tree = new RainforestTree(mcmap); break;
			case MANGROVE: tree = new MangroveTree(mcmap); break;
			default: return null;
		}
		tree.treeShape = shape; //set shape here as well so its in sync so you don't get weird results
		return tree;
	}
	
	public static void generateTree(int x, int y, int z, int height, Tree tree) {
		tree.height = height;
		tree.pos[0] = x;
		tree.pos[1] = y;
		tree.pos[2] = z;
		tree.checkParameters();
		tree.prepare();
		if(tree.treeFoliage) {
			tree.makeFoliage();
		}
		if(tree.treeWood) {
			tree.makeTrunk();
		}
	}
}
