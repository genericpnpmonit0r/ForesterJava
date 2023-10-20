package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.LogTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.Tiles;

public class BasicTree extends AbstractTreeFeature {

    // The axisConversionArray, when given a primary index, allows easy
    // access to the indices of the other two axies. Access the data at the
    // primary index location to get the horizontal secondary axis.
    // Access the data at the primary location plus three to get the
    // remaining, tertiary, axis.
    // All directions are specified by an index, 0, 1, or 2 which
    // correspond to x, y, and z.
    // The axisConversionArray is used in several places
    // notably the crossection and taperedLimb methods.
    // Example:
    // If the primary axis is z, then the primary index is 2.
    // The secondary index is axisConversionArray[2] which is 0,
    // the index for the x axis.
    // The remaining axis is axisConversionArray[2 + 3] which is 1,
    // the index for the y axis.
    // Using this method, the secondary axis will always be horizontal (x or z),
    // and the tertiary always vertical (y), if possible.
    static final byte[] axisConversionArray = {
            2, 0, 0, 1, 2, 1
    };

    // Set up the pseudorandom number generator
    Random rnd = new Random();

    // Make fields to hold the level data and the random seed
    Level thisLevel;

    // Field to hold the tree origin, x y and z.
    int[] origin = {
            0, 0, 0
    };
    // Field to hold the tree height.
    int height;
    // Other important tree information.
    int trunkHeight;
    double trunkHeightScale = 0.618;
    double branchDensity = 1.0;
    double branchSlope = 0.381;
    double widthScale = 1.0;
    double foliageDensity = 1.0;
    int trunkWidth = 1;
    int heightVariance = 12;
    int foliageHeight = 4;
    // The foliage coordinates are a list of [x,y,z,y of branch base] values for each cluster
    int[][] foliageCoords;

    public BasicTree(boolean doUpdate) {
        super(doUpdate);
    }

    void prepare() {
        // Initialize the instance variables.
        // Populate the list of foliage cluster locations.
        // Designed to be overridden in child classes to change basic
        // tree properties (trunk width, branch angle, foliage density, etc..).
        trunkHeight = (int) (height * trunkHeightScale);
        if (trunkHeight >= height) trunkHeight = height - 1;
        int clustersPerY = (int) (1.382 + Math.pow(foliageDensity * height / 13.0, 2));
        if (clustersPerY < 1) clustersPerY = 1;
        // The foliage coordinates are a list of [x,y,z,y of branch base]
        // values for each cluster
        int[][] tempFoliageCoords = new int[clustersPerY * height][4];
        int y = origin[1] + height - foliageHeight;
        int clusterCount = 1;
        int trunkTop = origin[1] + trunkHeight;
        int relativeY = y - origin[1];
        tempFoliageCoords[0][0] = origin[0];
        tempFoliageCoords[0][1] = y;
        tempFoliageCoords[0][2] = origin[2];
        tempFoliageCoords[0][3] = trunkTop;
        y--;

        while (relativeY >= 0) {
            int num = 0;

            float shapefac = treeShape(relativeY);
            if (shapefac < 0) {
                y--;
                relativeY--;
                continue;
            }

            // The originOffset is to put the value in the middle of the block.
            double originOffset = 0.5;
            while (num < clustersPerY) {
                double radius = widthScale * (shapefac * (rnd.nextFloat() + 0.328));
                double angle = rnd.nextFloat() * 2.0 * 3.14159;
                int x = Mth.floor(radius * Math.sin(angle) + origin[0] + originOffset);
                int z = Mth.floor(radius * Math.cos(angle) + origin[2] + originOffset);
                int[] checkStart = {
                        x, y, z
                };
                int[] checkEnd = {
                        x, y + foliageHeight, z
                };
                // check the center column of the cluster for obstructions.
                if (checkLine(checkStart, checkEnd) == -1) {
                    // If the cluster can be created, check the branch path
                    // for obstructions.
                    int[] checkBranchBase = {
                            origin[0], origin[1], origin[2]
                    };
                    double distance = Math.sqrt(Math.pow(Math.abs(origin[0] - checkStart[0]), 2) + Math.pow(Math.abs(origin[2] - checkStart[2]), 2));
                    double branchHeight = distance * branchSlope;
                    if ((checkStart[1] - branchHeight) > trunkTop) {
                        checkBranchBase[1] = trunkTop;

                    } else {
                        checkBranchBase[1] = (int) (checkStart[1] - branchHeight);
                    }
                    // Now check the branch path
                    if (checkLine(checkBranchBase, checkStart) == -1) {
                        // If the branch path is clear, add the position to the
                        // list of foliage positions
                        tempFoliageCoords[clusterCount][0] = x;
                        tempFoliageCoords[clusterCount][1] = y;
                        tempFoliageCoords[clusterCount][2] = z;
                        tempFoliageCoords[clusterCount][3] = checkBranchBase[1];
                        clusterCount++;
                    }
                }
                num++;
            }
            y--;
            relativeY--;
        }
        foliageCoords = new int[clusterCount][4];
        System.arraycopy(tempFoliageCoords, 0, foliageCoords, 0, clusterCount);

    }

    void crossection(int x, int y, int z, float radius, byte direction, Tile material) {
        // Create a circular cross section.
        //
        // Used to nearly everything in the foliage, branches, and trunk.
        // This is a good target for performance optimization.

        // Passed values:
        // x,y,z is the center location of the cross section
        // radius is the radius of the section from the center
        // direction is the direction the cross section is pointed, 0 for x, 1
        // for y, 2 for z material is the index number for the material to use
        int rad = (int) (radius + 0.618);
        byte secidx1 = axisConversionArray[direction];
        byte secidx2 = axisConversionArray[direction + 3];
        int[] center = {
                x, y, z
        };
        int[] position = {
                0, 0, 0
        };
        int offset1 = -rad;
        int offset2 = -rad;
        Tile thisMat;
        position[direction] = center[direction];
        while (offset1 <= rad) {
            position[secidx1] = center[secidx1] + offset1;
            offset2 = -rad;
            while (offset2 <= rad) {
                double thisdistance = Math.pow(Math.abs(offset1) + 0.5, 2) + Math.pow(Math.abs(offset2) + 0.5, 2);
                if (thisdistance > radius * radius) {
                    offset2++;
                    continue;
                }
                position[secidx2] = center[secidx2] + offset2;
                thisMat = thisLevel.getTile(position[0], position[1], position[2]);
                if (!(thisMat == null || thisMat == Tiles.LEAVES)) {
                    // If the material of the checked block is anything other
                    // than air or foliage, skip this tile.
                    offset2++;
                    continue;
                }
                placeBlock(thisLevel, position[0], position[1], position[2], material, 0);
                offset2++;
            }
            offset1++;
        }

    }

    float treeShape(int y) {
        // Take the y position relative to the base of the tree.
        // Return the distance the foliage should be from the trunk axis.
        // Return a negative number if foliage should not be created at this
        // height.  This method is intended for overriding in child classes,
        // allowing different shaped trees.  This method should return a
        // consistent value for each y (don't randomize).
        if (y < (((float) height) * 0.3)) return (float) -1.618;
        float radius = ((float) height) / ((float) 2.0);
        float adjacent = (((float) height) / ((float) 2.0)) - y;
        float distance;
        if (adjacent == 0) distance = radius;
        else if (Math.abs(adjacent) >= radius) distance = (float) 0.0;
        else distance = (float) Math.sqrt(Math.pow(Math.abs(radius), 2) - Math.pow(Math.abs(adjacent), 2));
        // Alter this factor to change the overall width of the tree.
        distance *= (float) 0.5;
        return distance;
    }

    float foliageShape(int y) {
        // Take the y position relative to the base of the foliage cluster.
        // Return the radius of the cluster at this y
        // Return a negative number if no foliage should be created at this
        // level this method is intended for overriding in child classes,
        // allowing foliage of different sizes and shapes.
        if ((y < 0) || (y >= foliageHeight)) return (float) -1;
        else if ((y == 0) || (y == (foliageHeight - 1))) return (float) 2;
        else return (float) 3;
    }

    void foliageCluster(int x, int y, int z) {
        // Generate a cluster of foliage, with the base at x, y, z.
        // The shape of the cluster is derived from foliageShape
        // crossection is called to make each level.
        int cury = y;
        int topy = y + foliageHeight;
        float radius;
        while (cury < topy) {
            radius = foliageShape(cury - y);
            crossection(x, cury, z, radius, (byte) 1, Tiles.LEAVES);
            cury++;
        }
    }

    void limb(int[] start, int[] end, Tile material) {
        // Create a limb from the start position to the end position.
        // Used for creating the branches and trunk.

        // Populate delta, the difference between start and end for all three
        // axies.  Set primidx to the index with the largest overall distance
        // traveled.
        int[] delta = {
                0, 0, 0
        };
        byte idx = 0;
        byte primidx = 0;
        while (idx < 3) {
            delta[idx] = end[idx] - start[idx];
            if (Math.abs(delta[idx]) > Math.abs(delta[primidx])) {
                primidx = idx;
            }
            idx++;
        }
        // If the largest distance is zero, don't bother to do anything else.
        if (delta[primidx] == 0) return;
        // set up the other two axis indices.
        byte secidx1 = axisConversionArray[primidx];
        byte secidx2 = axisConversionArray[primidx + 3];
        // primsign is digit 1 or -1 depending on whether the limb is headed
        // along the positive or negative primidx axis.
        byte primsign;
        if (delta[primidx] > 0) primsign = 1;
        else primsign = -1;
        // Initilize the per-step movement for the non-primary axies.
        double secfac1 = ((double) delta[secidx1]) / ((double) delta[primidx]);
        double secfac2 = ((double) delta[secidx2]) / ((double) delta[primidx]);
        // Initialize the coordinates.
        int[] coordinate = {
                0, 0, 0
        };
        // Loop through each crossection along the primary axis, from start to end
        int primoffset = 0;
        int endoffset = delta[primidx] + primsign;
        while (primoffset != endoffset) {
            coordinate[primidx] = Mth.floor(start[primidx] + primoffset + 0.5);
            coordinate[secidx1] = Mth.floor(start[secidx1] + (primoffset * secfac1) + 0.5);
            coordinate[secidx2] = Mth.floor(start[secidx2] + (primoffset * secfac2) + 0.5);

            int dir = LogTile.FACING_Y;
            int xdiff = Math.abs(coordinate[0] - start[0]);
            int zdiff = Math.abs(coordinate[2] - start[2]);
            int maxdiff = Math.max(xdiff, zdiff);

            if (maxdiff > 0) {
                if (xdiff == maxdiff) {
                    dir = LogTile.FACING_X;
                } else if (zdiff == maxdiff) {
                    dir = LogTile.FACING_Z;
                }
            }

            placeBlock(thisLevel, coordinate[0], coordinate[1], coordinate[2], material, dir);
            primoffset += primsign;
        }

    }

    void makeFoliage() {
        // Create the tree foliage.
        // Call foliageCluster at the correct locations
        int idx = 0;
        int finish = foliageCoords.length;
        while (idx < finish) {
            int x = foliageCoords[idx][0];
            int y = foliageCoords[idx][1];
            int z = foliageCoords[idx][2];
            foliageCluster(x, y, z);
            idx++;
        }
    }

    boolean trimBranches(int localY) {
        // For larger trees, randomly "prune" the branches so there
        // aren't too many.
        // Return true if the branch should be created.
        // This method is intended for overriding in child classes, allowing
        // decent amounts of branches on very large trees.
        // Can also be used to disable branches on some tree types, or
        // make branches more sparse.
        if (localY < (height * 0.2)) return false;
        else return true;
    }

    void makeTrunk() {
        // Create the trunk of the tree.
        int x = origin[0];
        int startY = origin[1];
        int topY = origin[1] + trunkHeight;
        int z = origin[2];
        int[] startCoord = {
                x, startY, z
        };
        int[] endCoord = {
                x, topY, z
        };
        limb(startCoord, endCoord, Tiles.LOG);
        if (trunkWidth == 2) {
            startCoord[0] += 1;
            endCoord[0] += 1;
            limb(startCoord, endCoord, Tiles.LOG);
            startCoord[2] += 1;
            endCoord[2] += 1;
            limb(startCoord, endCoord, Tiles.LOG);
            startCoord[0] += -1;
            endCoord[0] += -1;
            limb(startCoord, endCoord, Tiles.LOG);
        }
    }

    void makeBranches() {
        // Create the tree branches.
        // Call trimBranches for each branch to see if you should create it.
        // Call taperedLimb to the correct locations
        int idx = 0;
        int finish = foliageCoords.length;
        int[] baseCoord = {
                origin[0], origin[1], origin[2]
        };
        while (idx < finish) {
            int[] coordValues = foliageCoords[idx];
            int[] endCoord = {
                    coordValues[0], coordValues[1], coordValues[2]
            };
            baseCoord[1] = coordValues[3];
            int localY = baseCoord[1] - origin[1];
            if (trimBranches(localY)) {
                limb(baseCoord, endCoord, Tiles.LOG);
            }
            idx++;
        }
    }

    int checkLine(int[] start, int[] end) {
        // Check from coordinates start to end (both inclusive) for blocks
        // other than air and foliage If a block other than air and foliage is
        // found, return the number of steps taken.
        // If no block other than air and foliage is found, return -1.
        // Examples:
        // If the third block searched is stone, return 2
        // If the first block searched is lava, return 0

        int[] delta = {
                0, 0, 0
        };
        byte idx = 0;
        byte primidx = 0;
        while (idx < 3) {
            delta[idx] = end[idx] - start[idx];
            if (Math.abs(delta[idx]) > Math.abs(delta[primidx])) {
                primidx = idx;
            }
            idx++;
        }
        // If the largest distance is zero, don't bother to do anything else.
        if (delta[primidx] == 0) return -1;
        // set up the other two axis indices.
        byte secidx1 = axisConversionArray[primidx];
        byte secidx2 = axisConversionArray[primidx + 3];
        // primsign is digit 1 or -1 depending on whether the limb is headed
        // along the positive or negative primidx axis.
        byte primsign;
        if (delta[primidx] > 0) primsign = 1;
        else primsign = -1;
        // Initilize the per-step movement for the non-primary axies.
        double secfac1 = ((double) delta[secidx1]) / ((double) delta[primidx]);
        double secfac2 = ((double) delta[secidx2]) / ((double) delta[primidx]);
        // Initialize the coordinates.
        int[] coordinate = {
                0, 0, 0
        };
        // Loop through each crossection along the primary axis, from start to end
        int primoffset = 0;
        int endoffset = delta[primidx] + primsign;
        Tile thisMat;
        while (primoffset != endoffset) {
            coordinate[primidx] = start[primidx] + primoffset;
            coordinate[secidx1] = Mth.floor(start[secidx1] + (primoffset * secfac1));
            coordinate[secidx2] = Mth.floor(start[secidx2] + (primoffset * secfac2));
            thisMat = thisLevel.getTile(coordinate[0], coordinate[1], coordinate[2]);
            if (!isFree(thisMat)) {
                // If the material of the checked block is anything other than
                // air or foliage, stop looking.
                break;
            }
            primoffset += primsign;
        }
        // If you reached the end without finding anything, return -1.
        if (primoffset == endoffset) {
            return -1;
        }
        // Otherwise, return the number of steps you took.
        else {
            return Math.abs(primoffset);
        }
    }

    boolean checkLocation() {
        // Return true if the tree can be placed here.
        // Return false if the tree can not be placed here.
        // Examine the square under the trunk. Is it grass or dirt?
        // If not, return false
        // Examine center column for how tall the tree can be.
        // If the checked height is shorter than height, but taller
        // than 4, set the tree to the maximum height allowed.
        // If the space is too short, return false.
        int[] startPosition = {
                origin[0], origin[1], origin[2]
        };
        int[] endPosition = {
                origin[0], origin[1] + height - 1, origin[2]
        };
        // Check the location it is resting on
        final Tile tile = thisLevel.getTile(origin[0], origin[1] - 1, origin[2]);
        if (!(tile == Tiles.DIRT || tile == Tiles.GRASS || tile == Tiles.FARMLAND)) {
            return false;
        }
        int allowedHeight = checkLine(startPosition, endPosition);
        // If the set height is good, go with that
        if (allowedHeight == -1) {
            return true;
        }
        // If the space is too short, tell the build to abort
        else if (allowedHeight < 6) {
            return false;
        }
        // If the space is shorter than the set height, but not too short
        // shorten the height, and tell the build to continue
        else {
            height = allowedHeight;
            return true;
        }
    }

    @Override
    public void init(double heightInit, double widthInit, double foliageDensityInit) {
        // all of the parameters should be from 0.0 to 1.0
        // heightInit scales the maximum overall height of the tree (still
        // randomizes height within the possible range) widthInit scales the
        // maximum overall width of the tree (keep this above 0.3 or so)
        // foliageDensityInit scales how many foliage clusters are created.
        //
        // Note, you can call "place" without calling "init".
        // This is the same as calling init(1.0,1.0,1.0) and then calling place.
        heightVariance = (int) (heightInit * 12);
        if (heightInit > 0.5) foliageHeight = 5;
        widthScale = widthInit;
        foliageDensity = foliageDensityInit;
    }

    @Override
    public boolean place(Level level, Random random, int x, int y, int z) {
        // Note to Markus.
        // currently the following fields are set randomly. If you like, make
        // them parameters passed into "place".
        //
        // height: so the map generator can intelligently set the height of the
        // tree, and make forests with large trees in the middle and smaller
        // ones on the edges.

        // Initialize the instance fields for the level and the seed.
        thisLevel = level;
        long seed = random.nextLong();
        rnd.setSeed(seed);
        // Initialize the origin of the tree trunk
        origin[0] = x;
        origin[1] = y;
        origin[2] = z;
        // Sets the height. Take out this line if height is passed as a parameter
        if (height == 0) {
            height = 5 + rnd.nextInt(heightVariance);
        }
        if (!(checkLocation())) {
            return false;
        }
        prepare();
        makeFoliage();
        makeTrunk();
        makeBranches();
        return true;
    }
}