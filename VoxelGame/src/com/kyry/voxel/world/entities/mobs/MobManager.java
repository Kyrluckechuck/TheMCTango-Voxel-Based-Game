package com.kyry.voxel.world.entities.mobs;

import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glGenLists;

import java.util.ArrayList;

import com.kyry.voxel.utilities.GameObject;
import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.world.entities.Camera;

public class MobManager implements GameObject {
	/* Definitions */
	private ArrayList<Mob> mobs;
	public Player player;

	private int mobRenderID;
	/* Initialize */
	public MobManager() {
		init();
		initGL();
	}
	
	private void init() {
		mobs = new ArrayList<Mob>();
		player = new Player(new Camera(Globals.initPos.x, Globals.initPos.y, Globals.initPos.z, 5, 131,
				0, 1, 90, -90, 1), 0);
	}

	private void initGL() {
		mobRenderID = glGenLists(1);
	}
	/* Update mob */
	@Override
	public void update() {
		for (int i = 0; i < mobs.size(); i++) {
			if (mobs.get(i).isDead())
				mobs.remove(i);
			mobs.get(i).update();
		}
		player.update();
	}
	/* Render mob */
	@Override
	public void mobRender() {
		player.mobRender();
		for (int i = 0; i < mobs.size(); i++) {
			mobs.get(i).mobRender();
		}
		glCallList(mobRenderID);
	}
	/* Dispose of mob */
	@Override
	public void mobDispose() {
		player.mobDispose();
		glDeleteLists(mobRenderID, 1);
	}
	/* Get the player */
	public Player getPlayer() {
		return player;
	}
}
