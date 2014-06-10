package com.kyry.voxel.world.entities.mobs;

import org.lwjgl.util.vector.Vector3f;

import com.kyry.voxel.utilities.Constants;
import com.kyry.voxel.utilities.GameObject;
import com.kyry.voxel.world.entities.Camera;
import com.kyry.voxel.world.entities.Entity;

public class Mob extends Entity implements GameObject{

	private int mobID;
	private boolean isDead;

	public Mob(Camera camera, int id, int mobID) {
		this(camera, camera.getX(), camera.getY(), camera.getZ(), 0, 0, 0, id, mobID);
	}

	public Mob(Camera camera, float x, float y, float z, float rx, float ry, float rz, int id, int mobID) {
		super(camera, id);
		this.mobID = mobID;
	}

	@Override
	public void update() {
	}

	public void move() {
		getCamera().updateMouse();
		getCamera().updateKeyboard((float)(1/Constants.FPS), 1f);
		setPos(getCamera().getX(), getCamera().getY(), getCamera().getZ());
		setRot(getCamera().getPitch(), getCamera().getYaw(), getCamera().getRoll());
	}
	
	@Override
	public void render() {
	}

	@Override
	public void dispose() {
	}
	
	public int getID() {
		return mobID;
	}
	
	public void set(int mobID) {
		this.mobID = mobID;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}
}
