package forester;

import java.util.Random;

public abstract class Tree {
	public static final Random RANDOM = new Random();
	protected MCWorldAccessor mcmap;
	/**Contains tree x y z coordinates [0] = x, [1] = y, [2] = z*/
	public int[] pos;
	public int height;
	
	/* Tree settings, use the setters to set the parameters (less clutter) these are just the defaults, these are prefixed with tree_ to prevent conflicts with other sub-class instance fields */
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
	protected TreeShape tree_SHAPE = TreeShape.ROUND; //protected cause automatically set by getTree()
	
	/**
	 * EDGEHEIGHT is the height at the trees at the edge of the area.<br>
	 * ie, when radius = RADIUS
	 */
	public int tree_EDGEHEIGHT = 25;
	
	/** 
	 * Do you want branches, trunk, and roots?<br>
	 * <br>
	 * true makes all of that<br>
	 * false does not create the trunk and branches, or the roots (even if they are enabled further down)<br>
	 */
	public boolean tree_WOOD = true;
	
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
	public double tree_TRUNKTHICKNESS = 1.0D;
	
	
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
	public double tree_TRUNKHEIGHT = 0.7D;
	
	
	/**
	 * Do you want the trunk and tree broken off at the top?<br>
	 * removes about half of the top of the trunk, and any foliage and branches that would attach above it.<br>
	 * Only works if SHAPE is not a "stickly" sub-type This results in trees that are shorter than the height settings<br>
	 * <br>
	 * true does that stuff<br>
	 * false makes a normal tree (default)<br>
	 * Note, this works well with HOLLOWTRUNK (below) turned on as well.<br>
	 */
	public boolean tree_BROKENTRUNK = false;
	
	/**
	 * Do you want the trunk to be hollow (or filled) inside?<br>
	 * <br>
	 * Only works with larger sized trunks.<br>
	 * Only works if SHAPE is not a "stickly" sub-type<br>
	 * true makes the trunk hollow (or filled with other stuff)<br>
	 * false makes a solid trunk (default)<br>
	 */
	public boolean tree_HOLLOWTRUNK = false;
	
	
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
	public double tree_BRANCHDENSITY = 1.0D;
	
	/** 
	 * Do you want roots from the bottom of the tree?<br>
	 * Only works if SHAPE is ROUND or CONE
	 * <br>
	 * YES roots will penetrate anything, and may enter underground caves.<br>
	 * TOSTONE roots will be stopped by stone (default see STOPSROOTS below). There may be some penetration.<br>
	 * HANGING will hang downward in air. Good for "floating" type maps (I really miss "floating" terrain as a default option)<br>
	 * NO roots will not be generated<br>
	 */
	public TreeRoots tree_ROOTS = TreeRoots.YES;
	
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
	public boolean tree_ROOTBUTTRESSES = false;
	
	/**
	 * Do you want leaves on the trees?<br>
	 * <br>
	 * true there will be leaves<br>
	 * false there will be no leaves<br>
	 */
	public boolean tree_FOLIAGE = true;
	
	/**
	 * How thick should the foliage be?<br>
	 * General multiplier for the number of foliage clusters<br>
	 * Examples:<br>
	 * <br>
	 * 1.0 is normal<br>
	 * 0.3 will make very sparse spotty trees, half as many foliage clusters<br>
	 * 2.0 will make dense foliage, better for the RAINFOREST SHAPE<br>
	 */
	public double tree_FOLIAGEDENSITY = 1.0D;
		
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
	public int tree_LIGHTTREE = 0;
	
	/*Tree materials*/
	public int tree_WOODMAT = 17; //wood id
	public int tree_WOODDATA = 0; // metadata
	public int tree_LEAFMAT = 18; //leaf id
	public int tree_LEAFDATA = 0; // metadata
	public int tree_LIGHTMAT = 50; //light id (torch)
	public int tree_LIGHTDATA = 0; // metadata
	public int tree_TRUNKFILLMAT = 0; //trunk fill (air)
	public int tree_TRUNKFILLDATA = 0; // metadata
	
	/*what kind of blocks should stop roots*/
	public int[] tree_STOPSROOTS = new int[]{ 1,7 };
	/*what kind of blocks should stop branches (broken on non-flat worlds)*/
	public int[] tree_STOPSBRANCHES = new int[]{}; //leave empty to skip the check
	
	public Tree() {
		this.pos = new int[] { 0, 0, 0 };
		this.height = 1;
	}
	
	/** Make sure the parameters are not out of range to prevent out of bounds stuff, should be called before anything below */
	private void checkParameters() {
		if (tree_SHAPE == null) tree_SHAPE = TreeShape.ROUND;
		if (tree_EDGEHEIGHT < 1) tree_EDGEHEIGHT = 1;
		if (tree_TRUNKTHICKNESS < 0.0D) tree_TRUNKTHICKNESS = 0.0D;
		if (tree_TRUNKHEIGHT < 0.0D)tree_TRUNKHEIGHT = 0.0D;
		if (tree_ROOTS == null)tree_ROOTS = TreeRoots.NO;
		if (tree_FOLIAGEDENSITY < 0.0D) tree_FOLIAGEDENSITY = 0.0D;
		if (tree_BRANCHDENSITY < 0.0D)tree_BRANCHDENSITY = 0.0D;
		if (tree_LIGHTTREE < 0 || tree_LIGHTTREE > 4) tree_LIGHTTREE = 0;
	}

	/**
	 * Initialize the internal values for the Tree object.
	 */
	public void prepare() {}

	/**
	 * Generate the trunk and enter it in mcmap.
	 */
	public abstract void maketrunk();

	/**
	 * Generate the foliage and enter it in mcmap. Note, foliage will disintegrate if there is no log nearby
	 */
	public abstract void makefoliage();

	@Override
	public String toString() {
		return String.format("Tree{pos=(%d,%d,%d),height=%d,type=%s}", 
				this.pos[0], this.pos[1], this.pos[2], this.height, this.getClass().getSimpleName());
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
		tree.tree_SHAPE = shape; //set shape here as well so its in sync so you don't get weird results
		return tree;
	}
	
	public static void generateTree(int x, int y, int z, int height, Tree tree) {
		tree.height = height;
		tree.pos[0] = x;
		tree.pos[1] = y;
		tree.pos[2] = z;
		tree.checkParameters();
		tree.prepare();
		if(tree.tree_FOLIAGE) {
			tree.makefoliage();
		}
		if(tree.tree_WOOD) {
			tree.maketrunk();
		}
	}
}
