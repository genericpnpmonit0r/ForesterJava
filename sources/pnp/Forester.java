package pnp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import net.minecraft.src.Block;

public class Forester {
	//TODO: make these into instance fields not static ones
	// Tree settings
	public static ForesterShape SHAPE = ForesterShape.ROUND;
	public static int CENTERHEIGHT = 55;
	public static int EDGEHEIGHT = 25;
	public static int HEIGHTVARIATION = 16;
	public static boolean WOOD = true;
	public static double TRUNKTHICKNESS = 2.0D;
	public static double TRUNKHEIGHT = 0.7D;
	public static boolean BROKENTRUNK = false;
	public static boolean HOLLOWTRUNK = false;
	public static double BRANCHDENSITY = 2.0D;
	public static ForesterRoots ROOTS = ForesterRoots.YES;
	public static boolean ROOTBUTTRESSES = false;
	public static boolean FOLIAGE = true;
	public static double FOLIAGEDENSITY = 2.0D;
	public static boolean MAPHEIGHTLIMIT = false;
	public static int LIGHTTREE = 0;
	
	// Material settings
	/**
	 * 0 thru 4
	 */
	public static int WOODMAT = 17;
	public static int WOODDATA = 0; // metadata
	public static int LEAFMAT = 18;
	public static int LEAFDATA = 0; // metadata
	public static int LIGHTMAT = 50;
	public static int LIGHTDATA = 0; // metadata
	public static int TRUNKFILLMAT = 0;
	public static int TRUNKFILLDATA = 0; // metadata

	// Restrictions
	public static int[] PLANTON = { 2 }; // what blocks to plant on
	public static int[] STOPSROOTS = { 1,7 }; // what blocks to inhibit roots on
	public static int[] STOPSBRANCHES = {1,4,20,7}; // what blocks to inhibit branches on

	public static final Random RANDOM = new Random();

	static {
		if (SHAPE == null) {
			SHAPE = ForesterShape.ROUND;
		}
		
		if (CENTERHEIGHT < 1) {
			CENTERHEIGHT = 1;
		}

		if (EDGEHEIGHT < 1) {
			EDGEHEIGHT = 1;
		}

		int minheight = Math.min(CENTERHEIGHT, EDGEHEIGHT);
		if (HEIGHTVARIATION > minheight) {
			HEIGHTVARIATION = minheight;
		}

		if (TRUNKTHICKNESS < 0.0D) {
			TRUNKTHICKNESS = 0.0D;
		}

		if (TRUNKHEIGHT < 0.0D) {
			TRUNKHEIGHT = 0.0D;
		}

		if (ROOTS == null) {
			ROOTS = ForesterRoots.NO;
		}

		if (FOLIAGEDENSITY < 0.0D) {
			FOLIAGEDENSITY = 0.0D;
		}

		if (BRANCHDENSITY < 0.0D) {
			BRANCHDENSITY = 0.0D;
		}

		if (LIGHTTREE < 0 || LIGHTTREE > 4) {
			LIGHTTREE = 0;
		}
	}

	public static int[] range(int start, int end, int step) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		
		
		if (step < 0) {
			for (int i = start; i > end; i += step) {
				arr.add(i);
			}
		} else {
			for (int i = start; i < end; i += step) {
				arr.add(i);
			}
		}

		return arr.stream().mapToInt(i -> i).toArray();
	}
	
	public static Integer[] primitiveToObjInt(int[] primitive) {
		int length = primitive.length;
		Integer[] out = new Integer[length];
		for (int i = 0; i < length; i++) {
			out[i] = Integer.valueOf(primitive[i]);
		}
		return out;
	}
	
	public static int[] objToPrimitive(Integer[] obj) {
		int length = obj.length;
		int[] out = new int[length];
		for (int i = 0; i < length; i++) {
			out[i] = obj[i].intValue();
		}
		return out;
	}
	
	public static int max_key_abs(int[] arr) {
		int biggestABSValueSoFar = 0;
		int biggestValueSoFar = 0;
		
		for (int i = 0; i < arr.length; i++) {
			int abs = (int)Math.abs(arr[i]);
			if (abs > biggestABSValueSoFar) {
				biggestABSValueSoFar = abs;
				biggestValueSoFar = arr[i];
			}
		}
		
		return biggestValueSoFar;
	}
	
	public static int getArrayIndex(int[] arr, int value) {
        int k = -1;
        
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) {
                k = i;
                break;
            }
        }
        
	    return k;
	}
	
	public static int[] getOtherIndexes(int[] arr, int aroundIndex) {
		int[] indexes = new int[2];
		int i2 = 0;
		
		for (int i = 0; i < arr.length; i++) {
			if (i == aroundIndex) {
				if (i - 1 > -1) {
					indexes[i2] = i - 1;
					i2++;
				} else {
					indexes[i2] = arr.length - 1;
					i2++;
				}
				
				if (i + 1 <= arr.length - 1) {
					indexes[i2] = i + 1;
					i2++;
				} else {
					indexes[i2] = 0;
					i2++;
				}
			}
		}
		
		return indexes;
	}
	
	public static void assign_value(int x, int y, int z, int id, int meta, MCWorldAccessor mcmap) {
		mcmap.setBlockAndMetadataWithNotify(x, y, z, id, meta);
	}

	public static int choice(int... src) {
		return src[RANDOM.nextInt(src.length)];
	}

	public static int dist_to_mat(int[] cord, int[] vec, int[] matidxlist, 
			MCWorldAccessor mcmap, boolean invert, Integer limit) {
		double[] curcord = { 0, 0, 0 };
		for (int i = 0; i < 3; i++) {
			curcord[i] = i + .5;
		}
		
		int iterations = 0;
		boolean on_map = true;

		while (on_map) {
			int x = (int) curcord[0];
			int y = (int) curcord[1];
			int z = (int) curcord[2];
			Block block = Block.blocksList[mcmap.getBlockId(x, y, z)];
			
			if (block == null) {
				break;
			} else {
				int blockID = block.blockID;

				if (Arrays.asList(primitiveToObjInt(matidxlist)).contains(blockID) && !invert) {
					break;
				} else if (!Arrays.asList(primitiveToObjInt(matidxlist)).contains(blockID) && invert) {
					break;
				} else {
					for (int i = 0; i < 3; i++) {
						curcord[i] = curcord[i] + vec[i];
					}
					iterations++;
				}
			}
			
			if (limit != null && iterations > limit) {
				break;
			}
		}
		
		return iterations;
	}

	private static Tree getTreeBasedOnShape(MCWorldAccessor mcmap, ForesterShape shape) {
		switch (shape) {
		case NORMAL:
			return new NormalTree(mcmap);
		case BAMBOO:
			return new BambooTree(mcmap);
		case PALM:
			return new PalmTree(mcmap);
		case ROUND:
			return new RoundTree(mcmap);
		case CONE:
			return new ConeTree(mcmap);
		case RAINFOREST:
			return new RainforestTree(mcmap);
		case MANGROVE:
			return new MangroveTree(mcmap);
		default:
			return null;
		}
	}

	public static void generateTree(int x, int y, int z, 
			int height, MCWorldAccessor mcmap, ForesterShape shape) {
		Tree tree = Forester.getTreeBasedOnShape(mcmap, shape);
		tree.height = height;
		tree.pos[0] = x;
		tree.pos[1] = y;
		tree.pos[2] = z;
		tree.prepare();
		tree.makefoliage();
		tree.maketrunk();
	}
}
