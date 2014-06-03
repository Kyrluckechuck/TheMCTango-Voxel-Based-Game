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

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.geometry.Sphere;
import com.kyry.voxel.utilites.Constants;
import com.kyry.voxel.utilites.Frustum;
import com.kyry.voxel.utilites.Spritesheet;
import com.kyry.voxel.world.chunks.Chunk;
import com.kyry.voxel.world.chunks.ChunkManager;
import com.kyry.voxel.world.entities.mobs.MobManager;
import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.physics.CollisionLibrary;
import com.kyry.voxel.world.tiles.Tile;
import com.nishu.utils.Shader;
import com.nishu.utils.ShaderProgram;

public class WorldManager {

	MobManager mobManager;
	public static Sphere playerSphereUpper = new Sphere(0.25f);
	public static Sphere playerSphereLower = new Sphere(0.25f);
	// PhysicsWorld physicsWorld;

	//public ArrayList<Chunk> loadedChunks;
	//public ArrayList<Chunk> activeChunks;

	private ShaderProgram shader;
	private ChunkManager chunkManager;

	public WorldManager() {
		//initGL();
		init();
		createWorld();
	}

	private void init() {
		mobManager = new MobManager();
		chunkManager = new ChunkManager();
		//loadedChunks = new ArrayList<Chunk>();
		//activeChunks = new ArrayList<Chunk>();
	}

	private void createWorld() {
		Vector3f pos = Player.camera.getPos();
		for (int x = (int) (pos.getX() - Constants.WORLDRADIUS); x < pos.getX() + Constants.WORLDRADIUS; x++) {
			for (int y = (int) (pos.getY() - Constants.WORLDRADIUS); y < pos.getY() +Constants.WORLDRADIUS; y++) {
				for (int z = (int) (pos.getZ() - Constants.WORLDRADIUS); z < pos.getZ() + Constants.WORLDRADIUS; z++) {
					chunkManager.loadChunkToMem(x, y, z);
					chunkManager.loadedChunks.put(ChunkManager.key(x, y, z), chunkManager.activeChunks.get(ChunkManager.key(x,y,z)));
					//activeChunks.add(new Chunk(shader, 1, x, y, z));					
//					 * activeChunks.add(new Chunk(shader, 1, 2*x
//					 * Constants.CHUNKSIZE, y * Constants.CHUNKSIZE, 2*z
//					 * Constants.CHUNKSIZE));
					Constants.chunksLoaded++;					
				}
			}
		}
		/*for (int x = (int) (Player.camera.getX() / Constants.CHUNKSIZE)-1; x < (Player.camera.getX() / Constants.CHUNKSIZE)+2; x++){
			for (int y = (int) (Player.camera.getY() / Constants.CHUNKSIZE)-1; y < (Player.camera.getY() / Constants.CHUNKSIZE)+2; y++){
				for (int z = (int) (Player.camera.getZ() / Constants.CHUNKSIZE)-1; z < (Player.camera.getZ() / Constants.CHUNKSIZE)+2; z++){
					activeChunks.add(new Chunk(shader, 1, x, y, z));
					System.out.println("Chunk ("+x+")("+y+")("+z+") added at X:"+x*Constants.CHUNKSIZE+" Y: "+y*Constants.CHUNKSIZE+" Z: "+z*Constants.CHUNKSIZE);
					
				}
			}
		}*/
	}


	public void update() {
		mobManager.update();
		chunkManager.update();
	}

	public void render() {
		Constants.chunksFrustum = 0;
		int prevX = (int) (Constants.playerPrevPos.getX()/Constants.CHUNKSIZE);
		int prevY = (int) (Constants.playerPrevPos.getY()/Constants.CHUNKSIZE);
		int prevZ = (int) (Constants.playerPrevPos.getZ()/Constants.CHUNKSIZE);
		int nowX = (int) (Constants.playerPos.getX()/Constants.CHUNKSIZE);
		int nowY = (int) (Constants.playerPos.getY()/Constants.CHUNKSIZE);
		int nowZ = (int) (Constants.playerPos.getZ()/Constants.CHUNKSIZE);
		
		/*if (!new Vector3f (prevX,prevY,prevZ).equals(new Vector3f(nowX,nowY,nowZ))){
			createWorld();
		}*/
		Spritesheet.tiles.bind();
		
		chunkManager.render();
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
		
		/* renders RAY! keep
		glBegin(GL_QUADS);
		Shape.createCube(mobManager.getPlayer().getX() + Constants.ray.x,
				mobManager.getPlayer().getY() + Constants.ray.y,
				mobManager.getPlayer().getZ() + Constants.ray.z,
				Tile.getTile(Tile.Brick.getId()).getColor(),
				Tile.getTile(Tile.Brick.getId()).getTexCoords(), 0.2f);
		glEnd();*/
		
		
	}// end render

	public void skyBoxRender() {
		WorldRender.render();
	}

	public void logic() {
		getMobManager().getPlayer().getCamera().castRay();
		getMobManager().getPlayer().getCamera().applyTranslations();
		getMobManager().getPlayer().getCamera().applyPhysics();
	}

	/*public void saveChunk(Chunk c) {
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

	}*/

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
