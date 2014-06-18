package com.kyry.voxel.world.entities;

import org.lwjgl.util.vector.Vector3f;

public class Entity {
	/* Definitions */
	public static Camera camera;
	private Vector3f pos, rot;
	private int id;

	public Entity(float x, float y, float z, int id) {
		this(x, y, z, 0, 0, 0, id);
	}

	public Entity(float x, float y, float z, float rx, float ry, float rz, int id) {
		this(new Vector3f(x, y, z), new Vector3f(rx, ry, rz), id);
	}

	public Entity(Camera camera, int id) {
		this(camera, new Vector3f(camera.getX(), camera.getY(), camera.getZ()), new Vector3f(camera.getPitch(), camera.getYaw(), camera.getRoll()), id);
	}

	@SuppressWarnings("static-access")
	public Entity(Camera camera, Vector3f pos, Vector3f rot, int id) {
		this(pos, rot, id);
		this.camera = camera;
	}

	public Entity(Vector3f pos, Vector3f rot, int id) {
		this.pos = pos;
		this.rot = rot;
		this.id = id;
	}

	/* Return the camera */
	public Camera getCamera() {
		return camera;
	}
	/* Set the camera */
	@SuppressWarnings("static-access")
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	/* Get the ID */
	public int getID() {
		return id;
	}
	/* Get the position vector */
	public Vector3f getPos() {
		return pos;
	}
	/* Get the rotation */
	public Vector3f getRot() {
		return rot;
	}
	/* Set the position of the entity (X, Y, Z) */
	public void setPos(float x, float y, float z) {
		setPos(new Vector3f(x, y, z));
	}
	/* Set the position (vector) */
	public void setPos(Vector3f pos) {
		this.pos = pos;
	}
	/* Set the X Coordinate */
	public void setX(float x) {
		this.pos.x = x;
	}
	/* Set the Z coordinate */
	public void setZ(float z) {
		this.pos.z = z;
	}
	/* Set the Y coordinate */
	public void setY(float y) {
		this.pos.y = y;
	}
	/* Get the X coordinate */
	public float getX() {
		return pos.x;
	}
	/* Get the Y coordinate */
	public float getY() {
		return pos.y;
	}
	/* Get the Z coordinate */
	public float getZ() {
		return pos.z;
	}
	/* Set the rotation (rX, rY, rZ) */
	public void setRot(float rx, float ry, float rz) {
		setRot(new Vector3f(rx, ry, rz));
	}
	/* Set the rotation (vector) */
	public void setRot(Vector3f rot) {
		this.rot = rot;
	}
	/* Returns the pitch */
	public float getPitch() {
		return rot.x;
	}
	/* Returns the yaw */
	public float getYaw() {
		return rot.y;
	}
	/* Returns the roll */
	public float getRoll() {
		return rot.z;
	}
	/* Set the yaw */
	public void setYaw(float yaw) {
		this.rot.y = yaw;
	}
	/* Set the pitch */
	public void setPitch(float pitch) {
		this.rot.x = pitch;
	}
	/* Set the roll */
	public void setRoll(float roll) {
		this.rot.z = roll;
	}
}
