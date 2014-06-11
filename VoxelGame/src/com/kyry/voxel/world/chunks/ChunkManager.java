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
import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.utilities.Constants;
import com.kyry.voxel.utilities.Frustum;
import com.kyry.voxel.utilities.SimplexNoise;
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

	// Helper methods
	public static String key(float x, float z) {// creates a key for the chunk
		return new String((int) x + "_" + (int) z);
	}

	public static String key(float x, float y, float z) {// creates a key for
															// the chunk
		return new String((int) x + "_" + (int) y + "_" + (int) z);
	}

	public static String key(Vector2f vec) {// creates a key for the chunk
		return new String((int) vec.getX() + "_" + (int) vec.getY());
	}

	public static String key(Vector3f vec) {// creates a key for the chunk
		return new String((int) vec.getX() + "_" + (int) vec.getY() + "_" + (int) vec.getZ());
	}

	public static int keyX(String s) {
		return Integer.parseInt(s.split("_")[0]);
	}

	public static int keyY(String s) {
		return Integer.parseInt(s.split("_")[1]);
	}

	public static int keyZ(String s) {
		return Integer.parseInt(s.split("_")[2]);
	}

	public static Vector2f blockToChunk(Vector3f v) {
		return new Vector2f(blockToChunk1f(v.getX()), blockToChunk1f(v.getZ()));
		// return new
		// Vector3f(blockToChunk1f(v.x),blockToChunk1f(v.y),blockToChunk1f(v.z));
	}

	public static Vector2f blockToChunk(float x, float z) {
		return new Vector2f(blockToChunk1f(x), blockToChunk1f(z));
		// return new
		// Vector2f(blockToChunk1f(x),blockToChunk1f(y),blockToChunk1f(z));
	}

	public static Vector3f blockToChunk(float x, float y, float z) {
		return new Vector3f(blockToChunk1f(x), blockToChunk1f(y), blockToChunk1f(z));
	}

	public static int blockToChunk1f(float f) {
		int i = (int) f;
		if (i < 0) {
			i = ((int) ((i + 1) / 16)) - 1;
		} else if (i >= 0) {
			i = (int) (i / 16);
		}
		return (int) i;
	}

	public static String filePath(int x, int z) {
		return new String("E:\\Save\\" + x + "_" + z + ".dat");
	}

	public static boolean isCreated(int x, int z) {
		File f = new File(filePath(x, z));
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
	private static void saveChunk(float f, float h, short[][][] blocks) { // Save
																			// chunk
																			// to
																			// data
																			// file
		int x = (int) f;
		// int y = (int) g;
		int z = (int) h;
		try {
			File dir = new File(filePath(x, z));
			dir.getParentFile().mkdir();
			FileOutputStream saveFile = new FileOutputStream(filePath(x, z));
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			save.writeObject(blocks);
			save.flush();
			save.close();
			System.out.println("(" + x + "," + z + ") Saved Successfully.");

		} catch (IOException e) {
			System.out.println("Failed to save  (" + x + "," + z + ")");
			e.printStackTrace();
		}

	}

	public static Chunk loadChunkToMem(int x, int z) { // Add chunk from file to
														// memory chunk list
		if (isCreated(x, z)) {
			try {
				FileInputStream saveFile = new FileInputStream(filePath(x, z));
				ObjectInputStream restore = new ObjectInputStream(saveFile);
				Chunk chunk = new Chunk(shader, new Vector2f(x, z), (short[][][]) restore.readObject());
				// Chunk chunk = new Chunk(shader, World.MIXEDCHUNK, new
				// Vector2f(x, z), (short[][][]) restore.readObject());
				restore.close();
				loadedChunks.put(key(x, z), chunk);
				Constants.chunksLoaded++;
				System.out.println("(" + x + "," + z + ") Loaded Successfully.");

				return chunk;
			} catch (Exception e) {
				// Take a second try through, creating the chunk forcefully.
				createChunk(x, z);
				return (loadChunkToMem(x, z));
			}
		} else {
			createChunk(x, z);
			return (loadChunkToMem(x, z));
		}
	}

	public static void loadChunkToPhys(int x, int z) { // Add chunk from file to
		// memory chunk list
		String key = key(x, z);

		ArrayList<String> temp = activeChunks.get(key).getChunk();
		int i, o, u;
		for (int q = 0; q < temp.size(); q++) {

			i = ChunkManager.keyX(temp.get(q));
			o = ChunkManager.keyY(temp.get(q));
			u = ChunkManager.keyZ(temp.get(q));
			if (!CollisionLibrary.hasBlock(x, z, i, o, u)) {
				CollisionLibrary.newBlock(x, z, i, o, u);
				Constants.PhysBlocksLoaded++;
			}
			// This basically adds ONLY the blocks that were rendered to the
			// physics environment
		}
		System.out.println("Chunk (" + x + ", " + z + ") added to PhysWorld");

	}

	public static void removeChunkFromPhys(int x, int z) { // Add chunk from
															// file to
		// memory chunk list
		String key = key(x, z);
		System.out.println("Chunk (" + x + ", " + z + ") removed from PhysWorld");
		for (Map.Entry<String, Chunk> entry : activeChunks.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		}
		System.out.println(key);
		ArrayList<String> temp = activeChunks.get(key).getChunk();
		int i, o, u;
		for (int q = 0; q < temp.size(); q++) {

			i = ChunkManager.keyX(temp.get(q));
			o = ChunkManager.keyY(temp.get(q));
			u = ChunkManager.keyZ(temp.get(q));

			CollisionLibrary.removeBlock(x, z, i, o, u);
			Constants.PhysBlocksLoaded--;
			// This basically removes ONLY the blocks that had been rendered to
			// the physics environment
		}

	}

	public static void loadChunkToActive(int x, int z) { // Add chunk from
															// memory to active
															// chunk list

		for (int q = x - 1; q <= x + 1; q++) {
			for (int w = z - 1; w <= z + 1; w++) {
				if (!loadedChunks.containsKey(key(q, w))) {
					if (!isCreated(q, w)) {
						createChunk(q, w);
						loadChunkToMem(q, w);

					} else {
						loadChunkToMem(q, w);
					}
				}
			}
		}

		activeChunks.put(key(x, z), loadedChunks.get(key(x, z)));
		Constants.chunksActive++;
		activeChunks.get(key(x, z)).load();
		loadChunkToPhys(x, z);

	}

	public void removeChunkFromActive(String s) { // Remove chunk
		// from active
		// chunk list
		int x = ChunkManager.keyX(s);
		int z = ChunkManager.keyY(s);

		removeChunkFromActive(x, z);

	}

	public void removeChunkFromActive(int x, int z) { // Remove chunk
														// from active
														// chunk list
		removeChunkFromPhys(x, z);
		activeChunks.remove(key(x, z));
		Constants.chunksActive--;

	}

	public static void createChunk(int f, int h) {
		int sizeAll = Constants.CHUNKSIZE;
		int worldHeight = Constants.WORLDHEIGHT;
		short[][][] blocks = new short[sizeAll][worldHeight][sizeAll];
		
		/*
		 * if (type == World.AIRCHUNK) { for (int x = 0; x < sizeAll; x++) { for
		 * (int y = 0; y < sizeAll; y++) { for (int z = 0; z < sizeAll; z++) {
		 * blocks[x][y][z] = Block.Air.getId(); } } } } if (type ==
		 * World.MIXEDCHUNK) {
		 */
		SimplexNoise noise = new SimplexNoise();
		
		float freqH = (float) 32; 
		// change this and see what happens!:D
		float freqP = (float) 16;
		
		// int i = -1;
		/*int[] tiles = new int[width * width];
		for (int i = 0; i < tiles.length; i++) {
			int blockWidth = i % width;
			int blockLength = i / width;
			
			float groundHeight = (float) noise.noise((float) blockWidth / frequency, (float) blockLength / frequency);
			groundHeight *= Constants.WORLDHEIGHT/2;
			groundHeight += Constants.WORLDHEIGHT/2;
			tiles[blockWidth + blockLength * width] = (int) groundHeight;
			
		}*/
		//Absolute block coords
		for (int internX = 0; internX < sizeAll; internX++) {
			for (int internZ = 0; internZ < sizeAll; internZ++) {
				int x = (f * sizeAll) + internX;
				int z = (h * sizeAll) + internZ;
				/*int i = (int)(internX*internZ);
				int blockWidth = i % width;
				int blockLength = i / width;*/
				//height is given by the 2d noise (birds-eye view)
				float height = (float) noise.noise((float)(x / freqH), (float) (z / freqH));
				height *= 8;
				height += 50;
				for (int internY = 0; internY < worldHeight; internY++) {
					int y = internY;
					//probability of tile
					float tileProb = (float) noise.noise((float)x /freqP, (float)y / freqP, (float) z / freqP);
					tileProb *= 64;
					if(y < height){
						//make tile
						//do if tileProb is less than or whatever
						if(tileProb < -32){
							blocks[internX][internY][internZ] = Block.Air.getId();
						}else if(tileProb < 0 && tileProb >= -32){
							blocks[internX][internY][internZ] = Block.CrackedStone.getId();
						}else if(tileProb > 0 && tileProb <= 32){
							blocks[internX][internY][internZ] = Block.Dirt.getId();
						}else if(tileProb > 32){
							blocks[internX][internY][internZ] = Block.Grass.getId();
						}
						
					}else{ // air tile
						blocks[internX][internY][internZ] = Block.Air.getId();
					}
				}
			}
		}
		ChunkManager.saveChunk(f, h, blocks);
		System.out.println("(" + f + "," + h + ") Created Successfully.");
	}

	//
	// GETTERS & SETTERS ***To be honest, I don't think the chunks should be
	// easily movable.. that means that every file would have to change
	// according to each move, etc.. :S

	public void update() {
		// Basically will check if chunk is in the "bufferzone" if not then
		// load, chunk, if not then delete
		// DELETE
		ArrayList<String> toRemove = new ArrayList<String>();
		Iterator<Entry<String, Chunk>> iterator = activeChunks.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Chunk> entry = iterator.next();
			String key = entry.getKey();
			if (!isInZone(key)) { // checks if the chunk is in the render-zone
				toRemove.add(key);// end chunks to be removed
			} // end if
		}// end remove while

		// Remove Unused Chunks
		for (int q = 0; q < toRemove.size(); q++) {
			removeChunkFromActive(toRemove.get(q));
		}

		// End Removal Of Unused Chunks
		// ADD
		// BLOCK RELATIVE
		Vector2f blockPos = blockToChunk(Player.camera.getPos()); // Returns
																	// player's
																	// XZ Chunk
																	// coords
		for (int x = (int) (blockPos.getX() - Constants.WORLDRADIUS); x <= (int) (blockPos.getX() + Constants.WORLDRADIUS); x++) {
			for (int z = (int) (blockPos.getY() - Constants.WORLDRADIUS); z <= (int) (blockPos.getY() + Constants.WORLDRADIUS); z++) {
				String key = key(x, z);
				if (!activeChunks.containsKey(key)) {
					loadChunkToActive(x, z);
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

	private boolean isInZone(String key) { // Vector2f key, so z-coord is
											// actually keyY()
		boolean result = false;
		int x = keyX(key);
		int z = keyY(key);
		// chunk relative position of player
		Vector2f playerPos = blockToChunk(Player.camera.getX(), Player.camera.getZ());
		if (x <= (playerPos.getX() + Constants.WORLDRADIUS) && x >= (playerPos.getX() - Constants.WORLDRADIUS)) {
			if (z <= (playerPos.getY() + Constants.WORLDRADIUS) && z >= (playerPos.getY() - Constants.WORLDRADIUS)) {
				result = true;
			}
		}
		return result;
	}

	public void render() {
		// String key = ChunkManager.key(x, y, z);
		// for (Map.Entry<String, Chunk> entry : activeChunks.entrySet()) {
		// System.out.println("Key = " + entry.getKey() + ", Value = " +
		// entry.getValue());
		// }

		Iterator<Entry<String, Chunk>> iterator = activeChunks.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Chunk> entry = iterator.next();
			if (Frustum.getFrustum().cubeFullyInFrustum(entry.getValue().getPos().getX(), 0,// entry.getValue().getPos().getY(),
					entry.getValue().getPos().getY(), entry.getValue().getPos().getX() + Constants.CHUNKSIZE, 0 + Constants.WORLDHEIGHT,// entry.getValue().getPos().getY()
					// +
					// Constants.CHUNKSIZE,
					entry.getValue().getPos().getY() + Constants.CHUNKSIZE)) {
				/*
				 * if (Math.abs(entry.getValue().getCenter().getX() - (int)
				 * Player.camera.getX()) < 64 // &&
				 * Math.abs(entry.getValue().getCenter().getZ() // -
				 * Player.camera.getZ()) < 64 &&
				 * Math.abs(entry.getValue().getCenter().getY() -
				 * Player.camera.getY()) < 32) {
				 */
				Constants.chunksFrustum++;
				entry.getValue().render();

				// } //Commented out with the 64/32 code
			}
		}// end while for iterator
	}// end render
}
