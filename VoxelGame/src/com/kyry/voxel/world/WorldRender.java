package com.kyry.voxel.world;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import org.lwjgl.opengl.GL11;

import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.tiles.Tile;

public class WorldRender {

	public static void render() {
		glBegin(GL_QUADS);
		float playerX = 0;// Player.camera.getX();
		float playerY = 0;// Player.camera.getY();
		float playerZ = 0;// Player.camera.getZ();
		//float offset = 10f;

		
		  Shape.createSkybox(playerX-10,playerY-10,playerZ-10,
		  Tile.getTile(Tile.Brick.getId()).getColor(),
		  Tile.getTile(Tile.Brick.getId()).getTexCoords(), 20);
		 

		/*// Front Face
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Skybox1.getId());
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Brick.getId());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(1f, 0.0f);
		GL11.glVertex3f(-playerX - offset, -playerY - offset, -playerZ - offset);
		GL11.glTexCoord2f(1f, 1f);
		GL11.glVertex3f(-playerX - offset, -playerY + offset, -playerZ - offset);
		GL11.glTexCoord2f(0.0f, 1f);
		GL11.glVertex3f(-playerX + offset, -playerY + offset, -playerZ - offset);
		GL11.glTexCoord2f(0.0f, 0.0f);
		GL11.glVertex3f(-playerX + offset, -playerY - offset, -playerZ - offset);
		GL11.glEnd();

		// Back Face
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Skybox6.getId());
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Brick.getId());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0.0f, 0.0f);
		GL11.glVertex3f(-playerX - offset, -playerY - offset, -playerZ + offset);
		GL11.glTexCoord2f(1f, 0.0f);
		GL11.glVertex3f(-playerX + offset, -playerY - offset, -playerZ + offset);
		GL11.glTexCoord2f(1f, 1f);
		GL11.glVertex3f(-playerX + offset, -playerY + offset, -playerZ + offset);
		GL11.glTexCoord2f(0.0f, 1f);
		GL11.glVertex3f(-playerX - offset, -playerY + offset, -playerZ + offset);
		GL11.glEnd();

		// Top Face
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Skybox5.getId());
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Brick.getId());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(1f, 1f);
		GL11.glVertex3f(-playerX - offset, -playerY - offset, -playerZ - offset);
		GL11.glTexCoord2f(0.0f, 1f);
		GL11.glVertex3f(-playerX + offset, -playerY - offset, -playerZ - offset);
		GL11.glTexCoord2f(0.0f, 0.0f);
		GL11.glVertex3f(-playerX + offset, -playerY - offset, -playerZ + offset);
		GL11.glTexCoord2f(1f, 0.0f);
		GL11.glVertex3f(-playerX - offset, -playerY - offset, -playerZ + offset);
		GL11.glEnd();

		// Bottom Face
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Skybox3.getId());
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Brick.getId());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(1f, 0f);
		GL11.glVertex3f(-playerX - offset, -playerY + offset, -playerZ - offset);
		GL11.glTexCoord2f(1f, 1f);
		GL11.glVertex3f(-playerX - offset, -playerY + offset, -playerZ + offset);
		GL11.glTexCoord2f(0f, 1f);
		GL11.glVertex3f(-playerX + offset, -playerY + offset, -playerZ + offset);
		GL11.glTexCoord2f(0f, 0f);
		GL11.glVertex3f(-playerX + offset, -playerY + offset, -playerZ - offset);
		GL11.glEnd();

		// Right face
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Skybox4.getId());
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Brick.getId());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0.0f, 0.0f);
		GL11.glVertex3f(-playerX - offset, -playerY - offset, -playerZ - offset);
		GL11.glTexCoord2f(1f, 0.0f);
		GL11.glVertex3f(-playerX - offset, -playerY - offset, -playerZ + offset);
		GL11.glTexCoord2f(1f, 1f);
		GL11.glVertex3f(-playerX - offset, -playerY + offset, -playerZ + offset);
		GL11.glTexCoord2f(0.0f, 1f);
		GL11.glVertex3f(-playerX - offset, -playerY + offset, -playerZ - offset);
		GL11.glEnd();

		// Left Face
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Skybox2.getId());
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Brick.getId());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(1f, 0.0f);
		GL11.glVertex3f(-playerX + offset, -playerY - offset, -playerZ - offset);
		GL11.glTexCoord2f(1f, 1f);
		GL11.glVertex3f(-playerX + offset, -playerY + offset, -playerZ - offset);
		GL11.glTexCoord2f(0.0f, 1f);
		GL11.glVertex3f(-playerX + offset, -playerY + offset, -playerZ + offset);
		GL11.glTexCoord2f(0.0f, 0.0f);
		GL11.glVertex3f(-playerX + offset, -playerY - offset, -playerZ + offset);
		GL11.glEnd();*/

		glEnd();

	}

}
