package pnp;

import java.util.Random;

import net.minecraft.src.Block;

public class Trees {
	public enum Shape { normal, bamboo, palm, round, cone, rainforest, mangrove; }
	public enum Roots { yes, tostone, hanging, no; }
	
//  commented out globals from original, unlikely to be used in the final implementation
//  public static final String LOADNAME = "LevelSave";
//	public static final int TREECOUNT = 12;
//  public static final int X = 0;
//  public static final int Z = 0;
//	public static final int RADIUS = 80;
	public static Shape SHAPE = Shape.round;
	public static int CENTERHEIGHT = 55;
	public static int EDGEHEIGHT = 25;
	public static int HEIGHTVARIATION = 12;
	public static boolean WOOD = true;
	public static double TRUNKTHICKNESS = 3.0D;
	public static double TRUNKHEIGHT = 20D;
	public static boolean BROKENTRUNK = false;
	public static boolean HOLLOWTRUNK = false;
	public static double BRANCHDENSITY = 4000.0D;
	public static Roots ROOTS = Roots.no;
	public static boolean ROOTBUTTRESSES = false;
	public static boolean FOLIAGE = true;
	public static double FOLIAGEDENSITY = 100.0D;
	public static boolean MAPHEIGHTLIMIT = false;
	public static int LIGHTTREE = 4; //0 thru 4
//	public static final boolean ONLYINFORESTS = false;
	
	public static int WOODMAT = 17;
	public static int WOODDATA = 0; //metadata
	public static int LEAFMAT = 18;
	public static int LEAFDATA = 0; //metadata
	public static int LIGHTMAT = 50; //light id
	public static int LIGHTDATA = 0; //light metadata
	//hollow trunk fillers (air)
	public static int TRUNKFILLMAT = 0;
	public static int TRUNKFILLDATA = 0;
	
	public static int[] PLANTON = {2}; //what blocks to plant on
	public static int[] STOPSROOTS = {1}; //what blocks to inhibit roots on
	public static int[] STOPSBRANCHES = {}; //what blocks to inhibit branches on
	public static boolean HEIGHTCHECK = false;
	
//  public static final String INTERPOLATION = "linear";
//  public static final boolean LIGHTINGFIX = true;
//  public static final int MAXTRIES = 1000;
	
	public static final boolean VERBOSE = true;
	
	public static final int[] AXIS_CONVERT = {2, 0, 0, 1, 2, 1};
	
	//init random number generator
	public static final Random RANDOM = new Random();

	/* info:
	 * see porting/mceditforester/Forester.py for the original code
	 * and porting/BasicTree.java for the bare-bones MC implementation that doesn't have all the things
	 */
	
	//just an interface to allow more portability of code
	//implement this in whatever World class you have
	public static interface WorldAccessor {
		int getBlockId(int x, int y, int z);
		boolean setBlock(int x, int y, int z, int id);
		boolean setBlockAndMetadataWithNotify(int x, int y, int z, int id, int metadata);
	}
	
	static {
		//filter input
		if(SHAPE == null) {
			SHAPE = Shape.round;
			dbgPrint("SHAPE not set correctly, using 'round'");
		}
		
		if(CENTERHEIGHT < 1) {
			CENTERHEIGHT = 1;
		}
		
		if(EDGEHEIGHT < 1) {
			EDGEHEIGHT = 1;
		}
		
		int minheight = Math.min(CENTERHEIGHT, EDGEHEIGHT);
		if(HEIGHTVARIATION > minheight) {
			HEIGHTVARIATION = minheight;
		}
		
		if(TRUNKTHICKNESS < 0.0D) {
			TRUNKTHICKNESS = 0.0D;
		}
		
		if(TRUNKHEIGHT < 0.0D) {
			TRUNKHEIGHT = 0.0D;
		}
		
		if(ROOTS == null) {
			ROOTS = Roots.no;
			dbgPrint("ROOTS not set correctly, using 'no' and creating no roots");
		}
		
		if (FOLIAGEDENSITY < 0.0D) {
			FOLIAGEDENSITY = 0.0D;
		}
		
		if (BRANCHDENSITY < 0.0D) {
			 BRANCHDENSITY = 0.0D;
		}
		
		if(LIGHTTREE < 0 || LIGHTTREE > 4) {
			LIGHTTREE = 0;
			dbgPrint("LIGHTTREE not set correctly, using 0 for no torches");
		}
	}
	
	//argument 0 = start, argument 1 = end, (optional) argument 2 = step
	@Deprecated //do not use, unpredictable results
	static int[] range(int... range) {
		int start = range[0];
		int end = range[1];
		int step = 1;
		if(range.length == 3) {
			step = range[2];
		}
		
		int max = Math.max(start, end);
		System.out.println("max "+max);
		int min = Math.min(start, end);
		System.out.println("min "+min);
		boolean neg = false;
		
		if(step < 0) neg = true;
		if (step > 0) neg = false;
		System.out.println("neg "+neg);

		
		int c = 0;
		int i = 0;
		int temp[] = new int[max];
		if(neg) {
			for (i = max; neg ? i >= min : i <= min; i += step) {
				temp[c++] = i;
			}
		} else {
			for (i = min; neg ? i >= max : i <= max; i += step) {
				temp[c++] = i;
			}
		}
		
		System.out.println("count "+c);
		
		int result[] = new int[c];
		System.arraycopy(temp, 0, result, 0, result.length);
		temp = null;
		return result;
	}
	
	public static void assign_value(int x, int y, int z, int id, int meta, WorldAccessor mcmap) {
		mcmap.setBlockAndMetadataWithNotify(x, y, z, id, meta);
	}
	
	static boolean is_in_array(int value, int... array) {
		if(array.length == 0) return false;
		for(int v : array) {
			return v == value;
		}
		return false;
	}
	
	static void dbgPrint(String s) {
		if(VERBOSE) {
			System.err.println(s);
		}
	}
	
	static int choice(int... src) {
		return src[RANDOM.nextInt(src.length)];
	}
	
	public static int dist_to_mat(int[] cord, int[] vec, int[] matidxlist, WorldAccessor mcmap, boolean invert, boolean uselimit, int limit) {
		int[] curcord = {0,0,0};
		
		int i = 0;
		while(i < cord.length) {
			curcord[i] = (int)(i + 0.5);
			++i;
		}
		int iterations = 0;
		boolean on_map = true;
		
		while(on_map) {
			int x = curcord[0];
			int y = curcord[1];
			int z = curcord[2];
			Block cock = Block.blocksList[mcmap.getBlockId(x, y, z)];
			if(cock == null) {
				break;
			} else {
				int cock_id = cock.blockID;
				if(is_in_array(cock_id, matidxlist) && !invert) {
					break;
				} else if (!is_in_array(cock_id,matidxlist) && invert) {
					break;
				} else {
					i = 0;
					while(i < 3) {
						curcord[i] = curcord[i] + vec[i];
						i++;
					}
					iterations++;
				}
			}
			if(uselimit && iterations > limit) {
				break;
			}
		}
		//System.err.println("dist_to_mat iter count " + iterations);
		return iterations;
	}
	
	public static Trees.Tree get(WorldAccessor mcmap, Trees.Shape shape) {
		switch (shape) {
		case normal: return new NormalTree(mcmap);
		case bamboo: return new BambooTree(mcmap);
		case palm: return new PalmTree(mcmap);
		case round: return new RoundTree(mcmap);
		case cone: return new ConeTree(mcmap);
		case rainforest: return new RainforestTree(mcmap);
		case mangrove: return new MangroveTree(mcmap);
			
		default: return null;
		
		}
	}
	
	public static void generateTree(int x, int y, int z, int height, WorldAccessor mcmap, Trees.Shape shape) {
		Tree t = Trees.get(mcmap, shape);
		t.height = height;
		t.pos[0] = x;
		t.pos[1] = y;
		t.pos[2] = z;
		t.prepare();
		t.maketrunk();
		t.makefoliage();
	}
	
	public static abstract class Tree {
		protected WorldAccessor mcmap;
		public int[] pos;
		public int height;
		
		public Tree() {
			this.pos = new int[] {0,0,0};
			this.height = 1;
		}
		
		void prepare() {}
		
		abstract void maketrunk();
		
		abstract void makefoliage();
		
		@Override
		public String toString() {
			return "Tree{pos=("+this.pos[0]+", "+this.pos[1]+", "+this.pos[2]+"),height="+this.height+"}";
		}
	}
	
	public static abstract class StickTree extends Tree {
		public StickTree() {}
		
		@Override
		void maketrunk() {
			int x = this.pos[0];
			int y = this.pos[1];
			int z = this.pos[2];
			int i = 0;
			while (i < this.height) {
				assign_value(x,y,z, WOODMAT, WOODDATA, this.mcmap);
				y += 1;
				++i;
			}
		}
	}
	
	public static class NormalTree extends StickTree {
		
		public NormalTree(WorldAccessor mcmap) {
			this.mcmap = mcmap;
		}
		
		@Override
		void makefoliage() {
			int topy = this.pos[1] + this.height - 1;
			int start = topy - 2;
			int end = topy + 2;
			int rad = 0;
			for (int y = start; y < end; y++) {
				if( y > start + 1) {
					rad = 1;
				} else {
					rad = 2;
				}
				int x, z;
				for (int xoff = -rad; xoff < rad+1; xoff++) { //hope this is how range() works i have no fucking clue
					for (int zoff = -rad; zoff < rad+1; zoff++) {
						if(Math.random() > 0.618D && Math.abs(xoff) == Math.abs(zoff) && Math.abs(xoff) == rad) {
							continue;
						}
						x = this.pos[0] + xoff;
						z = this.pos[2] + zoff;
						assign_value(x, y, z, LEAFMAT,LEAFDATA, this.mcmap);
					}
				}
			}
		}
	}
	
	public static class BambooTree extends StickTree {
		
		public BambooTree(WorldAccessor mcmap) {
			this.mcmap = mcmap;
		}
		
		@Override
		void makefoliage() {
			int start = this.pos[1];
			int end = this.pos[1] + this.height + 1;
			//int[] array = new int[] {0,1};
			for (int y = start; y < end; y++) {
				for (int i = 0; i < 1; i++) { //or was it 2?
					int xoff = choice(-1, 1);
					int zoff = choice(-1, 1);
					int x = this.pos[0] + xoff;
					int z = this.pos[2] + zoff;
					assign_value(x, y, z, LEAFMAT,LEAFDATA, this.mcmap);
				}
			}
		}
	}
	
	public static class PalmTree extends StickTree {
		public PalmTree(WorldAccessor mcmap) {
			this.mcmap = mcmap;
		}
		
		@Override
		void makefoliage() {
			int y = this.pos[1] + this.height;
			for (int xoff = -2; xoff < 3; xoff++) {
				for (int zoff = -2; zoff < 3; zoff++) {
					if(Math.abs(xoff) == Math.abs(zoff)) {
						int x = this.pos[0] + xoff;
						int z = this.pos[2] + zoff;
						assign_value(x, y, z, LEAFMAT,LEAFDATA, this.mcmap);
					}
				}
			}
		}
	}
	
	public static abstract class ProceduralTree extends Tree { //meant to be subclassed, not used directly
		protected double trunkheight;
		protected double[] foliage_shape;
		protected int[][] foliage_coords;
		protected double trunkradius;
		protected double branchdensity;
		protected double branchslope;

		public ProceduralTree(WorldAccessor mcmap) {
			this.mcmap = mcmap;
		}
		
		void crossection(int x, int y, int z, double radius, int diraxis, int mat, int data) {
			int rad = (int)(radius * .618D);
			//if(rad <= 0) return; //this shit will break everything
			int secidx1 = AXIS_CONVERT[diraxis];
			int secidx2 = AXIS_CONVERT[diraxis + 3];
			int[] coord = {0,0,0};
			int[] center = {x,y,z};
			for (int off1 = -rad; off1 <= rad+1; off1++) {
				for (int off2 = -rad; off2 <= rad+1; off2++) {
					double thisdist = Math.sqrt(Math.pow(Math.abs(off1)+0.5D, 2) + Math.pow(Math.abs(off2)+0.5D, 2));
					if(thisdist > radius) {
						continue;
					}
					int pri = center[diraxis];
					int sec1 = center[secidx1] + off1;
					int sec2 = center[secidx2] + off2;
					coord[diraxis] = pri;
					coord[secidx1] = sec1;
					coord[secidx2] = sec2;
					assign_value(coord[0], coord[1], coord[2], mat,data, mcmap);
				}
			}
		}
		
		Double shapefunc(int y) {
			if(Math.random() < 100./(Math.pow(this.height, 2)) && y < this.trunkheight) {
				return this.height * .12;
			}
			return null;
		}
		
		void foliagecluster(int x, int y, int z) {
			double[] level_radius = this.foliage_shape;
			for (double i : level_radius) {
				this.crossection(x, y, z, i, 1, LEAFMAT, LEAFDATA);
				y +=1;
			}
		}
		
		void taperedcylinder(int start[], int end[], double startsize, double endsize, int blockmat, int blockdata) {
			int[] delta = {0,0,0};
			int primidx = 0;
			int i = 0;
			while(i < 3) {
				delta[i] = end[i] - start[i];
				if (Math.abs(delta[i]) > Math.abs(delta[primidx])) {
	                primidx = i;
	            }
				i++;
			}
			
			if(delta[primidx] == 0) return;
			
			int secidx1 = AXIS_CONVERT[primidx];
			int secidx2 = AXIS_CONVERT[primidx + 3];
			
			int primsign = delta[primidx] / Math.abs(delta[primidx]);			
			int secdelta1 = delta[secidx1];
			double secfac1 = ((double)secdelta1) / delta[primidx];
			int secdelta2 = delta[secidx2];
			double secfac2 = ((double)secdelta2) / delta[primidx];
			
			int[] coord = {0,0,0};
			int endoffset  = delta[primidx] + primsign;
			for (int primoffset = 0; primoffset != endoffset;) {
				int primloc = (int)(start[primidx] + primoffset + 0.5);
				int secloc1 = (int)(start[secidx1] + (primoffset*secfac1) + 0.5);
				int secloc2 = (int)(start[secidx2] + (primoffset*secfac2) + 0.5);
				coord[primidx] = primloc;
				coord[secidx1] = secloc1;
				coord[secidx2] = secloc2;
				int primdist = Math.abs(delta[primidx]);
				double radius = endsize + (startsize-endsize) * Math.abs(delta[primidx] - primoffset) / primdist;
				this.crossection(coord[0],coord[1],coord[2], radius, primidx, blockmat, blockdata);
				primoffset += primsign;
			}
		}
		
		void makeroots(double rootbases[][]) {
			System.out.println("makeroots");
			int[] treeposition = this.pos;
			int height = this.height;
			
			for (int coord[] : this.foliage_coords) {
				double dist = Math.sqrt(Math.pow(coord[0]-treeposition[0], 2) + Math.pow(coord[2]-treeposition[2], 2));
				int ydist = coord[1]-treeposition[1];
				double value = (this.branchdensity * 220 * height)/(Math.pow((ydist+dist), 3));
				if(value < Math.random()) {
					continue;
				}
				
				double[] rootbase = rootbases[RANDOM.nextInt(rootbases.length)];
				int rootx = (int) rootbase[0];
				int rootz = (int) rootbase[1];
				double rootbaseradius = rootbase[2];
				
				double rndr = Math.sqrt(Math.random()) * rootbaseradius * .618;
				double rndang = Math.random() * 2 * Math.PI;
				int rndx = (int)(rndr * Math.sin(rndang) + 0.5);
				int rndz = (int)(rndr * Math.cos(rndang) + 0.5);
				int rndy = (int)(Math.random() * rootbaseradius * 0.5);
				
				int[] startcoord = {rootx+rndx, treeposition[1]+rndy, rootz+rndz};
				
				int offset[] = {0,0,0};
				int i = 0;
				while(i < 3) {
					offset[i] = startcoord[i] - coord[i];
					++i;
				}
				if(SHAPE == Shape.mangrove) {
					i = 0;
					while(i < 3) { //funny while loop moment
						offset[i] = (int) (i * 1.618 - 1.5);
						i++;
					}
				}
				
				int[] endcoord = {0,0,0};
				i = 0;
				while(i < 3) {
					endcoord[i] = startcoord[i]+offset[i];
					i++;
				}
				
				double rootstartsize = (rootbaseradius * 0.618 * Math.abs(offset[1]) / height * 0.618);
				if(rootstartsize < 1.0) {
					rootstartsize = 1.0;
				}
				
				double endsize = 1.0;
				
				if(ROOTS == Roots.tostone || ROOTS == Roots.hanging) {
					int offlength = (int)(Math.sqrt(Math.pow(offset[0], 2) + Math.pow(offset[1], 2) + Math.pow(offset[2], 2)));
					if(offlength < 1) {
						continue;
					}
					double rootmid = endsize;
					int vec[] = {0,0,0};
					i = 0;
					while(i < 3) {
						vec[i] = offset[i] / offlength;
						i++;
					}
					int[] searchindex = {0};
					if(ROOTS == Roots.tostone) {
						searchindex = STOPSROOTS;			
					} else if(ROOTS == Roots.hanging) {
						searchindex = new int[] {0};
					}
					
					int startdist = (int)(Math.random() * 6 * Math.sqrt(rootstartsize) + 2.8);
					int[] searchstart = {0,0,0};
					i = 0;
					while(i < 3) {
						searchstart[i] = startcoord[i] + startdist * vec[i];
						i++;
					}
					dist = startdist+dist_to_mat(searchstart,vec,searchindex, this.mcmap, false, true, offlength); //this will 1000% crash with an index out of bounds exception i can feel it
					if(dist < offlength) {
						rootmid += (rootstartsize - endsize)*(1 - dist / offlength);
						i = 0;
						while(i < 3) {
							endcoord[i] = startcoord[i] + (int)(vec[i] * dist);
							i++;
						}
						if(ROOTS == Roots.hanging) {
							double remaining_dist = offlength - dist;
							int[] bottomcord = new int[endcoord.length]; //no fucking clue what ever the array[:] operator is in py
							//maybe its to copy an array
							//not even sure if this is correct but at least it doesn't throw an exception
							//TODO: figure out if correct
							System.arraycopy(endcoord, 0, bottomcord, 0, bottomcord.length);
							
							bottomcord[1] += -(int)(remaining_dist);
//							System.err.println("array length "+bottomcord.length);
//							for (int j : bottomcord) {
//								System.err.println(j);
//							}
							this.taperedcylinder(endcoord, bottomcord, rootmid, endsize, WOODMAT, WOODDATA);
						}
						
					}
					this.taperedcylinder(startcoord, endcoord, rootstartsize, rootmid, WOODMAT, WOODDATA);
				} else {
					this.taperedcylinder(startcoord, endcoord, rootstartsize, endsize, WOODMAT, WOODDATA);
				}
			}
		}
		
		void makebranches() {
			int[] treeposition = this.pos;
			int height = this.height;
			int topy = treeposition[1]+(int)(this.trunkheight + 0.5);
			
			double endrad = this.trunkradius * (1 - this.trunkheight / height);
			if(endrad < 1.0) {
				endrad = 1.0;
			}
			
			for (int coord[] : this.foliage_coords) {
				double dist = Math.sqrt(Math.pow(coord[0]-treeposition[0], 2) + Math.pow(coord[2]-treeposition[2], 2));
				int ydist = coord[1]-treeposition[1];
				double value = (this.branchdensity * 220 * height)/Math.pow((ydist+dist), 3);
				if(value < Math.random()) {
					continue;
				}
				
				int posy = coord[1];
				double branchy;
				double basesize;
				double slope = this.branchslope + (0.5 - Math.random())*.16;
				if(coord[1] - dist*slope > topy) {
					double threshhold = 1 / height;
					if(Math.random() < threshhold) {
						continue;
					}
					
					branchy = topy;
					basesize = endrad;
				} else {
					branchy = posy-dist*slope;
					basesize = (endrad + (this.trunkradius-endrad) * (topy - branchy) / this.trunkheight);
				}
				double startsize = basesize * (1+ Math.random()) * .618 * Math.pow(dist/height, 0.618);
				double rndr = Math.sqrt(Math.random()) * basesize * 0.618;
				double rndang = Math.random() * 2 * Math.PI;
				int rndx = (int) (rndr * Math.sin(rndang) + 0.5);
				int rndz = (int) (rndr * Math.cos(rndang) + 0.5);
				int[] startcoord = {treeposition[0]+rndx, (int)branchy, treeposition[2]+rndz};
				
				if(startsize < 1.0) {
					startsize = 1.0;
				}
				double endsize = 1.0;
				this.taperedcylinder(startcoord, coord, startsize, endsize, WOODMAT, WOODDATA);
			}
		}
		
		@Override
		void prepare() {
			int[] treeposition = this.pos;
			this.trunkradius = .618 * Math.sqrt(this.height * TRUNKTHICKNESS);
			int yend;
			if(this.trunkradius < 1) {
				this.trunkradius = 1;
			}
			if(BROKENTRUNK) {
				this.trunkheight = this.height * (.3 + Math.random() * .4);
				yend = (int)(treeposition[1] + this.trunkheight + .5);
			} else {
				this.trunkheight = this.height;
				yend = treeposition[1] + this.height;
			}
			this.branchdensity = BRANCHDENSITY / FOLIAGEDENSITY;
			
			int topy = treeposition[1]+(int)(this.trunkheight + 0.5D);
			
			int ystart = treeposition[1];
			int num_of_clusters_per_y = (int)(1.5 + Math.pow((FOLIAGEDENSITY * this.height / 19.), 2));
			int[][] foliage_coords = new int[num_of_clusters_per_y][3];
			if(num_of_clusters_per_y < 1) { 
				num_of_clusters_per_y = 1;
			}
			
			if(HEIGHTCHECK) {
				if(yend > 127) yend = 127;
				if(ystart > 127) ystart = 127;
			}
			System.out.println("yend: "+yend);
			System.out.println("ystart: "+ystart);
			
			
			
			for (int y = yend; y >= ystart; y--) { //again with the range(yend,ystart,-1) shit
				for(int i = 0; i < num_of_clusters_per_y; i++) {
					Double shapefac = this.shapefunc(y-ystart);
					System.out.println("y: "+y);
					System.out.println("ystart: "+ ystart);
					
					if(shapefac == null) continue;
					double r = (Math.sqrt(Math.random()) + .328) * 69;//shapefac.doubleValue();
					
					double theta = Math.random()*2*Math.PI;
					int x = (int)(r * Math.sin(theta) + treeposition[0]);
					int z = (int)(r * Math.cos(theta) + treeposition[2]);
					
					if(STOPSBRANCHES.length != 0) {
						double dist = (Math.sqrt(Math.pow(x-treeposition[0], 2) + Math.pow(z-treeposition[2], 2)));
						double slope = this.branchslope;
						int starty;
						if((y - dist*slope) > topy) {
							starty = topy;
						} else {
							starty = (int) (y-dist*slope);
						}
						
						int[] start = {treeposition[0], starty, treeposition[2]};
						int[] offset = {x - treeposition[0], y - starty, z-treeposition[2]};
						double offlength = Math.sqrt(Math.pow(offset[0], 2) + Math.pow(offset[1], 2) + Math.pow(offset[2], 2));
						if(offlength < 1) continue;
						
						int[] vec = {0,0,0};
						int j = 0;
						while(j < 3) {
							vec[j] = (int) (offset[j] / offlength);
							j++;
						}
						
						int mat_dist = dist_to_mat(start, vec, STOPSBRANCHES, this.mcmap, false, true, (int)offlength+3);
						if(mat_dist < (int)offlength+2) {
							continue;
						}
					}
					 //original shit:  foliage_coords += [[x,y,z]] whatever it means
					foliage_coords[i][0] = z;
					foliage_coords[i][1] = x;
					foliage_coords[i][2] = y;
				}
			}
			
			this.foliage_coords = foliage_coords;
		}
		
		@Override
		void makefoliage() {
			//i have no clue how multidimensional arrays work the source i am porting this from
			int[][] foliage_coords = this.foliage_coords;
			for (int[] coord : foliage_coords) {
				this.foliagecluster(coord[0],coord[1],coord[2]);
			}
			
			for (int[] cord : foliage_coords) {
				if(LIGHTTREE == 1) {
					assign_value(cord[0], cord[1]+1, cord[2], LIGHTMAT, LIGHTDATA, this.mcmap);
				} else if (is_in_array(LIGHTTREE, 2,3,4)) { //jank
					assign_value(cord[0]+1, cord[1], cord[2], LIGHTMAT, LIGHTDATA, this.mcmap);
					assign_value(cord[0]-1, cord[1], cord[2], LIGHTMAT, LIGHTDATA, this.mcmap);
					if(LIGHTTREE == 4) {
						assign_value(cord[0], cord[1], cord[2]+1, LIGHTMAT, LIGHTDATA, this.mcmap);
						assign_value(cord[0], cord[1], cord[2]-1, LIGHTMAT, LIGHTDATA, this.mcmap);
					}
				}
			}
		}
		
		
		@Override
		void maketrunk() {
			int height = this.height;
			double trunkheight = this.trunkheight;
			double trunkradius = this.trunkradius;
			int[] treeposition = this.pos;
			int starty = treeposition[1];
			int midy = treeposition[1]+(int)(trunkheight*.382);
			int topy = treeposition[1]+(int)(trunkheight + 0.5);
			
			int x = treeposition[0];
			int z = treeposition[2];
			double end_size_factor = trunkheight / height;
			double midrad = trunkradius * (1 - end_size_factor * .5);
			double endrad = trunkradius * (1 - end_size_factor);
			
			if(endrad < 1.0) endrad = 1.0;
			if(midrad < endrad) midrad = endrad;
			double[][] rootbases;
			double startrad;
			int i;
			if(ROOTBUTTRESSES || SHAPE == Shape.mangrove) {
				startrad = trunkradius * .8;
				
				double buttress_radius = trunkradius * 0.382;
				double posradius = trunkradius;
				int num_of_buttresses = (int)(Math.sqrt(trunkradius) + 3.5);
				rootbases = new double[num_of_buttresses][3]; //no idea if correcyt
				//maybe just num and not multiplied by height?????
				
				if(SHAPE == Shape.mangrove) {
					posradius = posradius * 2.618;
				}
				
				
				for (i = 0; i < num_of_buttresses; i++) {
					double rnang = Math.random() * 2 * Math.PI;
					double thisposradius = posradius * (0.9 + Math.random() * .2);
					
					int thisx = x + ((int)(thisposradius * Math.sin(rnang)));
					int thisz = z + ((int)(thisposradius * Math.cos(rnang)));
					
					double thisbuttressradius = buttress_radius * (0.618 + Math.random());
					if(thisbuttressradius < 1.0) {
						thisbuttressradius = 1.0;
					}
					
					this.taperedcylinder(new int[] {thisx, starty, thisz}, new int[] {x, midy, z},
							thisbuttressradius, thisbuttressradius, WOODMAT, WOODDATA);
					rootbases[i][0] = thisx;
					rootbases[i][1] = thisz;
					rootbases[i][2] = thisbuttressradius;
				}
				
			} else {
				startrad = trunkradius;
				rootbases = new double[][] {{x,z,startrad}}; //???????
			}
			
			this.taperedcylinder(new int[] {x, starty, z}, new int[] {x, midy, z},
					startrad, midrad, WOODMAT, WOODDATA);
			
			this.taperedcylinder(new int[] {x, midy, z}, new int[] {x, topy, z},
					midrad, endrad, WOODMAT, WOODDATA);
			
			this.makebranches();
			
			if(ROOTS == Roots.yes || ROOTS == Roots.tostone || ROOTS == Roots.hanging) {
				this.makeroots(rootbases);
			}
			
			if(trunkradius > 2 && HOLLOWTRUNK) {
				double wall_thickness = (1 + trunkradius * 0.1 * Math.random());
				if(wall_thickness < 1.3) {
					wall_thickness = 1.3;
				}
				double base_radius = trunkradius - wall_thickness;
				if(base_radius < 1) base_radius = 1.0;
				
				double mid_radius = midrad - wall_thickness;
				double top_radius = endrad - wall_thickness;
				
				int base_offset = (int)(wall_thickness);
				
				//TODO fix these random choice selectors
				
//				int[] x_choices = new int[1024];
				
//				for (i = (x - base_offset); i < (x+base_offset + 1); i++) {
//					x_choices[i] = i;
//				}
				
				int start_x = choice((x-base_offset), (x+base_offset+1));
				System.out.println("startx"+start_x);
				
//				int[] z_choices = new int[1024];
//				for (i = (z - base_offset); i < (z+base_offset + 1); i++) {
//					z_choices[i] = i;
//				}

				int start_z = choice((z-base_offset), (z+base_offset+1));
				System.out.println("startz"+start_z);
				
				//end_TODO
				
				this.taperedcylinder(new int[] {start_x, starty, start_z}, new int[] {x, midy, z},
						base_radius, mid_radius, TRUNKFILLMAT, TRUNKFILLDATA);
				
				int hollow_top_y = (int)(topy + trunkradius + 1.5);
				this.taperedcylinder(new int[] {x, midy, z}, new int[] {x, hollow_top_y, z},
						mid_radius, top_radius, TRUNKFILLMAT, TRUNKFILLDATA);
			}
			
		}
		
	}
	
	public static class RoundTree extends ProceduralTree {
		public RoundTree(WorldAccessor mcmap) {
			super(mcmap);
		}
		
		@Override
		void prepare() {
			this.branchslope = 0.382;
			super.prepare();
			this.foliage_shape = new double[] {2,3,3,2.5,1.6};
			this.trunkradius = this.trunkradius * 0.8;
			this.trunkheight = TRUNKHEIGHT * this.trunkheight;
		}
		
		@Override
		Double shapefunc(int y) {
			System.out.println("shapefuck: "+y);
			Double twigs = super.shapefunc(y);
			if(twigs != null) {
				return twigs;
			}
			if(y < this.height * (.282 + .1 * Math.sqrt(Math.random()))) {
				return null;
			}
			double radius = this.height  / 2.;
			double adj = this.height / 2. - y;
			double dist;
			if(adj == 0) {
				dist = radius;
			} else if (Math.abs(adj) > radius) {
				dist = 0;
			} else {
				dist = Math.sqrt((Math.pow(radius, 2) - Math.pow(adj, 2)));
				dist = dist * .618;
			}
			return Double.valueOf(dist);
		}	
	}
	
	public static class ConeTree extends ProceduralTree {

		public ConeTree(WorldAccessor mcmap) {
			super(mcmap);
		}
		
		@Override
		void prepare() {
			this.branchslope = 0.15;
			super.prepare();
			this.foliage_shape = new double[]{3,2.6,2,1};
			this.trunkradius = this.trunkradius * 0.5;
		}
		
		@Override
		Double shapefunc(int y) {
			Double twigs = super.shapefunc(y);
			if(twigs != null) {
				return twigs;
			}
			if(y < this.height * (.25 + .05 * Math.sqrt(Math.random()))) {
				return null;
			}
			double radius = (this.height - y) * 0.382;
			if(radius < 0) {
				radius = 0;
			}
			return Double.valueOf(radius);
		}		
	}
	
	public static class RainforestTree extends ProceduralTree {

		public RainforestTree(WorldAccessor mcmap) {
			super(mcmap);
		}
		
		@Override
		void prepare() {
			this.foliage_shape = new double[] {3.4, 2.6};
			this.branchslope = 1.0;
			super.prepare();
			this.trunkradius = this.trunkradius * 0.382;
			this.trunkheight = this.trunkheight * .9;
		}
		
		@Override
		Double shapefunc(int y) {
			Double twigs;
			if(y < this.height * 0.8) {
				if(EDGEHEIGHT < this.height) {
					twigs = super.shapefunc(y);
					if(twigs != null && Math.random() < 0.07) {
						return twigs;
					} 
				}
				return null;
			} else {
				double width = this.height * .382;
				double topdist = (this.height - y) / (this.height * 0.2);
				double dist = width * (0.618 + topdist) * (0.618 + Math.random()) * 0.382;
				return Double.valueOf(dist);
			}
		}
	}
	
	public static class MangroveTree extends RoundTree {

		public MangroveTree(WorldAccessor mcmap) {
			super(mcmap);
		}
		
		@Override
		void prepare() {
			this.branchslope = 1.0;
			super.prepare();
			this.trunkradius = this.trunkradius * 0.618;
		}
		
		@Override
		Double shapefunc(int y) {
			Double val = super.shapefunc(y);
			
			if(val == null) return val;
			
			double val2 = val.doubleValue(); //ugh why did the original author have to use something that doesn't have strict typing...
			
			val2 = val2 * 1.618;
			return Double.valueOf(val2);
		}
		
	}
}
