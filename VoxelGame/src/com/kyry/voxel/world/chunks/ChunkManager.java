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

	private static ShaderProgram shader;

	/* Constructor */
	public ChunkManager() {
		/* Removed until shaders are required */
		// Shader temp = new Shader("/shaders/chunk.vert",
		// "/shaders/chunk.frag");
		// shader = new ShaderProgram(temp.getvShader(), temp.getfShader());
	}

	/* Helper methods */
	public static String key(float x, float z) {// creates a key for the chunk
		return new String((int) x + "_" + (int) z);
	}

	public static String key(float x, float y, float z) {// creates a key for
															// the chunk
		return new String((int) x + "_" + (int) y + "_" + (int) z);
	}

	/* Creates a key for the chunk */
	public static String key(Vector2f vec) {
		return new String((int) vec.getX() + "_" + (int) vec.getY());
	}

	/* Creates a key for the chunk */
	public static String key(Vector3f vec) {
		return new String((int) vec.getX() + "_" + (int) vec.getY() + "_" + (int) vec.getZ());
	}

	/* Returns just the XCoordinate (First) */
	public static int keyX(String s) {
		return Integer.parseInt(s.split("_")[0]);
	}
	/* Returns just the YCoordinate (Second) */
	public static int keyY(String s) {
		return Integer.parseInt(s.split("_")[1]);
	}
	/* Returns just the ZCoordinate (Third) */
	public static int keyZ(String s) {
		return Integer.parseInt(s.split("_")[2]);
	}
	
	public static Vector2f blockToChunk(Vector3f v) {
		return new Vector2f(blockToChunk1f(v.getX()), blockToChunk1f(v.getZ()));
	}

	public static Vector2f blockToChunk(float x, float z) {
		return new Vector2f(blockToChunk1f(x), blockToChunk1f(z));
	}

	/* Converts world block coordinate to world chunk coordinate */
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

	/* Default save path */
	public static String filePath(int x, int z) {
		return new String("C:\\Save\\" + x + "_" + z + ".dat");
	}

	/* Checks if the file has been created on disk already */
	public static boolean isCreated(int x, int z) {
		File f = new File(filePath(x, z));
		if (f.exists() && !f.isDirectory()) {
			return true;
		} else
			return false;

	}

	/* The saving of a chunk to a data file */
	public static void saveChunk(float f, float h, byte[][][] blocks) {
		int x = (int) f;
		int z = (int) h;
		try {
			/* Create filepath */
			File dir = new File(filePath(x, z));
			/* Makes the directory if non-existent */
			dir.getParentFile().mkdir();
			/* Opens output stream */
			FileOutputStream saveFile = new FileOutputStream(filePath(x, z));
			/* Alters stream for objects */
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			/*Writes only the (X, Y, Z) & blocktype to the data file */
			save.writeObject(blocks);
			/*Flush the stream */
			save.flush();
			/* Close the stream */
			save.close();
			System.out.println("(" + x + "," + z + ") Saved Successfully.");

		} catch (IOException e) {
			System.out.println("Failed to save  (" + x + "," + z + ")");
			e.printStackTrace();
		}

	}

	/* Add chunk from file to memory chunk list */
	public static Chunk loadChunkToMem(int x, int z) {
			try {
				/* Opens input stream */
				FileInputStream saveFile = new FileInputStream(filePath(x, z));
				/* Alters stream for objects */
				ObjectInputStream restore = new ObjectInputStream(saveFile);
				/* Creates a chunk with default shader, chunk (X, Z) coordinates, and the read in chunk data */
				Chunk chunk = new Chunk(shader, new Vector2f(x, z), (byte[][][]) restore.readObject());
				/* Closes stream */
				restore.close();
				/* Puts the loaded chunk into loadedChunks (memory) */
				loadedChunks.put(key(x, z), chunk);
				Globals.chunksLoaded++;
				System.out.println("(" + x + "," + z + ") Loaded Successfully.");

				return chunk;
			} catch (Exception e) {
				/* Take a second try through, creating the chunk forcefully. */
				createChunk(x, z);
				return (loadChunkToMem(x, z));
			}
	}
	/* Add blocks rendered in that chunk to the physics environment */
	public static void loadChunkToPhys(int x, int z) {
		String key = key(x, z);

		ArrayList<String> temp = activeChunks.get(key).getRenderedBlocks();
		int i, o, u;
		for (int q = 0; q < temp.size(); q++) {

			i = ChunkManager.keyX(temp.get(q));
			o = ChunkManager.keyY(temp.get(q));
			u = ChunkManager.keyZ(temp.get(q));
			/*Checks if it exists already */
			if (!CollisionLibrary.hasBlock(x, z, i, o, u)) {
				/*If not, create it */
				CollisionLibrary.newBlock(x, z, i, o, u);
				Globals.PhysBlocksLoaded++;
			}
		}
		System.out.println("Chunk (" + x + ", " + z + ") added to PhysWorld");

	}
	
	/* Remove blocks rendered in that chunk from the physics environment */
	public static void removeChunkFromPhys(int x, int z) {
		String key = key(x, z);
		try {
		ArrayList<String> temp = activeChunks.get(key).getRenderedBlocks();
		int i, o, u;
		for (int q = 0; q < temp.size(); q++) {

			i = ChunkManager.keyX(temp.get(q));
			o = ChunkManager.keyY(temp.get(q));
			u = ChunkManager.keyZ(temp.get(q));
			/* Remove the block */
			CollisionLibrary.removeBlock(x, z, i, o, u);
			Globals.PhysBlocksLoaded--;
		}
		System.out.println("Chunk (" + x + ", " + z + ") removed from PhysWorld");
		} catch (NullPointerException e){
			System.out.println("Chunk (" + x + ", " + z + ") failed to remove");
		}
	}
	/*Add chunk from memory to chunks needing to be rendered */
	public static void loadChunkToActive(int chunkX, int chunkZ) {

		for (int potentialX = chunkX - 1; potentialX <= chunkX + 1; potentialX++) {
			for (int potentialZ = chunkZ - 1; potentialZ <= chunkZ + 1; potentialZ++) {
				/* Check if chunk is already in memory */
				if (!loadedChunks.containsKey(key(potentialX, potentialZ))) {
					/* Checks if file already exists for the chunk */
					if (!isCreated(potentialX, potentialZ)) {
						/*Creates chunk, then loads it recursively */
						createChunk(potentialX, potentialZ);
						loadChunkToMem(potentialX, potentialZ);

					} else {
						/*Just load the chunk */
						loadChunkToMem(potentialX, potentialZ);
					}
				}
			}
		}
		/* Put the chunk into the render list */
		activeChunks.put(key(chunkX, chunkZ), loadedChunks.get(key(chunkX, chunkZ)));
		Globals.chunksActive++;
		/* Calculate the blocks needing to be rendered */
		activeChunks.get(key(chunkX, chunkZ)).load();
		/*Load physics from chunk */
		loadChunkToPhys(chunkX, chunkZ);

	}
	/* Remove chunk from render list */
	public static void removeChunkFromActive(String s) { 
		int x = ChunkManager.keyX(s);
		int z = ChunkManager.keyY(s);

		removeChunkFromActive(x, z);

	}

	public static void removeChunkFromActive(int x, int z) { // Remove chunk
		// from active
		// chunk list
		removeChunkFromPhys(x, z);
		activeChunks.get(key(x, z)).dispose();
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
		/* Remove the chunk from the ActiveChunks list */
		removeChunkFromActive(key);
		/* Load a temporary copy of the internal memory's chunk */
		Chunk temp = loadedChunks.get(key);
		/* Modify Block to be new type */
		temp.blocks[internX][internY][internZ] = blockType;
		Globals.PhysBlocksLoaded -= temp.temp.size();
		Globals.RenderBlocksLoaded -= temp.temp.size();
		/* Dispose of old chunk's memory data */
		loadedChunks.get(key).dispose();
		/* Remove old chunk from memory */
		loadedChunks.remove(key);
		/* Put the new chunk into memory */
		loadedChunks.put(key, temp);
		/* Load new chunk to ActiveChunks list */
		loadChunkToActive(chunkX, chunkZ);
		/*Add Chunk To Be Saved Via Another Thread */
		Globals.chunkToSave.add(key);
	}
}// end class
