package com.kyry.voxel.geometry;

import org.lwjgl.util.vector.Vector3f;

public class Sphere {
	public Vector3f center;
	public float radius;

	public Sphere(final float radius) {
		center = new Vector3f();
		this.radius = radius;
	}

	public void update(final Vector3f position) {
		center.x = position.x;
		center.y = position.y;
		center.z = position.z;
	}

	public float getX() {
		return center.x;
	}
	public float getY() {
		return center.y;
	}
	public float getZ() {
		return center.z;
	}
}