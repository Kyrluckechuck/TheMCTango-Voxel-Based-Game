package com.kyry.voxel.world;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import org.lwjgl.opengl.GL11;

import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.utilities.Constants;
import com.kyry.voxel.world.blocks.Block;
import com.kyry.voxel.world.entities.mobs.Player;
import com.kyry.voxel.world.physics.CollisionLibrary;

public class WorldRender {

	public static void render() {
		glBegin(GL_QUADS);
		float playerX = Player.camera.getX();
		float playerY = Player.camera.getY();
		float playerZ = Player.camera.getZ();
		// float offset = 10f;
		Shape.createSkybox(playerX - 10, playerY - 10, playerZ - 10, Block
				.getTile(Block.Skybox1.getId()).getColor(),
				Block.getTile(Block.Skybox1.getId()).getTexCoords(), Block
						.getTile(Block.Skybox2.getId()).getTexCoords(), Block
						.getTile(Block.Skybox3.getId()).getTexCoords(), Block
						.getTile(Block.Skybox4.getId()).getTexCoords(), Block
						.getTile(Block.Skybox5.getId()).getTexCoords(), Block
						.getTile(Block.Skybox6.getId()).getTexCoords(), 40);
		/*
		 * Shape.createSkybox(playerX - 10, playerY - 10, playerZ - 10, Block
		 * .getTile(Block.Brick.getId()).getColor(),
		 * Block.getTile(Block.Brick.getId()).getTexCoords(), 20);
		 */

		/*
		 * // Front Face // GL11.glBindTexture(GL11.GL_TEXTURE_2D,
		 * Block.Skybox1.getId()); GL11.glBindTexture(GL11.GL_TEXTURE_2D,
		 * Block.Brick.getId()); GL11.glBegin(GL11.GL_QUADS);
		 * GL11.glTexCoord2f(1f, 0.0f); GL11.glVertex3f(-playerX - offset,
		 * -playerY - offset, -playerZ - offset); GL11.glTexCoord2f(1f, 1f);
		 * GL11.glVertex3f(-playerX - offset, -playerY + offset, -playerZ -
		 * offset); GL11.glTexCoord2f(0.0f, 1f); GL11.glVertex3f(-playerX +
		 * offset, -playerY + offset, -playerZ - offset);
		 * GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-playerX + offset,
		 * -playerY - offset, -playerZ - offset); GL11.glEnd();
		 * 
		 * // Back Face // GL11.glBindTexture(GL11.GL_TEXTURE_2D,
		 * Block.Skybox6.getId()); GL11.glBindTexture(GL11.GL_TEXTURE_2D,
		 * Block.Brick.getId()); GL11.glBegin(GL11.GL_QUADS);
		 * GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-playerX - offset,
		 * -playerY - offset, -playerZ + offset); GL11.glTexCoord2f(1f, 0.0f);
		 * GL11.glVertex3f(-playerX + offset, -playerY - offset, -playerZ +
		 * offset); GL11.glTexCoord2f(1f, 1f); GL11.glVertex3f(-playerX +
		 * offset, -playerY + offset, -playerZ + offset);
		 * GL11.glTexCoord2f(0.0f, 1f); GL11.glVertex3f(-playerX - offset,
		 * -playerY + offset, -playerZ + offset); GL11.glEnd();
		 * 
		 * // Top Face // GL11.glBindTexture(GL11.GL_TEXTURE_2D,
		 * Block.Skybox5.getId()); GL11.glBindTexture(GL11.GL_TEXTURE_2D,
		 * Block.Brick.getId()); GL11.glBegin(GL11.GL_QUADS);
		 * GL11.glTexCoord2f(1f, 1f); GL11.glVertex3f(-playerX - offset,
		 * -playerY - offset, -playerZ - offset); GL11.glTexCoord2f(0.0f, 1f);
		 * GL11.glVertex3f(-playerX + offset, -playerY - offset, -playerZ -
		 * offset); GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-playerX +
		 * offset, -playerY - offset, -playerZ + offset); GL11.glTexCoord2f(1f,
		 * 0.0f); GL11.glVertex3f(-playerX - offset, -playerY - offset, -playerZ
		 * + offset); GL11.glEnd();
		 * 
		 * // Bottom Face // GL11.glBindTexture(GL11.GL_TEXTURE_2D,
		 * Block.Skybox3.getId()); GL11.glBindTexture(GL11.GL_TEXTURE_2D,
		 * Block.Brick.getId()); GL11.glBegin(GL11.GL_QUADS);
		 * GL11.glTexCoord2f(1f, 0f); GL11.glVertex3f(-playerX - offset,
		 * -playerY + offset, -playerZ - offset); GL11.glTexCoord2f(1f, 1f);
		 * GL11.glVertex3f(-playerX - offset, -playerY + offset, -playerZ +
		 * offset); GL11.glTexCoord2f(0f, 1f); GL11.glVertex3f(-playerX +
		 * offset, -playerY + offset, -playerZ + offset); GL11.glTexCoord2f(0f,
		 * 0f); GL11.glVertex3f(-playerX + offset, -playerY + offset, -playerZ -
		 * offset); GL11.glEnd();
		 * 
		 * // Right face // GL11.glBindTexture(GL11.GL_TEXTURE_2D,
		 * Block.Skybox4.getId()); GL11.glBindTexture(GL11.GL_TEXTURE_2D,
		 * Block.Brick.getId()); GL11.glBegin(GL11.GL_QUADS);
		 * GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-playerX - offset,
		 * -playerY - offset, -playerZ - offset); GL11.glTexCoord2f(1f, 0.0f);
		 * GL11.glVertex3f(-playerX - offset, -playerY - offset, -playerZ +
		 * offset); GL11.glTexCoord2f(1f, 1f); GL11.glVertex3f(-playerX -
		 * offset, -playerY + offset, -playerZ + offset);
		 * GL11.glTexCoord2f(0.0f, 1f); GL11.glVertex3f(-playerX - offset,
		 * -playerY + offset, -playerZ - offset); GL11.glEnd();
		 * 
		 * // Left Face // GL11.glBindTexture(GL11.GL_TEXTURE_2D,
		 * Block.Skybox2.getId()); GL11.glBindTexture(GL11.GL_TEXTURE_2D,
		 * Block.Brick.getId()); GL11.glBegin(GL11.GL_QUADS);
		 * GL11.glTexCoord2f(1f, 0.0f); GL11.glVertex3f(-playerX + offset,
		 * -playerY - offset, -playerZ - offset); GL11.glTexCoord2f(1f, 1f);
		 * GL11.glVertex3f(-playerX + offset, -playerY + offset, -playerZ -
		 * offset); GL11.glTexCoord2f(0.0f, 1f); GL11.glVertex3f(-playerX +
		 * offset, -playerY + offset, -playerZ + offset);
		 * GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-playerX + offset,
		 * -playerY - offset, -playerZ + offset); GL11.glEnd();
		 */
		glEnd();

	}

}
