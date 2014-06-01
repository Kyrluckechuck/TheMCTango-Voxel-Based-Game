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
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.kyry.voxel.geometry.AABB;
import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.utilites.Constants;
import com.kyry.voxel.world.World;
import com.kyry.voxel.world.WorldManager;
import com.kyry.voxel.world.WorldRender;
import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.physics.CollisionLibrary;
//import com.kyry.voxel.world.physics.PhysicsWorld;
import com.kyry.voxel.world.tiles.Tile;
import com.nishu.utils.ShaderProgram;

import org.lwjgl.util.vector.Vector3f;

public class Chunk implements Serializable {

	public Vector3f pos;
	// public short[][][] chunks;
	// public short[][][] blocks;
	public ShaderProgram shader;

	public int vcID, sizeX, sizeY, sizeZ, type;
	public boolean isActive;

	public Random rand;

	public Chunk(ShaderProgram shader, int type, float x, float y, float z) {
		this(shader, type, new Vector3f(x, y, z));
	}

	public Chunk(ShaderProgram shader, int type, Vector3f pos) {
		this.pos = pos;
		this.shader = shader;
		this.type = type;

		initGL();
		init();
	}

	public void initGL() {
		rand = new Random(); // initialize random number generator

		sizeX = (int) pos.getX() + Constants.CHUNKSIZE;// TBH, idk..
		sizeY = (int) pos.getY() + Constants.CHUNKSIZE;
		sizeZ = (int) pos.getZ() + Constants.CHUNKSIZE;

		vcID = glGenLists(1); // Generate blank list for vcID

		// blocks = new short[sizeX][sizeY][sizeZ];
		// loadChunk(x,y,z);

		loadChunk((int) pos.getX(), (int) pos.getY(), (int) pos.getZ());
		rebuild();
	}

	public void init() {
	}

	void createChunk(int chunkX, int chunkY, int chunkZ) {
		int sizeAll = Constants.CHUNKSIZE;
		short[][][] blocks = new short[sizeAll][sizeAll][sizeAll];
		int internX = (int) Player.camera.getX() - chunkX * Constants.CHUNKSIZE;
		int internY = (int) Player.camera.getY() - chunkX * Constants.CHUNKSIZE;
		int internZ = (int) Player.camera.getZ() - chunkX * Constants.CHUNKSIZE;
		/*
		 * if (type == World.AIRCHUNK) { for (int x = 0; x < sizeAll; x++) { for
		 * (int y = 0; y < sizeAll; y++) { for (int z = 0; z < sizeAll; z++) {
		 * blocks[x][y][z] = Tile.Air.getId(); } } } } if (type ==
		 * World.MIXEDCHUNK) {
		 */
		for (int x = 0; x < sizeAll; x++) {
			for (int y = 0; y < sizeAll; y++) {
				for (int z = 0; z < sizeAll; z++) {
					blocks[x][y][z] = Tile.Grass.getId();
					if (y == 14) {
						// blocks[x][y][z] = Tile.Sand.getId();
					} else if ((x == 0) && (z == 0)) {
						blocks[x][y][z] = Tile.Air.getId();
					} else if (y == 0) {
						blocks[x][y][z] = Tile.Brick.getId();
					} else if (y > 14) {
						blocks[x][y][z] = Tile.Air.getId();
					} else if (blocks[x][y][z] == Tile.CrackedStone.getId() && (rand.nextInt(7) == 0)) {
						blocks[x][y][z] = Tile.CrackedStone.getId();
					} else if (rand.nextInt(2) == 0) {
						if (rand.nextBoolean())
							blocks[x][y][z] = Tile.Air.getId();
					} else
						blocks[x][y][z] = Tile.CrackedStone.getId();

					/*
					 * if (rand.nextInt(5) == 0) if (rand.nextBoolean())
					 * blocks[x][y][z] = Tile.CrackedStone.getId(); if
					 * (rand.nextInt(9) == 0) if (rand.nextBoolean())
					 * blocks[x][y][z] = Tile.Brick.getId(); else
					 * blocks[x][y][z] = Tile.Glass.getId();
					 */
					try {
						if (blocks[x][y][z] != Tile.Air.getId()) {
							// PhysicsWorld.newBlock(x, y, z);
							// CollisionLibrary.newBlock(x,y,z);
							CollisionLibrary.newBlock(chunkX, chunkY, chunkZ, x, y, z);
							/*
							 * Vector3f somePosition = new Vector3f(x,y,z);
							 * CollisionLibrary
							 * .BlockList.get(Constants.BlocksLoaded
							 * ).update(somePosition);
							 */
						}
					} catch (NullPointerException e) {
						System.out.println("Block error in chunk (" + chunkX + "," + chunkY + "," + chunkZ + ")" + " at X: " + x + " Y: " + y + " Z: " + z);
					}
				}
			}
			// }
		}
		// Store data to (chunkX)(chunkY)(chunkZ).dat;
		/*
		 * "("+x+")"+"("+y+")"+"("+z+")"+"=" + blocks[x][y][z].getId()"Like so:
		 * (0)(0)(0)=0; (0)(0)(1)=6; ... (15)(15)(15)=3; .. (x)(y)(z)=blockId;
		 */
		saveChunk(chunkX, chunkY, chunkZ, blocks);
	}

	private void saveChunk(int chunkX, int chunkY, int chunkZ, short[][][] blocks) {
		try {
			File dir = new File("E:\\Save\\(" + chunkX + ")" + "(" + chunkY + ")" + "(" + chunkZ + ").dat");
			dir.getParentFile().mkdir();
			FileOutputStream saveFile = new FileOutputStream("E:\\Save\\(" + chunkX + ")" + "(" + chunkY + ")" + "(" + chunkZ + ").dat");
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			save.writeObject(blocks);
			save.flush();
			save.close();
			System.out.println("(" + chunkX + "," + chunkY + "," + chunkZ + ") Saved Successfully.");

		} catch (IOException e) {
			System.out.println("Failed to save  (" + chunkX + "," + chunkY + "," + chunkZ + ")");
			e.printStackTrace();
		}

	}

	public short[][][] loadChunk(int chunkX, int chunkY, int chunkZ) {
		try {
			FileInputStream saveFile = new FileInputStream("E:\\Save\\(" + chunkX + ")" + "(" + chunkY + ")" + "(" + chunkZ + ").dat");
			ObjectInputStream restore = new ObjectInputStream(saveFile);
			short[][][] chunk = (short[][][]) restore.readObject();
			restore.close();
			System.out.println("(" + chunkX + "," + chunkY + "," + chunkZ + ") Loaded Successfully.");
			return chunk;
		} catch (IOException | ClassNotFoundException e) { // Take a second try
															// through, creating
															// the chunk
															// forcefully.
			createChunk(chunkX, chunkY, chunkZ);
			return (loadChunk(chunkX,chunkY,chunkZ));

		}
	}

	public void update() {
	}

	public void render() {
		if (type != World.AIRCHUNK) {
			// shader.use();
			// int texLoc = GL20.glGetUniformLocation(shader.getProgram(),
			// "u_texture");
			// GL20.glUniform1i(texLoc, 0);
			glCallList(vcID);

			// shader.release();
			// System.out.println("MixedChunk");
		}
		if (type != World.MIXEDCHUNK) {
			System.out.println("AirChunk");
		}
	}

	public void rebuild() {
		if (type != World.AIRCHUNK) {
			glNewList(vcID, GL_COMPILE);
			glBegin(GL_QUADS);
			int sizeAll = Constants.CHUNKSIZE;
			int loadChunkX = (int) (Player.camera.getX() / Constants.CHUNKSIZE);
			int loadChunkY = (int) (Player.camera.getY() / Constants.CHUNKSIZE);
			int loadChunkZ = (int) (Player.camera.getZ() / Constants.CHUNKSIZE);

			for (int chunksLoaded = 0; chunksLoaded < 4; chunksLoaded++) {
				short[][][] blocks = loadChunk(loadChunkX + chunksLoaded, loadChunkY, loadChunkZ);

				try {
					for (int x = 0; x < sizeAll; x++) {
						for (int y = 0; y < sizeAll; y++) {
							for (int z = 0; z < sizeAll; z++) {
								if (blocks[x][y][z] != 0) {
									// && !checkTileNotInView(loadChunkX, loadChunkY, loadChunkZ, x, y, z)
									// ^^^Commented out stuff is broken? :O (AKA Needs to be updated to chunks)
									Shape.createCube(x + loadChunkX * sizeAll, y + loadChunkY * sizeAll, z + loadChunkZ * sizeAll, Tile.getTile(blocks[x][y][z]).getColor(), Tile.getTile(blocks[x][y][z]).getTexCoords(), 1);
								}
							}
						}
					}

				} catch (NullPointerException e) {
					e.printStackTrace();
					System.out.println("Block [0][0][0] is: " + blocks[0][0][0]);
				}
				// WorldRender.render();
				// float offset = 15;
				glEnd();
				glEndList();
			}
		}
	}

	private boolean checkTileNotInView(int chunkX, int chunkY, int chunkZ, int x, int y, int z) {

		short[][][] blocks = loadChunk(chunkX, chunkY, chunkZ);

		boolean facesHidden[] = new boolean[6];
		if (x > pos.getX()) {
			if (blocks[x - 1][y][z] != 0)
				facesHidden[0] = true;
			else
				facesHidden[0] = false;
		} else {
			facesHidden[0] = false;
		}
		if (x < sizeX - 1) {
			if (blocks[x + 1][y][z] != 0)
				facesHidden[1] = true;
			else
				facesHidden[1] = false;
		} else {
			facesHidden[1] = false;
		}

		if (y > pos.getY()) {
			if (blocks[x][y - 1][z] != 0)
				facesHidden[2] = true;
			else
				facesHidden[2] = false;
		} else {
			facesHidden[2] = false;
		}
		if (y < sizeY - 1) {
			if (blocks[x][y + 1][z] != 0)
				facesHidden[3] = true;
			else
				facesHidden[3] = false;
		} else {
			facesHidden[3] = false;
		}

		if (z > pos.getZ()) {
			if (blocks[x][y][z - 1] != 0)
				facesHidden[4] = true;
			else
				facesHidden[4] = false;
		} else {
			facesHidden[4] = false;
		}
		if (z < sizeZ - 1) {
			if (blocks[x][y][z + 1] != 0)
				facesHidden[5] = true;
			else
				facesHidden[5] = false;
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

	public short getBlockID(int x, int y, int z) {

		int loadChunkX = (int) Player.camera.getX() / Constants.CHUNKSIZE;
		int loadChunkY = (int) Player.camera.getY() / Constants.CHUNKSIZE;
		int loadChunkZ = (int) Player.camera.getZ() / Constants.CHUNKSIZE;

		short[][][] blocks = loadChunk(loadChunkX, loadChunkY, loadChunkZ);

		if (x < pos.getX() || x > pos.getX() + Constants.CHUNKSIZE || y < pos.getY() || y > pos.getY() + Constants.CHUNKSIZE || z < pos.getZ() || z > pos.getZ() + Constants.CHUNKSIZE)
			return 1;
		return blocks[x][y][z];
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Vector3f getCenter() {
		return new Vector3f(pos.getX() - (Constants.CHUNKSIZE / 2), pos.getY() - (Constants.CHUNKSIZE / 2), pos.getZ() - (Constants.CHUNKSIZE / 2));
	}

	public Vector3f getPos() {
		return pos;
	}

	public int getType() {
		return type;
	}
}
