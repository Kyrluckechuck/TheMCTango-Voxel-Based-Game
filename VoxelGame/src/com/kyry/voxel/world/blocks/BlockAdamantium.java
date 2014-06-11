package com.kyry.voxel.world.blocks;

import com.kyry.voxel.utilities.Constants;
import com.kyry.voxel.utilities.Spritesheet;
import com.nishu.utils.Color4f;

public class BlockAdamantium extends Block {

	@Override
	public short getId() {
		return 9;
	}

	@Override
	public Color4f getColor() {
		return Color4f.DEFAULT;
	}

	@Override
	public float[] getTexCoords() {
		float actualSize = Constants.TextureSize;
		float size = Spritesheet.tiles.uniformSize();
		int height = (int) (getId() / (float)actualSize);
		return new float[] { (getId()-(size*height)) * size, height * size };
	}
	@Override
	public boolean isDestroyable() {
		return false;
	}	
}
