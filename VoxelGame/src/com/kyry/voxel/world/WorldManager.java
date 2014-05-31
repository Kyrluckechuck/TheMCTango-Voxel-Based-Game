package com.kyry.voxel.world;

import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.geometry.Sphere;
import com.kyry.voxel.utilites.Constants;
import com.kyry.voxel.utilites.Frustum;
import com.kyry.voxel.utilites.Spritesheet;
import com.kyry.voxel.world.chunks.Chunk;
import com.kyry.voxel.world.entities.mobs.MobManager;
import com.kyry.voxel.world.physics.CollisionLibrary;
import com.kyry.voxel.world.tiles.Tile;
import com.nishu.utils.Shader;
import com.nishu.utils.ShaderProgram;

public class WorldManager {

	MobManager mobManager;
	public static Sphere playerSphereUpper = new Sphere(0.25f);;
	public static Sphere playerSphereLower = new Sphere(0.25f);;
	// PhysicsWorld physicsWorld;

	public ArrayList<Chunk> loadedChunks;
	public ArrayList<Chunk> activeChunks;

	private ShaderProgram shader;

	public WorldManager() {
		initGL();
		init();
		createWorld();
	}

	private void initGL() {
		Shader temp = new Shader("/shaders/chunk.vert", "/shaders/chunk.frag");
		shader = new ShaderProgram(temp.getvShader(), temp.getfShader());
	}

	private void init() {
		mobManager = new MobManager();
		loadedChunks = new ArrayList<Chunk>();
		activeChunks = new ArrayList<Chunk>();
	}

	private void createWorld() {
		for (int x = 0; x < Constants.WORLDSIZE; x++) {
			for (int y = 0; y < Constants.WORLDSIZE; y++) {
				for (int z = 0; z < Constants.WORLDSIZE; z++) {
					activeChunks.add(new Chunk(shader, 1, x
							* Constants.CHUNKSIZE, y * Constants.CHUNKSIZE, z
							* Constants.CHUNKSIZE));
					/*
					 * activeChunks.add(new Chunk(shader, 1, 2*x
					 * Constants.CHUNKSIZE, y * Constants.CHUNKSIZE, 2*z
					 * Constants.CHUNKSIZE));
					 */
					Constants.chunksLoaded++;
				}
			}
		}
		// saveChunksTemp();
	}

	private void saveChunksTemp() {
		for (int i = 0; i < activeChunks.size(); i++) {
			saveChunk(activeChunks.get(i));
		}
	}

	public void update() {
		mobManager.update();

	}

	public void render() {
		// float groundDim = 10000f;
		// {
		// glBegin(GL_QUADS);
		// glColor4f(0.5f, 0.5f, 0.5f, 1);
		// glVertex4f(-groundDim, 0, -groundDim, 1);
		// glColor4f(0.5f, 0.5f, 0.5f, 1);
		// glVertex4f(-groundDim, 0, +groundDim, 1);
		// glColor4f(0.5f, 0.5f, 0.5f, 1);
		// glVertex4f(+groundDim, 0, +groundDim, 1);
		// glColor4f(0.5f, 0.5f, 0.5f, 1);
		// glVertex4f(+groundDim, 0, -groundDim, 1);
		// glEnd();
		// }
		// ////////
		Constants.chunksFrustum = 0;
		Spritesheet.tiles.bind();
		/*
		 * GL11.glBegin(GL11.GL_QUADS); Vector3f playerPos=
		 * PhysicsWorld.playerBody
		 * .getWorldTransform(PhysicsWorld.DEFAULT_TRANSFORM).origin;
		 * Shape.createCube(playerPos.x, playerPos.y + 0.2f, playerPos.z,
		 * Tile.getTile(Tile.Wireframe.getId()).getColor(),
		 * Tile.getTile(Tile.Wireframe.getId()).getTexCoords(), 1f);
		 * GL11.glEnd();
		 */

		// get vector from physics()

		//

		for (int i = 0; i < activeChunks.size(); i++) {
			if (Frustum.getFrustum().cubeInFrustum(
					activeChunks.get(i).getPos().getX(),
					activeChunks.get(i).getPos().getY(),
					activeChunks.get(i).getPos().getZ(),
					activeChunks.get(i).getPos().getX() + Constants.CHUNKSIZE,
					activeChunks.get(i).getPos().getY() + Constants.CHUNKSIZE,
					activeChunks.get(i).getPos().getZ() + Constants.CHUNKSIZE)) {
				if (Math.abs(activeChunks.get(i).getCenter().getX()
						- (int) mobManager.getPlayer().getX()) < 64
						&& Math.abs(activeChunks.get(i).getCenter().getZ()
								- mobManager.getPlayer().getZ()) < 64
						&& Math.abs(activeChunks.get(i).getCenter().getY()
								- mobManager.getPlayer().getY()) < 32) {
					Constants.chunksFrustum++;
					activeChunks.get(i).render();

				}
			}

		}
		mobManager.render();
		/*
		 * glBegin(GL_QUADS); for (int x = 0; x < Constants.BlocksLoaded; x++) {
		 * 
		 * Shape.createCube(CollisionLibrary.BlockList.get(x).center.x,
		 * CollisionLibrary.BlockList.get(x).center.y + .01f,
		 * CollisionLibrary.BlockList.get(x).center.z,
		 * Tile.getTile(Tile.Wireframe.getId()).getColor(), Tile
		 * .getTile(Tile.Wireframe.getId()).getTexCoords(), 1f);
		 * 
		 * }
		glEnd();*/
		glBegin(GL_QUADS);
		Shape.createCube(mobManager.getPlayer().getX() + Constants.ray.x,
				mobManager.getPlayer().getY() + Constants.ray.y,
				mobManager.getPlayer().getZ() + Constants.ray.z,
				Tile.getTile(Tile.Brick.getId()).getColor(),
				Tile.getTile(Tile.Brick.getId()).getTexCoords(), 0.2f);
		glEnd();
		
		
	}// end render

	public void skyBoxRender() {
		WorldRender.render();
	}

	public void logic() {

		getMobManager().getPlayer().getCamera().castRay();
		getMobManager().getPlayer().getCamera().applyTranslations();
		getMobManager().getPlayer().getCamera().applyPhysics();

	}

	public void saveChunk(Chunk c) {
		// BufferedWriter bw = null;
		// try {
		// bw = new BufferedWriter(new FileWriter("/000chunk.dat"));
		// bw.write(c.getPos().getX() + " " + c.getPos().getY() + " "
		// + c.getPos().getZ());
		// for (int x = (int) c.getPos().getX(); x < (int) c.getPos().getX()
		// + Constants.CHUNKSIZE; x++) {
		// for (int y = (int) c.getPos().getY(); y < (int) c.getPos()
		// .getY() + Constants.CHUNKSIZE; y++) {
		// for (int z = (int) c.getPos().getZ(); z < (int) c.getPos()
		// .getZ() + Constants.CHUNKSIZE; z++) {
		// bw.write(String.valueOf(c.getTileID(x, y, z)));
		// }
		// }
		// }
		// bw.close();
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		// }

	}

	/*
	 * private void loadChunksTemp() { for (int i = 0; i < activeChunks.size();
	 * i++) { loadChunk(activeChunks.get(i)); } }
	 * 
	 * public void loadChunk(Chunk c) { BufferedReader br = null; String[]
	 * tokens; String line; int sdat, length; try { br = new BufferedReader(new
	 * FileReader("/000chunk.dat")); line = br.readLine(); if (line == null) {
	 * break; } else { tokens = line.split(""); length = tokens.length; sdat =
	 * Integer.parseInt(tokens [0]);
	 * 
	 * br.write(c.getPos().getX() + " " + c.getPos().getY() + " " +
	 * c.getPos().getZ()); for (int x = (int) c.getPos().getX(); x < (int)
	 * c.getPos().getX() + Constants.CHUNKSIZE; x++) { for (int y = (int)
	 * c.getPos().getY(); y < (int) c.getPos().getY() + Constants.CHUNKSIZE;
	 * y++) { for (int z = (int) c.getPos().getZ(); z < (int) c.getPos().getZ()
	 * + Constants.CHUNKSIZE; z++) { br.write(String.valueOf(c.getTileID(x, y,
	 * z))); } } }
	 * 
	 * br.close(); } } catch (IOException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * }
	 */

	public MobManager getMobManager() {
		return mobManager;
	}

}
