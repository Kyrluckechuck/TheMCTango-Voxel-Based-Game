package com.kyry.voxel.utilities.testing;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.GL11.*;

import com.kyry.voxel.utilities.SimplexNoise;

public class RandomTerrain2D {

	private int size = 4; // tile size in pixels

	private int width = 256, length = 256; // map dimensions
	private int[] tiles; // tile array

	public RandomTerrain2D() {

		createWindow();

		makeTiles();

		loop();

	}

	private void makeTiles() {

		tiles = new int[width * length];

		SimplexNoise noise = new SimplexNoise();

		for (int i = 0; i < tiles.length; i++) {
			int x = i % width; // what are the coordinates from i ?
			int y = i / width;

			float frequency =  (float) 128; // change this and see what happens! :D

			float h = (float) noise.noise((float) x / frequency, (float) y / frequency);
			h *= 64;
			/*
			 * if (h >= -127 && h < -50) tiles[x + y * width] = 1; // dirt tile
			 * else if (h >= -50 && h < 0) tiles[x + y * width] = 2; // grass
			 * tile else if (h >= 0 && h < 50) tiles[x + y * width] = 3; //
			 * grass tile else if (h >= 50 && h <= 127) tiles[x + y * width] =
			 * 4; // grass tile
			 */
			if (h >= -127 && h < -100)
				tiles[x + y * width] = 1; // hole tile
			else if (h >= -100 && h < -75)
				tiles[x + y * width] = 2; // grass tile
			else if (h >= -75 && h < -50)
				tiles[x + y * width] = 3; // grass tile
			else if (h >= -50 && h < -25)
				tiles[x + y * width] = 4; // grass tile
			else if (h >= -25 && h < 0)
				tiles[x + y * width] = 5; // ground tile
			else if (h >= 0 && h < 25)
				tiles[x + y * width] = 6; // ground tile
			else if (h >= 25 && h < 50)
				tiles[x + y * width] = 7; // grass tile
			else if (h >= 50 && h < 75)
				tiles[x + y * width] = 8; // grass tile
			else if (h >= 75 && h < 100)
				tiles[x + y * width] = 9; // grass tile
			else if (h >= 100 && h <= 127)
				tiles[x + y * width] = 10; // mountain tile
		}

	}

	private void loop() {
		while (!Display.isCloseRequested()) {

			tick();

			render();

			Display.update();

			Display.sync(60);
		}
	}

	private void render() {

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		for (int y = 0; y < length; y++) {
			for (int x = 0; x < width; x++) {

				if (tiles[x + y * width] == 1) {
					render(x * size, y * size, size, size, 0.2f, 0.2f, 1.0f); // render
																				// dirt
																				// tile
				} else if (tiles[x + y * width] == 2) {
					render(x * size, y * size, size, size, 0.15f, 0.325f, 0.4f); // render
																				// grass
																				// tile
				} else if (tiles[x + y * width] == 3) {
					render(x * size, y * size, size, size, 0.1f, 0.45f, 0.6f); // render
					// grass
					// tile
				} else if (tiles[x + y * width] == 4) {
					render(x * size, y * size, size, size, 0.05f, 0.575f, 0.2f); // render
					// grass
					// tile
				} else if (tiles[x + y * width] == 5) {
					render(x * size, y * size, size, size, 0.0f, 0.7f, 0.0f); // render
					// grass
					// tile
				} else if (tiles[x + y * width] == 6) {
					render(x * size, y * size, size, size, 0.15f, 0.6f, 0.0f); // render
					// grass
					// tile
				} else if (tiles[x + y * width] == 7) {
					render(x * size, y * size, size, size, 0.25f, 0.5f, 0.0f); // render
					// grass
					// tile
				} else if (tiles[x + y * width] == 8) {
					render(x * size, y * size, size, size, 0.4f, 0.4f, 0.0f); // render
					// grass
					// tile
				}else if (tiles[x + y * width] == 9) {
					render(x * size, y * size, size, size, 0.5f, 0.35f, 0.0f); // render
					// grass
					// tile
				}else if (tiles[x + y * width] == 10) {
					render(x * size, y * size, size, size, 0.6f, 0.3f, 0.0f); // render
					// grass
					// tile
				}
				

			}
		}

	}

	private boolean wasDown = false;

	private void tick() {

		boolean down = Keyboard.isKeyDown(Keyboard.KEY_R);

		if (!wasDown && down) {
			makeTiles();
		}

		wasDown = down;

	}

	private void render(int x, int y, int width, int height, float r, float g, float b) {

		GL11.glBegin(GL11.GL_QUADS);

		GL11.glColor3f(r, g, b);

		GL11.glVertex2f(x, y);
		GL11.glVertex2f(x + width, y);
		GL11.glVertex2f(x + width, y + height);
		GL11.glVertex2f(x, y + height);

		GL11.glEnd();

	}

	private void createWindow() {

		try {
			Display.setDisplayMode(new DisplayMode(1024, 768));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

	}

	public static void main(String args[]) {
		new RandomTerrain2D();
	}

}