package com.kyry.voxel.geometry;

import org.lwjgl.util.vector.Vector3f;



public class AABB {
	public Vector3f center;
	public float r[];

	public AABB(final Vector3f pos, final float size) {
		this(pos.x, pos.y, pos.z, size);
	}

	public AABB(final float x, final float y, final float z, final float size) {
		center = new Vector3f(x+(size*.5f),y+(size*.5f),z+(size*.5f));

		r = new float[3];
		r[0] = (size * 0.5f); //size*0.5 to make it radius
		r[1] = (size * 0.5f);
		r[2] = (size * 0.5f);
	}

	public void update(final Vector3f position) {
		center.x = position.x;
		center.y = position.y;
		center.z = position.z;
	}
}
