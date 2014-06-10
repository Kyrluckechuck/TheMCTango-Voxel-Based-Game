package com.kyry.voxel.utilities.testing;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.GL11.*;

import com.kyry.voxel.utilities.SimplexNoise;

public class RandomTerrain3D {

	private int size = 4; // tile size in pixels

	private int width = 256, height = 256, length = 256; // map dimensions
	private int[] tiles; // tile array

	public RandomTerrain3D() {

		createWindow();

		makeTiles();

		loop();

	}

	private void makeTiles() {

		tiles = new int[width * height * length];

		SimplexNoise noise = new SimplexNoise();

		for (int i = 0; i < tiles.length; i++) {
			int x = i % width; // what are the coordinates from i ?
			int y = i / width;
			int z = i / height;

			int frequency = 128; // change this and see what happens! :D

			float h = (float) noise.noise((float) x / frequency, (float) y / frequency, (float) z / frequency);

			h *= 127;

			if (h >= -127 && h < -100)
				tiles[x + y + z] = 1; // dirt tile
			else if (h >= -100 && h < -75)
				tiles[x + y + z] = 2; // grass tile
			else if (h >= -75 && h < -50)
				tiles[x + y + z] = 3; // grass tile
			else if (h >= -50 && h < -25)
				tiles[x + y + z] = 4; // grass tile
			else if (h >= -25 && h < 0)
				tiles[x + y + z] = 5; // grass tile
			else if (h >= 0 && h < 25)
				tiles[x + y + z] = 6; // grass tile
			else if (h >= 25 && h < 50)
				tiles[x + y + z] = 7; // grass tile
			else if (h >= 50 && h < 75)
				tiles[x + y + z] = 8; // grass tile
			else if (h >= 75 && h < 100)
				tiles[x + y + z] = 9; // grass tile
			else if (h >= 100 && h <= 127)
				tiles[x + y + z] = 10; // grass tile

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

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				for (int z = 0; z < length; z++) {

					if (tiles[x + y + z] == 1) {
						render(x * size, y * size, z * size, size, size, size, 0.6f, 0.2f, 0.1f); // render
						// dirt
						// tile
					} else if (tiles[x + y + z] == 2) {
						render(x * size, y * size, z * size, size, size, size, 0.2f, 0.8f, 0.2f); // render
						// grass
						// tile
					} else if (tiles[x + y + z] == 3) {
						render(x * size, y * size, z * size, size, size, size, 0.2f, 0.8f, 0.2f); // render
						// grass
						// tile
					} else if (tiles[x + y + z] == 4) {
						render(x * size, y * size, z * size, size, size, size, 0.2f, 0.8f, 0.2f); // render
						// grass
						// tile
					}

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

	private void render(int x, int y, int z, int width, int height, int length, float r, float g, float b) {

		GL11.glBegin(GL11.GL_QUADS);

		GL11.glColor3f(r, g, b);

		for (int q = x; q < q + width; q += width) {
			for (int w = y; w < w + width; w += height) {
				for (int e = z; e < e + width; e += length) {
					GL11.glVertex3f(q, w, e);
				}
			}
		}
		System.out.println("made");
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
		new RandomTerrain3D();
	}

}