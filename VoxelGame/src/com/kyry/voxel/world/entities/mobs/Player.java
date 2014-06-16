package com.kyry.voxel.world.entities.mobs;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.opengl.GL11;

import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.geometry.Sphere;
import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.world.WorldManager;
import com.kyry.voxel.world.blocks.Block;
import com.kyry.voxel.world.entities.Camera;
//import com.kyry.voxel.world.physics.PhysicsWorld;

public class Player extends Mob {

	public Player(Camera camera, int id) {
		super(camera, camera.getX(), camera.getY(), camera.getZ(), camera
				.getPitch(), camera.getYaw(), camera.getRoll(), id, 0);
		Vector3f somePosition = new Vector3f(camera.getX(), camera.getY(), camera.getZ());
		Vector3f someOtherPosition = new Vector3f(camera.getX(), camera.getY()-Globals.playerHeight, camera.getZ());
		WorldManager.playerSphereUpper.update(somePosition);
		WorldManager.playerSphereLower.update(someOtherPosition);
	}

	public void update() {
		mobMove();
	}

	public void mobRender() {
//		GL11.glBegin(GL11.GL_QUADS);
//		Vector3f playerPos= PhysicsWorld.playerBody.getWorldTransform(PhysicsWorld.DEFAULT_TRANSFORM).origin;
//		Shape.createCube(playerPos.x, playerPos.y, playerPos.z,
//				Block.getTile(Block.Void.getId()).getColor(),
//				Block.getTile(Block.Void.getId()).getTexCoords(), 0.01f);
//		GL11.glEnd();
		//System.out.println("HEYY!");
	}

	public void mobDispose() {
	}

}
