package pnp;

import java.util.Random;

public class TreeGenUtil {
	
	public static int choiceInt(Random rnd, int... src) {
		int len = src.length;
		return src[rnd.nextInt(len)];
	}
}
