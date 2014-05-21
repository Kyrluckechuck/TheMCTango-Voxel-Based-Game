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

import java.util.Random;

import org.lwjgl.opengl.GL20;

import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.utilites.Constants;
import com.kyry.voxel.world.World;
import com.kyry.voxel.world.WorldRender;
import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.physics.PhysicsWorld;
import com.kyry.voxel.world.tiles.Tile;
import com.nishu.utils.ShaderProgram;
import com.nishu.utils.Vector3f;

public class Chunk {

	public Vector3f pos;
	public short[][][] tiles;
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
		rand = new Random();

		sizeX = (int) pos.getX() + Constants.CHUNKSIZE;
		sizeY = (int) pos.getY() + Constants.CHUNKSIZE;
		sizeZ = (int) pos.getZ() + Constants.CHUNKSIZE;

		vcID = glGenLists(1);

		tiles = new short[sizeX][sizeY][sizeZ];

		createChunk();
		rebuild();
	}

	public void init() {
	}

	private void createChunk() {
		if (type == World.AIRCHUNK) {
			for (int x = (int) pos.getX(); x < sizeX; x++) {
				for (int y = (int) pos.getY(); y < sizeY; y++) {
					for (int z = (int) pos.getZ(); z < sizeZ; z++) {
						tiles[x][y][z] = Tile.Air.getId();
					}
				}
			}
		}
		if (type == World.MIXEDCHUNK) {
			for (int x = (int) pos.getX(); x < sizeX; x++) {
				for (int y = (int) pos.getY(); y < sizeY; y++) {
					for (int z = (int) pos.getZ(); z < sizeZ; z++) {
						tiles[x][y][z] = Tile.Grass.getId();
						if (y == 14) {
							tiles[x][y][z] = Tile.Sand.getId();
						} else if (y == 0) {
							tiles[x][y][z] = Tile.Brick.getId();
						} else if (y > 14) {
							tiles[x][y][z] = Tile.Air.getId();
						} else if (tiles[x][y][z] == Tile.CrackedStone.getId()
								&& (rand.nextInt(7) == 0)) {
							tiles[x][y][z] = Tile.CrackedStone.getId();
						} else if (rand.nextInt(2) == 0) {
							if (rand.nextBoolean())
								tiles[x][y][z] = Tile.Air.getId();
						} else
							tiles[x][y][z] = Tile.CrackedStone.getId();

						/*
						 * if (rand.nextInt(5) == 0) if (rand.nextBoolean())
						 * tiles[x][y][z] = Tile.CrackedStone.getId(); if
						 * (rand.nextInt(9) == 0) if (rand.nextBoolean())
						 * tiles[x][y][z] = Tile.Brick.getId(); else
						 * tiles[x][y][z] = Tile.Glass.getId();
						 */
						try {
							if (tiles[x][y][z] != Tile.Air.getId()){
								PhysicsWorld.newBlock(x, y, z);
							}
						} catch (NullPointerException e) {
							System.out.println("Block error at X: "+ x +" Y: "+y+" Z: "+z);
						}
					}
				}
			}
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
			
			glBegin(GL_QUADS);
			float playerX = Player.camera.getX();
			float playerY = Player.camera.getY();
			float playerZ = Player.camera.getZ();
			
			Shape.createSkybox(playerX-10,playerY-10,playerZ-10,
					Tile.getTile(Tile.Brick.getId()).getColor(),
					Tile.getTile(Tile.Brick.getId()).getTexCoords(),
					20);
			glEnd();
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
			for (int x = (int) pos.getX(); x < sizeX; x++) {
				for (int y = (int) pos.getY(); y < sizeY; y++) {
					for (int z = (int) pos.getZ(); z < sizeZ; z++) {
						if (tiles[x][y][z] != 0 && !checkTileNotInView(x, y, z)) {
							Shape.createCube(
									x,
									y,
									z,
									Tile.getTile(tiles[x][y][z]).getColor(),
									Tile.getTile(tiles[x][y][z]).getTexCoords(),
									1);
						}
					}
				}
			}
			//WorldRender.render();
			//float offset = 15;
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
		if (x < sizeX - 1) {
			if (tiles[x + 1][y][z] != 0)
				facesHidden[1] = true;
			else
				facesHidden[1] = false;
		} else {
			facesHidden[1] = false;
		}

		if (y > pos.getY()) {
			if (tiles[x][y - 1][z] != 0)
				facesHidden[2] = true;
			else
				facesHidden[2] = false;
		} else {
			facesHidden[2] = false;
		}
		if (y < sizeY - 1) {
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
		if (z < sizeZ - 1) {
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

	public short getTileID(int x, int y, int z) {
		if (x < pos.getX() || x > pos.getX() + Constants.CHUNKSIZE
				|| y < pos.getY() || y > pos.getY() + Constants.CHUNKSIZE
				|| z < pos.getZ() || z > pos.getZ() + Constants.CHUNKSIZE)
			return 1;
		return tiles[x][y][z];
	}

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
