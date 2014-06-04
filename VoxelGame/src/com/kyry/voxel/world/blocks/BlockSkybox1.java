package com.kyry.voxel.world.blocks;

import com.kyry.voxel.utilites.Constants;
import com.kyry.voxel.utilites.Spritesheet;
import com.nishu.utils.Color4f;

public class BlockSkybox1 extends Block {

	@Override
	public short getId() {
		return 209;
	}

	@Override
	public Color4f getColor() {
		return Color4f.DEFAULT;
	}

	@Override
	public float[] getTexCoords() {
		float actualSize = Constants.TextureSize;
		float size = Spritesheet.tiles.uniformSize();
		int height = (int) (getId() / (float) actualSize);
		if (height == (getId() / (float) actualSize)) {
			return new float[] { (getId() - (actualSize * height)) * size,
					(height) * size };
		} else {
			return new float[] { (getId() - (actualSize * height)) * size,
					(height + 1) * size };
		}
		// Height is (+1) to account for the fact it's actually 1 larger than
		// the column.
	}
}
