package com.kyry.voxel;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.io.File;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.world.World;
import com.nishu.utils.GameLoop;
import com.nishu.utils.Screen;
import com.nishu.utils.Window;

/**
 * Voxel Game. 
 * Main game class containing 'main' method
 * Creates gameLoop object to run game
 * @author Justin & Eliel
 * June 17th 2014
 *   */

public class GameStart extends Screen {

	private GameLoop gameLoop;
	private World world;

	public GameStart() {
		Thread.currentThread().setPriority((int) (Thread.MAX_PRIORITY * 0.95));
		/* czech for multithreading */
		gameLoop = new GameLoop();
		gameLoop.setScreen(this);
		gameLoop.start((int) Globals.FPS);

	}

	@Override
	public void init() {
		/* Set up the main camera */
		initCamera();
		/* Instantiate world */
		world = new World();
	}

	@Override
	public void initGL() {
		/* Begin OpenGL preperations, resetting viewport, etc */
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		gluPerspective(67.0f, Globals.WIDTH / Globals.HEIGHT,
				Globals.viewClose, Globals.viewDistance);
		glMatrixMode(GL_MODELVIEW);

		glEnable(GL_DEPTH_TEST);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glEnable(GL_LINE_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

		/* This is for setting what the fog will look like */
		{
			FloatBuffer fogColours = BufferUtils.createFloatBuffer(4);
			fogColours.put(new float[] { Globals.fogColor.r,
					Globals.fogColor.g, Globals.fogColor.b,
					Globals.fogColor.a });
			GL11.glClearColor(Globals.fogColor.r, Globals.fogColor.g,
					Globals.fogColor.b, Globals.fogColor.a);
			fogColours.flip();
			GL11.glFog(GL11.GL_FOG_COLOR, fogColours);
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
			GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_NICEST);
			GL11.glFogf(GL11.GL_FOG_START, Globals.fogNear);
			GL11.glFogf(GL11.GL_FOG_END, Globals.fogFar);
			GL11.glFogf(GL11.GL_FOG_DENSITY, Globals.fogDensity);
			// }
		}
	}

	private void initCamera() {
	}

	@SuppressWarnings("static-access")
	@Override
	public void update() {
		/* Catch when to close the program */
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			dispose();
		}
/*		if (Keyboard.isKeyDown(Keyboard.KEY_F10)) {
			Globals.fullscreen = !Globals.fullscreen;
			Window.dispose();
			try {
				Display.setFullscreen(Globals.fullscreen);
				Window.createWindow(Globals.WIDTH, Globals.HEIGHT, "VoxelGame", true);
				} catch (LWJGLException e) {
				e.printStackTrace();
			}
		}*/
		/* Set the global FPS based on the gameLoop's FPS */
		if (gameLoop.getFPS() > 1) {
			Globals.FPS = gameLoop.getFPS();
		} else {
			/* Account for it being extremely poor (stops values from becoming indefinite in case they are divided by zero) */
			Globals.FPS = 1f;
		}
		/* Update the world */
		world.update();
	}

	@Override
	public void worldRender() {
		/* Clears the OpenGL buffer cache */
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// background
		// glClearColor(0.5f, 0f, 0.75f, 1f);
		/* Render the world */
		world.worldRender();

	}

	@Override
	public void dispose() {
		/*Clean Up */
		world.dispose();
	}
	

/**
 * Main Method!
 * Create the LWJGL window
 * Enable VSync to remove tearing
 * Set natives file path (Helps with loading via .jar file) 
 * Start the game by calling the constructor
 * */
	public static void main(String[] args) {
		Display.setVSyncEnabled(true);
		Window.createWindow(Globals.WIDTH, Globals.HEIGHT, "VoxelGame", true);
		System.setProperty("org.lwjgl.librarypath", new File("lib/natives").getAbsolutePath());
//		Queue<Chunk> toRemove = new ConcurrentLinkedQueue<Chunk>();
//		Queue<Chunk> toAdd = new ConcurrentLinkedQueue<Chunk>();
//		new BackgroundProcessing().start();
        new GameStart();
		
	}
}
