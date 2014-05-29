package com.kyry.voxel.world.entities;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.kyry.voxel.geometry.AABB;
import com.kyry.voxel.geometry.Sphere;
//import com.bulletphysics.linearmath.Transform;
import com.kyry.voxel.utilites.Constants;
import com.kyry.voxel.world.WorldManager;
import com.kyry.voxel.world.chunks.Chunk;
import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.physics.CollisionLibrary;
//import com.kyry.voxel.world.physics.PhysicsWorld;
import com.kyry.voxel.world.tiles.Tile;
import com.nishu.utils.Time;

import static org.lwjgl.opengl.GL11.*;

public class Camera extends Entity {

	private float speed, maxU, maxD;

	/*
	 * Rotation x - pitch y - yaw z - roll
	 */

	public Camera(float x, float y, float z, float speed, float maxU,
			float maxD, int id) {
		super(x, y, z, 0, 0, 0, id);
		this.speed = speed;
		this.maxU = maxU;
		this.maxD = maxD;
	}

	public Camera(float x, float y, float z, float rx, float ry, float rz,
			float speed, float maxU, float maxD, int id) {
		super(x, y, z, rx, ry, rz, id);
		this.speed = speed;
		this.maxU = maxU;
		this.maxD = maxD;
	}

	public void updateMouse() {
		float dx = Mouse.getDX() * speed * 0.16f;
		float dy = Mouse.getDY() * speed * 0.16f;

		if (getYaw() + dx >= 360) {
			setYaw(getYaw() + dx - 360);
		} else if (getYaw() + dx < 0) {
			setYaw(360 - getYaw() + dx);
		} else {
			setYaw(getYaw() + dx);
		}

		if (getPitch() - dy >= maxD && getPitch() - dy <= maxU) {
			setPitch(getPitch() + -dy);
		} else if (getPitch() - dy < maxD) {
			setPitch(maxD);
		} else if (getPitch() - dy > maxU) {
			setPitch(maxU);
		}
		// System.out.println(PhysicsWorld.checkCollision());
	}

	public void updateKeyboard(float delay, float speed) {
		int x = (int) (Constants.PLAYER_SPEED
				* Math.cos((double) (getPitch() / 180 * Math.PI)) * Math
				.sin((double) (getYaw() / 180 * Math.PI)));
		int y = 0;/*
				 * (int) (Constants.PLAYER_SPEED *
				 * Math.cos((double)(getYaw()/180 * Math.PI)) *
				 * Math.sin((double)(getPitch()/180 * Math.PI)));
				 */
		int z = -1
				* (int) (Constants.PLAYER_SPEED * Math
						.cos((double) (getYaw() / 180 * Math.PI)));
		/*
		 * Vector3f vectorY = new Vector3f(0, 1, 0); Vector3f DOWN = new
		 * Vector3f(0, -2, 0); Vector3f UP = new Vector3f(0, 2, 0);
		 */
		// PhysicsWorld.clearForcesOnPlayer();
		boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_W);
		boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_S);
		boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_A);
		boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_D);
		boolean space = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);

		Vector3f somePosition = new Vector3f(camera.getX(), camera.getY(),
				camera.getZ());
		WorldManager.playerSphere.update(somePosition);

		if (keyUp && keyRight && !keyLeft && !keyDown) {// NE
			/*
			 * Vector3f vectorUP = new Vector3f(x,y,z); Vector3f vectorRight =
			 * new Vector3f(); vectorRight.cross(vectorUP, vectorY); Vector3f v
			 * = new Vector3f(); v.add(vectorRight, vectorUP);
			 * v.scale(0.5f);//the vector addition has too big of a magnitude
			 * make a bit smaller (half)
			 */// PhysicsWorld.moveCharacter(v);
			/*
			 * for (x = 0; x < Constants.BlocksLoaded; x++) { Vector3f
			 * someNewPosition = new Vector3f(camera.getX(), camera.getY() + 1f,
			 * camera.getZ());
			 * if((CollisionLibrary.testCircleAABB(WorldManager.playerSphere,
			 * CollisionLibrary.BlockList.get(x)))){
			 * WorldManager.playerSphere.update(somePosition); }; }
			 */
			move(speed * delay * (float) Time.getDelta(), 0, -speed * delay
					* (float) Time.getDelta());

		}
		if (keyUp && keyLeft && !keyRight && !keyDown) {// NW
			/*
			 * Vector3f vectorUP = new Vector3f(x,y,z); Vector3f vectorLeft =
			 * new Vector3f(); vectorLeft.cross(vectorY, vectorUP); Vector3f v =
			 * new Vector3f(); v.add(vectorLeft, vectorUP); v.scale(0.5f);//the
			 * vector addition has too big of a magnitude make a bit smaller
			 * (half)
			 */// PhysicsWorld.moveCharacter(v);
			move(-speed * delay * (float) Time.getDelta(), 0, -speed * delay
					* (float) Time.getDelta());
		}
		if (keyUp && !keyLeft && !keyRight && !keyDown) {// N
			// PhysicsWorld.moveCharacter(new Vector3f(x, y, z));
			move(0, 0, -speed * delay * (float) Time.getDelta());
		}
		if (keyDown && keyLeft && !keyRight && !keyUp) {// SW
			/*
			 * Vector3f vectorDown = new Vector3f(-x,-y,-z); Vector3f vectorLeft
			 * = new Vector3f(); vectorLeft.cross(vectorDown, vectorY); Vector3f
			 * v = new Vector3f(); v.add(vectorLeft, vectorDown);
			 * v.scale(0.5f);//the vector addition has too big of a magnitude
			 * make a bit smaller (half) //PhysicsWorld.moveCharacter(v);
			 */
			move(-speed * delay * (float) Time.getDelta(), 0, speed * delay
					* (float) Time.getDelta());
		}
		if (keyDown && keyRight && !keyLeft && !keyUp) {// SE
			/*
			 * Vector3f vectorDown = new Vector3f(-x,-y,-z); Vector3f vectorLeft
			 * = new Vector3f(); vectorLeft.cross(vectorY, vectorDown); Vector3f
			 * v = new Vector3f(); v.add(vectorLeft, vectorDown);
			 * v.scale(0.5f);//the vector addition has too big of a magnitude
			 * make a bit smaller (half) //PhysicsWorld.moveCharacter(v);
			 */
			move(speed * delay * (float) Time.getDelta(), 0, speed * delay
					* (float) Time.getDelta());
		}
		if (keyDown && !keyUp && !keyLeft && !keyRight) {// S
			// PhysicsWorld.moveCharacter(new Vector3f(-x, -y, -z));
			move(0, 0, speed * delay * (float) Time.getDelta());
		}
		if (keyLeft && !keyRight && !keyUp && !keyDown) {// W
			/*
			 * Vector3f vector = new Vector3f(x,y,z); vector.cross(vectorY,
			 * vector); //PhysicsWorld.moveCharacter(vector);
			 */
			move(-speed * delay * (float) Time.getDelta(), 0, 0);
		}
		if (keyRight && !keyLeft && !keyUp && !keyDown) {// E
			/*
			 * Vector3f vector = new Vector3f(x,y,z); vector.cross(vector,
			 * vectorY); //PhysicsWorld.moveCharacter(vector);
			 */
			move(speed * delay * (float) Time.getDelta(), 0, 0);
		}
		if (space && !shift) {// UP
			// PhysicsWorld.moveCharacter(UP);
			move(0, -0.1f, 0);
		}
		if (shift && !space) {// DOWN
			// PhysicsWorld.moveCharacter(DOWN);
//			boolean moveAllowed = true;
//			for (x = 0; x < Constants.BlocksLoaded; x++) {
//
//				if (CollisionLibrary.testCircleAABB(WorldManager.playerSphere,
//						CollisionLibrary.BlockList.get(x))) {
//					moveAllowed = false;
//					System.out.println(x);
//				}
//				if (!moveAllowed)
//					break;
//			}
//			// camera.setY(camera.getY() - 1f);
//			if (moveAllowed == true) {
//				move(0, 0.1f, 0);
//			}
			move(0, 0.1f, 0);
		}
	}

	public Vector3f getPosition() {
		return new Vector3f(getX(), getY(), getZ());
	}

	public void move(float dX, float dY, float dZ) {
		// Move Camera
		float origX = getX();
		float origY = getY();
		float origZ = getZ();
		Vector3f someOldPosition = new Vector3f(getX(), getY(), getZ());
		//WorldManager.playerSphere.update(someOldPosition);
		
		setZ((float) (getZ() + (dX
				* (float) Math.cos(Math.toRadians(getYaw() - 90)) + dZ
				* Math.cos(Math.toRadians(getYaw())))));

		setX((float) (getX() - (dX
				* (float) Math.sin(Math.toRadians(getYaw() - 90)) + dZ
				* Math.sin(Math.toRadians(getYaw())))));
		setY((float) (getY() + (dY
				* (float) Math.sin(Math.toRadians(getPitch() - 90)) + dZ
				* Math.sin(Math.toRadians(getPitch())))));
		// Move Player
		Vector3f somePosition = new Vector3f(getX(), getY(), getZ());
		WorldManager.playerSphere.update(somePosition);
		
		boolean moveAllowed = true;
		for (int x = 0; x < Constants.BlocksLoaded; x++) {

			if (CollisionLibrary.testCircleAABB(WorldManager.playerSphere,
					CollisionLibrary.BlockList.get(x))) {
				moveAllowed = false;
				//System.out.println(x);
			}
			if (!moveAllowed)
				break;
		}
		// camera.setY(camera.getY() - 1f);
		if (!moveAllowed) {
			setX(origX);
			setY(origY);
			setZ(origZ);
			WorldManager.playerSphere.update(someOldPosition);
			System.out.println("Collision!  - STAHPED MOVING");
		}
	}

	public void applyPhysics(Vector3f playerPosition) {
		{
			setX(playerPosition.x);
			setY(playerPosition.y);
			setZ(playerPosition.z);
		}
	}

	public void applyTranslations() {
		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_MODELVIEW);
		glRotatef(getPitch(), 1, 0, 0);
		glRotatef(getYaw(), 0, 1, 0);
		glRotatef(getRoll(), 0, 0, 1);
		glTranslatef(-getX(), -getY(), -getZ());
		glPopAttrib();
	}

}
