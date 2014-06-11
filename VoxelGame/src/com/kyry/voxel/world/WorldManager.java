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
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.geometry.Sphere;
import com.kyry.voxel.utilities.Constants;
import com.kyry.voxel.utilities.Frustum;
import com.kyry.voxel.utilities.Spritesheet;
import com.kyry.voxel.world.blocks.Block;
import com.kyry.voxel.world.chunks.Chunk;
import com.kyry.voxel.world.chunks.ChunkManager;
import com.kyry.voxel.world.entities.mobs.MobManager;
import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.physics.CollisionLibrary;
import com.nishu.utils.Shader;
import com.nishu.utils.ShaderProgram;

public class WorldManager {

	MobManager mobManager;
	public static Sphere playerSphereUpper = new Sphere(0.25f);
	public static Sphere playerSphereLower = new Sphere(0.25f);

	//public ArrayList<Chunk> loadedChunks;
	//public ArrayList<Chunk> activeChunks;

	private ShaderProgram shader;
	public static ChunkManager chunkManager;

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
		Vector3f pos3f = Player.camera.getPos();
		Vector2f pos2f = ChunkManager.blockToChunk(pos3f);
		int y = 0;
		for (int x = (int) (pos2f.getX() - Constants.WORLDRADIUS); x <= pos2f.getX() + Constants.WORLDRADIUS; x++) {
			//for (int y = (int) (pos.getY() - Constants.WORLDRADIUS); y <= pos.getY() +Constants.WORLDRADIUS; y++) {
				for (int z = (int) (pos2f.getY() - Constants.WORLDRADIUS); z <= pos2f.getY() + Constants.WORLDRADIUS; z++) {
					ChunkManager.loadChunkToActive(x, z);
					Constants.chunksActive++;
				}
			//}
		}
	}


	public void update() {
		mobManager.update();
		chunkManager.update();
	}

	public void render() {
		Constants.chunksFrustum = 0;

		Spritesheet.tiles.bind();
		
		chunkManager.render();
		mobManager.render();
		
		// renders RAY! keep
		glBegin(GL_QUADS);
		Shape.createCube(mobManager.getPlayer().getX() + Constants.ray.x,
				mobManager.getPlayer().getY() + Constants.ray.y,
				mobManager.getPlayer().getZ() + Constants.ray.z,
				Block.getTile(Block.Void.getId()).getColor(),
				Block.getTile(Block.Void.getId()).getTexCoords(), 0.0099f);
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

	public MobManager getMobManager() {
		return mobManager;
	}

}
