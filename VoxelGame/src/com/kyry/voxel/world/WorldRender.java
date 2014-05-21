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
		float playerX = 0;//Player.camera.getX();
		float playerY = 0;//Player.camera.getY();
		float playerZ = 0;//Player.camera.getZ();
		GL11.glDisable(GL11.GL_CULL_FACE);

		Shape.createSkybox(playerX-10,playerY-10,playerZ-10,
				Tile.getTile(Tile.Brick.getId()).getColor(),
				Tile.getTile(Tile.Brick.getId()).getTexCoords(),
				20);
		
		
		glEnd();
		
	}
 
}
