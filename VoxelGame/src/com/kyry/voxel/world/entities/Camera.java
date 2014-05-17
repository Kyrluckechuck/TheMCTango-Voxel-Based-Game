package com.kyry.voxel.world.entities;

import javax.vecmath.Vector3f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.bulletphysics.linearmath.Transform;
import com.kyry.voxel.world.chunks.Chunk;
import com.kyry.voxel.world.entities.mobs.Player;
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
	}

	public void updateKeyboard(float delay, float speed) {
		boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_W);
		boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_S);
		boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_A);
		boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_D);
		boolean space = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);

		if (keyUp && keyRight && !keyLeft && !keyDown) {
			move(speed * delay * (float) Time.getDelta(), 0, -speed * delay
					* (float) Time.getDelta());
		}
		if (keyUp && keyLeft && !keyRight && !keyDown) {
			move(-speed * delay * (float) Time.getDelta(), 0, -speed * delay
					* (float) Time.getDelta());
		}
		if (keyUp && !keyLeft && !keyRight && !keyDown) {
			move(0, 0, -speed * delay * (float) Time.getDelta());
		}
		if (keyDown && keyLeft && !keyRight && !keyUp) {
			move(-speed * delay * (float) Time.getDelta(), 0, speed * delay
					* (float) Time.getDelta());
		}
		if (keyDown && keyRight && !keyLeft && !keyUp) {
			move(speed * delay * (float) Time.getDelta(), 0, speed * delay
					* (float) Time.getDelta());
		}
		if (keyDown && !keyUp && !keyLeft && !keyRight) {
			move(0, 0, speed * delay * (float) Time.getDelta());
		}
		if (keyLeft && !keyRight && !keyUp && !keyDown) {
			move(-speed * delay * (float) Time.getDelta(), 0, 0);
		}
		if (keyRight && !keyLeft && !keyUp && !keyDown) {
			move(speed * delay * (float) Time.getDelta(), 0, 0);
		}
		if (space && !shift) {
			setY(getY() + speed * delay * (float) Time.getDelta());
		}
		if (shift && !space) {// && Chunk.tiles[(int) getCamera().getX()][(int)
								// (getCamera().getY() -1) ][(int)
								// getCamera().getZ()] == Tile.Air.getId()) {
			// System.out.println("X: " + (int)Player.camera.getX() +" Y: " +
			// (int) Player.camera.getY() + " Z: "+(int) Player.camera.getZ());
			/*if (Chunk.tiles[(int) Player.camera.getX()][(int) Player.camera.getY() - 4][(int) Player.camera.getZ()] != 0) {
				System.out.println("Missing Tile: "
						+ Chunk.tiles[(int) Player.camera.getX()][(int) Player.camera.getY()][(int) Player.camera.getZ()]);
				// System.out.println();
			}*/
			setY(getY() - speed * delay * (float) Time.getDelta());
		}
	}

	public Vector3f getPosition(){
		return new Vector3f(getX(), getY(), getZ());
	}
	
	public void move(float x, float y, float z) {
		setZ((float) (getZ() + (x
				* (float) Math.cos(Math.toRadians(getYaw() - 90)) + z
				* Math.cos(Math.toRadians(getYaw())))));
		setX((float) (getX() - (x
				* (float) Math.sin(Math.toRadians(getYaw() - 90)) + z
				* Math.sin(Math.toRadians(getYaw())))));
		setY((float) (getY() + (y
				* (float) Math.sin(Math.toRadians(getPitch() - 90)) + z
				* Math.sin(Math.toRadians(getPitch())))));
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
