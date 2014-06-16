package com.kyry.voxel.world;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;

import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.utilities.Spritesheet;
import com.kyry.voxel.world.blocks.Block;
import com.nishu.utils.GameLoop;

public class HUD {

	static float screenObjOffset = 50; // Offset for object size
	static float screenXMid = Globals.WIDTH / 2; // Middle of screen (X-axis)
	static float screenYMid = Globals.HEIGHT / 2; // Middle of screen (Y-axis)
	static TrueTypeFont font; // Define font to be used

	public static void renderHUD() {
		World.ready2D();

		/* Render the current block to be placed */
		renderPlayerBlock();
		/* Render 2D crosshair image and currently selected block in top right */
		renderHUDObjects();
		if (Globals.renderHUDText) {
			renderText(); // Render 2D text to screen
		}
	}

	public static void renderHUDObjects() {
		/* Bind spritesheet of images to blocks */
		Spritesheet.blocks.bind(); // 
		/* Crosshair */
		float x1Crosshair = screenXMid - screenObjOffset / 2;
		float y1Crosshair = screenYMid - screenObjOffset / 2;
		float x2Crosshair = screenXMid + screenObjOffset / 2;
		float y2Crosshair = screenYMid + screenObjOffset / 2;
		renderHUDObject(Block.Crosshair.getId(), x1Crosshair, y1Crosshair, x2Crosshair, y2Crosshair);

		/* Selected Object In Hand */
		float x1Selected = screenXMid * 2 - screenObjOffset * 2 - 10;
		float y1Selected = 0 + 10;
		float x2Selected = screenXMid * 2 - 10;
		float y2Selected = 0 + screenObjOffset * 2 + 10;
		renderHUDObject(Globals.blockToAddType, x1Selected, y1Selected, x2Selected, y2Selected);

		/* Gray Background For Object In Hand (ease of sight) */
		float x1SelectedOutline = screenXMid * 2 - screenObjOffset * 2 - 10 * 2;
		float y1SelectedOutline = 0;
		float x2SelectedOutline = screenXMid * 2 + 10;
		float y2SelectedOutline = 0 + screenObjOffset * 2 + 10 * 2;
		renderHUDObject(Block.TransparentGray.getId(), x1SelectedOutline, y1SelectedOutline, x2SelectedOutline, y2SelectedOutline);

	}

	private static void renderText() {
		/* All text rendering. Straightforward. */
		font.drawString(10, 15, "FPS: " + GameLoop.getFPS() + "       " + "noClip: " + Globals.noClip + "Fog: " + Globals.fogEnabled, Globals.textColor);
		font.drawString(10, 40, "Camera X: " + (int) World.worldManager.getMobManager().getPlayer().getX() + " Y: " + (int) World.worldManager.getMobManager().getPlayer().getY() + " Z: "
				+ (int) World.worldManager.getMobManager().getPlayer().getZ(), Globals.textColor);
		font.drawString(10, 65, "Rotx: " + (int) World.worldManager.getMobManager().getPlayer().getPitch() + " Roty: " + (int) World.worldManager.getMobManager().getPlayer().getYaw() + " Rotz: "
				+ (int) World.worldManager.getMobManager().getPlayer().getRoll(), Globals.textColor);
		font.drawString(10, 90, "Chunks: " + Globals.chunksLoaded + " (" + Globals.chunksFrustum + ")" + "PhysBlocks: " + Globals.PhysBlocksLoaded + "   RenderBlocks: " + Globals.RenderBlocksLoaded, Globals.textColor);

		font.drawString(10, 115, "playerSphereUpper X: " + (int) WorldManager.playerSphereUpper.getX() + " Y: " + (int) WorldManager.playerSphereUpper.getY() + " Z: " + (int) WorldManager.playerSphereUpper.getZ(), Globals.textColor);

		font.drawString(10, 140, "playerSphereLower X: " + (int) WorldManager.playerSphereLower.getX() + " Y: " + (int) WorldManager.playerSphereLower.getY() + " Z: " + (int) WorldManager.playerSphereLower.getZ(), Globals.textColor);
		font.drawString(10, 165, "playerSpeed X: " + Globals.playerSpeed.x + " Y: " + Globals.playerSpeed.y + " Z: " + Globals.playerSpeed.z, Globals.textColor);
		font.drawString(10, 190, "Selected Block Type: " + Globals.blockToAddType, Globals.textColor);

		font.drawString(screenXMid*2 - 155, 55, "Selected", Globals.textColor);
		font.drawString(screenXMid*2 - 170, 70, "Block Type:", Globals.textColor);
		font.drawString(screenXMid*2 - 155, 85, Block.getTile(Globals.blockToAddType).getName(), Globals.textColor);
		
		font.drawString(10, 205, " ", Color.white);

		TextureImpl.unbind(); // Unbind the texture used for font

	}

	private static void renderHUDObject(byte texture, float x1, float y1, float x2, float y2) {
		/* Get texture coordinates (on spritesheet) */
		float[] texCoords = Block.getTile(texture).getTexCoords();
		glBegin(GL_QUADS);
		/* Account for the multi-textured objects (aka Grass) */
		if (texCoords.length > 2) {
			glTexCoord2f(texCoords[2], texCoords[3]);
			glVertex2f(x1, y1);
			glTexCoord2f(texCoords[2] + Spritesheet.blocks.uniformSize(), texCoords[3]);
			glVertex2f(x1, y2);
			glTexCoord2f(texCoords[2] + Spritesheet.blocks.uniformSize(), texCoords[3] - Spritesheet.blocks.uniformSize());
			glVertex2f(x2, y2);
			glTexCoord2f(texCoords[2], texCoords[3] - Spritesheet.blocks.uniformSize());
			glVertex2f(x2, y1);
			/* All other textures */
		} else { 
			glTexCoord2f(texCoords[0], texCoords[1]);
			glVertex2f(x1, y1);
			glTexCoord2f(texCoords[0] + Spritesheet.blocks.uniformSize(), texCoords[1]);
			glVertex2f(x1, y2);
			glTexCoord2f(texCoords[0] + Spritesheet.blocks.uniformSize(), texCoords[1] + Spritesheet.blocks.uniformSize());
			glVertex2f(x2, y2);
			glTexCoord2f(texCoords[0], texCoords[1] + Spritesheet.blocks.uniformSize());
			glVertex2f(x2, y1);

		}
		glEnd();
	}

	private static void renderPlayerBlock() {
		/* Draw semi-isometric view of block */
		byte renderBlock = Globals.blockToAddType;
		float x1SelectedOutline = Globals.WIDTH / 3 * 2.5f;
		float y1SelectedOutline = Globals.HEIGHT - 75;
		float size = 300; // Size parameter
		render3DHUDObject(renderBlock, x1SelectedOutline, y1SelectedOutline, size);
	}

	private static void render3DHUDObject(byte textureID, float x1, float y1, float ratio) {
		/* First offset (was mainly instituted for fine-tuning) */
		float tidOffset = ratio / 6f; 
		/* Rotate offset for slight turning of the image */
		float rotateOffset = 300 / 3.75f;
		glBegin(GL_QUADS);
		/*  Get texture coordinates */
		float[] texCoords = Block.getTile(textureID).getTexCoords();
		
		/* Account for the multi-textured objects (aka Grass) */
		if (texCoords.length > 2) { 
			/* Top Face */
			// Top Vertex
			glTexCoord2f(texCoords[2], texCoords[3]);
			glVertex2f(x1 - tidOffset * 3.5f, (int) (y1 - ratio * 0.6f + rotateOffset));
			// Mid Left Vertex
			glTexCoord2f(texCoords[2] + Spritesheet.blocks.uniformSize(), texCoords[3]);
			glVertex2f(x1 - ratio, (int) (y1 - tidOffset + rotateOffset * 0.5));
			// Bottom Vertex
			glTexCoord2f(texCoords[2] + Spritesheet.blocks.uniformSize(), texCoords[3] - Spritesheet.blocks.uniformSize());
			glVertex2f(x1 + tidOffset * 4.5f, (int) (y1 + tidOffset + rotateOffset * 0.3));
			// Mid Right Vertex
			glTexCoord2f(texCoords[2], texCoords[3] - Spritesheet.blocks.uniformSize());
			glVertex2f(x1 + ratio * 0.7f, (int) (y1 - tidOffset + rotateOffset * 0.2));

			/* Front Face */
			// Top Left Vertex
			glTexCoord2f(texCoords[4], texCoords[5]);
			glVertex2f(x1 - ratio, (int) (y1 - tidOffset + rotateOffset * 0.5f));
			// Bottom Left Vertex
			glTexCoord2f(texCoords[4] + Spritesheet.blocks.uniformSize(), texCoords[5]);
			glVertex2f(x1 - ratio, (int) (y1 + rotateOffset * 2.5));
			// Bottom Right Vertex
			glTexCoord2f(texCoords[4] + Spritesheet.blocks.uniformSize(), texCoords[5] - Spritesheet.blocks.uniformSize());
			glVertex2f(x1 + tidOffset * 4.5f, (int) (y1 + tidOffset + rotateOffset * 2.8));
			// Top Right Vertex
			glTexCoord2f(texCoords[4], texCoords[5] - Spritesheet.blocks.uniformSize());
			glVertex2f(x1 + tidOffset * 4.5f, (int) (y1 + tidOffset + rotateOffset * 0.3));
			/* All other textures */
		} else { 
			/* Top Face */
			// Top Vertex
			glTexCoord2f(texCoords[0], texCoords[1]);
			glVertex2f(x1 - tidOffset * 3.5f, (int) (y1 - ratio * 0.6f + rotateOffset));
			// Mid Left Vertex
			glTexCoord2f(texCoords[0] + Spritesheet.blocks.uniformSize(), texCoords[1]);
			glVertex2f(x1 - ratio, (int) (y1 - tidOffset + rotateOffset * 0.5));
			// Bottom Vertex
			glTexCoord2f(texCoords[0] + Spritesheet.blocks.uniformSize(), texCoords[1] + Spritesheet.blocks.uniformSize());
			glVertex2f(x1 + tidOffset * 4.5f, (int) (y1 + tidOffset + rotateOffset * 0.3));
			// Mid Right Vertex
			glTexCoord2f(texCoords[0], texCoords[1] + Spritesheet.blocks.uniformSize());
			glVertex2f(x1 + ratio * 0.7f, (int) (y1 - tidOffset + rotateOffset * 0.2));

			/* Front Face */
			// Top Left Vertex
			glTexCoord2f(texCoords[0], texCoords[1]);
			glVertex2f(x1 - ratio, (int) (y1 - tidOffset + rotateOffset * 0.5f));
			// Bottom Left Vertex
			glTexCoord2f(texCoords[0] + Spritesheet.blocks.uniformSize(), texCoords[1]);
			glVertex2f(x1 - ratio, (int) (y1 + rotateOffset * 2.5));
			// Bottom Right Vertex
			glTexCoord2f(texCoords[0] + Spritesheet.blocks.uniformSize(), texCoords[1] + Spritesheet.blocks.uniformSize());
			glVertex2f(x1 + tidOffset * 4.5f, (int) (y1 + tidOffset + rotateOffset * 2.8));
			// Top Right Vertex
			glTexCoord2f(texCoords[0], texCoords[1] + Spritesheet.blocks.uniformSize());
			glVertex2f(x1 + tidOffset * 4.5f, (int) (y1 + tidOffset + rotateOffset * 0.3));
		}

		glEnd();
	}

}
