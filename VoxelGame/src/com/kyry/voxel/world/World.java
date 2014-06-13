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
import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.utilities.Spritesheet;
import com.kyry.voxel.world.blocks.Block;
import com.kyry.voxel.world.chunks.ChunkManager;

public class World extends Screen {

	// public static final int AIRCHUNK = 0, MIXEDCHUNK = 1;

	private WorldManager worldManager;
	private TrueTypeFont font;

	public static boolean noClip = true;
	private boolean renderText = true;

	float screenObjOffset = 50;
	float screenXMid = Globals.WIDTH / 2;
	float screenYMid = Globals.HEIGHT / 2;

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
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.isKeyDown(Keyboard.KEY_F3)) {
					renderText = !renderText;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_F4)) {
					if (noClip)
						Globals.PLAYER_SPEED -= 15f;
					else
						Globals.PLAYER_SPEED += 15f;
					noClip = !noClip;
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
			if (Globals.selectedBlockType < 9)
				Globals.selectedBlockType++;
			else
				Globals.selectedBlockType = 0;
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
						ChunkManager.changeBlock(Globals.blockToAdd, Globals.selectedBlockType);
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

		if (renderText) {
			ready2D(); // Setup 2D matrix rendering environment
			render2D();
		}

	}

	private void render2D() {
		renderText(); // Render 2D text to screen
		renderHUD(); // render 2D crosshair image
	}

	public void ready2D() {
		glCullFace(GL_BACK);
		glClearDepth(1);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		glOrtho(0, Globals.WIDTH, Globals.HEIGHT, 0, -1, 1);
		glViewport(0, 0, Globals.WIDTH, Globals.HEIGHT);
		glMatrixMode(GL_MODELVIEW);
	}

	private void renderText() {
		font.drawString(10, 15, "FPS: " + GameLoop.getFPS() + "       " + "noClip " + noClip, Globals.textColor);
		font.drawString(10, 40, "Camera X: " + (int) worldManager.getMobManager().getPlayer().getX() + " Y: " + (int) worldManager.getMobManager().getPlayer().getY() + " Z: " + (int) worldManager.getMobManager().getPlayer().getZ(),
				Globals.textColor);
		font.drawString(10, 65, "Rotx: " + (int) worldManager.getMobManager().getPlayer().getPitch() + " Roty: " + (int) worldManager.getMobManager().getPlayer().getYaw() + " Rotz: "
				+ (int) worldManager.getMobManager().getPlayer().getRoll(), Globals.textColor);
		font.drawString(10, 90, "Chunks: " + Globals.chunksLoaded + " (" + Globals.chunksFrustum + ")" + "PhysBlocks: " + Globals.PhysBlocksLoaded + "   RenderBlocks: " + Globals.RenderBlocksLoaded, Globals.textColor);

		font.drawString(10, 115, "playerSphereUpper X: " + (int) WorldManager.playerSphereUpper.getX() + " Y: " + (int) WorldManager.playerSphereUpper.getY() + " Z: " + (int) WorldManager.playerSphereUpper.getZ(), Globals.textColor);

		font.drawString(10, 140, "playerSphereLower X: " + (int) WorldManager.playerSphereLower.getX() + " Y: " + (int) WorldManager.playerSphereLower.getY() + " Z: " + (int) WorldManager.playerSphereLower.getZ(), Globals.textColor);
		font.drawString(10, 165, "playerSpeed X: " + Globals.playerSpeed.x + " Y: " + Globals.playerSpeed.y + " Z: " + Globals.playerSpeed.z, Globals.textColor);
		font.drawString(10, 190, "Selected Block Type: " + Globals.selectedBlockType, Globals.textColor);

		font.drawString(10, 205, " ", Color.white);

		TextureImpl.unbind();

	}

	private void renderHUD() {

		Spritesheet.tiles.bind();
		float x1Crosshair = screenXMid - screenObjOffset;
		float y1Crosshair = screenYMid - screenObjOffset;
		float x2Crosshair = screenXMid + screenObjOffset;
		float y2Crosshair = screenYMid + screenObjOffset;
		renderHUDObject(Block.Crosshair.getId(), x1Crosshair, y1Crosshair, x2Crosshair, y2Crosshair); // Draw
																										// Crosshair
		float x1Selected = screenXMid * 2 - screenObjOffset * 2 - 10;
		float y1Selected = 0 + 10;
		float x2Selected = screenXMid * 2 - 10;
		float y2Selected = 0 + screenObjOffset * 2 + 10;
		renderHUDObject(Globals.selectedBlockType, x1Selected, y1Selected, x2Selected, y2Selected); // Draw
																										// Selected
																										// Object
		float x1SelectedOutline = screenXMid * 2 - screenObjOffset * 2 - 10 * 2;
		float y1SelectedOutline = 0;
		float x2SelectedOutline = screenXMid * 2 + 10;
		float y2SelectedOutline = 0 + screenObjOffset * 2 + 10 * 2;
		renderHUDObject(Block.TransparentGray.getId(), x1SelectedOutline, y1SelectedOutline, x2SelectedOutline, y2SelectedOutline); // Draw
																																	// Selected
																																	// Object
		// for ()
		// renderHUDObject(Block.Crosshair.getId());

	}

	private void renderHUDObject(short texture, float x1, float y1, float x2, float y2) {
		glBegin(GL_QUADS);
		float[] texCoords = Block.getTile(texture).getTexCoords();
		glTexCoord2f(texCoords[0], texCoords[1]);
		glVertex2f(x1, y1);
		glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1]);
		glVertex2f(x1, y2);
		glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1] + Spritesheet.tiles.uniformSize());
		glVertex2f(x2, y2);
		glTexCoord2f(texCoords[0], texCoords[1] + Spritesheet.tiles.uniformSize());
		glVertex2f(x2, y1);
		glEnd();
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
		renderSkyBox(); // Render Skybox (outside of world, non-interactible)
	}

	public void renderSkyBox() {
		glCullFace(GL_BACK);
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		gluPerspective(67.0f, Globals.WIDTH / (float) Globals.HEIGHT, Globals.viewClose, Globals.viewDistance);
		glMatrixMode(GL_MODELVIEW);

		glEnable(GL_DEPTH_TEST);
		Skybox.skyRender();
	}

	@Override
	public void dispose() {
		Display.destroy();
		System.exit(0);
	}
}
