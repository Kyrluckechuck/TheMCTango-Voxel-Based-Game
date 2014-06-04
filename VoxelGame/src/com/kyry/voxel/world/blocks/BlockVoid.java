package com.kyry.voxel.world.blocks;

import com.kyry.voxel.utilites.Spritesheet;
import com.nishu.utils.Color4f;

public class BlockVoid extends Block {

	@Override
	public short getId() {
		return 1;
	}

	@Override
	public Color4f getColor() {
		return new Color4f(0.5f, 0.5f, 0.5f, 1);
	}

	@Override
	public float[] getTexCoords() {
		return new float[] { 0f, 0f };
	}
}
