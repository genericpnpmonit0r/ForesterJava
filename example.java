import forester.*;
//.... other class stuff ....

//example to generate a huge tree
//there are a lot more parameters you can mess with and have variying results

int x = (int) this.thePlayer.posX; //x coord
int y = (int) this.thePlayer.posY; //y coord
int z = (int) this.thePlayer.posZ; //z coord

int height = 120; //tree height

Tree tree = Tree.getTree(theWorld, TreeShape.ROUND); //create tree with the specified shape
//set tree parameters, the parameters have javadoc comments in the Tree class
tree.setTreeSeed(123456L);
tree.treeBranchDensity = 10000D;
tree.treeLights = 2;
tree.treeRootButtresses = false;
tree.treeRoots = TreeRoots.TO_STONE;
tree.treeFoliageDensity = 0.3D;
tree.treeTrunkHeight = 1.0D;
tree.treeTrunkThickness = 4.0D;

Tree.generateTree(x,y,z, height, tree); //and finally generate the tree at the x y z location
