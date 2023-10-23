//example to generate a huge tree
//there are a lot more parameters you can mess with and have variying results

int x = (int) this.thePlayer.posX; //x coord
int y = (int) this.thePlayer.posY; //y coord
int z = (int) this.thePlayer.posZ; //z coord

int height = 120; //tree height

Tree tree = Tree.getTree(theWorld, TreeShape.ROUND); //create tree with the specified shape
//set tree parameters
tree.tree_BRANCHDENSITY = 10000D;
tree.tree_LIGHTTREE = 2;
tree.tree_ROOTBUTTRESSES = false;
tree.tree_ROOTS = TreeRoots.TO_STONE;
tree.tree_FOLIAGEDENSITY = 0.3D;
tree.tree_TRUNKHEIGHT = 1.0D;
tree.tree_TRUNKTHICKNESS = 4.0D;

Tree.generateTree(x,y,z, height, tree); //and finally generate the tree at the x y z location
