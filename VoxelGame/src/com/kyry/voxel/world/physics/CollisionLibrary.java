package com.kyry.voxel.world.physics;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.kyry.voxel.geometry.AABB;
import com.kyry.voxel.geometry.Sphere;
import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.world.chunks.ChunkManager;

public class CollisionLibrary {
	/* Definitions */
	public static HashMap<String, AABB> BlockMap = new HashMap<String, AABB>();
	/* Create new block with default size */
	public static void newBlock(float f, float h, float x, float y, float z) {
		newBlock(f, h, x, y, z, 1);
	}
	/* Create new block with specified size */
	public static void newBlock(float chunkX, float chunkZ, float x, float y, float z, float r) {
		String key = ChunkManager.key((int) (chunkX*Globals.CHUNKSIZE + x), (int) y, (int) (chunkZ*Globals.CHUNKSIZE + z));
		// BlockList.add(new AABB(chunkX, chunkY, chunkZ, x, y, z, r));
		BlockMap.put(key, new AABB(chunkX, chunkZ, x, y, z, r));
		Globals.PhysBlocksLoaded++;

	}
	/* Check if the block exists */
	public static boolean hasBlock(float chunkX, float chunkZ, float x, float y, float z) {
		String key = ChunkManager.key((int) (chunkX*Globals.CHUNKSIZE + x), (int) y, (int) (chunkZ*Globals.CHUNKSIZE + z));
		// BlockList.add(new AABB(chunkX, chunkY, chunkZ, x, y, z, r));
		if (BlockMap.containsKey(key)) {
			return true;
		} else {
			return false;
		}
	}
	/* Remove block based on internal block (X, Y, Z) and chunk (X, Z) coordinates*/
	public static void removeBlock(int chunkX, int chunkZ, int x, int y, int z) {
		removeBlock((int) (chunkX*Globals.CHUNKSIZE + x), y, (int) (chunkZ*Globals.CHUNKSIZE + z));
	}
	/* Remove block based on world block (X, Y, Z) coordinates*/
	private static void removeBlock(int x, int y, int z) {
		String key = ChunkManager.key(x, y, z);
		BlockMap.remove(key);
	}
	/* Test for AABB-> AABB collision */
	public static boolean testAABBAABB(final AABB box1, final AABB box2) {
		if (Math.abs(box1.center.x - box2.center.x) > (box1.r[0] + box2.r[0]))
			return false;
		if (Math.abs(box1.center.y - box2.center.y) > (box1.r[1] + box2.r[1]))
			return false;
		if (Math.abs(box1.center.z - box2.center.z) > (box1.r[2] + box2.r[2]))
			return false;
		return true;
	}
	/* Test sphere -> sphere collision */
	public static boolean testCircleCircle(final Sphere c1, final Sphere c2) {
		Vector3f dest = new Vector3f(0, 0, 0);
		Vector3f.sub(c1.center, c2.center, dest);
		final float distSQ = dest.x * dest.x + dest.y * dest.y + dest.z * dest.z;
		final float radiusSum = c1.radius + c2.radius;

		return distSQ <= radiusSum * radiusSum;
	}
	/* Test distance of AABB*/
	public static float sqDistPointAABB(final Vector3f p, final AABB aabb) {
		float sqDist = 0.0f;
		float v;
		float minX, minY, minZ, maxX, maxY, maxZ;

		/* Get the minX, maxX, minY, maxY and minZ, maxZ points of the AABB */
		minX = aabb.center.x - aabb.r[0];
		maxX = aabb.center.x + aabb.r[0];

		minY = aabb.center.y - aabb.r[1];
		maxY = aabb.center.y + aabb.r[1];

		minZ = aabb.center.z - aabb.r[2];
		maxZ = aabb.center.z + aabb.r[2];

		/* Test the bounds against the points X axis */
		v = p.x;

		if (v < minX)
			sqDist += (minX - v) * (minX - v);
		if (v > maxX)
			sqDist += (v - maxX) * (v - maxX);

		/* Test the bounds against the points Y axis */
		v = p.y;

		if (v < minY)
			sqDist += (minY - v) * (minY - v);
		if (v > maxY)
			sqDist += (v - maxY) * (v - maxY);

		/* Test the bounds against the points Z axis */
		v = p.z;

		if (v < minZ)
			sqDist += (minZ - v) * (minZ - v);
		if (v > maxZ)
			sqDist += (v - maxZ) * (v - maxZ);

		return sqDist;
	}
	/* Test sphere -> AABB collision */
	public static boolean testSphereAABB(final Sphere sphere, final AABB cube) {
		/* Get the squared distance between circle center and the AABB */
		float sqDist = sqDistPointAABB(sphere.center, cube);
		float r = sphere.radius;

		return sqDist <= r * r;
	}
}
