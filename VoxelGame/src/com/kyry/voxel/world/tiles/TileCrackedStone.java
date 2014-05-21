package com.kyry.voxel.world.tiles;

import com.kyry.voxel.utilites.Constants;
import com.kyry.voxel.utilites.Spritesheet;
import com.nishu.utils.Color4f;

public class TileCrackedStone extends Tile{

	@Override
	public short getId() {
		return 3;
	}

	@Override
	public Color4f getColor() {
		return Color4f.GRAY;
	}

	@Override
	public float[] getTexCoords() {
		float actualSize = Constants.TextureSize;
		float size = Spritesheet.tiles.uniformSize();
		int height = (int) (getId() / (float)actualSize);
		return new float[] { (getId()-(size*height) -1) * size, height * size };
	}

}
