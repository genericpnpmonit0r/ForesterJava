package forester;

public enum TreeRoots {
	/**
	 * roots will penetrate anything, and may enter underground caves.
	 */
	YES,
	/**
	 * roots will be stopped by stone (default see STOPSROOTS below). There may be
	 * some penetration.
	 */
	TO_STONE,
	/**
	 * will hang downward in air. Good for "floating" type maps (I really miss
	 * "floating" terrain as a default option)
	 */
	HANGING,
	/**
	 * roots will not be generated
	 */
	NO;
}
