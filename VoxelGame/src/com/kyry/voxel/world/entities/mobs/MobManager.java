package com.kyry.voxel.world.entities.mobs;

import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glGenLists;

import java.util.ArrayList;

import com.kyry.voxel.utilites.Constants;
import com.kyry.voxel.utilites.GameObject;
import com.kyry.voxel.world.entities.Camera;
import com.nishu.utils.Vector3f;

public class MobManager implements GameObject {

	private ArrayList<Mob> mobs;
	public Player player;

	private int mobRenderID;

	public MobManager() {
		init();
		initGL();
	}

	private void init() {
		mobs = new ArrayList<Mob>();
		// player = new Player(new Camera(0, (Constants.WORLDSIZE *
		// Constants.CHUNKSIZE) + 2, 0, 5, 131, 0, 1, 90, -90, 1), 0);
		player = new Player(new Camera(4, 9, 4, 5, 131,
				0, 1, 90, -90, 1), 0);
	}

	private void initGL() {
		mobRenderID = glGenLists(1);
	}

	@Override
	public void update() {
		for (int i = 0; i < mobs.size(); i++) {
			if (mobs.get(i).isDead())
				mobs.remove(i);
			mobs.get(i).update();
		}
		player.update();
	}

	@Override
	public void render() {
		for (int i = 0; i < mobs.size(); i++) {
			mobs.get(i).render();
		}
		glCallList(mobRenderID);
	}

	@Override
	public void dispose() {
		player.dispose();
		glDeleteLists(mobRenderID, 1);
	}

	public Player getPlayer() {
		return player;
	}
}
