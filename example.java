import forester.*;
//.... other class stuff ....

//example to generate a huge tree
//there are a lot more parameters you can mess with and have variying results


/* the older way using direct field accesses: */
int x = (int) this.thePlayer.posX; //x coord
int y = (int) this.thePlayer.posY; //y coord
int z = (int) this.thePlayer.posZ; //z coord
Tree tree = new RoundTree(); //create tree with the specified shape
//set tree parameters, the parameters have javadoc comments in the Tree class
tree.treeBranchDensity = 10000D;
tree.treeLights = 2;
tree.treeRootButtresses = false;
tree.treeRoots = TreeRoots.TO_STONE;
tree.treeFoliageDensity = 0.3D;
tree.treeTrunkHeight = 1.0D;
tree.treeTrunkThickness = 4.0D;
tree.height = 120;
tree.generate(this.world, this.random, x,y,z); //and finally generate the tree at the x y z location

/* the newer way using the TreeBuilder: (handier for making static instances of tree classes for re-use) */

Tree tree = new TreeBuilder(ConeTree.class)
			.withBranchDensity(1000D)
			.withLights(3)
			.withBranchStoppingBlocks(2,4,7,20)
			.withRoots(TreeRoots.TO_STONE)
			.withFoliageDensity(0.45D)
			.withTrunkThickness(4D)
			.withRootButtresses(false)
			.build();

/* if height is not specified, the height will be set to 25 + random.nextInt(12)
 * the tree random seed is derived from the next long of the random parameter */
tree.generate(this.world, this.random, x,y,z);

/* you can also use the shim to adapt to MCP worldGenerator class: */
private Tree tree = new TreeBuilder(RoundTree.class)
			.withBranchDensity(1000D)
			.withLights(3)
			.withBranchStoppingBlocks(2,4,7,20)
			.withRoots(TreeRoots.NO)
			.withFoliageDensity(0.45D)
			.withTrunkThickness(4D)
			.withRootButtresses(false)
			.build();
			
private WorldGeneratorShim shim0 = new WorldGeneratorShim(tree); 
/* the boolean argument sets if the generation should cause block updates */
private WorldGeneratorShim shim1 = new WorldGeneratorShim(new MangroveTree(), false);

/* then you can use the shim in anything that takes a 'WorldGenerator' instance */
