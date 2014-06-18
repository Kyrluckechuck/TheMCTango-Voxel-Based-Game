package com.kyry.voxel.world.entities;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_TRANSFORM_BIT;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.kyry.voxel.geometry.AABB;
//import com.bulletphysics.linearmath.Transform;
import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.world.WorldManager;
import com.kyry.voxel.world.chunks.ChunkManager;
import com.kyry.voxel.world.physics.CollisionLibrary;

public class Camera extends Entity {

	private float speed, maxU, maxD;
	private final float playerHeight = Globals.playerHeight;

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
	}

	public void updateKeyboard(float deltaTime, float speed) {
		boolean keyForward = Keyboard.isKeyDown(Keyboard.KEY_W);
		boolean keyBackward = Keyboard.isKeyDown(Keyboard.KEY_S);
		boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_A);
		boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_D);
		boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);

		float deltaDis = deltaTime * speed;

		Vector3f somePosition = new Vector3f(camera.getX(), camera.getY(), camera.getZ());
		Vector3f someOtherPosition = new Vector3f(camera.getX(), camera.getY() - playerHeight, camera.getZ());
		WorldManager.playerSphereUpper.update(somePosition);
		WorldManager.playerSphereLower.update(someOtherPosition);
		/* If forward and right */
		if (keyForward && keyRight && !keyLeft && !keyBackward) {// NE
			playerMove(deltaDis, 0, -deltaDis);
		}
		/* If forward and left */
		if (keyForward && keyLeft && !keyRight && !keyBackward) {// NW
			playerMove(-deltaDis, 0, -deltaDis);
		}
		/* If just forward */
		if (keyForward && !keyLeft && !keyRight && !keyBackward) {// N
			playerMove(0, 0, -deltaDis);
		}
		/* If backward and left */
		if (keyBackward && keyLeft && !keyRight && !keyForward) {// SW
			playerMove(-deltaDis, 0, deltaDis);
		}
		/* If backward and right */
		if (keyBackward && keyRight && !keyLeft && !keyForward) {// SE
			playerMove(deltaDis, 0, deltaDis);
		}
		/* If just backward */
		if (keyBackward && !keyForward && !keyLeft && !keyRight) {// S
			playerMove(0, 0, deltaDis);
		}
		/* If just left */
		if (keyLeft && !keyRight && !keyForward && !keyBackward) {// W
			playerMove(-deltaDis, 0, 0);
		}
		/* If just right */
		if (keyRight && !keyLeft && !keyForward && !keyBackward) {// E
			playerMove(deltaDis, 0, 0);
		}
		/* If just up */
		if (keyUp && !keyDown) {// JUMP (Space)
			jump(deltaDis);
		}
		/* If just down */
		if (keyDown && !keyUp) {// DOWN (Shift)
			playerMove(0, deltaDis, 0);
		}
		/* If noClip is disabled, enable gravity */
		if (!Globals.noClip)
			gravity();
	}

	public Vector3f getPosition() {
		return new Vector3f(getX(), getY(), getZ());
	}

	private void jump(float deltaDis) {
		/* If player can jump then jump */
		if (Globals.jumpEnabled) {
			playerMove(0, -deltaDis * Globals.jumpPower, 0);
			Globals.jumpCounter = 0;
		} else if (!Globals.jumpEnabled && Globals.jumpCounter < Globals.jumpFrames) {
			playerMove(0, -deltaDis * Globals.jumpPower, 0);
			Globals.jumpCounter++;
		}
		/* If noClip is enabled, disable jumping */
		if (!Globals.noClip) {
			Globals.jumpEnabled = false;
		} else {
			Globals.jumpEnabled = true;
		}
	}

	public void playerMove(float dX, float dY, float dZ) {
		/* Move Camera */
		Vector3f someOldPositionUpper = new Vector3f(getX(), getY(), getZ());
		Vector3f someOldPositionLower = new Vector3f(getX(), getY() - playerHeight, getZ());
		/* If noClip enabled, do special move */
		if (Globals.noClip) {
			noClipMove(dX * Globals.PLAYER_SPEED, dY * Globals.PLAYER_SPEED, dZ * Globals.PLAYER_SPEED);
		} else if (!Globals.noClip) {
			/*Do normal moving */
			float origX = getX();
			float origY = getY();
			float origZ = getZ();

			float newX = (float) (getX() - (dX * (float) Math.sin(Math.toRadians(getYaw() - 90)) + dZ * Math.sin(Math.toRadians(getYaw()))));
			float newY = (float) (getY() - dY);
			float newZ = (float) (getZ() + (dX * (float) Math.cos(Math.toRadians(getYaw() - 90)) + dZ * Math.cos(Math.toRadians(getYaw()))));
			/* Move to next possible position */
			/* Try X + Y + Z movement */
			if (!tryMove(newX, newY, newZ)) {
				/* If not allowed try just X + Y movement */
				if (!tryMove(newX, newY, origZ)) {
					/*  If not allowed try just Z + Y movement */
					if (!tryMove(origX, newY, newZ)) {
						/* If not allowed revert to original position */
						setX(origX);
						setY(origY);
						setZ(origZ);
						WorldManager.playerSphereUpper.update(someOldPositionUpper);
						WorldManager.playerSphereLower.update(someOldPositionLower);
					}
				}

				System.out.println("Collision!  - STAHPED MOVING");
			}
		}// End check if no clip
	}

	private boolean tryMove(float newX, float newY, float newZ) {
		setX(newX);
		setY(newY);
		setZ(newZ);
		/* Move Player's Invisible Collision object */
		Vector3f somePositionUpper = new Vector3f(getX(), getY(), getZ());
		Vector3f somePositionLower = new Vector3f(getX(), getY() - playerHeight, getZ());
		WorldManager.playerSphereUpper.update(somePositionUpper);
		WorldManager.playerSphereLower.update(somePositionLower);
		boolean moveAllowed = true;
		/* Cycle through possible collision blocks */
		Iterator<Entry<String, AABB>> iterator = CollisionLibrary.BlockMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, AABB> entry = iterator.next();
			if ((CollisionLibrary.testSphereAABB(WorldManager.playerSphereUpper, entry.getValue())) || (CollisionLibrary.testSphereAABB(WorldManager.playerSphereLower, entry.getValue()))) {
				moveAllowed = false;
				break;
			}
		}
		return moveAllowed;
	}

	private void noClipMove(float dX, float dY, float dZ) {
		setZ((float) (getZ() + (dX * (float) Math.cos(Math.toRadians(getYaw() - 90)) + dZ * Math.cos(Math.toRadians(getYaw())))));

		setX((float) (getX() - (dX * (float) Math.sin(Math.toRadians(getYaw() - 90)) + dZ * Math.sin(Math.toRadians(getYaw())))));
		setY((float) (getY() + (dY * (float) Math.sin(Math.toRadians(getPitch() - 90)) + dZ * Math.sin(Math.toRadians(getPitch())))));
		/* Move Player */
		Vector3f somePositionUpper = new Vector3f(getX(), getY(), getZ());
		Vector3f somePositionLower = new Vector3f(getX(), getY() - playerHeight, getZ());
		WorldManager.playerSphereUpper.update(somePositionUpper);
		WorldManager.playerSphereLower.update(somePositionLower);
	}

	private void gravity() {
		float origY = getY();
		/* Change in the Y position, by changing the speed */
		float dY = (1 / Globals.FPS) * (Globals.playerSpeed.getY() - Globals.gravity);

		if (Globals.playerSpeed.getY() >= Globals.maxSpeed) {
			dY = (1 / Globals.FPS) * (Globals.maxSpeed - Globals.gravity);
		} else if(Globals.playerSpeed.getY() <= (-1*Globals.maxSpeed)) {
				dY = (1 / Globals.FPS) * ((-1*Globals.maxSpeed) - Globals.gravity);
		}
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
			}
		}
		/* If not allowed revert to original */
		if (!moveAllowed) {
			setY(origY);
			WorldManager.playerSphereUpper.update(new Vector3f(getX(), getY(), getZ()));
			WorldManager.playerSphereLower.update(new Vector3f(getX(), getY() - playerHeight, getZ()));
			Globals.jumpEnabled = true;
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
		Vector3f diffPos = new Vector3f((getX() - Globals.playerPrevPos.x), (getY() - Globals.playerPrevPos.y), (getZ() - Globals.playerPrevPos.z));
		/* scale it. delta Pos / time */
		diffPos.scale(Globals.FPS);
		Globals.playerSpeed = diffPos;
		/* Current are now the previous value for the next iteration */
		Globals.playerPrevPos = new Vector3f(getX(), getY(), getZ());
	}

	/* Basically see if ray collides within given range, if so, give block and possible face to place new block */
	public void castRay() {
		int x, y, z = 0;
		defineRay();
		/* Make it a unit vector */
		Globals.ray = (Vector3f) Globals.ray.scale((float) Math.pow(Globals.ray.length(), -1));
		/* Pick correct block */

		for (int i = 0; i < (Globals.rayDistance * 4); i++) {
			x = (int) (getX() + (Globals.ray.x * i * 0.25));
			y = (int) (getY() + (Globals.ray.y * i * 0.25));
			z = (int) (getZ() + (Globals.ray.z * i * 0.25));

			if (x < 0)
				x--;
			if (z < 0)
				z--;

			int chunkX = ChunkManager.blockToChunk1f(x);
			int chunkZ = ChunkManager.blockToChunk1f(z);

			int internX = x - (chunkX * Globals.CHUNKSIZE);
			int internY = y;
			int internZ = z - (chunkZ * Globals.CHUNKSIZE);
			try {
				/* Make sure it's within bounds */
				if ((y < Globals.WORLDHEIGHT) && y >= 1) {
					/* Make sure it's not air */
					if (ChunkManager.loadedChunks.get(ChunkManager.key(chunkX, chunkZ)).blocks[internX][internY][internZ] > 0) {
						
						Globals.selectedBlock = new Vector3f(x, y, z);

						for (int q = 1; q < 500; q++) {
							float takeDist = (float) (q * 0.001);
							int faceX = (int) (getX() + (Globals.ray.x * (i * 0.25 - takeDist)));
							int faceY = (int) (getY() + (Globals.ray.y * (i * 0.25 - takeDist)));
							int faceZ = (int) (getZ() + (Globals.ray.z * (i * 0.25 - takeDist)));

							if (faceX < 0)
								faceX--;
							if (faceZ < 0)
								faceZ--;

							int actualFaceX = faceX;
							int actualFaceY = faceY;
							int actualFaceZ = faceZ;

							int faceChunkX = ChunkManager.blockToChunk1f(faceX);
							int faceChunkZ = ChunkManager.blockToChunk1f(faceZ);
							faceX = faceX - faceChunkX * Globals.CHUNKSIZE;
							faceZ = faceZ - faceChunkZ * Globals.CHUNKSIZE;

							if ((ChunkManager.loadedChunks.get(ChunkManager.key(faceChunkX, faceChunkZ)).blocks[faceX][faceY][faceZ] == 0)
									&& (new Vector3f((int) WorldManager.playerSphereLower.getX(), (int) WorldManager.playerSphereLower.getY(), (int) WorldManager.playerSphereLower.getZ()) != new Vector3f((int) x, (int) y, (int) z))
									&& (new Vector3f((int) WorldManager.playerSphereUpper.getX(), (int) WorldManager.playerSphereUpper.getY(), (int) WorldManager.playerSphereUpper.getZ()) != new Vector3f((int) x, (int) y, (int) z))) {
								Globals.blockToAdd = new Vector3f(actualFaceX, actualFaceY, actualFaceZ);
								break;
							}
						}
						break;
					} else {
						Globals.selectedBlock = null;
						Globals.blockToAdd = null;
					}
				}
			} catch (NullPointerException e) {
				System.out.println("Null Pointer");
			}
		}
		// pick correct face
	}

	public void defineRay() {
		float x = Globals.rayConstant * (float) (Math.cos(Math.toRadians(getPitch())) * Math.sin(Math.toRadians(getYaw())));

		float y = Globals.rayConstant * (float) (Math.cos(Math.toRadians(getPitch() + 90)));

		float z = Globals.rayConstant * (float) (Math.cos(Math.toRadians(getYaw())) * Math.sin(Math.toRadians(getPitch() - 90)));
		Globals.ray = new Vector3f(x, y, z);
	}

}