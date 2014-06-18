package com.kyry.voxel.world.chunks;

import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;

import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.world.blocks.Block;
//import com.kyry.voxel.world.physics.PhysicsWorld;
import com.nishu.utils.ShaderProgram;

public class Chunk {

	public Vector2f pos;
	// public ShaderProgram shader;

	public int vcID, sizeX, sizeY, sizeZ, worldX, worldZ, internX, internY, internZ;
	public boolean isActive;

	public Random rand;
	public byte[][][] blocks;
	public ArrayList<String> temp = new ArrayList<String>();

	public Chunk(ShaderProgram shader, int x, int z) {
		this(shader, new Vector2f(x, z), ChunkManager.loadChunkToMem(x, z).blocks);
	}

	public Chunk(ShaderProgram shader, Vector2f grr, byte[][][] loadedTiles) {
		this.pos = new Vector2f(grr.getX(), grr.getY());
		// this.shader = shader;
		this.blocks = loadedTiles;
		initGL();
		init();
	}

	public void set() {

	}

	public void initGL() {
		/* Initialize random number generator */
		rand = new Random();

		sizeX = Globals.CHUNKSIZE;
		sizeY = Globals.WORLDHEIGHT;
		sizeZ = Globals.CHUNKSIZE;
		/* World chunk coords */
		worldX = (int) pos.getX() * Globals.CHUNKSIZE;
		worldZ = (int) pos.getY() * Globals.CHUNKSIZE;
		/* Generate blank display list */
		vcID = glGenLists(1);

	}

	public void init() {
		/* Removed and changed the loading process to speed up rendering */
		// rebuild();
	}

	public void load() {
		/*
		 * ***This method is used to help diagnose bugs, it basically just
		 * prints all contents of a HashMap*** for (Map.Entry<String, Chunk>
		 * entry : ChunkManager.loadedChunks.entrySet()) {
		 * System.out.println("Key = " + entry.getKey() + ", Value = " +
		 * entry.getValue()); }
		 */
		/* Build what needs to be rendered of the chunk */
		rebuild();
	}

	public void update() {
	}

	public void render() {
		// shader.use();
		// int texLoc = GL20.glGetUniformLocation(shader.getProgram(),
		// "u_texture");
		// GL20.glUniform1i(texLoc, 0);
		/* Render Display List */
		glCallList(vcID);

		// shader.release();
	}

	public void rebuild() {
		temp = new ArrayList<String>();
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				for (int z = 0; z < sizeZ; z++) {
					if ((y > -1) && (blocks[x][y][z] != 0 && !checkTileNotInView(x, y, z))) {
						/* In short, build a list of things needed to be rendered */
						temp.add(ChunkManager.key(x, y, z));
					}
				}
			}
		}

		System.out.println("Total blocks rendered for this chunk: " + temp.size());
		glNewList(vcID, GL_COMPILE);
		glBegin(GL_QUADS);

		for (int q = 0; q < temp.size(); q++) {

			int x = ChunkManager.keyX(temp.get(q));
			int y = ChunkManager.keyY(temp.get(q));
			int z = ChunkManager.keyZ(temp.get(q));
			/* Render all blocks within the list created previously */
			Shape.createCube((int) worldX + x, (int) y, (int) worldZ + z, Block.getTile(blocks[x][y][z]).getColor(), Block.getTile(blocks[x][y][z]).getTexCoords(), 1);
			Globals.RenderBlocksLoaded++;
		}
		glEnd();
		glEndList();

	}

	private boolean checkTileNotInView(int x, int y, int z) {
		boolean facesHidden[] = new boolean[6];
		/*
		 * for(int q = 0; q < 6; q++){ if (y==14){ facesHidden[q] = false; }
		 * else { facesHidden[q] = true; } }
		 */
		if (x > pos.getX()) {
			if ((x - 1) < 0) {
				int x1 = (int) (((int) pos.getX() * Globals.CHUNKSIZE) + (x - 1));
				int z1 = (int) (((int) pos.getY() * Globals.CHUNKSIZE) + z);
				Chunk grr = ChunkManager.loadedChunks.get(ChunkManager.key(ChunkManager.blockToChunk(x1, z1)));

				if (grr.blocks[Globals.CHUNKSIZE - 1][y][z] != 0)
					facesHidden[0] = true;
				else
					facesHidden[0] = false;
			} else {

				if (blocks[x - 1][y][z] != 0)
					facesHidden[0] = true;
				else
					facesHidden[0] = false;
			}
		} else {
			facesHidden[0] = false;
		}

		if (x < (sizeX - 1)) {
			if ((x + 1) > Globals.CHUNKSIZE) {
				int x1 = (int) (((int) pos.getX() * Globals.CHUNKSIZE) + (x + 1));
				int z1 = (int) (((int) pos.getY() * Globals.CHUNKSIZE) + z);
				Chunk grr = ChunkManager.loadedChunks.get(ChunkManager.key(ChunkManager.blockToChunk(x1, z1)));

				if (grr.blocks[0][y][z] != 0)
					facesHidden[1] = true;
				else
					facesHidden[1] = false;
			} else {
				if (blocks[x + 1][y][z] != 0)
					facesHidden[1] = true;
				else
					facesHidden[1] = false;
			}
		} else {
			facesHidden[1] = false;

		}

		if (y > 0) {
			if (blocks[x][y - 1][z] != 0)
				facesHidden[2] = true;
			else
				facesHidden[2] = false;
		} else {
			facesHidden[2] = false;
		}
		if (y < (sizeY - 1)) {
			if (blocks[x][y + 1][z] != 0)
				facesHidden[3] = true;
			else
				facesHidden[3] = false;
		} else {
			facesHidden[3] = false;
		}

		if (z > pos.getY()) {
			if ((z - 1) < 0) {
				int x1 = (int) (((int) pos.getX() * Globals.CHUNKSIZE) + x);
				int z1 = (int) (((int) pos.getY() * Globals.CHUNKSIZE) + (z - 1));
				Chunk grr = ChunkManager.loadedChunks.get(ChunkManager.key(ChunkManager.blockToChunk(x1, z1)));

				if (grr.blocks[Globals.CHUNKSIZE - 1][y][z] != 0)
					facesHidden[4] = true;
				else
					facesHidden[4] = false;
			} else {
				if (blocks[x][y][z - 1] != 0)
					facesHidden[4] = true;
				else
					facesHidden[4] = false;
			}
		} else {
			facesHidden[4] = false;

		}
		if (z < (sizeZ - 1)) {
			if ((z + 1) > Globals.CHUNKSIZE) {
				int x1 = (int) (((int) pos.getX() * Globals.CHUNKSIZE) + x);
				int z1 = (int) (((int) pos.getY() * Globals.CHUNKSIZE) + (z + 1));
				Chunk grr = ChunkManager.loadedChunks.get(ChunkManager.key(ChunkManager.blockToChunk(x1, z1)));

				// System.out.println(ChunkManager.key(ChunkManager.blockToChunk(x1,
				// z1)) + " " + grr);
				if (grr.blocks[0][y][z] != 0)
					facesHidden[5] = true;
				else
					facesHidden[5] = false;
			} else {
				if (blocks[x][y][z + 1] != 0)
					facesHidden[5] = true;
				else
					facesHidden[5] = false;
			}
		} else {
			facesHidden[5] = false;
		}
		return facesHidden[0] && facesHidden[1] && facesHidden[2] && facesHidden[3] && facesHidden[4] && facesHidden[5];
	}

	public void dispose() {
		// shader.dispose();
		glDeleteLists(vcID, 1);
	}

	public boolean isActive() {
		return isActive;
	}

	/*
	 * public short getBlockID(int x, int y, int z) {
	 * 
	 * int loadChunkX = (int) (Player.camera.getX() / Globals.CHUNKSIZE); int
	 * loadChunkY = (int) (Player.camera.getY() / Globals.CHUNKSIZE); int
	 * loadChunkZ = (int) (Player.camera.getZ() / Globals.CHUNKSIZE);
	 * 
	 * if (x < pos.getX() || x > pos.getX() + Globals.CHUNKSIZE || y <
	 * pos.getY() || y > pos.getY() + Globals.CHUNKSIZE || z < pos.getZ() || z >
	 * pos.getZ() + Globals.CHUNKSIZE) return 1; return blocks[x][y][z]; }
	 */

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Vector2f getCenter() {
		return new Vector2f(pos.getX() - (Globals.CHUNKSIZE / 2), pos.getY() - (Globals.CHUNKSIZE / 2));
	}

	public Vector2f getPos() {
		return new Vector2f(pos.getX(), pos.getY());
	}

	// public int getType() {
	// return type;
	// }
	public ArrayList<String> getRenderedBlocks() {
		return temp;
	}

	public void setChunk(Chunk c) {

	}
}
