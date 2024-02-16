package forester;

import java.util.Random;

/**
 * This class contains only static utility methods to use in the various tree
 * classes, the tree options and the tree getter can be found in {@link forester.Tree}
 */
public class Forester {
	private Forester() { //static methods only.
	}

	public static int[] range(int start, int end, int step) { // use only if needed, for maximum speed use a normal for loop, since this instantiates new objects in a loop
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

	public static int distToMat(int[] cord, int[] vec, int[] matidxlist, MCLevel mcmap, boolean invert, int limit) {
		int iterations = 0;
		boolean on_map = true;

		while (on_map) {
			int blockID = mcmap.getBlockId(cord[0], cord[1], cord[2]);

			if (arrayContains(matidxlist, blockID) && !invert) {
				break;
			} else if (!arrayContains(matidxlist, blockID) && invert) {
				break;
			} else {
				for (int i = 0; i < 3; i++) {
					cord[i] = cord[i] + vec[i];
				}
				iterations++;
			}

			if (iterations > limit) {
				break;
			}
		}
		
		return iterations;
	}

	public static boolean arrayContains(int[] array, int key) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == key) {
				return true;
			}
		}
		return false;
	}
}
