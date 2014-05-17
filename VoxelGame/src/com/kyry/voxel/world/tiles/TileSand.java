package com.kyry.voxel.world.tiles;

import com.kyry.voxel.utilites.Spritesheet;
import com.nishu.utils.Color4f;

public class TileSand extends Tile{

	@Override
	public byte getId() {
		return 5;
	}

	@Override
	public Color4f getColor() {
		return Color4f.DEFAULT;
	}

	@Override
	public float[] getTexCoords() {
		return new float[] { (getId() ) * Spritesheet.tiles.uniformSize(), 0f };
	}
}
