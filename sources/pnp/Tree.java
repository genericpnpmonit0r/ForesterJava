package pnp;

public abstract class Tree {
	protected MCWorldAccessor mcmap;
	public int[] pos;
	public int height;

	public Tree() {
		this.pos = new int[] { 0, 0, 0 };
		this.height = 1;
	}

	public void prepare() { }

	public void maketrunk() { }

	public void makefoliage() { }

	@Override
	public String toString() {
		return String.format("Tree{pos=(%d,%d,%d),height=%d,type=}", 
				this.pos[0], this.pos[1], this.pos[2], this.height, this.getClass().getSimpleName());
	}
}