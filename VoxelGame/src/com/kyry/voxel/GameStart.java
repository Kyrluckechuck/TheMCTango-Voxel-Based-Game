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

import com.kyry.voxel.utilities.Constants;
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
		gameLoop.start((int) Constants.FPS);

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

		gluPerspective(67.0f, Constants.WIDTH / Constants.HEIGHT,
				Constants.viewClose, Constants.viewDistance);
		glMatrixMode(GL_MODELVIEW);

		glEnable(GL_DEPTH_TEST);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glEnable(GL_LINE_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

		/*
		 * if (Constants.fogEnabled) { GL11.glEnable(GL11.GL_FOG);
		 */
		{
			FloatBuffer fogColours = BufferUtils.createFloatBuffer(4);
			fogColours.put(new float[] { Constants.fogColor.r,
					Constants.fogColor.g, Constants.fogColor.b,
					Constants.fogColor.a });
			GL11.glClearColor(Constants.fogColor.r, Constants.fogColor.g,
					Constants.fogColor.b, Constants.fogColor.a);
			fogColours.flip();
			GL11.glFog(GL11.GL_FOG_COLOR, fogColours);
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
			GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_NICEST);
			GL11.glFogf(GL11.GL_FOG_START, Constants.fogNear);
			GL11.glFogf(GL11.GL_FOG_END, Constants.fogFar);
			GL11.glFogf(GL11.GL_FOG_DENSITY, Constants.fogDensity);
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
			Constants.FPS = gameLoop.getFPS();
		} else {
			Constants.FPS = 1f;
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
		Window.createWindow(Constants.WIDTH, Constants.HEIGHT, "Voxels", true);
		Display.setVSyncEnabled(true);
//		Queue<Chunk> toRemove = new ConcurrentLinkedQueue<Chunk>();
//		Queue<Chunk> toAdd = new ConcurrentLinkedQueue<Chunk>();
//		new BackgroundProcessing().start();
		new GameStart();
		
		
	}
}
