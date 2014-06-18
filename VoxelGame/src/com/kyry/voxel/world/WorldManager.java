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
	/* Initialization of player's size, Mob Manager and Chunk Manager*/
	MobManager mobManager;
	public static Sphere playerSphereUpper = new Sphere(0.25f);
	public static Sphere playerSphereLower = new Sphere(0.25f);

//	private ShaderProgram shader; //Ignored due to performance woe's
	public static ChunkManager chunkManager;

	public WorldManager() {
		/* Initialize and create world */
		init();
		createWorld();
	}

	private void init() {
		/* Instantiate */
		mobManager = new MobManager();
		chunkManager = new ChunkManager();
	}

	private void createWorld() {
		/* Get player's coordinates */
		Vector3f pos3f = Player.camera.getPos();
		/* Get player's coordinates in Chunk (X, Y) */
		Vector2f pos2f = ChunkManager.blockToChunk(pos3f);
		/* Load chunks around player (8 around him making it a cube (9 including his chunk )) */
		for (int x = (int) (pos2f.getX() - Globals.WORLDRADIUS); x <= pos2f.getX() + Globals.WORLDRADIUS; x++) {
				for (int z = (int) (pos2f.getY() - Globals.WORLDRADIUS); z <= pos2f.getY() + Globals.WORLDRADIUS; z++) {
					ChunkManager.loadChunkToActive(x, z);
					Globals.chunksActive++;
				}
		}
	}


	public void update() {
		/* Updating */
		mobManager.update();
		chunkManager.update();
	}

	public void mapRender() {
		/* Reset chunks in frustum count */
		Globals.chunksFrustum = 0;
		/* Bind the spritesheet */
		Spritesheet.blocks.bind();
		/* Render Chunks */
		chunkManager.render();
		/* Render player stuff */
		mobManager.mobRender();
	}// end render

	public void skyBoxRender() {
		/* Render Skybox */
		Skybox.skyRender();
	}

	public void logic() {
		/*Ray Cast */
		getMobManager().getPlayer().getCamera().castRay();
		/* Apply changes to camera */
		getMobManager().getPlayer().getCamera().applyTranslations();
		/* Apply physics */
		getMobManager().getPlayer().getCamera().applyPhysics();
	}

	public MobManager getMobManager() {
		return mobManager;
	}

}
