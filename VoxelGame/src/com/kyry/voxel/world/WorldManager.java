package com.kyry.voxel.world;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.kyry.voxel.geometry.Sphere;
import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.utilities.Spritesheet;
import com.kyry.voxel.world.chunks.ChunkManager;
import com.kyry.voxel.world.entities.mobs.MobManager;
import com.kyry.voxel.world.entities.mobs.Player;

public class WorldManager {

	MobManager mobManager;
	public static Sphere playerSphereUpper = new Sphere(0.25f);
	public static Sphere playerSphereLower = new Sphere(0.25f);

	//public ArrayList<Chunk> loadedChunks;
	//public ArrayList<Chunk> activeChunks;

//	private ShaderProgram shader;
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
//		int y = 0;
		for (int x = (int) (pos2f.getX() - Globals.WORLDRADIUS); x <= pos2f.getX() + Globals.WORLDRADIUS; x++) {
			//for (int y = (int) (pos.getY() - Globals.WORLDRADIUS); y <= pos.getY() +Globals.WORLDRADIUS; y++) {
				for (int z = (int) (pos2f.getY() - Globals.WORLDRADIUS); z <= pos2f.getY() + Globals.WORLDRADIUS; z++) {
					ChunkManager.loadChunkToActive(x, z);
					Globals.chunksActive++;
				}
			//}
		}
	}


	public void update() {
		mobManager.update();
		chunkManager.update();
	}

	public void mapRender() {
		Globals.chunksFrustum = 0;

		Spritesheet.blocks.bind();
		
		chunkManager.render();
		mobManager.mobRender();
		
		// renders RAY! keep
/*		glBegin(GL_QUADS);
		Shape.createCube(mobManager.getPlayer().getX() + Globals.ray.x,
				mobManager.getPlayer().getY() + Globals.ray.y,
				mobManager.getPlayer().getZ() + Globals.ray.z,
				Block.getTile(Block.Void.getId()).getColor(),
				Block.getTile(Block.Void.getId()).getTexCoords(), 0.0099f);
		glEnd();*/
	}// end render

	public void skyBoxRender() {
		Skybox.skyRender();
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
