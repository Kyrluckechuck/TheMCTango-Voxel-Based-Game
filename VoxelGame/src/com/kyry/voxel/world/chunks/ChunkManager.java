package com.kyry.voxel.world.chunks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.kyry.voxel.geometry.AABB;
import com.kyry.voxel.geometry.Coordinate3f;
import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.utilities.Constants;
import com.kyry.voxel.utilities.Frustum;
import com.kyry.voxel.world.World;
import com.kyry.voxel.world.blocks.Block;
import com.kyry.voxel.world.entities.mobs.MobManager;
import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.physics.CollisionLibrary;
import com.nishu.utils.Shader;
import com.nishu.utils.ShaderProgram;

public class ChunkManager {

	public static HashMap<String, Boolean> chunkMap = new HashMap<String, Boolean>();
	public static HashMap<String, Chunk> activeChunks = new HashMap<String, Chunk>();
	public static HashMap<String, Chunk> loadedChunks = new HashMap<String, Chunk>();
	public static Random rand = rand = new Random();
	// public static MobManager mobManager;

	private static ShaderProgram shader;

	// Constructor
	public ChunkManager() {
		// mobManager = new MobManager();
		// Shader temp = new Shader("/shaders/chunk.vert",
		// "/shaders/chunk.frag");
		// shader = new ShaderProgram(temp.getvShader(), temp.getfShader());
	}

	public static String filePath(Coordinate3f coord) {
		return new String("C:\\Save\\" + coord.key() + ".dat");
	}

	public static boolean isCreated(Coordinate3f coord) {
		File f = new File(filePath(coord));
		if (f.exists() && !f.isDirectory()) {
			return true;
		} else
			return false;
		// return isCreated(key(x, y, z));

	}

	/*
	 * public static boolean isCreated(int x, int y, int z){ try {
	 * FileInputStream saveFile; saveFile = new FileInputStream(filePath(x, y,
	 * z)); ObjectInputStream restore = new ObjectInputStream(saveFile);
	 * short[][][] test = (short[][][]) restore.readObject(); restore.close();
	 * return true; } catch (IOException | ClassNotFoundException e) {
	 * //e.printStackTrace(); return false; }
	 * 
	 * 
	 * //return isCreated(key(x, y, z));
	 * 
	 * }
	 *//*
		 * public static boolean isCreated(String s){ boolean result = false;
		 * try{
		 * 
		 * result = chunkMap.get(s); } catch(Exception e){ } return result; }
		 */
	// /////////////////////////////////
	/*
	 * public void readChunk(int x, int y, int z){ //String s = "" + x + "_" + y
	 * + "_"+ z; checkChunk(x, y, z ); }
	 * 
	 * public void checkChunk(int x, int y, int z){ if (isCreated(x, y, z)){
	 * //can't make a chunk thats already made //Load the chunk somehow
	 * //short[][][] blocks = Chunk.loadChunk(x,y,z); }else if(!isCreated(x, y,
	 * z)){ //Chunk.createChunk(x,y,z);
	 * System.out.println("Should have been created"); chunkMap.put(key(x,y,z),
	 * true); //Chunk.createChunk(s); //Create the chunk, then load the chunk
	 * somehow } }
	 */
	// SAVE / LOAD
	private static void saveChunk(Coordinate3f coord, short[][][] blocks) { 
		// Save chunk to data file
	
		try {
			File dir = new File(filePath(coord));
			dir.getParentFile().mkdir();
			FileOutputStream saveFile = new FileOutputStream(filePath(coord));
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			save.writeObject(blocks);
			save.flush();
			save.close();
			System.out.println("(" + coord.key() + ") Saved Successfully.");

		} catch (IOException e) {
			System.out.println("Failed to save  (" + coord.key() + ")");
			e.printStackTrace();
		}

	}

	public static Chunk loadChunkToMem(Coordinate3f coord) { // Add chunk from file to
														// memory chunk list
		if (isCreated(coord)) {
			try {
				FileInputStream saveFile = new FileInputStream(filePath(coord));
				ObjectInputStream restore = new ObjectInputStream(saveFile);
				Chunk chunk = new Chunk(shader, coord.toChunk(), (short[][][]) restore.readObject());
//				Chunk chunk = new Chunk(shader, World.MIXEDCHUNK, new Vector2f(x, z), (short[][][]) restore.readObject());
				restore.close();
				loadedChunks.put(coord.key(), chunk);
				Constants.chunksLoaded++;
				System.out.println("(" +coord.key() + ") Loaded Successfully.");

				return chunk;
			} catch (Exception e) {
				// Take a second try through, creating the chunk forcefully.
				createChunk(coord);
				return (loadChunkToMem(coord));
			}
		} else {
			createChunk(coord);
			return (loadChunkToMem(coord));
		}
	}

	public static void loadChunkToPhys(Coordinate3f coord) { // Add chunk from file to
		// memory chunk list

		ArrayList<String> temp = activeChunks.get(coord.key()).getChunk();
		int i, o, u;
		for (int q = 0; q < temp.size(); q++) {
			Coordinate3f tempCoord = new Coordinate3f(temp.get(q));
			if (!CollisionLibrary.hasBlock(coord.x, coord.z, tempCoord.x, tempCoord.y, tempCoord.z)) {
				CollisionLibrary.newBlock(coord.x, coord.z, tempCoord.x, tempCoord.y, tempCoord.z);
				Constants.PhysBlocksLoaded++;
			}
			// This basically adds ONLY the blocks that were rendered to the
			// physics environment
		}
		System.out.println("Chunk (" + coord.key() + ") added to PhysWorld");

	}

	public static void removeChunkFromPhys(Coordinate3f coord) { // Add chunk from
															// file to
		// memory chunk list
		System.out.println("Chunk (" + coord.key() + ") removed from PhysWorld");
		for (Map.Entry<String, Chunk> entry : activeChunks.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		}
		System.out.println(coord.key());
		ArrayList<String> temp = activeChunks.get(coord.key()).getChunk();
		int i, o, u;
		for (int q = 0; q < temp.size(); q++) {
			Coordinate3f tempCoord =  new Coordinate3f(temp.get(q));
			CollisionLibrary.removeBlock((int)coord.x, (int)coord.z, (int)tempCoord.x, (int)tempCoord.y, (int)tempCoord.z);
			Constants.PhysBlocksLoaded--;
			// This basically removes ONLY the blocks that had been rendered to
			// the physics environment
		}

	}

	public static void loadChunkToActive(Coordinate3f coord) { // Add chunk from
															// memory to active
															// chunk list
		
		for (int q = (int)coord.x - 1; q <= coord.x + 1; q++) {
			for (int w = (int)coord.z - 1; w <= coord.z + 1; w++) {
				Coordinate3f tempCoord = new Coordinate3f(q, 0, w);
				if (!loadedChunks.containsKey(tempCoord.key())) {
					if (!isCreated(tempCoord)) {
						createChunk(tempCoord);
						loadChunkToMem(tempCoord);

					} else {
						loadChunkToMem(tempCoord);
					}
				}
			}
		}
		
		activeChunks.put(coord.key(), loadedChunks.get(coord.key()));
		Constants.chunksActive++;
		activeChunks.get(coord.key()).load();
		loadChunkToPhys(coord);
	}

	public void removeChunkFromActive(Coordinate3f coord) { // Remove chunk
														// from active
														// chunk list
		removeChunkFromPhys(coord);
		activeChunks.remove(coord.key());
		Constants.chunksActive--;

	}

	public static void createChunk(Coordinate3f coord) {
		int sizeAll = Constants.CHUNKSIZE;
		int worldHeight = Constants.WORLDHEIGHT;
		short[][][] blocks = new short[sizeAll][worldHeight][sizeAll];
		// int internX = (int) Player.camera.getX() - chunkX *
		// Constants.CHUNKSIZE ;
		// int internY = (int) Player.camera.getY() - chunkX *
		// Constants.CHUNKSIZE ;
		// int internZ = (int) Player.camera.getZ() - chunkX *
		// Constants.CHUNKSIZE ;
		/*
		 * if (type == World.AIRCHUNK) { for (int x = 0; x < sizeAll; x++) { for
		 * (int y = 0; y < sizeAll; y++) { for (int z = 0; z < sizeAll; z++) {
		 * blocks[x][y][z] = Block.Air.getId(); } } } } if (type ==
		 * World.MIXEDCHUNK) {
		 */
		for (int x = 0; x < sizeAll; x++) {
			for (int y = 0; y < worldHeight; y++) {
				for (int z = 0; z < sizeAll; z++) {
					blocks[x][y][z] = Block.Grass.getId();
					if ((y == 14) && (rand.nextInt(3) == 0)) {
						blocks[x][y][z] = Block.Air.getId();
					} else if ((y == 14)) {
						//nothing because default is Grass
					} else if (y == 0) {
						blocks[x][y][z] = Block.Brick.getId();

					} else if (y > 14) {
						blocks[x][y][z] = Block.Air.getId();
					} else if (x == 0 || x == Constants.CHUNKSIZE - 1 || z == 0 || z == Constants.CHUNKSIZE - 1) {
						blocks[x][y][z] = Block.Sand.getId();

					} else if (blocks[x][y][z] == Block.CrackedStone.getId() && (rand.nextInt(7) == 0)) {
						blocks[x][y][z] = Block.CrackedStone.getId();
						// } else if (rand.nextInt(2) == 0) {
						// if (rand.nextBoolean())
						// blocks[x][y][z] = Block.Air.getId();
					} else
						blocks[x][y][z] = Block.CrackedStone.getId();

					/*
					 * if (rand.nextInt(5) == 0) if (rand.nextBoolean())
					 * blocks[x][y][z] = Block.CrackedStone.getId(); if
					 * (rand.nextInt(9) == 0) if (rand.nextBoolean())
					 * blocks[x][y][z] = Block.Brick.getId(); else
					 * blocks[x][y][z] = Block.Glass.getId();
					 */

				}
			}
			// }
		}
		// Store data to (chunkX)(chunkY)(chunkZ).dat;
		/*
		 * "("+x+")"+"("+y+")"+"("+z+")"+"=" + blocks[x][y][z].getId()"Like so:
		 * (0)(0)(0)=0; (0)(0)(1)=6; ... (15)(15)(15)=3; .. (x)(y)(z)=blockId;
		 */
		ChunkManager.saveChunk(coord, blocks);
		// ///
		// Seems redundant and unneeded. The createChunk method will not always
		// be called in one instance of the game, loadChunk will be
		// Chunk chunk = new Chunk(shader, World.MIXEDCHUNK, new
		// Vector3f(f,g,h), blocks);
		// activeChunks.put(key(f,g,h), chunk);
		System.out.println("(" + coord.key() + ") Created Successfully.");
	}

	//
	// GETTERS & SETTERS ***To be honest, I don't think the chunks should be
	// easily movable.. that means that every file would have to change
	// according to each move, etc.. :S

	public void update() {
		// Basically will check if chunk is in the "bufferzone" if not then
		// load, chunk, if not then delete
		// DELETE

		/*
		 * Iterator<Entry<String, Chunk>> iterator =
		 * activeChunks.entrySet().iterator(); while (iterator.hasNext()) {
		 * Entry<String, Chunk> entry = iterator.next(); String key =
		 * entry.getKey(); if (!isInZone(key)) { // removes chunk
		 * removeChunkFromActive(key); // iterator.remove(); } }// end while for
		 * iterator
		 */
		// ADD
		// BLOCK RELATIVE
		
		//kinda complex, kinda cool, I create a new instance of coord from vector then call the method
		//toChunk() and automatically converts it from only one line
		Coordinate3f blockPos = new Coordinate3f(Player.camera.getPos()).toChunk(); //Returns player's XZ Chunk coords
		for (int x = (int) (blockPos.getX() - Constants.WORLDRADIUS); x <= (int) (blockPos.getX() + Constants.WORLDRADIUS); x++) {
			for (int z = (int) (blockPos.getZ() - Constants.WORLDRADIUS); z <= (int) (blockPos.getZ() + Constants.WORLDRADIUS); z++) {
				Coordinate3f tempCoord =  new Coordinate3f(x, 0, z);
				if (!activeChunks.containsKey(tempCoord.key())) {
					loadChunkToActive(tempCoord);
					// Chunk to loaded buffer
				}
			}// end for z
		}// end for x

	}// End Update()

	/*
	 * private void removeChunk(String key) {// remove chunk from.. current set
	 * and // Collision zone? // activeChunks.remove(key);//removes
	 * chunk//causes ERROR! // remove the collision blocks for (int x = 0; x <
	 * Constants.CHUNKSIZE; x++) { for (int y = 0; y < Constants.WORLDHEIGHT;
	 * y++) { for (int z = 0; z < Constants.CHUNKSIZE; z++) {
	 * CollisionLibrary.removeBlock(keyX(key), keyY(key), x, y, z); }// end for
	 * z }// end for y }// end for x }
	 */

	private boolean isInZone(Coordinate3f coord) { // Vector2f key, so z-coord is
											// actually keyY()
		boolean result = false;
		int x = (int)coord.x;
		int z = (int)coord.z;
		// chunk relative position of player
		Coordinate3f playerPos = new Coordinate3f(Player.camera.getPos());
		if (x <= (playerPos.getX() + Constants.WORLDRADIUS) && x >= (playerPos.getX() - Constants.WORLDRADIUS)) {
			if (z <= (playerPos.getY() + Constants.WORLDRADIUS) && z >= (playerPos.getY() - Constants.WORLDRADIUS)) {
				result = true;
			}
		}
		return result;
	}

	public void render() {
		// String key = ChunkManager.key(x, y, z);
//		 for (Map.Entry<String, Chunk> entry : activeChunks.entrySet()) {
//		 System.out.println("Key = " + entry.getKey() + ", Value = " +
//		 entry.getValue());
//		 }

		Iterator<Entry<String, Chunk>> iterator = activeChunks.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Chunk> entry = iterator.next();
			if (Frustum.getFrustum().cubeFullyInFrustum(entry.getValue().getPos().getX(), 0,// entry.getValue().getPos().getY(),
					entry.getValue().getPos().getY(), entry.getValue().getPos().getX() + Constants.CHUNKSIZE, 0+Constants.WORLDHEIGHT,// entry.getValue().getPos().getY()
																												// +
																												// Constants.CHUNKSIZE,
					entry.getValue().getPos().getY() + Constants.CHUNKSIZE)) {
/*				if (Math.abs(entry.getValue().getCenter().getX() - (int) Player.camera.getX()) < 64
				// && Math.abs(entry.getValue().getCenter().getZ()
				// - Player.camera.getZ()) < 64
						&& Math.abs(entry.getValue().getCenter().getY() - Player.camera.getY()) < 32) {*/
					Constants.chunksFrustum++;
					entry.getValue().render();

//				} //Commented out with the 64/32 code
			}
		}// end while for iterator
	}// end render
}
