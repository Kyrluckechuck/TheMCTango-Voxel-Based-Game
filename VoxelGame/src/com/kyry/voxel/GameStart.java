package com.kyry.voxel;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.nio.FloatBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.world.World;
import com.kyry.voxel.world.chunks.Chunk;
import com.nishu.utils.GameLoop;
import com.nishu.utils.Screen;
import com.nishu.utils.Window;

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
		initCamera();

		world = new World();
	}

	@Override
	public void initGL() {
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

		/*
		 * if (Globals.fogEnabled) { GL11.glEnable(GL11.GL_FOG);
		 */
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
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			dispose();
		}
		if (gameLoop.getFPS() > 1) {
			Globals.FPS = gameLoop.getFPS();
		} else {
			Globals.FPS = 1f;
		}
		world.update();
	}

	@Override
	public void worldRender() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// background
		// glClearColor(0.5f, 0f, 0.75f, 1f);

		world.worldRender();

	}

	@Override
	public void dispose() {
		world.dispose();
	}
	


	public static void main(String[] args) {
		Window.createWindow(Globals.WIDTH, Globals.HEIGHT, "Voxels", true);
		Display.setVSyncEnabled(true);
//		Queue<Chunk> toRemove = new ConcurrentLinkedQueue<Chunk>();
//		Queue<Chunk> toAdd = new ConcurrentLinkedQueue<Chunk>();
//		new BackgroundProcessing().start();
		new GameStart();
		
		
	}
}
