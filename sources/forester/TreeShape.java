package forester;

public enum TreeShape {
	/**
	 * the normal minecraft shape, only gets taller and shorter
	 */
	NORMAL,
	/**
	 * a trunk with foliage, only gets taller and shorter
	 */
	BAMBOO,
	/**
	 * a trunk with a fan at the top, only gets taller and shorter, these last four
	 * are best suited for very large trees, heights greater than 8
	 */
	PALM,
	/**
	 * procedural spherical shaped tree, can scale up to immense size
	 */
	ROUND,
	/**
	 * procedural, like a pine tree, also can scale up to immense size
	 */
	CONE,
	/**
	 * many slender trees, most at the lower range of the height, with a few at the
	 * upper end.
	 */
	RAINFOREST,
	/**
	 * many slender trees, most at the lower range of the height, with a few at the
	 * upper end.
	 */
	MANGROVE;
}
