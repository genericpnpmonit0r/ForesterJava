package pnp;

public interface MCWorldAccessor {
	int getBlockId(int x, int y, int z);
	boolean setBlock(int x, int y, int z, int id);
	boolean setBlockAndMetadataWithNotify(int x, int y, int z, int id, int metadata);
}