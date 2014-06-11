package com.kyry.voxel.world.entities.mobs;

import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glGenLists;

import java.util.ArrayList;

import com.kyry.voxel.utilities.Constants;
import com.kyry.voxel.utilities.GameObject;
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
		// player = new Player(new Camera(0, (Constants.viewDistance *
		// Constants.CHUNKSIZE) + 2, 0, 5, 131, 0, 1, 90, -90, 1), 0);
		player = new Player(new Camera(Constants.initPos.x, Constants.initPos.y, Constants.initPos.z, 5, 131,
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
	public void mobRender() {
		player.mobRender();
		for (int i = 0; i < mobs.size(); i++) {
			mobs.get(i).mobRender();
		}
		glCallList(mobRenderID);
	}

	@Override
	public void mobDispose() {
		player.mobDispose();
		glDeleteLists(mobRenderID, 1);
	}

	public Player getPlayer() {
		return player;
	}
}
