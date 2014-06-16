package com.kyry.voxel.world;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FOG;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_MODULATE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV_MODE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glTexEnvi;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.awt.Font;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

//import com.bulletphysics.linearmath.Transform;
import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.utilities.Spritesheet;
import com.kyry.voxel.world.blocks.Block;
import com.kyry.voxel.world.chunks.Chunk;
import com.kyry.voxel.world.chunks.ChunkManager;
import com.nishu.utils.Screen;

public class World extends Screen {

	// public static final int AIRCHUNK = 0, MIXEDCHUNK = 1;

	static WorldManager worldManager;

	public World() {
		initGL();
		init();
	}

	@Override
	public void init() {

		Spritesheet.blocks.bind();
		Block.createTileMap();

		Font tempFont = new Font("Arial", Font.BOLD, 16);
		HUD.font = new TrueTypeFont(tempFont, true);

		worldManager = new WorldManager();

	}

	@Override
	public void initGL() {
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
		glEnable(GL_CULL_FACE);
		glEnable(GL_TEXTURE_2D);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_LIGHTING);
	}

	@Override
	public void update() {
		input();
		worldManager.update();
		saveModifiedChunks();

	}

	private void saveModifiedChunks() {
		if (Globals.chunkToSave.size() > 0) {
			ExecutorService executor = Executors.newCachedThreadPool();// Executors.newFixedThreadPool(2);//2
																		// Threads
			String key = Globals.chunkToSave.get(0);
			Chunk temp = ChunkManager.activeChunks.get(key);
			executor.submit(new SavePool(temp, key));
			executor.shutdown();
			System.out.println("Chunk Save Submitted.");
/*			try {
				executor.awaitTermination(1, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
			}*/
			Globals.chunkToSave.remove(0);
			System.out.println("Chunk Save Completed.");

		}
	}

	private void input() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.isKeyDown(Keyboard.KEY_F3)) {
					Globals.renderHUDText = !Globals.renderHUDText;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_F4)) {
					if (Globals.noClip)
						Globals.PLAYER_SPEED -= 15f;
					else
						Globals.PLAYER_SPEED += 15f;
					Globals.noClip = !Globals.noClip;
				}
				// KEY_F5 toggles textColor between Black and White
				if (Keyboard.isKeyDown(Keyboard.KEY_F5)) {
					if (Globals.textColor == Color.white) {
						Globals.textColor = Color.black;
					} else {
						Globals.textColor = Color.white;
					}
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_F8)) {
					Globals.fogEnabled = !Globals.fogEnabled;
				}
				getInteractionKeys();

			}

		}
		getInteractionMouse();

	}

	private void getInteractionKeys() {

		boolean cycleBlockType = Keyboard.isKeyDown(Keyboard.KEY_1);

		if (cycleBlockType) {
			if (Globals.blockToAddType < 8)
				Globals.blockToAddType++;
			else
				Globals.blockToAddType = 2;
		}
	}

	public void getInteractionMouse() {

		while (Mouse.next()) {
			if (Mouse.getEventButtonState()) {
				boolean clickBlockDelete = Mouse.isButtonDown(0); // to remove
																	// block
				boolean clickBlockAdd = Mouse.isButtonDown(1); // to add block
				if (clickBlockAdd && !clickBlockDelete) {
					Mouse.setGrabbed(true);
					if (Globals.blockToAdd != null) {
						ChunkManager.changeBlock(Globals.blockToAdd, Globals.blockToAddType);
					}

				}
				if (!clickBlockAdd && clickBlockDelete) {
					Mouse.setGrabbed(true);
					if (Globals.selectedBlock != null)
						ChunkManager.changeBlock(Globals.selectedBlock, Block.Air.getId());
				}

			}
		}
	}

	@Override
	public void worldRender() {
		ready3D(); // Setup 3D matrix rendering environment
		gameLogic(); // Perform any logisitical changes
		render3D();
		// renderSkyBox();

		glLoadIdentity(); // Reset 3D rendering matrix environment

		/*
		 * if (renderText) { ready2D(); // Setup 2D matrix rendering environment
		 * HUD.renderHUD(); //Render ALL Heads Up Display Elements }
		 */

		// ready2D(); // Setup 2D matrix rendering environment
		HUD.renderHUD(); // Render ALL Heads Up Display Elements

	}

	public static void ready2D() {
		glCullFace(GL_BACK);
		glClearDepth(1);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		glOrtho(0, Globals.WIDTH, Globals.HEIGHT, 0, -1, 1);
		glViewport(0, 0, Globals.WIDTH, Globals.HEIGHT);
		glMatrixMode(GL_MODELVIEW);
	}

	private void gameLogic() {
		worldManager.logic();
	}

	public void ready3D() {
		if (Globals.fogEnabled) {
			glEnable(GL_FOG);
		} else {
			glDisable(GL_FOG);
		}
		glCullFace(GL_FRONT);
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		gluPerspective(67.0f, Globals.WIDTH / (float) Globals.HEIGHT, Globals.viewClose, Globals.viewDistance);
		glMatrixMode(GL_MODELVIEW);

		glEnable(GL_DEPTH_TEST);
	}

	private void render3D() {
		worldManager.mapRender(); // Render world (interactable map)
		readyPlayerRelative3D();
		renderSkyBox(); // Render Skybox (outside of world, non-interactible)

	}

	public void readyPlayerRelative3D() {
		glCullFace(GL_BACK);
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		gluPerspective(67.0f, Globals.WIDTH / (float) Globals.HEIGHT, Globals.viewClose, Globals.viewDistance);
		glMatrixMode(GL_MODELVIEW);

		glEnable(GL_DEPTH_TEST);
	}

	public void renderSkyBox() {

		Skybox.skyRender();
	}

	@Override
	public void dispose() {
		Display.destroy();
		System.exit(0);
	}

}

class SavePool implements Runnable {

	private Chunk data;
	private String id;

	public SavePool(Chunk data, String id) {
		this.id = id;
		this.data = data;
	}

	public void run() {
		try {
			// Save edited File (delete, save new)
			// Files.delete(path);
			Vector2f pos = data.pos;
			String path = ChunkManager.filePath((int) pos.getX(), (int) pos.getY());
			File dir = new File(path);
			dir.delete();
			ChunkManager.saveChunk(pos.getX(), pos.getY(), data.blocks);
		} catch (Exception e) {
		}
		System.out.println("Chunk: " + id + " Should Theoretically Have Updated Successfully.");
	}
}
