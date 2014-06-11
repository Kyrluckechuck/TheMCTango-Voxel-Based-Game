package com.kyry.voxel.world.entities;

import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.kyry.voxel.geometry.AABB;
import com.kyry.voxel.geometry.Sphere;
//import com.bulletphysics.linearmath.Transform;
import com.kyry.voxel.utilities.Constants;
import com.kyry.voxel.world.World;
import com.kyry.voxel.world.WorldManager;
import com.kyry.voxel.world.blocks.Block;
import com.kyry.voxel.world.chunks.Chunk;
import com.kyry.voxel.world.chunks.ChunkManager;
import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.physics.CollisionLibrary;
//import com.kyry.voxel.world.physics.PhysicsWorld;
import com.nishu.utils.Time;

import static org.lwjgl.opengl.GL11.*;

public class Camera extends Entity {

	private float speed, maxU, maxD;
	private final float playerHeight = Constants.playerHeight;

	/*
	 * Rotation x - pitch y - yaw z - roll
	 */

	public Camera(float x, float y, float z, float speed, float maxU, float maxD, int id) {
		super(x, y, z, 0, 0, 0, id);
		this.speed = speed;
		this.maxU = maxU;
		this.maxD = maxD;
	}

	public Camera(float x, float y, float z, float rx, float ry, float rz, float speed, float maxU, float maxD, int id) {
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

	public void updateKeyboard(float deltaTime, float speed) {
		boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_W);
		boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_S);
		boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_A);
		boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_D);
		boolean space = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);

		float deltaDis = deltaTime * speed;

		Vector3f somePosition = new Vector3f(camera.getX(), camera.getY(), camera.getZ());
		Vector3f someOtherPosition = new Vector3f(camera.getX(), camera.getY() - playerHeight, camera.getZ());
		WorldManager.playerSphereUpper.update(somePosition);
		WorldManager.playerSphereLower.update(someOtherPosition);

		if (keyUp && keyRight && !keyLeft && !keyDown) {// NE
			playerMove(deltaDis, 0, -deltaDis);
		}
		if (keyUp && keyLeft && !keyRight && !keyDown) {// NW
			playerMove(-deltaDis, 0, -deltaDis);
		}
		if (keyUp && !keyLeft && !keyRight && !keyDown) {// N
			playerMove(0, 0, -deltaDis);
		}
		if (keyDown && keyLeft && !keyRight && !keyUp) {// SW
			playerMove(-deltaDis, 0, deltaDis);
		}
		if (keyDown && keyRight && !keyLeft && !keyUp) {// SE
			playerMove(deltaDis, 0, deltaDis);
		}
		if (keyDown && !keyUp && !keyLeft && !keyRight) {// S
			playerMove(0, 0, deltaDis);
		}
		if (keyLeft && !keyRight && !keyUp && !keyDown) {// W
			playerMove(-deltaDis, 0, 0);
		}
		if (keyRight && !keyLeft && !keyUp && !keyDown) {// E
			playerMove(deltaDis, 0, 0);
		}
		if (space && !shift) {// JUMP
			// made into a method cause its more complex and dont want clutter
			jump(deltaDis);
		}
		if (shift && !space) {// DOWN
			playerMove(0, deltaDis, 0);
		}
		if (!World.noClip)
			gravity();
	}

	public Vector3f getPosition() {
		return new Vector3f(getX(), getY(), getZ());
	}

	private void jump(float deltaDis) {
		if (Constants.jumpEnabled) {
			// if player can jump, then freaking jump already
			playerMove(0, -deltaDis * Constants.jumpPower, 0);
			Constants.jumpCounter = 0;
		} else if (!Constants.jumpEnabled && Constants.jumpCounter < Constants.jumpFrames) {
			playerMove(0, -deltaDis * Constants.jumpPower, 0);
			Constants.jumpCounter++;
		}
		if (!World.noClip) {
			// only disable jump if not noclip
			Constants.jumpEnabled = false;
		}
	}

	public void playerMove(float dX, float dY, float dZ) {
		// Move Camera
		Vector3f someOldPositionUpper = new Vector3f(getX(), getY(), getZ());
		Vector3f someOldPositionLower = new Vector3f(getX(), getY() - playerHeight, getZ());

		if (World.noClip) {
			// Constants.jumpEnabled = true;//not proper
			noClipMove(dX * Constants.PLAYER_SPEED, dY * Constants.PLAYER_SPEED, dZ * Constants.PLAYER_SPEED);
		} else if (!World.noClip) {// move normally
			float origX = getX();
			float origY = getY();
			float origZ = getZ();

			float newX = (float) (getX() - (dX * (float) Math.sin(Math.toRadians(getYaw() - 90)) + dZ * Math.sin(Math.toRadians(getYaw()))));
			float newY = (float) (getY() - dY);
			float newZ = (float) (getZ() + (dX * (float) Math.cos(Math.toRadians(getYaw() - 90)) + dZ * Math.cos(Math.toRadians(getYaw()))));
			// Move to next possible position
			//boolean moveAllowed = tryMove(newX, newY, newZ);

			if (!tryMove(newX, newY, newZ)) {// try X + Y + Z movement
				if (!tryMove(newX, newY, origZ)) { // if not allowed try just X + Y movement
					if (!tryMove(origX, newY, newZ)) { //if not allowed try just Z + Y movement
						//if not allowed revert to original position
						setX(origX);
						setY(origY);
						setZ(origZ);
						WorldManager.playerSphereUpper.update(someOldPositionUpper);
						WorldManager.playerSphereLower.update(someOldPositionLower);
					}
				}
				
				System.out.println("Collision!  - STAHPED MOVING");
			}
			// end new working code /|\ \|/ old dead code
			/*
			 * // Check if it collides boolean moveAllowed = true; Vector3f
			 * playerPos = Player.camera.getPos(); for (int x = (int)
			 * playerPos.getX() - 1; x <= (int) playerPos.getX() + 1; x++) { for
			 * (int y = (int) playerPos.getY() - 1; y <= (int) playerPos.getY()
			 * + 1; y++) { for (int z = (int) playerPos.getZ() - 1; z <= (int)
			 * playerPos.getZ() + 1; z++) { String key =
			 * ChunkManager.key(ChunkManager.blockToChunk(x, y, z)); AABB grr =
			 * CollisionLibrary.BlockMap.get(key); // System.out.println(grr);
			 * if
			 * ((CollisionLibrary.testCircleAABB(WorldManager.playerSphereUpper,
			 * grr)) ||
			 * (CollisionLibrary.testCircleAABB(WorldManager.playerSphereLower,
			 * grr))) { moveAllowed = false; break; // System.out.println(x); }
			 * } }
			 * 
			 * } // if it does collide then revert to original position if
			 * (!moveAllowed) { setX(origX); setY(origY); setZ(origZ);
			 * WorldManager.playerSphereUpper.update(someOldPositionUpper);
			 * WorldManager.playerSphereLower.update(someOldPositionLower);
			 * System.out.println("Collision!  - STAHPED MOVING"); }
			 */

		}// End check if no clip
	}

	private boolean tryMove(float newX, float newY, float newZ) {
		setX(newX);
		setY(newY);
		setZ(newZ);
		// Move Player's Invisible Collision object
		Vector3f somePositionUpper = new Vector3f(getX(), getY(), getZ());
		Vector3f somePositionLower = new Vector3f(getX(), getY() - playerHeight, getZ());
		WorldManager.playerSphereUpper.update(somePositionUpper);
		WorldManager.playerSphereLower.update(somePositionLower);
		// begin new working code \|/
		boolean moveAllowed = true;
		Iterator<Entry<String, AABB>> iterator = CollisionLibrary.BlockMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, AABB> entry = iterator.next();
			if ((CollisionLibrary.testSphereAABB(WorldManager.playerSphereUpper, entry.getValue())) || (CollisionLibrary.testSphereAABB(WorldManager.playerSphereLower, entry.getValue()))) {
				moveAllowed = false;
				break;
				// System.out.println(x);
			}
		}
		return moveAllowed;
	}

	private void noClipMove(float dX, float dY, float dZ) {
		setZ((float) (getZ() + (dX * (float) Math.cos(Math.toRadians(getYaw() - 90)) + dZ * Math.cos(Math.toRadians(getYaw())))));

		setX((float) (getX() - (dX * (float) Math.sin(Math.toRadians(getYaw() - 90)) + dZ * Math.sin(Math.toRadians(getYaw())))));
		setY((float) (getY() + (dY * (float) Math.sin(Math.toRadians(getPitch() - 90)) + dZ * Math.sin(Math.toRadians(getPitch())))));
		// Move Player
		Vector3f somePositionUpper = new Vector3f(getX(), getY(), getZ());
		Vector3f somePositionLower = new Vector3f(getX(), getY() - playerHeight, getZ());
		WorldManager.playerSphereUpper.update(somePositionUpper);
		WorldManager.playerSphereLower.update(somePositionLower);
	}

	private void gravity() {
		float origY = getY();
		// change in the y direction,by changing the speed
		float dY = (1 / Constants.FPS) * (Constants.playerSpeed.y - Constants.gravity);

		setY((getY() + dY));
		WorldManager.playerSphereUpper.update(new Vector3f(getX(), getY(), getZ()));
		WorldManager.playerSphereLower.update(new Vector3f(getX(), getY() - playerHeight, getZ()));
		boolean moveAllowed = true;
		Iterator<Entry<String, AABB>> iterator = CollisionLibrary.BlockMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, AABB> entry = iterator.next();
			if ((CollisionLibrary.testSphereAABB(WorldManager.playerSphereUpper, entry.getValue())) || (CollisionLibrary.testSphereAABB(WorldManager.playerSphereLower, entry.getValue()))) {
				moveAllowed = false;
				break;
				// System.out.println(x);
			}
		}
		if (!moveAllowed) {// if not allowed revert to original
			setY(origY);
			WorldManager.playerSphereUpper.update(new Vector3f(getX(), getY(), getZ()));
			WorldManager.playerSphereLower.update(new Vector3f(getX(), getY() - playerHeight, getZ()));
			Constants.jumpEnabled = true;
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

	public void applyPhysics() {
		Vector3f diffPos = new Vector3f((getX() - Constants.playerPrevPos.x), (getY() - Constants.playerPrevPos.y), (getZ() - Constants.playerPrevPos.z));
		diffPos.scale(Constants.FPS);// scale it. delta Pos / time
		Constants.playerSpeed = diffPos;

		// Current are now the previous value for the next iteration
		Constants.playerPrevPos = new Vector3f(getX(), getY(), getZ());
	}

	public void castRay() {
		int x, y, z = 0;
		defineRay();
		// make it a unit vector
		Constants.ray = (Vector3f) Constants.ray.scale((float) Math.pow(Constants.ray.length(), -1));
		// System.out.println(Constants.ray.x); //Just had to comment this out
		// for my own debugging purposes
		// Pick correct block
		for (int i = 0; i < Constants.rayDistance; i++) {
			x = (int) (getX() + (Constants.ray.x * i));
			y = (int) (getX() + (Constants.ray.y * i));
			z = (int) (getX() + (Constants.ray.z * i));
			/*
			 * if(ChunkManager.isCreated(x, y, z)){ }
			 */
		}
		// pick correct face
	}

	public void defineRay() {
		float x = Constants.rayConstant * (float) (Math.cos(Math.toRadians(getPitch())) * Math.sin(Math.toRadians(getYaw())));

		float y = Constants.rayConstant * (float) (Math.cos(Math.toRadians(getPitch() + 90)));

		float z = Constants.rayConstant * (float) (Math.cos(Math.toRadians(getYaw())) * Math.sin(Math.toRadians(getPitch() - 90)));
		Constants.ray = new Vector3f(x, y, z);
	}

}