package pnp;

public abstract class Tree {
	protected MCWorldAccessor mcmap;
	public int[] pos;
	public int height;

	public Tree() {
		this.pos = new int[] { 0, 0, 0 };
		this.height = 1;
	}

	/**
	 * Initialize the internal values for the Tree object.
	 */
	public void prepare() { }

	/**
	 * Generate the trunk and enter it in mcmap.
	 */
	public void maketrunk() { }

	/**
	 * Generate the foliage and enter it in mcmap. Note, foliage will disintegrate if there is no log nearby
	 */
	public void makefoliage() { }

	@Override
	public String toString() {
		return String.format("Tree{pos=(%d,%d,%d),height=%d,type=%s}", 
				this.pos[0], this.pos[1], this.pos[2], this.height, this.getClass().getSimpleName());
	}
}
