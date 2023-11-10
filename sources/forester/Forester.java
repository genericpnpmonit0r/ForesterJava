package forester;

import java.util.Random;

import net.minecraft.src.Block;

/**
 * This class contains only static utility methods to use in the various tree
 * classes, the tree options and the tree getter can be found in {@link forester.Tree}
 */
public class Forester {
	private Forester() { //static methods only.
	}

	public static int[] range(int start, int end, int step) { // use only if needed, for maximum speed use a normal for loop
		int[] tbl = {};
		int[] temp = null;

		if (step < 0) {
			for (int i = start; i > end; i += step) {
				temp = new int[tbl.length + 1];
				System.arraycopy(tbl, 0, temp, 0, tbl.length);
				temp[tbl.length] = i;
				tbl = temp;
			}
		} else {
			for (int i = start; i < end; i += step) {
				temp = new int[tbl.length + 1];
				System.arraycopy(tbl, 0, temp, 0, tbl.length);
				temp[tbl.length] = i;
				tbl = temp;
			}
		}

		return tbl;
	}

	/*
	public static Integer[] rangeBoxed(int start, int end, int step) {
		ArrayList<Integer> ints = new ArrayList<>();

		if (step < 0) {
			for (int i = start; i > end; i += step) {
				ints.add(i);
			}
		} else {
			for (int i = start; i < end; i += step) {
				ints.add(i);
			}
		}

		return ints.toArray(new Integer[0]);
	}
	*/

	public static int maxKeyAbs(int[] arr) {
		int biggestABSValueSoFar = 0;
		int biggestValueSoFar = 0;

		for (int i = 0; i < arr.length; i++) {
			int abs = Math.abs(arr[i]);
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

	public static int choice(Random rnd, int... src) {
		return src[rnd.nextInt(src.length)];
	}

	/* original code for the loop was curcord = [i + .5 for i in cord] */
	public static int distToMat(int[] cord, int[] vec, int[] matidxlist, MCWorldAccessor mcmap, boolean invert, Integer limit) {
		double[] curcord = { 0, 0, 0 };
		for (int i = 0; i < cord.length; i++) { //not sure if correct
			curcord[i] = cord[i] + .5;
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

				if (arrayContains(matidxlist, blockID) && !invert) {
					break;
				} else if (!arrayContains(matidxlist, blockID) && invert) {
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

	public static boolean arrayContains(int[] array, int key) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == key)
				return true;
		}
		return false;
	}
	
	//debug entry point to test range() impl
	/*
	public static void main(String[] args) {
		final int start = 20;
		final int end = 0;
		final int step = -1;
		
		int[] unboxed = range(start,end,step);
		
		Integer[] boxed = rangeBoxed(start,end,step);
		
		System.out.println("unbox: "+Arrays.toString(unboxed));
		System.out.println("boxed: "+Arrays.toString(boxed));
	}
	*/
}
