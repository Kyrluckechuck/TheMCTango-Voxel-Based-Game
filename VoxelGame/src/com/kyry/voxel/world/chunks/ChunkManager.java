package com.kyry.voxel.world.chunks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.utilities.Frustum;
import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.utilities.SimplexNoise;
import com.kyry.voxel.world.blocks.Block;
import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.physics.CollisionLibrary;
import com.nishu.utils.ShaderProgram;

public class ChunkManager {

	public static HashMap<String, Boolean> chunkMap = new HashMap<String, Boolean>();
	public static HashMap<String, Chunk> activeChunks = new HashMap<String, Chunk>();
	public static HashMap<String, Chunk> loadedChunks = new HashMap<String, Chunk>();
//	public static Random rand = rand = new Random();
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

	// SAVE / LOAD
	public static void saveChunk(float f, float h, byte[][][] blocks) { // Save
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

	/* Add chunk from file to memory chunk list */
	public static Chunk loadChunkToMem(int x, int z) {
//		if (isCreated(x, z)) {
			try {
				FileInputStream saveFile = new FileInputStream(filePath(x, z));
				ObjectInputStream restore = new ObjectInputStream(saveFile);
				Chunk chunk = new Chunk(shader, new Vector2f(x, z), (byte[][][]) restore.readObject());
				// Chunk chunk = new Chunk(shader, World.MIXEDCHUNK, new
				// Vector2f(x, z), (short[][][]) restore.readObject());
				restore.close();
				loadedChunks.put(key(x, z), chunk);
				Globals.chunksLoaded++;
				System.out.println("(" + x + "," + z + ") Loaded Successfully.");

				return chunk;
			} catch (Exception e) {
				// Take a second try through, creating the chunk forcefully.
				createChunk(x, z);
				return (loadChunkToMem(x, z));
			}
//		} else {
//			createChunk(x, z);
//			return (loadChunkToMem(x, z));
//		}
	}

	public static void loadChunkToPhys(int x, int z) { // Add chunk from file to
		// memory chunk list
		String key = key(x, z);

		ArrayList<String> temp = activeChunks.get(key).getRenderedBlocks();
		int i, o, u;
		for (int q = 0; q < temp.size(); q++) {

			i = ChunkManager.keyX(temp.get(q));
			o = ChunkManager.keyY(temp.get(q));
			u = ChunkManager.keyZ(temp.get(q));
			if (!CollisionLibrary.hasBlock(x, z, i, o, u)) {
				CollisionLibrary.newBlock(x, z, i, o, u);
				Globals.PhysBlocksLoaded++;
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
		// for (Map.Entry<String, Chunk> entry : activeChunks.entrySet()) {
		// System.out.println("Key = " + entry.getKey() + ", Value = " +
		// entry.getValue());
		// }
		// System.out.println(key);
		ArrayList<String> temp = activeChunks.get(key).getRenderedBlocks();
		int i, o, u;
		for (int q = 0; q < temp.size(); q++) {

			i = ChunkManager.keyX(temp.get(q));
			o = ChunkManager.keyY(temp.get(q));
			u = ChunkManager.keyZ(temp.get(q));

			CollisionLibrary.removeBlock(x, z, i, o, u);
			Globals.PhysBlocksLoaded--;
			// This basically removes ONLY the blocks that had been rendered to
			// the physics environment
		}

	}

	public static void loadChunkToActive(int chunkX, int chunkZ) { // Add chunk from
															// memory to active
															// chunk list

		for (int potentialX = chunkX - 1; potentialX <= chunkX + 1; potentialX++) {
			for (int potentialZ = chunkZ - 1; potentialZ <= chunkZ + 1; potentialZ++) {
				if (!loadedChunks.containsKey(key(potentialX, potentialZ))) {
					if (!isCreated(potentialX, potentialZ)) {
						createChunk(potentialX, potentialZ);
						loadChunkToMem(potentialX, potentialZ);

					} else {
						loadChunkToMem(potentialX, potentialZ);
					}
				}
			}
		}

		activeChunks.put(key(chunkX, chunkZ), loadedChunks.get(key(chunkX, chunkZ)));
		Globals.chunksActive++;
		activeChunks.get(key(chunkX, chunkZ)).load();
		loadChunkToPhys(chunkX, chunkZ);

	}

	public static void removeChunkFromActive(String s) { // Remove chunk
		// from active
		// chunk list
		int x = ChunkManager.keyX(s);
		int z = ChunkManager.keyY(s);

		removeChunkFromActive(x, z);

	}

	public static void removeChunkFromActive(int x, int z) { // Remove chunk
		// from active
		// chunk list
		removeChunkFromPhys(x, z);
		activeChunks.remove(key(x, z));
		Globals.chunksActive--;

	}

	@SuppressWarnings("static-access")
	public static void createChunk(int f, int h) {
		int sizeAll = Globals.CHUNKSIZE;
		int worldHeight = Globals.WORLDHEIGHT;
		byte[][][] blocks = new byte[sizeAll][worldHeight][sizeAll];
		
		
		SimplexNoise noise = new SimplexNoise();

		float freqH = (float) 32;
		// change this and see what happens!:D
		float freqP = (float) 32; // was 64
		// Absolute block coords
		for (int internX = 0; internX < sizeAll; internX++) {
			for (int internZ = 0; internZ < sizeAll; internZ++) {
				int x = (f * sizeAll) + internX;
				int z = (h * sizeAll) + internZ;
				/*
				 * int i = (int)(internX*internZ); int blockWidth = i % width;
				 * int blockLength = i / width;
				 */
				// height is given by the 2d noise (birds-eye view)
				float height = (float) noise.noise((float) (x / freqH), (float) (z / freqH));
				height *= 8;
				height += 50;
				blocks[internX][0][internZ] = Block.Adamantium.getId();

				for (int internY = 1; internY < worldHeight; internY++) {
					int y = internY;
					// probability of tile
					float tileProb = (float) noise.noise((float) x / freqP, (float) y / freqP, (float) z / freqP);
					tileProb *= 64;
					if (y < height) {
						// make tile
						// do if tileProb is less than or whatever
						if (tileProb < -32) {
							blocks[internX][internY][internZ] = Block.Air.getId();
						} else if (tileProb < 0 && tileProb >= -32) {
							blocks[internX][internY][internZ] = Block.Stone.getId();
						} else if (tileProb > 0 && tileProb <= 32) {
							blocks[internX][internY][internZ] = Block.Cobblestone.getId();
						} else if (tileProb > 32) {
							blocks[internX][internY][internZ] = Block.Grass.getId();
						}

					} else { // air tile
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
		for (int x = (int) (blockPos.getX() - Globals.WORLDRADIUS); x <= (int) (blockPos.getX() + Globals.WORLDRADIUS); x++) {
			for (int z = (int) (blockPos.getY() - Globals.WORLDRADIUS); z <= (int) (blockPos.getY() + Globals.WORLDRADIUS); z++) {
				String key = key(x, z);
				if (!activeChunks.containsKey(key)) {
					loadChunkToActive(x, z);
					// Chunk to loaded buffer
				}
			}// end for z
		}// end for x

		// iterate through queue

	}// End Update()

	public static boolean isInZone(String key) { // Vector2f key, so z-coord is
		// actually keyY()
		boolean result = false;
		int x = keyX(key);
		int z = keyY(key);
		// chunk relative position of player
		Vector2f playerPos = blockToChunk(Player.camera.getX(), Player.camera.getZ());
		if (x <= (playerPos.getX() + Globals.WORLDRADIUS) && x >= (playerPos.getX() - Globals.WORLDRADIUS)) {
			if (z <= (playerPos.getY() + Globals.WORLDRADIUS) && z >= (playerPos.getY() - Globals.WORLDRADIUS)) {
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
			if (Frustum.getFrustum().cubeFullyInFrustum(entry.getValue().getPos().getX(), 0, entry.getValue().getPos().getY(), entry.getValue().getPos().getX() + Globals.CHUNKSIZE, 0 + Globals.WORLDHEIGHT,
					entry.getValue().getPos().getY() + Globals.CHUNKSIZE)) {
				Globals.chunksFrustum++;
				entry.getValue().render();
			}
		}// end while for iterator
		/* Render selected block */
		if ((Globals.selectedBlock != null) && (Globals.blockToAdd != null) && (Globals.blockToAdd.y < Globals.WORLDHEIGHT && Globals.blockToAdd.y > 0) && (Globals.selectedBlock.y < Globals.WORLDHEIGHT && Globals.selectedBlock.y > 0)) {
			float padding = 0.001f;
			GL11.glBegin(GL11.GL_QUADS);
			Shape.createCube(Globals.selectedBlock.x - padding, Globals.selectedBlock.y - padding, Globals.selectedBlock.z - padding, Block.TransparentGray.getColor(), Block.TransparentGray.getTexCoords(), 1 + (2 * padding));
			Shape.createCube(Globals.blockToAdd.x - padding, Globals.blockToAdd.y - padding, Globals.blockToAdd.z - padding, Block.Wireframe.getColor(), Block.Wireframe.getTexCoords(), 1 + (2 * padding));

			GL11.glEnd();
		}
	}// end render

	public static void changeBlock(Vector3f blockToAdd, byte blockType) {
		int worldX = (int) blockToAdd.getX();
		int worldY = (int) blockToAdd.getY();
		int worldZ = (int) blockToAdd.getZ();
		int chunkX = (blockToChunk1f(worldX));
		int chunkZ = (blockToChunk1f(worldZ));
		int internX = (int) worldX - chunkX * Globals.CHUNKSIZE;
		int internY = (int) worldY;
		int internZ = (int) worldZ - chunkZ * Globals.CHUNKSIZE;
		String key = key(chunkX, chunkZ);
		removeChunkFromActive(key);
		Chunk temp = loadedChunks.get(key);
		temp.blocks[internX][internY][internZ] = blockType;
		loadedChunks.remove(key);
		loadedChunks.put(key, temp);
		activeChunks.remove(key);
		loadChunkToActive(chunkX, chunkZ);
		Globals.chunkToSave.add(key);
		//Potentially Call Save Chunk Via Threading Spot
	}
}// end class
