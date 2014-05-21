package com.kyry.voxel.world.tiles;

import com.nishu.utils.Color4f;

public class TileAir extends Tile{

	@Override
	public short getId() {
		return 0;
	}

	@Override
	public Color4f getColor() {
		return Color4f.WHITE;
	}

	@Override
	public float[] getTexCoords() {
		return new float[] {-1, -1};
	}
}
