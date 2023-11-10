package forester;

/**
 * A simple interface to make porting easier, implement this in whatever world class you have
 */
public interface MCWorldAccessor {
	int getBlockId(int x, int y, int z);

	boolean setBlock(int x, int y, int z, int id);

	boolean setBlockAndMetadataWithNotify(int x, int y, int z, int id, int metadata);
	
	boolean setBlockAndMetadata(int x, int y, int z, int id, int metadata);
}
