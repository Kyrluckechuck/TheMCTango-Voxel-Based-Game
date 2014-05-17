package com.kyry.voxel.world.tiles;

import com.kyry.voxel.utilites.Spritesheet;
import com.nishu.utils.Color4f;

public class TileCrackedStone extends Tile{

	@Override
	public byte getId() {
		return 3;
	}

	@Override
	public Color4f getColor() {
		return Color4f.GRAY;
	}

	@Override
	public float[] getTexCoords() {
		return new float[] { 2 * Spritesheet.tiles.uniformSize(), 0* Spritesheet.tiles.uniformSize() };
	}

}
