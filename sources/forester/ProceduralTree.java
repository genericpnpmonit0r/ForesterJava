package forester;

import java.util.ArrayList;

/**
 * Set up the methods for a larger more complicated tree.<BR>
 * <BR>
 * This tree type has roots, a trunk, and branches all of varying width, and many foliage clusters.<BR>
 * MUST BE SUBCLASSED.  Specifically, this.foliage_shape must be set.<BR>
 * Subclass 'prepare' and 'shapefunc' to make different shaped trees.<BR>
 */
public abstract class ProceduralTree extends Tree {
	protected double trunkHeight;
	protected double[] foliageShape;
	protected ArrayList<int[]> foliageCoords;
	protected double trunkRadius;
	protected double branchDensity;
	protected double branchSlope;
	
	protected ProceduralTree() {}

	/**
	 * Create a round section of type matidx in mcmap.
	 * <br>
	 * @param x x coord of center
	 * @param y y coord of center
	 * @param z z coord of center
	 * @param radius the radius of the section
	 * @param dirAxis The list index for the axis to make the section perpendicular to. 0 indicates the x axis, 1 the y, 2 the z. The section will extend along the other two axes
	 * @param blockID the integer value to make the section out of
	 * @param metadata the integer value to make the metadata of the section out of
	 */
	protected void crossSection(int x, int y, int z, double radius, int dirAxis, int blockID, int metadata) {
		int rad = (int) (radius + .618D);
		if (rad <= 0) return;
		int[] coord = { 0, 0, 0 };
		int[] center = { x, y, z };
		int secidx1 = Forester.getOtherIndexes(center, dirAxis)[0];
		int secidx2 = Forester.getOtherIndexes(center, dirAxis)[1];
		for (int off1 = -rad; off1 < rad + 1; off1++) {
			for (int off2 = -rad; off2 < rad + 1; off2++) {
				double thisdist = Math
						.sqrt(Math.pow(Math.abs(off1) + 0.5D, 2) + Math.pow(Math.abs(off2) + 0.5D, 2));
				if (thisdist > radius) {
					continue;
				}
				int pri = center[dirAxis];
				int sec1 = center[secidx1] + off1;
				int sec2 = center[secidx2] + off2;
				coord[dirAxis] = pri;
				coord[secidx1] = sec1;
				coord[secidx2] = sec2;
				this.assignValue(coord[0], coord[1], coord[2], blockID, metadata);
			}
		}
	}

	/**
	 * Take y and return a radius for the location of the foliage cluster.<br>
	 * <br>
	 * If no foliage cluster is to be created, return null<br>
	 * Designed for sub-classing. Only makes clusters close to the trunk.<br>
	 * @param y y coordinate
	 */
	protected double shapeFunc(int y) {
		if (random.nextDouble() < 100 / (Math.pow(this.height, 2)) && y < this.trunkHeight) {
			return this.height * .12;
		}
		return Double.NaN;
	}

	/**
	 * generate a round cluster of foliage at the location center.<br>
	 * <br>
	 * The shape of the cluster is defined by the list this.foliage_shape.<br>
	 * This list must be set in a subclass of ProceduralTree.<br>
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	protected void foliageCluster(int x, int y, int z) {
		double[] level_radius = this.foliageShape;
		
		for (double i : level_radius) {
			this.crossSection(x, y, z, i, 1, this.treeLeafBlock, this.treeLeafMetadata);
			y += 1;
		}
	}

	/**
	 * Create a tapered cylinder in mcmap.<br>
	 * <br>
     * @param start beginning x y z coordinates
     * @param end ending x y z coordinates
     * @param startSize the beginning radius
     * @param endSize the ending radius
     * @param blockID block id
     * @param metadata block metadata
	 */
	protected void taperedCylinder(int[] start, int[] end, double startSize, double endSize, int blockID, int metadata) {
		int[] delta = { 0, 0, 0 };
		for (int i = 0; i < 3; i++) {
			delta[i] = end[i] - start[i];
		}
		int maxdist = Forester.maxKeyAbs(delta);
		if (maxdist == 0) {
			return;
		}
		
		int primidx = Forester.getArrayIndex(delta, maxdist);
		int secidx1 = Forester.getOtherIndexes(delta, primidx)[0]; //an axis conversion array can probably be used as well
		int secidx2 = Forester.getOtherIndexes(delta, primidx)[1];

		int primsign = delta[primidx] / Math.abs(delta[primidx]);
		int secdelta1 = delta[secidx1];
		double secfac1 = ((double) secdelta1) / delta[primidx];
		int secdelta2 = delta[secidx2];
		double secfac2 = ((double) secdelta2) / delta[primidx];

		int[] coord = { 0, 0, 0 };
		int endoffset = delta[primidx] + primsign;
		for (int primoffset = 0; primoffset != endoffset; primoffset += primsign) {
			int primloc = start[primidx] + primoffset;
			int secloc1 = (int) (start[secidx1] + (primoffset * secfac1));
			int secloc2 = (int) (start[secidx2] + (primoffset * secfac2));
			coord[primidx] = primloc;
			coord[secidx1] = secloc1;
			coord[secidx2] = secloc2;
			if(useLogRot) {
				metadata = logRotation(coord, start);
			}
			
			int primdist = Math.abs(delta[primidx]);
			double radius = endSize + (startSize - endSize) * Math.abs(delta[primidx] - primoffset) / primdist;
			this.crossSection(coord[0], coord[1], coord[2], radius, primidx, blockID, metadata);
		}
	}

	/**
	 * generate the roots and enter them in mcmap.<br>
	 * <br>
	 * @param rootBases [[x,z,base_radius], ...] and is the list of locations<br> the roots can originate from, and the size of that location.<br>
	 */
	protected void makeRoots(ArrayList<double[]> rootBases) {
		int[] treeposition = this.pos;
		int height = this.height;

		for (int[] coord : this.foliageCoords) {
			double dist = Math.sqrt(Math.pow(coord[0] - treeposition[0], 2) + Math.pow(coord[2] - treeposition[2], 2));
			int ydist = coord[1] - treeposition[1];
			double value = (this.branchDensity * 220 * height) / (Math.pow((ydist + dist), 3));
			if (value < random.nextDouble()) {
				continue;
			}

			double[] rootbase = rootBases.get(random.nextInt(rootBases.size()));
			int rootx = (int) rootbase[0];
			int rootz = (int) rootbase[1];
			double rootbaseradius = rootbase[2];

			double rndr = Math.sqrt(random.nextDouble()) * rootbaseradius * .618;
			double rndang = random.nextDouble() * 2 * Math.PI;
			int rndx = (int) (rndr * Math.sin(rndang) + 0.5);
			int rndz = (int) (rndr * Math.cos(rndang) + 0.5);
			int rndy = (int) (random.nextDouble() * rootbaseradius * 0.5);

			int[] startcoord = { rootx + rndx, treeposition[1] + rndy, rootz + rndz };

			int[] offset = { 0, 0, 0 };
			int i;
			for (i = 0; i < 3; i++) {
				offset[i] = startcoord[i] - coord[i];
			}

			if (this instanceof MangroveTree) {
				for (i = 0; i < 3; i++) {
					offset[i] = (int) (offset[i] * 1.618 - 1.5);
				}
			}

			int[] endcoord = { 0, 0, 0 };
			for (i = 0; i < 3; i++) {
				endcoord[i] = startcoord[i] + offset[i];
			}

			double rootstartsize = (rootbaseradius * 0.618 * Math.abs(offset[1]) / height * 0.618);
			if (rootstartsize < 1.0) {
				rootstartsize = 1.0;
			}

			double endsize = 1.0;

			if (this.treeRoots == TreeRoots.TO_STONE || this.treeRoots == TreeRoots.HANGING) {
				int offlength = (int) (Math.sqrt(Math.pow(offset[0], 2) + Math.pow(offset[1], 2) + Math.pow(offset[2], 2)));
				if (offlength < 1) {
					continue;
				}
				double rootmid = endsize;
				int[] vec = { 0, 0, 0 };
				for (i = 0; i < 3; i++) {
					vec[i] = offset[i] / offlength;
				}
				
				int[] searchindex = { 0 };
				if (this.treeRoots == TreeRoots.TO_STONE) {
					searchindex = this.treeRootStoppingBlocks;
				} else if (this.treeRoots == TreeRoots.HANGING) {
					searchindex = new int[] { 0 };
				}

				int startdist = (int) (random.nextDouble() * 6 * Math.sqrt(rootstartsize) + 2.8);
				int[] searchstart = { 0, 0, 0 };
				for (i = 0; i < 3; i++) {
					searchstart[i] = startcoord[i] + startdist*vec[i];
				}
				dist = startdist + Forester.distToMat(searchstart, vec, searchindex, this.mcmap, false, offlength);
				
				if (dist < offlength) {
					rootmid += (rootstartsize - endsize) * (1 - dist / offlength);
					for (i = 0; i < 3; i++) {
						endcoord[i] = startcoord[i] + (int)(vec[i] * dist);
					}
					if (this.treeRoots == TreeRoots.HANGING) {
						double remaining_dist = offlength - dist;
						int[] bottomcord = new int[endcoord.length];
						System.arraycopy(endcoord, 0, bottomcord, 0, endcoord.length);
                        bottomcord[1] -= ((int) (remaining_dist)); /*not sure if correct, original code was bottomcord[1] += -int(remaining_dist)*/
						this.taperedCylinder(endcoord, bottomcord, rootmid, endsize, this.treeWoodBlock, this.treeWoodMetadata);
					}

				}
				this.taperedCylinder(startcoord, endcoord, rootstartsize, rootmid, this.treeWoodBlock, this.treeWoodMetadata);
			} else {
				this.taperedCylinder(startcoord, endcoord, rootstartsize, endsize, this.treeWoodBlock, this.treeWoodMetadata);
			}
		}
	}

	/** Generate the branches and enter them in mcmap. */
	protected void makeBranches() {
		int[] treeposition = this.pos;
		int height = this.height;
		int topy = treeposition[1] + (int) (this.trunkHeight + 0.5);

		double endrad = this.trunkRadius * (1 - this.trunkHeight / height);
		if (endrad < 1.0) {
			endrad = 1.0;
		}

		for (int[] coord : this.foliageCoords) {
			double dist = Math.sqrt(Math.pow(coord[0] - treeposition[0], 2) + Math.pow(coord[2] - treeposition[2], 2));
			int ydist = coord[1] - treeposition[1];
			double value = (this.branchDensity * 220 * height) / Math.pow((ydist + dist), 3);
			if (value < random.nextDouble()) {
				continue;
			}

			int posy = coord[1];
			double branchy;
			double basesize;
			double slope = this.branchSlope + (0.5 - random.nextDouble()) * .16;
			if (coord[1] - dist * slope > topy) {
				double threshhold = (double) 1 / height;
				if (random.nextDouble() < threshhold) {
					continue;
				}

				branchy = topy;
				basesize = endrad;
			} else {
				branchy = posy - dist * slope;
				basesize = (endrad + (this.trunkRadius - endrad) * (topy - branchy) / this.trunkHeight);
			}
			double startsize = basesize * (1 + random.nextDouble()) * .618 * Math.pow(dist / height, 0.618);
			double rndr = Math.sqrt(random.nextDouble()) * basesize * 0.618;
			double rndang = random.nextDouble() * 2 * Math.PI;
			int rndx = (int) (rndr * Math.sin(rndang) + 0.5);
			int rndz = (int) (rndr * Math.cos(rndang) + 0.5);
			int[] startcoord = { treeposition[0] + rndx, (int) branchy, treeposition[2] + rndz };

			if (startsize < 1.0) {
				startsize = 1.0;
			}
			double endsize = 1.0;
			this.taperedCylinder(startcoord, coord, startsize, endsize, this.treeWoodBlock, this.treeWoodMetadata);
		}
	}
	
	/**
	 * Experimental log x/z rotation
	 * @param coord x y z array
	 * @param start starting coord
	 * @return rotation metadata
	 */
	protected int logRotation(int[] coord, int[] start) {
		int dir = 0;
		int xdiff = Math.abs(coord[0] - start[0]);
		int zdiff = Math.abs(coord[2] - start[2]);
		int maxdiff = Math.max(xdiff, zdiff);
		
		if(maxdiff > 0) {
			if(xdiff == maxdiff) {
				dir = this.logRotXMetadata;
			} else {
				dir = this.logRotZMetadata;
			}
		}
		
		return dir;
	}

	/** Initialize the internal values for the Tree object. Primarily, sets up the foliage cluster locations.*/
	@Override
	protected void prepare() {
		int[] treeposition = this.pos;
		this.trunkRadius = .618 * Math.sqrt(this.height * this.treeTrunkThickness);
		int yend;
		if (this.trunkRadius < 1) {
			this.trunkRadius = 1;
		}
		if (this.treeBrokenTrunk) {
			this.trunkHeight = this.height * (.3 + random.nextDouble() * .4);
			yend = (int) (treeposition[1] + this.trunkHeight + .5);
		} else {
			this.trunkHeight = this.height;
			yend = treeposition[1] + this.height;
		}
		this.branchDensity = this.treeBranchDensity / this.treeFoliageDensity;

		int topy = treeposition[1] + (int) (this.trunkHeight + 0.5D);

		int ystart = treeposition[1];
		int num_of_clusters_per_y = (int) (1.5 + Math.pow((this.treeFoliageDensity * this.height / 19.), 2));
		ArrayList<int[]> foliage_coords = new ArrayList<int[]>();
		
		if (num_of_clusters_per_y < 1) {
			num_of_clusters_per_y = 1;
		}

		/*
		if (yend > this.mcmap.getHeight())
			yend = this.mcmap.getHeight();
		if (ystart > this.mcmap.getHeight())
			ystart = this.mcmap.getHeight();
		*/
		
		for (int y = yend; y > ystart; y--) {
			for (int i = 0; i < num_of_clusters_per_y; i++) {
				double shapefac = this.shapeFunc(y - ystart);
				if (Double.isNaN(shapefac))
					continue;
				double r = (Math.sqrt(random.nextDouble()) + .328) * shapefac;

				double theta = random.nextDouble() * 2 * Math.PI;
				int x = (int) (r * Math.sin(theta) + treeposition[0]);
				int z = (int) (r * Math.cos(theta) + treeposition[2]);

				if (this.treeBranchStoppingBlocks.length != 0) {
					double dist = (Math.sqrt(Math.pow(x - treeposition[0], 2) + Math.pow(z - treeposition[2], 2)));
					double slope = this.branchSlope;
					int starty;
					if ((y - dist * slope) > topy) {
						starty = topy;
					} else {
						starty = (int) (y - dist * slope);
					}

					int[] start = { treeposition[0], starty, treeposition[2] };
					int[] offset = { x - treeposition[0], y - starty, z - treeposition[2] };
					double offlength = Math
							.sqrt(Math.pow(offset[0], 2) + Math.pow(offset[1], 2) + Math.pow(offset[2], 2));
					if (offlength < 1)
						continue;

					int[] vec = { 0, 0, 0 };
					for (int k = 0; k < 3; k++) {
						vec[k] = (int) (offset[k] / offlength);
					}
						
					int mat_dist = Forester.distToMat(start, vec, this.treeBranchStoppingBlocks, this.mcmap, false, (int) offlength + 3);
					if (mat_dist < (int) offlength + 2) {
						continue;
					}
				}
				foliage_coords.add(new int[] { x, y, z });
			}
		}
		
		this.foliageCoords = foliage_coords;
	}

	/**
	 * Generate the foliage for the tree in mcmap.<br>
	 * <br>
	 * NOTE: foliage will disintegrate if there is no foliage below, or
	 * if there is no "log" block within range 2 (square) at the same level or one level below
	 */
	@Override
	protected void makeFoliage() {
		ArrayList<int[]> foliage_coords = this.foliageCoords;
		for (int[] coord : foliage_coords) {
			this.foliageCluster(coord[0], coord[1], coord[2]);
		}

		for (int[] cord : foliage_coords) {
			this.assignValue(cord[0], cord[1], cord[2], this.treeWoodBlock, this.treeWoodMetadata);
			if (this.treeLights == 1) {
				this.assignValue(cord[0], cord[1] + 1, cord[2], this.treeLightingBlock, this.treeLightingMetadata);
			} else if (this.treeLights == 2 || this.treeLights == 3 || this.treeLights == 4) { // jank
				this.assignValue(cord[0] + 1, cord[1], cord[2], this.treeLightingBlock, this.treeLightingMetadata);
				this.assignValue(cord[0] - 1, cord[1], cord[2], this.treeLightingBlock, this.treeLightingMetadata);
				if (this.treeLights == 4) {
					this.assignValue(cord[0], cord[1], cord[2] + 1, this.treeLightingBlock, this.treeLightingMetadata);
					this.assignValue(cord[0], cord[1], cord[2] - 1, this.treeLightingBlock, this.treeLightingMetadata);
				}
			}
		}
	}

	/** Generate the trunk, roots, and branches in mcmap. */
	@Override
	protected void makeTrunk() {
		int height = this.height;
		double trunkheight = this.trunkHeight;
		double trunkradius = this.trunkRadius;
		int[] treeposition = this.pos;
		int starty = treeposition[1];
		int midy = treeposition[1] + (int) (trunkheight * .382);
		int topy = treeposition[1] + (int) (trunkheight + 0.5);

		int x = treeposition[0];
		int z = treeposition[2];
		double end_size_factor = trunkheight / height;
		double midrad = trunkradius * (1 - end_size_factor * .5);
		double endrad = trunkradius * (1 - end_size_factor);

		if (endrad < 1.0)
			endrad = 1.0;
		if (midrad < endrad)
			midrad = endrad;
		ArrayList<double[]> rootbases = new ArrayList<double[]>();
		double startrad;
		int i;
		
		if (this.treeRootButtresses || this instanceof MangroveTree) {
			startrad = trunkradius * .8;
			rootbases.add(new double[] {x, z, startrad});
			double buttress_radius = trunkradius * 0.382;
			double posradius = trunkradius;
			int num_of_buttresses = (int) (Math.sqrt(trunkradius) + 3.5);

			if (this instanceof MangroveTree) {
				posradius = posradius * 2.618;
			}

			for (i = 0; i < num_of_buttresses; i++) {
				double rnang = random.nextDouble() * 2 * Math.PI;
				double thisposradius = posradius * (0.9 + random.nextDouble() * .2);

				int thisx = x + ((int) (thisposradius * Math.sin(rnang)));
				int thisz = z + ((int) (thisposradius * Math.cos(rnang)));

				double thisbuttressradius = buttress_radius * (0.618 + random.nextDouble());
				if (thisbuttressradius < 1.0) {
					thisbuttressradius = 1.0;
				}

				this.taperedCylinder(new int[] { thisx, starty, thisz }, new int[] { x, midy, z }, thisbuttressradius, thisbuttressradius, this.treeWoodBlock, this.treeWoodMetadata);
				rootbases.add(new double[] {thisx, thisz, thisbuttressradius});
			}

		} else {
			startrad = trunkradius;
			rootbases.add(new double[] {x, z, startrad});
		}

		this.taperedCylinder(new int[] { x, starty, z }, new int[] { x, midy, z }, startrad, midrad, this.treeWoodBlock, this.treeWoodMetadata);
		this.taperedCylinder(new int[] { x, midy, z }, new int[] { x, topy, z }, midrad, endrad, this.treeWoodBlock, this.treeWoodMetadata);

		this.makeBranches();

		if (this.treeRoots == TreeRoots.YES || this.treeRoots == TreeRoots.TO_STONE || this.treeRoots == TreeRoots.HANGING) {
			this.makeRoots(rootbases);
		}

		if (trunkradius > 2 && this.treeHollowTrunk) {
			double wall_thickness = (1 + trunkradius * 0.1 * random.nextDouble());
			if (wall_thickness < 1.3) {
				wall_thickness = 1.3;
			}
			double base_radius = trunkradius - wall_thickness;
			if (base_radius < 1)
				base_radius = 1.0;

			double mid_radius = midrad - wall_thickness;
			double top_radius = endrad - wall_thickness;

			int base_offset = (int) (wall_thickness);
			int[] x_choices = Forester.range(x - base_offset, x + base_offset, 1);
			int start_x = Forester.choice(random, x_choices);

			int[] z_choices = Forester.range(z - base_offset, z + base_offset, 1);
			int start_z = Forester.choice(random, z_choices);

			this.taperedCylinder(new int[] { start_x, starty, start_z }, new int[] { x, midy, z }, base_radius, mid_radius, this.treeTrunkFillerBlock, this.treeTrunkFillerMetadata);
			int hollow_top_y = (int) (topy + trunkradius + 1.5);
			this.taperedCylinder(new int[] { x, midy, z }, new int[] { x, hollow_top_y, z }, mid_radius, top_radius, this.treeTrunkFillerBlock, this.treeTrunkFillerMetadata);
		}
	}
}
