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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.kyry.voxel.geometry.AABB;
import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.utilities.Constants;
import com.kyry.voxel.world.World;
import com.kyry.voxel.world.WorldManager;
import com.kyry.voxel.world.Skybox;
import com.kyry.voxel.world.blocks.Block;
import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.physics.CollisionLibrary;
//import com.kyry.voxel.world.physics.PhysicsWorld;
import com.nishu.utils.ShaderProgram;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Chunk implements Serializable {

	public Vector2f pos;
	// public short[][][] chunks;
	// public short[][][] blocks;
	public ShaderProgram shader;

	public int vcID, sizeX, sizeY, sizeZ, worldX, worldZ, internX, internY, internZ;//, type; // worldY,
	public boolean isActive;

	public Random rand;
	public short[][][] blocks;
	// boolean[][][] temp = new
	// boolean[Constants.CHUNKSIZE][Constants.WORLDHEIGHT][Constants.CHUNKSIZE];
	// public ArrayList<Vector3f> temp = new ArrayList<Vector3f>();
	public ArrayList<String> temp = new ArrayList<String>();

	public Chunk(ShaderProgram shader, int x, int z) {
		this(shader, new Vector2f(x, z), ChunkManager.loadChunkToMem(x, z).blocks);
	}
/*	public Chunk(ShaderProgram shader, int type, int x, int z) {
		this(shader, type, new Vector2f(x, z), ChunkManager.loadChunkToMem(x, z).blocks);
	}
*/
	public Chunk(ShaderProgram shader, Vector2f grr, short[][][] loadedTiles) {
		this.pos = new Vector2f(grr.getX(), grr.getY());
		this.shader = shader;
//		this.type = type;
		this.blocks = loadedTiles;// loads blocks
		initGL();
		init();
	}
/*	public Chunk(ShaderProgram shader, int type, Vector2f grr, short[][][] loadedTiles) {
		this.pos = new Vector3f(grr.getX(), 0, grr.getY());
		this.shader = shader;
		this.type = type;
		this.blocks = loadedTiles;// loads blocks
		initGL();
		init();
	}*/

	public void set() {

	}

	public void initGL() {
		rand = new Random(); // initialize random number generator

		sizeX = Constants.CHUNKSIZE;// TBH, idk -> LOL I do
		sizeY = Constants.WORLDHEIGHT;
		sizeZ = Constants.CHUNKSIZE;

		// internX = (int) ( Player.camera.getX() - pos.getX() *
		// Constants.CHUNKSIZE); //Internal chunk coords
		// internY = (int) ( Player.camera.getY() - pos.getY() *
		// Constants.CHUNKSIZE);
		// internZ = (int) ( Player.camera.getZ() - pos.getZ() *
		// Constants.CHUNKSIZE);

		worldX = (int) pos.getX() * Constants.CHUNKSIZE; // World chunk coords
		// worldY = 0;//(int) pos.getY() * Constants.CHUNKSIZE;
		worldZ = (int) pos.getY() * Constants.CHUNKSIZE;

		vcID = glGenLists(1); // Generate blank list for vcID

		// blocks = new short[sizeX][sizeY][sizeZ];
		// loadChunk(x,y,z);

		// loadChunk((int) pos.getX(), (int) pos.getY(), (int) pos.getZ());
		// loadChunk(0,0,0);

	}

	public void init() {
		// rebuild();
	}

	public void load() {
		/*
		 * for (Map.Entry<String, Chunk> entry :
		 * ChunkManager.loadedChunks.entrySet()) { System.out.println("Key = " +
		 * entry.getKey() + ", Value = " + entry.getValue()); }
		 */
		rebuild();
	}

	public void update() {
	}

	public void render() {
//		if (type != World.AIRCHUNK) {
			// shader.use();
			// int texLoc = GL20.glGetUniformLocation(shader.getProgram(),
			// "u_texture");
			// GL20.glUniform1i(texLoc, 0);
			glCallList(vcID);

			// shader.release();
			// System.out.println("MixedChunk");
//		}
//		if (type != World.MIXEDCHUNK) {
//			System.out.println("AirChunk");
//		}
	}

	public void rebuild() {
		temp = new ArrayList<String>();
//		if (type != World.AIRCHUNK) {
			for (int x = 0; x < sizeX; x++) {
				for (int y = 0; y < sizeY; y++) {
					for (int z = 0; z < sizeZ; z++) {
						// if ((blocks[x][y][z] = 0)) { //RIGHT HERE IS WHERE THAT CHECK AROUND AIR
						// if ((blocks[x-1][y][z] = 0)) {
						// if ((blocks[x+1][y][z] = 0)) {
						// if ((blocks[x+1][y-1][z] = 0)) {
						// etc
						if ((y>-1)&&(blocks[x][y][z] != 0 && !checkTileNotInView(x, y, z))) {
							temp.add(ChunkManager.key(x, y, z));
							// temp[x][y][z] = true;
						}
						// else{
						// temp[x][y][z] = false;
						// }
					}
				}
			}
			/*
			 * for (int x = 0; x < sizeX; x++) { for (int y = 0; y < sizeY; y++)
			 * { for (int z = 0; z < sizeZ; z++) { if ((blocks[x][y][z] != 0 &&
			 * !checkTileNotInView(x, y, z))) {
			 * temp.add(ChunkManager.key(x,y,z)); // temp[x][y][z] = true; } //
			 * else{ // temp[x][y][z] = false; // } } } }
			 */System.out.println("Total chunk coords being done: " + temp.size());
			glNewList(vcID, GL_COMPILE);
			glBegin(GL_QUADS);
			// int sizeAll = Constants.CHUNKSIZE;
			/*
			 * Vector3f loadChunkVector = ChunkManager.blockToChunk(
			 * Player.camera.getX(), Player.camera.getY(),
			 * Player.camera.getZ());
			 */

			/*
			 * for (int x = 0; x < sizeX; x++) { for (int y = 0; y < sizeY; y++)
			 * { for (int z = 0; z < sizeZ; z++) { if ((temp[x][y][z])) {
			 * Shape.createCube((int) worldX + x, (int) y, (int) worldZ + z,
			 * Block.getTile(blocks[x][y][z]).getColor(),
			 * Block.getTile(blocks[x][y][z]).getTexCoords(), 1); } } } }
			 */
			for (int q = 0; q < temp.size(); q++) {

				int x = ChunkManager.keyX(temp.get(q));
				int y = ChunkManager.keyY(temp.get(q));
				int z = ChunkManager.keyZ(temp.get(q));

				Shape.createCube((int) worldX + x, (int) y, (int) worldZ + z, Block.getTile(blocks[x][y][z]).getColor(), Block.getTile(blocks[x][y][z]).getTexCoords(), 1);
				Constants.RenderBlocksLoaded++;
			}
			glEnd();
			glEndList();

//		}
	}

	private boolean checkTileNotInView(int x, int y, int z) {
		boolean facesHidden[] = new boolean[6];
		/*
		 * for(int q = 0; q < 6; q++){ if (y==14){ facesHidden[q] = false; }
		 * else { facesHidden[q] = true; } }
		 */
		if (x > pos.getX()) {
			if ((x - 1) < 0) {
				int x1 = (int) (((int) pos.getX() * Constants.CHUNKSIZE) + (x - 1));
				int z1 = (int) (((int) pos.getY() * Constants.CHUNKSIZE) + z);
				Chunk grr = ChunkManager.loadedChunks.get(ChunkManager.key(ChunkManager.blockToChunk(x1, z1)));

				// System.out.println(ChunkManager.key(ChunkManager.blockToChunk(x1,
				// z1)) + " " + grr);
				if (grr.blocks[Constants.CHUNKSIZE-1][y][z] != 0)
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
			if ((x + 1) > Constants.CHUNKSIZE) {
				int x1 = (int) (((int) pos.getX() * Constants.CHUNKSIZE) + (x + 1));
				int z1 = (int) (((int) pos.getY() * Constants.CHUNKSIZE) + z);
				Chunk grr = ChunkManager.loadedChunks.get(ChunkManager.key(ChunkManager.blockToChunk(x1, z1)));

				// System.out.println(ChunkManager.key(ChunkManager.blockToChunk(x1,
				// z1)) + " " + grr);
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
				int x1 = (int) (((int) pos.getX() * Constants.CHUNKSIZE) + x);
				int z1 = (int) (((int) pos.getY() * Constants.CHUNKSIZE) + (z - 1));
				Chunk grr = ChunkManager.loadedChunks.get(ChunkManager.key(ChunkManager.blockToChunk(x1, z1)));

				// System.out.println(ChunkManager.key(ChunkManager.blockToChunk(x1,
				// z1)) + " " + grr);
				if (grr.blocks[Constants.CHUNKSIZE-1][y][z] != 0)
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
			if ((z + 1) > Constants.CHUNKSIZE) {
				int x1 = (int) (((int) pos.getX() * Constants.CHUNKSIZE) + x);
				int z1 = (int) (((int) pos.getY() * Constants.CHUNKSIZE) + (z + 1));
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
		shader.dispose();
		glDeleteLists(vcID, 1);
	}

	public boolean isActive() {
		return isActive;
	}

	/*
	 * public short getBlockID(int x, int y, int z) {
	 * 
	 * int loadChunkX = (int) (Player.camera.getX() / Constants.CHUNKSIZE); int
	 * loadChunkY = (int) (Player.camera.getY() / Constants.CHUNKSIZE); int
	 * loadChunkZ = (int) (Player.camera.getZ() / Constants.CHUNKSIZE);
	 * 
	 * if (x < pos.getX() || x > pos.getX() + Constants.CHUNKSIZE || y <
	 * pos.getY() || y > pos.getY() + Constants.CHUNKSIZE || z < pos.getZ() || z
	 * > pos.getZ() + Constants.CHUNKSIZE) return 1; return blocks[x][y][z]; }
	 */

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Vector2f getCenter() {
		return new Vector2f(pos.getX() - (Constants.CHUNKSIZE / 2), pos.getY() - (Constants.CHUNKSIZE / 2));
	}

	public Vector2f getPos() {
		return new Vector2f(pos.getX(), pos.getY());
	}

//	public int getType() {
//		return type;
//	}
	public ArrayList<String> getRenderedBlocks(){
		return temp;
	}
	public void setChunk(Chunk c){
		
	}
}
