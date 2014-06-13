package com.kyry.voxel.world.blocks;

import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.utilities.Spritesheet;
import com.nishu.utils.Color4f;

public class Crosshair extends Block {

	@Override
	public short getId() {
		return 17;
	}

	@Override
	public Color4f getColor() {
		return Color4f.DEFAULT;
	}

	@Override
	public float[] getTexCoords() {
		float actualSize = Globals.TextureSize;
		float size = Spritesheet.tiles.uniformSize();
		int height = (int) (getId() / (float) actualSize);
		if (height == (getId() / (float) actualSize)) {
			return new float[] { (getId() - (actualSize * height)) * size,
					(height) * size };
		} else {
			return new float[] { (getId() - (actualSize * height)) * size,
					(height) * size };
		}
		// Height is (+1) to account for the fact it's actually 1 larger than
		// the column.
	}
}
