package com.kyry.voxel.world;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import com.kyry.voxel.geometry.Shape;
import com.kyry.voxel.world.blocks.Block;
import com.kyry.voxel.world.entities.mobs.Player;

public class Skybox {

	public static void skyRender() {
		glBegin(GL_QUADS);
		float playerX = Player.camera.getX();
		float playerY = Player.camera.getY();
		float playerZ = Player.camera.getZ();
		float offset = 20f;
		/* Create the skybox around the player (Helps the look of unloaded chunks) */
		Shape.createSkybox(playerX - offset, playerY - offset, playerZ - offset, Block
				.getTile(Block.Skybox1.getId()).getColor(),
				Block.getTile(Block.Skybox1.getId()).getTexCoords(), Block
						.getTile(Block.Skybox2.getId()).getTexCoords(), Block
						.getTile(Block.Skybox3.getId()).getTexCoords(), Block
						.getTile(Block.Skybox4.getId()).getTexCoords(), Block
						.getTile(Block.Skybox5.getId()).getTexCoords(), Block
						.getTile(Block.Skybox6.getId()).getTexCoords(), 2*offset);
		glEnd();

	}

}
