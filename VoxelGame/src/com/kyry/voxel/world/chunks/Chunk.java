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

	public int vcID, sizeX, sizeY, sizeZ, worldX, worldY, worldZ, internX,
			internY, internZ, type;
	public boolean isActive;

	public Random rand;
	private short[][][] tiles;

	public Chunk(ShaderProgram shader, int type, float x, float y, float z) {
		this(shader, type, new Vector3f(x, y, z), ChunkManager.loadChunk(x, y, z).tiles);
	}

	public Chunk(ShaderProgram shader, int type, Vector3f pos, short[][][] loadedTiles) {
		this.pos = pos;
		this.shader = shader;
		this.type = type;
		this.tiles = loadedTiles;//loads tiles
		initGL();
		init();
	}
	
	public void set(){
		
	}

	public void initGL() {
		rand = new Random(); // initialize random number generator

		sizeX = Constants.CHUNKSIZE;// TBH, idk -> LOL I do
		sizeY = Constants.CHUNKSIZE;
		sizeZ = Constants.CHUNKSIZE;

		// internX = (int) ( Player.camera.getX() - pos.getX() *
		// Constants.CHUNKSIZE); //Internal chunk coords
		// internY = (int) ( Player.camera.getY() - pos.getY() *
		// Constants.CHUNKSIZE);
		// internZ = (int) ( Player.camera.getZ() - pos.getZ() *
		// Constants.CHUNKSIZE);

		worldX = (int) pos.getX() * Constants.CHUNKSIZE; // World chunk coords
		worldY = (int) pos.getY() * Constants.CHUNKSIZE;
		worldZ = (int) pos.getZ() * Constants.CHUNKSIZE;

		vcID = glGenLists(1); // Generate blank list for vcID

		// blocks = new short[sizeX][sizeY][sizeZ];
		// loadChunk(x,y,z);

		// loadChunk((int) pos.getX(), (int) pos.getY(), (int) pos.getZ());
		// loadChunk(0,0,0);

	}

	public void init() {
		rebuild();
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
			/*Vector3f loadChunkVector = ChunkManager.blockToChunk(
					Player.camera.getX(), Player.camera.getY(),
					Player.camera.getZ());*/
			
			for (int x = 0; x < sizeX; x++) {
				for (int y = 0; y < sizeY; y++) {
					for (int z = 0; z < sizeZ; z++) {
						if (tiles[x][y][z] != 0 ) {//&& !checkTileNotInView(x, y, z)
							Shape.createCube(
									(int) worldX + x,
									(int) worldY + y,
									(int) worldZ + z,
									Tile.getTile(tiles[x][y][z]).getColor(),
									Tile.getTile(tiles[x][y][z]).getTexCoords(),
									1);
						}
					}
				}
			}
			/*for (int chunksLoadedX = -1; chunksLoadedX <= 1; chunksLoadedX++) {
				for (int chunksLoadedY = -1; chunksLoadedY <= 1; chunksLoadedY++) {
					for (int chunksLoadedZ = -1; chunksLoadedZ <= 1; chunksLoadedZ++) {
						short[][][] blocks = ChunkManager.loadChunk(
								loadChunkVector.x + chunksLoadedX,
								loadChunkVector.y + chunksLoadedY,
								loadChunkVector.z + chunksLoadedZ);
						Shape.createCube(x + loadChunkVector.x * sizeAll, y
								+ loadChunkVector.y * sizeAll, z
								+ loadChunkVector.z * sizeAll,
								Tile.getTile(blocks[x][y][z]).getColor(), Tile
										.getTile(blocks[x][y][z])
										.getTexCoords(), 1);

						// WorldRender.render();
						// float offset = 15;

					}
				}
			}*/
			glEnd();
			glEndList();
		}
	}
	private boolean checkTileNotInView(int x, int y, int z) {
		boolean facesHidden[] = new boolean[6];
		if (x > pos.getX()) {
			if (tiles[x - 1][y][z] != 0)
				facesHidden[0] = true;
			else
				facesHidden[0] = false;
		} else {
			facesHidden[0] = false;
		}
		if (x < (sizeX - 1)) {
			if (tiles[x + 1][y][z] != 0)
				facesHidden[1] = true;
			else
				facesHidden[1] = false;
		} else {
			facesHidden[1] = false;
		}

		if (y > (pos.getY())) {
			if (tiles[x][y - 1][z] != 0)
				facesHidden[2] = true;
			else
				facesHidden[2] = false;
		} else {
			facesHidden[2] = false;
		}
		if (y < (sizeY - 1)) {
			if (tiles[x][y + 1][z] != 0)
				facesHidden[3] = true;
			else
				facesHidden[3] = false;
		} else {
			facesHidden[3] = false;
		}

		if (z > pos.getZ()) {
			if (tiles[x][y][z - 1] != 0)
				facesHidden[4] = true;
			else
				facesHidden[4] = false;
		} else {
			facesHidden[4] = false;
		}
		if (z < (sizeZ - 1)) {
			if (tiles[x][y][z + 1] != 0)
				facesHidden[5] = true;
			else
				facesHidden[5] = false;
		} else {
			facesHidden[5] = false;
		}
		return facesHidden[0] && facesHidden[1] && facesHidden[2]
				&& facesHidden[3] && facesHidden[4] && facesHidden[5];
	}
	public void dispose() {
		shader.dispose();
		glDeleteLists(vcID, 1);
	}

	public boolean isActive() {
		return isActive;
	}

	/*public short getBlockID(int x, int y, int z) {

		int loadChunkX = (int) (Player.camera.getX() / Constants.CHUNKSIZE);
		int loadChunkY = (int) (Player.camera.getY() / Constants.CHUNKSIZE);
		int loadChunkZ = (int) (Player.camera.getZ() / Constants.CHUNKSIZE);

		if (x < pos.getX() || x > pos.getX() + Constants.CHUNKSIZE
				|| y < pos.getY() || y > pos.getY() + Constants.CHUNKSIZE
				|| z < pos.getZ() || z > pos.getZ() + Constants.CHUNKSIZE)
			return 1;
		return blocks[x][y][z];
	}*/

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Vector3f getCenter() {
		return new Vector3f(pos.getX() - (Constants.CHUNKSIZE / 2), pos.getY()
				- (Constants.CHUNKSIZE / 2), pos.getZ()
				- (Constants.CHUNKSIZE / 2));
	}

	public Vector3f getPos() {
		return pos;
	}

	public int getType() {
		return type;
	}
}
