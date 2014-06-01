package com.kyry.voxel.world.physics;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.kyry.voxel.geometry.AABB;
import com.kyry.voxel.geometry.Sphere;
import com.kyry.voxel.utilites.Constants;

public class CollisionLibrary {
	public static ArrayList<AABB> BlockList = new ArrayList<AABB>();

	public static void newBlock(int chunkX, int chunkY, int chunkZ, float x, float y, float z) {
		newBlock (chunkX, chunkY, chunkZ,x, y, z, 1);
	}

	public static void newBlock(int chunkX, int chunkY, int chunkZ, float x, float y, float z, float r) {
		BlockList.add(new AABB(chunkX, chunkY, chunkZ, x, y, z, r));
		Constants.BlocksLoaded++;
	}

	public static boolean testAABBAABB(final AABB box1, final AABB box2) {
		if (Math.abs(box1.center.x - box2.center.x) > (box1.r[0] + box2.r[0]))
			return false;
		if (Math.abs(box1.center.y - box2.center.y) > (box1.r[1] + box2.r[1]))
			return false;
		if (Math.abs(box1.center.z - box2.center.z) > (box1.r[2] + box2.r[2]))
			return false;
		return true;
	}

	public static boolean testCircleCircle(final Sphere c1, final Sphere c2) {
		Vector3f dest = new Vector3f(0, 0, 0);
		Vector3f.sub(c1.center, c2.center, dest);
		final float distSQ = dest.x * dest.x + dest.y * dest.y + dest.z
				* dest.z;
		// final float distSQ = c1.center.distSQ(c2.center);
		final float radiusSum = c1.radius + c2.radius;

		return distSQ <= radiusSum * radiusSum;
	}

	public static float sqDistPointAABB(final Vector3f p, final AABB aabb) {
		float sqDist = 0.0f;
		float v;
		float minX, minY, minZ, maxX, maxY, maxZ;

		// get the minX, maxX, minY, maxY and minZ, maxZ points of the AABB
		minX = aabb.center.x - aabb.r[0];
		maxX = aabb.center.x + aabb.r[0];

		minY = aabb.center.y - aabb.r[1];
		maxY = aabb.center.y + aabb.r[1];

		minZ = aabb.center.z - aabb.r[2];
		maxZ = aabb.center.z + aabb.r[2];

		// test the bounds against the points X axis
		v = p.x;

		if (v < minX)
			sqDist += (minX - v) * (minX - v);
		if (v > maxX)
			sqDist += (v - maxX) * (v - maxX);

		// test the bounds against the points Y axis
		v = p.y;

		if (v < minY)
			sqDist += (minY - v) * (minY - v);
		if (v > maxY)
			sqDist += (v - maxY) * (v - maxY);

		// test the bounds against the points Z axis
		v = p.z;

		if (v < minZ)
			sqDist += (minZ - v) * (minZ - v);
		if (v > maxZ)
			sqDist += (v - maxZ) * (v - maxZ);

		return sqDist;
	}

	public static boolean testCircleAABB(final Sphere sphere, final AABB box) {
		// get the squared distance between circle center and the AABB
		float sqDist = sqDistPointAABB(sphere.center, box);
		float r = sphere.radius;

		return sqDist <= r * r;
	}
}
