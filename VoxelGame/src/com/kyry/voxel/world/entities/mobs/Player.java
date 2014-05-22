package com.kyry.voxel.world.entities.mobs;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.world.entities.Camera;
import com.kyry.voxel.world.physics.PhysicsWorld;
import com.kyry.voxel.world.tiles.Tile;

public class Player extends Mob {

	public Player(Camera camera, int id) {
		super(camera, camera.getX(), camera.getY(), camera.getZ(), camera
				.getPitch(), camera.getYaw(), camera.getRoll(), id, 0);
	}

	public void update() {
		move();
	}

	public void render() {
//		GL11.glBegin(GL11.GL_QUADS);
//		Vector3f playerPos= PhysicsWorld.playerBody.getWorldTransform(PhysicsWorld.DEFAULT_TRANSFORM).origin;
//		Shape.createCube(playerPos.x, playerPos.y, playerPos.z,
//				Tile.getTile(Tile.Void.getId()).getColor(),
//				Tile.getTile(Tile.Void.getId()).getTexCoords(), 0.01f);
//		GL11.glEnd();
		//System.out.println("HEYY!");
	}

	public void dispose() {
	}

}
