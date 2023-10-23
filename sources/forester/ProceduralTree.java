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
	protected double trunkheight;
	protected double[] foliage_shape;
	private int[][] foliage_coords;
	protected double trunkradius;
	protected double branchdensity;
	protected double branchslope;

	public ProceduralTree(MCWorldAccessor mcmap) {
		this.mcmap = mcmap;
	}

	/**
	 * Create a round section of type matidx in mcmap.
	 * <br>
	 * @param x x coord of center
	 * @param y y coord of center
	 * @param z z coord of center
	 * @param radius the radius of the section
	 * @param diraxis The list index for the axis to make the section perpendicular to. 0 indicates the x axis, 1 the y, 2 the z. The section will extend along the other two axies
	 * @param mat the integer value to make the section out of
	 * @param data the integer value to make the metadata of the section out of
	 */
	private void crossection(int x, int y, int z, double radius, int diraxis, int mat, int data) {
		int rad = (int) (radius + .618D);
		if (rad <= 0) return;
		int[] coord = { 0, 0, 0 };
		int[] center = { x, y, z };
		int secidx1 = Forester.getOtherIndexes(center, diraxis)[0];
		int secidx2 = Forester.getOtherIndexes(center, diraxis)[1];
		for (int off1 : Forester.range(-rad, rad + 1, 1)) {
			for (int off2 : Forester.range(-rad, rad + 1, 1)) {
				double thisdist = Math
						.sqrt(Math.pow(Math.abs(off1) + 0.5D, 2) + Math.pow(Math.abs(off2) + 0.5D, 2));
				if (thisdist > radius) {
					continue;
				}
				int pri = center[diraxis];
				int sec1 = center[secidx1] + off1;
				int sec2 = center[secidx2] + off2;
				coord[diraxis] = pri;
				coord[secidx1] = sec1;
				coord[secidx2] = sec2;
				Forester.assign_value(coord[0], coord[1], coord[2], mat, data, mcmap);
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
	protected Double shapefunc(int y) {
		if (Math.random() < 100 / (Math.pow(this.height, 2)) && y < this.trunkheight) {
			return this.height * .12;
		}
		return null;
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
	private void foliagecluster(int x, int y, int z) {
		double[] level_radius = this.foliage_shape;
		
		for (double i : level_radius) {
			this.crossection(x, y, z, i, 1, this.tree_LEAFMAT, this.tree_LEAFDATA);
			y += 1;
		}
	}

	/**
	 * Create a tapered cylinder in mcmap.<br>
	 * <br>
     * @param start beginning x y z coordinates
     * @param end ending x y z coordinates
     * @param startsize the beginning radius
     * @param endsize the ending radius
     * @param blockmat block id
     * @param blockdata block metadata
	 */
	private void taperedcylinder(int[] start, int[] end, double startsize, double endsize, int blockmat, int blockdata) {
		int[] delta = { 0, 0, 0 };
		for (int i = 0; i < 3; i++) {
			delta[i] = (int)(end[i] - start[i]);
		}
		int maxdist = Forester.max_key_abs(delta);
		if (maxdist == 0) {
			return;
		}
		
		int primidx = Forester.getArrayIndex(delta, maxdist);
		int secidx1 = Forester.getOtherIndexes(delta, primidx)[0];
		int secidx2 = Forester.getOtherIndexes(delta, primidx)[1];

		int primsign = (int) (delta[primidx] / Math.abs(delta[primidx]));
		int secdelta1 = delta[secidx1];
		double secfac1 = ((double) secdelta1) / delta[primidx];
		int secdelta2 = delta[secidx2];
		double secfac2 = ((double) secdelta2) / delta[primidx];

		int[] coord = { 0, 0, 0 };
		int endoffset = delta[primidx] + primsign;
		for (int primoffset : Forester.range(0, endoffset, primsign)) {
			int primloc = (int) (start[primidx] + primoffset);
			int secloc1 = (int) (start[secidx1] + (primoffset * secfac1));
			int secloc2 = (int) (start[secidx2] + (primoffset * secfac2));
			coord[primidx] = primloc;
			coord[secidx1] = secloc1;
			coord[secidx2] = secloc2;
			int primdist = Math.abs(delta[primidx]);
			double radius = endsize + (startsize - endsize) * Math.abs(delta[primidx] - primoffset) / primdist;
			this.crossection(coord[0], coord[1], coord[2], radius, primidx, blockmat, blockdata);
		}
	}

	/**
	 * generate the roots and enter them in mcmap.<br>
	 * <br>
	 * rootbases = [[x,z,base_radius], ...] and is the list of locations<br>
	 * the roots can originate from, and the size of that location.<br>
	 */
	private void makeroots(double[][] rootbases) {
		int[] treeposition = this.pos;
		int height = this.height;

		for (int[] coord : this.foliage_coords) {
			double dist = Math
					.sqrt(Math.pow(coord[0] - treeposition[0], 2) + Math.pow(coord[2] - treeposition[2], 2));
			int ydist = coord[1] - treeposition[1];
			double value = (this.branchdensity * 220 * height) / (Math.pow((ydist + dist), 3));
			if (value < Math.random()) {
				continue;
			}

			double[] rootbase = rootbases[RANDOM.nextInt(rootbases.length)];
			int rootx = (int) rootbase[0];
			int rootz = (int) rootbase[1];
			double rootbaseradius = rootbase[2];

			double rndr = Math.sqrt(Math.random()) * rootbaseradius * .618;
			double rndang = Math.random() * 2 * Math.PI;
			int rndx = (int) (rndr * Math.sin(rndang) + 0.5);
			int rndz = (int) (rndr * Math.cos(rndang) + 0.5);
			int rndy = (int) (Math.random() * rootbaseradius * 0.5);

			int[] startcoord = { rootx + rndx, treeposition[1] + rndy, rootz + rndz };

			int[] offset = { 0, 0, 0 };
			int i;
			for (i = 0; i < 3; i++) {
				offset[i] = startcoord[i] - coord[i];
			}

			if (this.tree_SHAPE == TreeShape.MANGROVE) {
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

			if (this.tree_ROOTS == TreeRoots.TO_STONE || this.tree_ROOTS == TreeRoots.HANGING) {
				int offlength = (int) (Math
						.sqrt(Math.pow(offset[0], 2) + Math.pow(offset[1], 2) + Math.pow(offset[2], 2)));
				if (offlength < 1) {
					continue;
				}
				double rootmid = endsize;
				int vec[] = { 0, 0, 0 };
				for (i = 0; i < 3; i++) {
					vec[i] = offset[i] / offlength;
				}
				
				int[] searchindex = { 0 };
				if (this.tree_ROOTS == TreeRoots.TO_STONE) {
					searchindex = this.tree_STOPSROOTS;
				} else if (this.tree_ROOTS == TreeRoots.HANGING) {
					searchindex = new int[] { 0 };
				}

				int startdist = (int) (Math.random() * 6 * Math.sqrt(rootstartsize) + 2.8);
				int[] searchstart = { 0, 0, 0 };
				for (i = 0; i < 3; i++) {
					searchstart[i] = startcoord[i] + startdist*vec[i];
				}
				dist = startdist + Forester.dist_to_mat(searchstart, vec, searchindex, this.mcmap, false, offlength);
				
				if (dist < offlength) {
					rootmid += (rootstartsize - endsize) * (1 - dist / offlength);
					for (i = 0; i < 3; i++) {
						endcoord[i] = startcoord[i] + (int)(vec[i] * dist);
					}
					if (this.tree_ROOTS == TreeRoots.HANGING) {
						double remaining_dist = offlength - dist;
						int[] bottomcord = new int[endcoord.length];
						System.arraycopy(endcoord, 0, bottomcord, 0, endcoord.length);
						bottomcord[1] += -((int) (remaining_dist));
						this.taperedcylinder(endcoord, bottomcord, rootmid, endsize, this.tree_WOODMAT, this.tree_WOODDATA);
					}

				}
				this.taperedcylinder(startcoord, endcoord, rootstartsize, rootmid, this.tree_WOODMAT, this.tree_WOODDATA);
			} else {
				this.taperedcylinder(startcoord, endcoord, rootstartsize, endsize, this.tree_WOODMAT, this.tree_WOODDATA);
			}
		}
	}

	/** Generate the branches and enter them in mcmap. */
	private void makebranches() {
		int[] treeposition = this.pos;
		int height = this.height;
		int topy = treeposition[1] + (int) (this.trunkheight + 0.5);

		double endrad = this.trunkradius * (1 - this.trunkheight / height);
		if (endrad < 1.0) {
			endrad = 1.0;
		}

		for (int coord[] : this.foliage_coords) {
			double dist = Math
					.sqrt(Math.pow(coord[0] - treeposition[0], 2) + Math.pow(coord[2] - treeposition[2], 2));
			int ydist = coord[1] - treeposition[1];
			double value = (this.branchdensity * 220 * height) / Math.pow((ydist + dist), 3);
			if (value < Math.random()) {
				continue;
			}

			int posy = coord[1];
			double branchy;
			double basesize;
			double slope = this.branchslope + (0.5 - Math.random()) * .16;
			if (coord[1] - dist * slope > topy) {
				double threshhold = 1 / height;
				if (Math.random() < threshhold) {
					continue;
				}

				branchy = topy;
				basesize = endrad;
			} else {
				branchy = posy - dist * slope;
				basesize = (endrad + (this.trunkradius - endrad) * (topy - branchy) / this.trunkheight);
			}
			double startsize = basesize * (1 + Math.random()) * .618 * Math.pow(dist / height, 0.618);
			double rndr = Math.sqrt(Math.random()) * basesize * 0.618;
			double rndang = Math.random() * 2 * Math.PI;
			int rndx = (int) (rndr * Math.sin(rndang) + 0.5);
			int rndz = (int) (rndr * Math.cos(rndang) + 0.5);
			int[] startcoord = { treeposition[0] + rndx, (int) branchy, treeposition[2] + rndz };

			if (startsize < 1.0) {
				startsize = 1.0;
			}
			double endsize = 1.0;
			this.taperedcylinder(startcoord, coord, startsize, endsize, this.tree_WOODMAT, this.tree_WOODDATA);
		}
	}

	/** Initialize the internal values for the Tree object. Primarily, sets up the foliage cluster locations.*/
	@Override
	public void prepare() {
		int[] treeposition = this.pos;
		this.trunkradius = .618 * Math.sqrt(this.height * this.tree_TRUNKTHICKNESS);
		int yend;
		if (this.trunkradius < 1) {
			this.trunkradius = 1;
		}
		if (this.tree_BROKENTRUNK) {
			this.trunkheight = this.height * (.3 + Math.random() * .4);
			yend = (int) (treeposition[1] + this.trunkheight + .5);
		} else {
			this.trunkheight = this.height;
			yend = treeposition[1] + this.height;
		}
		this.branchdensity = this.tree_BRANCHDENSITY / this.tree_FOLIAGEDENSITY;

		int topy = treeposition[1] + (int) (this.trunkheight + 0.5D);

		int ystart = treeposition[1];
		int num_of_clusters_per_y = (int) (1.5 + Math.pow((this.tree_FOLIAGEDENSITY * this.height / 19.), 2));
		ArrayList<int[]> foliage_coords = new ArrayList<int[]>();
		
		if (num_of_clusters_per_y < 1) {
			num_of_clusters_per_y = 1;
		}

		if (yend > 127)
			yend = 127;
		if (ystart > 127)
			ystart = 127;

		for (int y : Forester.range(yend, ystart, -1)) {
			for (int i = 0; i < num_of_clusters_per_y; i++) {
				Double shapefac = this.shapefunc(y - ystart);
				if (shapefac == null)
					continue;
				double r = (Math.sqrt(Math.random()) + .328) * shapefac.doubleValue();

				double theta = Math.random() * 2 * Math.PI;
				int x = (int) (r * Math.sin(theta) + treeposition[0]);
				int z = (int) (r * Math.cos(theta) + treeposition[2]);

				if (this.tree_STOPSBRANCHES.length != 0) {
					double dist = (Math.sqrt(Math.pow(x - treeposition[0], 2) + Math.pow(z - treeposition[2], 2)));
					double slope = this.branchslope;
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
						
					int mat_dist = Forester.dist_to_mat(start, vec, this.tree_STOPSBRANCHES, this.mcmap, false, (int) offlength + 3);
					if (mat_dist < (int) offlength + 2) {
						continue;
					}
				}
				foliage_coords.add(new int[] { x, y, z });
			}
		}

		this.foliage_coords = foliage_coords.stream().map(u -> u).toArray(int[][]::new);;
	}

	/**
	 * Generate the foliage for the tree in mcmap.<br>
	 * <br>
	 * NOTE: foliage will disintegrate if there is no foliage below, or
	 * if there is no "log" block within range 2 (square) at the same level or one level below
	 */
	@Override
	public void makefoliage() {
		int[][] foliage_coords = this.foliage_coords;
		for (int[] coord : foliage_coords) {
			this.foliagecluster(coord[0], coord[1], coord[2]);
		}

		for (int[] cord : foliage_coords) {
			Forester.assign_value(cord[0], cord[1], cord[2], this.tree_WOODMAT, this.tree_WOODDATA, this.mcmap);
			if (this.tree_LIGHTTREE == 1) {
				Forester.assign_value(cord[0], cord[1] + 1, cord[2], this.tree_LIGHTMAT, this.tree_LIGHTDATA, this.mcmap);
			} else if (this.tree_LIGHTTREE == 2 || this.tree_LIGHTTREE == 3 || this.tree_LIGHTTREE == 4) { // jank
				Forester.assign_value(cord[0] + 1, cord[1], cord[2], this.tree_LIGHTMAT, this.tree_LIGHTDATA, this.mcmap);
				Forester.assign_value(cord[0] - 1, cord[1], cord[2], this.tree_LIGHTMAT, this.tree_LIGHTDATA, this.mcmap);
				if (this.tree_LIGHTTREE == 4) {
					Forester.assign_value(cord[0], cord[1], cord[2] + 1, this.tree_LIGHTMAT, this.tree_LIGHTDATA, this.mcmap);
					Forester.assign_value(cord[0], cord[1], cord[2] - 1, this.tree_LIGHTMAT, this.tree_LIGHTDATA, this.mcmap);
				}
			}
		}
	}

	/** Generate the trunk, roots, and branches in mcmap. */
	@Override
	public void maketrunk() {
		int height = this.height;
		double trunkheight = this.trunkheight;
		double trunkradius = this.trunkradius;
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
		double[][] rootbases;
		double startrad;
		int i;
		
		if (this.tree_ROOTBUTTRESSES || this.tree_SHAPE == TreeShape.MANGROVE) {
			startrad = trunkradius * .8;

			double buttress_radius = trunkradius * 0.382;
			double posradius = trunkradius;
			int num_of_buttresses = (int) (Math.sqrt(trunkradius) + 3.5);
			rootbases = new double[num_of_buttresses][3];

			if (this.tree_SHAPE == TreeShape.MANGROVE) {
				posradius = posradius * 2.618;
			}

			for (i = 0; i < num_of_buttresses; i++) {
				double rnang = Math.random() * 2 * Math.PI;
				double thisposradius = posradius * (0.9 + Math.random() * .2);

				int thisx = x + ((int) (thisposradius * Math.sin(rnang)));
				int thisz = z + ((int) (thisposradius * Math.cos(rnang)));

				double thisbuttressradius = buttress_radius * (0.618 + Math.random());
				if (thisbuttressradius < 1.0) {
					thisbuttressradius = 1.0;
				}

				this.taperedcylinder(new int[] { thisx, starty, thisz }, new int[] { x, midy, z },
						thisbuttressradius, thisbuttressradius, this.tree_WOODMAT, this.tree_WOODDATA);
				rootbases[i][0] = thisx;
				rootbases[i][1] = thisz;
				rootbases[i][2] = thisbuttressradius;
			}

		} else {
			startrad = trunkradius;
			rootbases = new double[][] { { x, z, startrad } };
		}

		this.taperedcylinder(new int[] { x, starty, z }, new int[] { x, midy, z }, startrad, midrad, this.tree_WOODMAT,
				this.tree_WOODDATA);

		this.taperedcylinder(new int[] { x, midy, z }, new int[] { x, topy, z }, midrad, endrad, this.tree_WOODMAT, this.tree_WOODDATA);

		this.makebranches();

		if (this.tree_ROOTS == TreeRoots.YES || this.tree_ROOTS == TreeRoots.TO_STONE || this.tree_ROOTS == TreeRoots.HANGING) {
			this.makeroots(rootbases);
		}

		if (trunkradius > 2 && this.tree_HOLLOWTRUNK) {
			double wall_thickness = (1 + trunkradius * 0.1 * Math.random());
			if (wall_thickness < 1.3) {
				wall_thickness = 1.3;
			}
			double base_radius = trunkradius - wall_thickness;
			if (base_radius < 1)
				base_radius = 1.0;

			double mid_radius = midrad - wall_thickness;
			double top_radius = endrad - wall_thickness;

			int base_offset = (int) (wall_thickness);
			int[] x_choices = Forester.range(x - base_offset, x + base_offset + 1, 1);
			int start_x = Forester.choice(RANDOM, x_choices);

			int[] z_choices = Forester.range(z - base_offset, z + base_offset + 1, 1);
			int start_z = Forester.choice(RANDOM, z_choices);

			this.taperedcylinder(new int[] { start_x, starty, start_z }, 
					new int[] { x, midy, z }, base_radius,
					mid_radius, this.tree_TRUNKFILLMAT, this.tree_TRUNKFILLDATA);
			int hollow_top_y = (int) (topy + trunkradius + 1.5);
			this.taperedcylinder(new int[] { x, midy, z }, new int[] { x, hollow_top_y, z }, mid_radius, top_radius,
					this.tree_TRUNKFILLMAT, this.tree_TRUNKFILLDATA);
		}
	}
}
