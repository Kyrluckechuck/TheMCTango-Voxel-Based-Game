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

	static WorldManager worldManager;

	public World() {
		/* Initialize */
		initGL();
		init();
	}

	@Override
	public void init() {
		/* Bind the spritesheet to the render buffer */
		Spritesheet.blocks.bind();
		/* Create the tilemap */
		Block.createTileMap();
		/* Create the HUD font */
		Font tempFont = new Font("Arial", Font.BOLD, 16);
		HUD.font = new TrueTypeFont(tempFont, true);
		/* Initialize WorldManager */
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
		/* Get input */
		input();
		/* Update */
		worldManager.update();
		/*Save any chunks that have been modified to disk */
		saveModifiedChunks();

	}

	private void saveModifiedChunks() {
		if (Globals.chunkToSave.size() > 0) {
			/* Creates an executor service for threading */
			ExecutorService executor = Executors.newCachedThreadPool();
			/* Create the key for the chunk which needs to be saved */
			String key = Globals.chunkToSave.get(0);
			/* Get the modified chunk */
			Chunk temp = ChunkManager.activeChunks.get(key);
			/* Submit modified chunk to new executor thread */
			executor.submit(new SavePool(temp, key));
			/* Shut down executor service */
			executor.shutdown();
			System.out.println("Chunk Save Submitted.");
			/* Remove the chunk from the queue */
			Globals.chunkToSave.remove(0);
			System.out.println("Chunk Save Completed.");

		}
	}

	private void input() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				/* Toggle the rendering of the HUD via the F3 Key */
				if (Keyboard.isKeyDown(Keyboard.KEY_F3)) {
					Globals.renderHUDText = !Globals.renderHUDText;
				}
				/* Toggle noClip of player via the F4 Key */
				if (Keyboard.isKeyDown(Keyboard.KEY_F4)) {
					Globals.noClip = !Globals.noClip;
				}
				/* Toggle text colour via the F5 key (Black or White) */
				if (Keyboard.isKeyDown(Keyboard.KEY_F5)) {
					if (Globals.textColor == Color.white) {
						Globals.textColor = Color.black;
					} else {
						Globals.textColor = Color.white;
					}
				}
				/* Toggle the rendering of the Fog via the F8 Key */
				if (Keyboard.isKeyDown(Keyboard.KEY_F8)) {
					Globals.fogEnabled = !Globals.fogEnabled;
				}
				/* Get Keyboard Interactions */ 
				getInteractionKeys();

			}

		}
		/* Get Mouse Interactions */
		getInteractionMouse();

	}

	private void getInteractionKeys() {

		boolean cycleBlockType = Keyboard.isKeyDown(Keyboard.KEY_1);
		/* Cycle through blocks to add */
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
				/* To remove block */
				boolean clickBlockDelete = Mouse.isButtonDown(0);
				/* To add block */
				boolean clickBlockAdd = Mouse.isButtonDown(1);
				
				if (clickBlockAdd && !clickBlockDelete) {
					Mouse.setGrabbed(true);
					if (Globals.blockToAdd != null) {
						/* Place the new block */
						ChunkManager.changeBlock(Globals.blockToAdd, Globals.blockToAddType);
					}

				}
				if (!clickBlockAdd && clickBlockDelete) {
					Mouse.setGrabbed(true);
					if (Globals.selectedBlock != null)
						/* Remove the selected block */
						ChunkManager.changeBlock(Globals.selectedBlock, Block.Air.getId());
				}

			}
		}
	}

	@Override
	public void worldRender() {
		/* Setup 3D matrix rendering environment */
		ready3D();
		/* Perform any logisitical changes */
		gameLogic();
		/* Perform 3D renderings */
		render3D();
		/* Reset 3D rendering matrix environment */
		glLoadIdentity();
		/* Render Heads Up Display (HUD) elements */
		HUD.renderHUD();
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
		/* Perform worldmanager logic */
		worldManager.logic();
	}

	public void ready3D() {
		/* if fogEnabled is true, enable fog rendering, else, disable */
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
		/* Render world (interactable map) */
		worldManager.mapRender();
		/* Setup rendering environment for relative to player conditions */
		readyPlayerRelative3D();
		/* Render Skybox (outside of world, non-interactible) */
		renderSkyBox();

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
		/* Render the skybox */
		Skybox.skyRender();
	}

	@Override
	public void dispose() {
		Display.destroy();
		System.exit(0);
	}

}

class SavePool implements Runnable {
	/* Allow the object to store the chunk */
	private Chunk data;
	private String id;
	
    /* Set the passed chunk as the Object's chunk */
	public SavePool(Chunk data, String id) {
		this.id = id;
		this.data = data;
	}

	public void run() {
		try {
			/* Get chunk's world (X, Y) */
			Vector2f pos = data.pos;
			/* Set file path */
			String path = ChunkManager.filePath((int) pos.getX(), (int) pos.getY());
			File dir = new File(path);
			/* Remove the file */
			dir.delete();
			/* Save chunk with modifications */
			ChunkManager.saveChunk(pos.getX(), pos.getY(), data.blocks);
		} catch (Exception e) {
		}
		System.out.println("Chunk: " + id + " Should Theoretically Have Updated Successfully.");
	}
}
