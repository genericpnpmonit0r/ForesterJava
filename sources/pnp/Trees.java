package pnp;

import java.util.Random;

import net.minecraft.src.Block;

public class Trees {
	public enum Shape { normal, bamboo, palm, stickly, round, cone, procedural, rainforest, mangrove; }
	enum Roots { yes, tostone, hanging, no; }

//  commented out globals from original, unlikely to be used in the final implementation
//  public static final String LOADNAME = "LevelSave";
//	public static final int TREECOUNT = 12;
//  public static final int X = 0;
//  public static final int Z = 0;
//	public static final int RADIUS = 80;
	public static Shape SHAPE = Shape.procedural;
	public static  int CENTERHEIGHT = 55;
	public static  int EDGEHEIGHT = 9;
	public static  int HEIGHTVARIATION = 12;
	public static  boolean WOOD = true;
	public static  double TRUNKTHICKNESS = 1.0D;
	public static  double TRUNKHEIGHT = 0.7D;
	public static  boolean BROKENTRUNK = false;
	public static  boolean HOLLOWTRUNK = false;
	public static  double BRANCHDENSITY = 1.0D;
	public static Roots ROOTS = Roots.tostone;
	public static  boolean ROOTBUTTRESSES = false;
	public static  boolean FOLIAGE = true;
	public static  double FOLIAGEDENSITY = 1.0D;
	public static  boolean MAPHEIGHTLIMIT = false;
	public static int LIGHTTREE = 0; //0 thru 4
//	public static final boolean ONLYINFORESTS = false;
	
	public static  int WOODMAT = 17;
	public static  int WOODDATA = 0; //metadata
	public static  int LEAFMAT = 18;
	public static  int LEAFDATA = 0; //metadata
	public static  int LIGHTMAT = 50; //light id
	public static  int LIGHTDATA = 0; //light metadata
	//hollow trunk fillers (air)
	public static  int TRUNKFILLMAT = 0;
	public static  int TRUNKFILLDATA = 0;
	
	public static  int[] PLANTON = {2}; //what blocks to plant on
	public static  int[] STOPSROOTS = {1}; //what blocks to inhibit roots on
	public static  int[] STOPSBRANCHES = {1,4,20}; //what blocks to inhibit branches on
	public static  boolean HEIGHTCHECK = false;
	
//  public static final String INTERPOLATION = "linear";
//  public static final boolean LIGHTINGFIX = true;
//  public static final int MAXTRIES = 1000;
	
	//init random number generator
	public static final Random RANDOM = new Random();

	/* info:
	 * see porting/mceditforester/Forester.py for the original code
	 * and porting/BasicTree.java for the bare-bones MC implementation that doesn't have all the things
	 */
	
	//just an interface to allow more portability of code
	//implement this in whatever world class you have
	public static interface WorldAccessor {
		int getBlockId(int x, int y, int z);
		boolean setBlock(int x, int y, int z, int id);
		boolean setBlockAndMetadataWithNotify(int x, int y, int z, int id, int metadata);
	}
	
	public static void generateTree(int x, int y, int z, int height, WorldAccessor mcmap, Shape shape) {
		Tree t = null;
		switch (shape) {
		case bamboo:
			t = new BambooTree(mcmap);
			break;
		case cone:
			t = new ConeTree(mcmap);
			break;
		case mangrove:
			t = new MangroveTree(mcmap);
			break;
		case normal:
			t = new NormalTree(mcmap);
			break;
		case palm:
			t = new PalmTree(mcmap);
			break;
		case rainforest:
			t = new RainforestTree(mcmap);
			break;
		case round:
			t = new RoundTree(mcmap);
			break;
		default:
			System.err.printf("case not implemented %s", shape);
			return;
		}
		t.height = height;
		t.pos[0] = x;
		t.pos[1] = y;
		t.pos[2] = z;
		t.prepare();
		t.maketrunk();
		t.makefoliage();
	}
	
	public static void assign_value(int x, int y, int z, int id, int meta, WorldAccessor mcmap) {
		System.out.printf("assign_value: x:%d y:%d z:%d id:%d\n", x,y,z,id);
		//Thread.dumpStack();
		mcmap.setBlockAndMetadataWithNotify(x, y, z, id, meta);
	}
	
	static boolean is_in_array(int[] array, int value) {
		for(int v : array) {
			if(v == value) return true;
		}
		return false;
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
				if(is_in_array(matidxlist, cock_id) && !invert) {
					break;
				} else if (!is_in_array(matidxlist, cock_id) && invert) {
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
		
		return iterations;
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
		
		void maketrunk() {}
		
		void makefoliage() {}
		
		@Override
		public String toString() {
			return "Tree{pos=("+this.pos[0]+", "+this.pos[1]+", "+this.pos[2]+"),height="+this.height+"}";
		}
	}
	
	public static abstract class StickTree extends Tree {
		
		public StickTree(WorldAccessor mcmap) {
			this.mcmap = mcmap;
		}
		
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
			super(mcmap);
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
			super(mcmap);
		}
		
		@Override
		void makefoliage() {
			int start = this.pos[1];
			int end = this.pos[1] + this.height + 1;
			//int[] array = new int[] {0,1};
			for (int y = start; y < end; y++) {
				for (int i = 0; i < 1; i++) { //or was it 2?
					int xoff = TreeGenUtil.choiceInt(RANDOM, -1, 1);
					int zoff = TreeGenUtil.choiceInt(RANDOM, -1, 1);
					int x = this.pos[0] + xoff;
					int z = this.pos[2] + zoff;
					assign_value(x, y, z, LEAFMAT,LEAFDATA, this.mcmap);
				}
			}
		}
	}
	
	public static class PalmTree extends StickTree {
		public PalmTree(WorldAccessor mcmap) {
			super(mcmap);
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
		
		void crossection(int[] center, double radius, int diraxis, int mat, int data) {
			int rad = (int)(radius * .618D);
			if(rad <= 0) return;
			int secidx1 = (diraxis - 1)%3;
			int secidx2 = (1 + diraxis)%3;
			int[] coord = {0,0,0};
			for (int off1 = -rad; off1 < rad+1; off1++) {
				for (int off2 = -rad; off2 < rad+1; off2++) {
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
		
		double shapefunc(int y) {
			if(Math.random() < 100./(Math.pow(this.height, 2)) && y < this.trunkheight) {
				return this.height * .12;
			}
			return Double.MIN_VALUE;
		}
		
		void foliagecluster(int x, int y, int z) {
			System.out.printf("foliagecluster %d %d %d\n", x,y,z);
			double[] level_radius = this.foliage_shape;
			for (int i = 0; i < level_radius.length; i++) {
				this.crossection(new int[] {x,y,z}, level_radius[i], 1, LEAFMAT, LEAFDATA);
				y += 1;
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
			
			int secidx1 =(primidx - 1)%3;
			int secidx2 =(1 + primidx)%3;
			
			int primsign = delta[primidx] / Math.abs(delta[primidx]);
			
			int secdelta1 = delta[secidx1];
			float secfac1 = ((float)secdelta1) / delta[primidx];
			int secdelta2 = delta[secidx2];
			float secfac2 = ((float)secdelta2) / delta[primidx];
			
			int[] coord = {0,0,0};
			int endoffset  = delta[primidx] + primsign;
			for (int primoffset = 0; endoffset < primsign; primoffset++) {
				int primloc = start[primidx] + primsign;
				int secloc1 = (int) (start[secidx1] + primoffset*secfac1);
				int secloc2 = (int) (start[secidx2] + primoffset*secfac2);
				coord[primidx] = primloc;
				coord[secidx1] = secloc1;
				coord[secidx2] = secloc2;
				int primdist = Math.abs(delta[primidx]);
				double radius = endsize + (startsize-endsize) * Math.abs(delta[primidx] - primoffset) / primdist;
				this.crossection(coord, radius, primidx, blockmat, blockdata);
			}
		}
		
		void makeroots(double rootbases[][]) {
			int[] treeposition = this.pos;
			int height = this.height;
			
			int[][] coord =  this.foliage_coords;
			for (int c = 0; c < coord.length; c++) {
				double dist = Math.sqrt(Math.pow(coord[c][0]-treeposition[0], 2) + Math.pow(coord[c][2]-treeposition[2], 2));
				int ydist = coord[c][1]-treeposition[1];
				double value = (this.branchdensity * 220 * height)/Math.pow((ydist+dist), 3);
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
					offset[i] = startcoord[i] - coord[c][i];
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
							double remaining_dist = offlength - dist; //maybe its to copy an array but i hate system.arraycopy()
							int[] bottomcord = new int[endcoord.length - 1]; //TODO: no fucking clue what ever the array[:] operator is in py
							bottomcord[1] += (int)(remaining_dist);
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
			
			int[][] coord =  this.foliage_coords;
			for (int c = 0; c < coord.length; c++) {
				double dist = Math.sqrt(Math.pow(coord[c][0]-treeposition[0], 2) + Math.pow(coord[c][2]-treeposition[2], 2));
				int ydist = coord[c][1]-treeposition[1];
				double value = (this.branchdensity * 220 * height)/Math.pow((ydist+dist), 3);
				if(value < Math.random()) {
					continue;
				}
				
				int posy = coord[c][1];
				double branchy;
				double basesize;
				double slope = this.branchslope + (0.5 - Math.random())*.16;
				if(coord[c][1] - dist*slope > topy) {
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
				this.taperedcylinder(startcoord, coord[c], startsize, endsize, WOODMAT, WOODDATA);
			}
		}
		
		@Override
		void prepare() {
			System.out.println("prepare");
			this.trunkradius = .618 * Math.sqrt(this.height * TRUNKTHICKNESS);
			int yend;
			if(this.trunkradius < 1) {
				this.trunkradius = 1;
			}
			if(BROKENTRUNK) {
				this.trunkheight = this.height * (.3 + Math.random() * .4);
				yend = (int)(pos[1] + this.trunkheight + .5);
			} else {
				this.trunkheight = this.height;
				yend = (int)(pos[1] + this.trunkheight);
			}
			this.branchdensity = BRANCHDENSITY / FOLIAGEDENSITY;
			
			int topy = pos[1]+(int)(this.trunkheight + 0.5D);
			
			int ystart = pos[1];
			int num_of_clusters_per_y = (int)(1.5 + Math.pow((FOLIAGEDENSITY * this.height / 19.), 2));
			int[][] foliage_coords = new int[num_of_clusters_per_y * this.height][3];
			if(num_of_clusters_per_y < 1) num_of_clusters_per_y = 1;
			
			if(HEIGHTCHECK) {
				if(yend > 127) yend = 127;
				if(ystart > 127) ystart = 127;
			}
			
			System.out.println(ystart);
			System.out.println(yend);
			for (int y = -1; ystart < yend; y++) { //again with the range(yend,ystart,-1) shit
				for (int i = 0; i < num_of_clusters_per_y; i++) {
					double shapefac = this.shapefunc(y-ystart);
					if(shapefac == Double.MIN_VALUE) continue;
					double r = (Math.sqrt(Math.random()) + .328) * shapefac;
					
					double theta = Math.random()*2*Math.PI;
					int x = (int)(r * Math.sin(theta) + pos[0]);
					int z = (int)(r * Math.cos(theta) + pos[2]);
					
					if(STOPSBRANCHES.length != 0) {
						double dist = (Math.sqrt(Math.pow(x-pos[0], 2) + Math.pow(z-pos[2], 2)));
						double slope = this.branchslope;
						int starty;
						if((y - dist*slope) > topy) {
							starty = topy;
						} else {
							starty = (int) (y-dist*slope);
						}
						
						int[] start = {pos[0], starty, pos[2]};
						int[] offset = {x - pos[0], y - starty, z-pos[2]};
						double offlength = Math.sqrt(Math.pow(offset[0], 2) + Math.pow(offset[1], 2) + Math.pow(offset[2], 2));
						System.out.println(offlength);
						if(offlength < 1) continue;
						
						int[] vec = {0,0,0};
						i = 0;
						while(i < 3) {
							vec[i] = (int) (offset[i] / offlength);
							++i;
						}
						
						int mat_dist = dist_to_mat(start, vec, STOPSBRANCHES, this.mcmap, false, true, (int)offlength+3);
						if(mat_dist < offlength+2) {
							System.out.println(mat_dist);
							continue;
							
						}
					}
					 //original shit:  foliage_coords += [[x,y,z]] whatever it means
					//System.out.printf("assigning foliage coords %d %d %d", (int)x,(int)y,(int)z);
					foliage_coords[i][0] = x;
					foliage_coords[i][1] = y;
					foliage_coords[i][2] = z;
				}
			}
			
			this.foliage_coords = foliage_coords;
		}
		
		@Override
		void makefoliage() {
			//i have no clue how multidimensional arrays work the source i am porting this from
			int l = foliage_coords.length;
			int i;
			for (i = 0; i < l; i++) {
				int x = foliage_coords[i][0];
				int y = foliage_coords[i][1];
				int z = foliage_coords[i][2];
				this.foliagecluster(x,y,z);
			}
			for (i = 0; i < l; i++) {
				int x = foliage_coords[i][0];
				int y = foliage_coords[i][1];
				int z = foliage_coords[i][2];
				if(LIGHTTREE == 1) {
					assign_value(x, y+1, z, LIGHTMAT, LIGHTDATA, this.mcmap);
				} else if (LIGHTTREE > 1) {
					assign_value(x+1, y, z, LIGHTMAT, LIGHTDATA, this.mcmap);
					assign_value(x-1, y, z, LIGHTMAT, LIGHTDATA, this.mcmap);
					if(LIGHTTREE == 4) {
						assign_value(x, y, z+1, LIGHTMAT, LIGHTDATA, this.mcmap);
						assign_value(x, y, z-1, LIGHTMAT, LIGHTDATA, this.mcmap);
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
				
				int[] x_choices = {0,0,0};
				
				for (i = (x - base_offset); i < (x+base_offset + 1); i++) {
					x_choices[i] = i;
				}
				int start_x = TreeGenUtil.choiceInt(RANDOM, x_choices);
				
				int[] z_choices = {0,0,0};
				for (i = (z - base_offset); i < (z+base_offset + 1); i++) {
					z_choices[i] = i;
				}
				
				int start_z = TreeGenUtil.choiceInt(RANDOM, z_choices);
				
				this.taperedcylinder(new int[] {start_x, starty, start_z}, new int[] {x, midy, z},
						base_radius, mid_radius, WOODMAT, WOODDATA);
				
				int hollow_top_y = (int)(topy + trunkradius + 1.5);
				this.taperedcylinder(new int[] {x, midy, z}, new int[] {x, hollow_top_y, z},
						mid_radius, top_radius, WOODMAT, WOODDATA);
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
		double shapefunc(int y) {
			double twigs = super.shapefunc(y);
			if(twigs != Double.MIN_VALUE) {
				return twigs;
			}
			if(y < this.height * (.282 + .1 * Math.sqrt(Math.random()))) {
				return Double.MIN_VALUE;
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
			return dist;
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
		double shapefunc(int y) {
			double twigs = super.shapefunc(y);
			if(twigs != Double.MIN_VALUE) {
				return twigs;
			}
			if(y < this.height * (.25 + .05 * Math.sqrt(Math.random()))) {
				return Double.MIN_VALUE;
			}
			double radius = (this.height - y) * 0.382;
			if(radius < 0) {
				radius = 0;
			}
			return radius;
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
		double shapefunc(int y) {
			double twigs;
			if(y < this.height * 0.8) {
				if(EDGEHEIGHT < this.height) {
					twigs = super.shapefunc(y);
					if(twigs != Double.MIN_VALUE && Math.random() < 0.07) {
						return twigs;
					} 
				}
				return Double.MIN_VALUE;
			} else {
				double width = this.height * .382;
				double topdist = (this.height - y) / (this.height * 0.2);
				double dist = width * (0.618 + topdist) * (0.618 + Math.random()) * 0.382;
				return dist;
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
		double shapefunc(int y) {
			double val = super.shapefunc(y);
			
			if(val == Double.MIN_VALUE) return val;
			
			val = val * 1.618;
			return val;
		}
		
	}
}
