package com.kyry.voxel.world;

import org.lwjgl.opengl.GL11;

import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.tiles.Tile;

public class worldRender {

	public static void render() {
		float offset = 10;
		float playerX = Player.camera.getX();
		float playerY = Player.camera.getY();
		float playerZ = Player.camera.getZ();
		   //Front Face
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Skybox1.getId());
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Skybox6.getId());
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Skybox5.getId());
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Skybox3.getId());
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Skybox4.getId());
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tile.Skybox2.getId());
		GL11.glBegin(GL11.GL_QUADS);
		    GL11.glTexCoord2f(1f, 0.0f);
		    GL11.glVertex3f(-playerX + offset, -playerY - offset, -playerZ - offset);
		    GL11.glTexCoord2f(1f, 1f);
		    GL11.glVertex3f(-playerX + offset, -playerY + offset, -playerZ - offset);
		    GL11.glTexCoord2f(0.0f, 1f);
		    GL11.glVertex3f(-playerX + offset, -playerY + offset, -playerZ + offset);
		    GL11.glTexCoord2f(0.0f, 0.0f);
		    GL11.glVertex3f(-playerX + offset, -playerY - offset, -playerZ + offset);
		GL11.glEnd();
		
	}
 
}
