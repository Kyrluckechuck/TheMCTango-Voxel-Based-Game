package com.kyry.voxel.world;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.awt.Font;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;

import com.nishu.utils.GameLoop;
import com.nishu.utils.Screen;
//import com.bulletphysics.linearmath.Transform;
import com.kyry.voxel.utilities.Constants;
import com.kyry.voxel.utilities.Spritesheet;
import com.kyry.voxel.world.blocks.Block;
import com.kyry.voxel.world.chunks.ChunkManager;

public class World extends Screen {

	// public static final int AIRCHUNK = 0, MIXEDCHUNK = 1;

	private WorldManager worldManager;
	private TrueTypeFont font;

	public static boolean noClip = true;
	private boolean renderText = true;

	public World() {
		initGL();
		init();
	}

	@Override
	public void init() {

		Spritesheet.tiles.bind();
		Block.createTileMap();

		Font tempFont = new Font("Arial", Font.PLAIN, 16);
		font = new TrueTypeFont(tempFont, true);

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
	}

	private void input() {
		if (Mouse.isButtonDown(0)) {
			Mouse.setGrabbed(true);
		}
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.isKeyDown(Keyboard.KEY_F3)) {
					renderText = !renderText;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_F4)) {
					if (noClip)
						Constants.PLAYER_SPEED -= 15f;
					else
						Constants.PLAYER_SPEED += 15f;
					noClip = !noClip;
				}
				// KEY_F5 toggles textColor between Black and White
				if (Keyboard.isKeyDown(Keyboard.KEY_F5)) {
					if (Constants.textColor == Color.white) {
						Constants.textColor = Color.black;
					} else {
						Constants.textColor = Color.white;
					}
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_F8)) {
					Constants.fogEnabled = !Constants.fogEnabled;
				}
				getInteractionKeys();

			}

		}

	}
	private void getInteractionKeys(){
		boolean clickBlockDelete = Mouse.isButtonDown(0); // to remove block
		boolean clickBlockAdd = Mouse.isButtonDown(1); // to add block
		boolean cycleBlockType = Keyboard.isKeyDown(Keyboard.KEY_1); 
/*		if (clickBlockAdd && !clickBlockDelete) {
			if (Constants.blockToAdd != null) {

				ChunkManager.changeBlock(Constants.selectedBlockType);
			}

		}
		if (!clickBlockAdd && clickBlockDelete) {
			if (Constants.selectedBlock != null)
				ChunkManager.changeBlock(Block.Air.getId());
		}*/
		if (cycleBlockType) {
			if (Constants.selectedBlockType <= 9)
				Constants.selectedBlockType++;
			else
				Constants.selectedBlockType = 0;
		}
	}

	@Override
	public void worldRender() {
		render3D(); // Setup 3D matrix rendering environment
		gameLogic(); // Perform any logisitical changes
		mapRender(); // Render world (interactable map)
		renderSkyBox(); // Render Skybox (outside of world, non-interactible)

		glLoadIdentity(); // Reset 3D rendering matrix environment

		if (renderText) {
			render2D(); // Setup 2D matrix rendering environment
			renderText(); // Render 2D text to screen
		}

	}

	private void gameLogic() {
		worldManager.logic();
	}

	private void mapRender() {
		worldManager.render();
	}

	/*
	 * private void skyBoxRender() { worldManager.skyBoxRender();
	 * 
	 * }
	 */

	private void renderText() {
		font.drawString(10, 15, "FPS: " + GameLoop.getFPS() + "       " + "noClip " + noClip, Constants.textColor);
		font.drawString(10, 40, "Camera X: " + (int) worldManager.getMobManager().getPlayer().getX() + " Y: " + (int) worldManager.getMobManager().getPlayer().getY() + " Z: " + (int) worldManager.getMobManager().getPlayer().getZ(),
				Constants.textColor);
		font.drawString(10, 65, "Rotx: " + (int) worldManager.getMobManager().getPlayer().getPitch() + " Roty: " + (int) worldManager.getMobManager().getPlayer().getYaw() + " Rotz: "
				+ (int) worldManager.getMobManager().getPlayer().getRoll(), Constants.textColor);
		font.drawString(10, 90, "Chunks: " + Constants.chunksLoaded + " (" + Constants.chunksFrustum + ")" + "PhysBlocks: " + Constants.PhysBlocksLoaded + "   RenderBlocks: " + Constants.RenderBlocksLoaded, Constants.textColor);

		font.drawString(10, 115, "playerSphereUpper X: " + (int) WorldManager.playerSphereUpper.getX() + " Y: " + (int) WorldManager.playerSphereUpper.getY() + " Z: " + (int) WorldManager.playerSphereUpper.getZ(), Constants.textColor);

		font.drawString(10, 140, "playerSphereLower X: " + (int) WorldManager.playerSphereLower.getX() + " Y: " + (int) WorldManager.playerSphereLower.getY() + " Z: " + (int) WorldManager.playerSphereLower.getZ(), Constants.textColor);
		font.drawString(10, 165, "playerSpeed X: " + Constants.playerSpeed.x + " Y: " + Constants.playerSpeed.y + " Z: " + Constants.playerSpeed.z, Constants.textColor);
		font.drawString(10, 180, " ", Color.white);
		font.drawString(10, 195, "Selected Block Type: " + Constants.selectedBlockType, Color.white);

		TextureImpl.unbind();

	}

	public void render2D() {
		glCullFace(GL_BACK);
		glClearDepth(1);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		glOrtho(0, Constants.WIDTH, Constants.HEIGHT, 0, -1, 1);
		glViewport(0, 0, Constants.WIDTH, Constants.HEIGHT);
		glMatrixMode(GL_MODELVIEW);
	}

	public void render3D() {
		if (Constants.fogEnabled) {
			glEnable(GL_FOG);
		} else {
			glDisable(GL_FOG);
		}
		glCullFace(GL_FRONT);
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		gluPerspective(67.0f, Constants.WIDTH / (float) Constants.HEIGHT, Constants.viewClose, Constants.viewDistance);
		glMatrixMode(GL_MODELVIEW);

		glEnable(GL_DEPTH_TEST);
	}

	public void renderSkyBox() {
		glCullFace(GL_BACK);
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		gluPerspective(67.0f, Constants.WIDTH / (float) Constants.HEIGHT, Constants.viewClose, Constants.viewDistance);
		glMatrixMode(GL_MODELVIEW);

		glEnable(GL_DEPTH_TEST);
		Skybox.render();
	}

	@Override
	public void dispose() {
		Display.destroy();
		System.exit(0);
	}
}
