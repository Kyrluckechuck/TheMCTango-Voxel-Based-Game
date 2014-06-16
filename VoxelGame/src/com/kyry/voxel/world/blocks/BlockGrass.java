package com.kyry.voxel.world.blocks;

import com.kyry.voxel.utilities.Spritesheet;
import com.nishu.utils.Color4f;

public class BlockGrass extends Block {

	/* 
	 * Texture coords for multi-textured tiles
	 * bottom - first
	 * top - second
	 * front - third
	 * back - fourth
	 * left - fifth
	 * right - sixth
	 */
	
	@Override
	public byte getId() {
		return 2;
	}

	@Override
	public Color4f getColor() {
		return Color4f.WHITE;
	}

	@Override
	public float[] getTexCoords() {
		return new float[] { 4 * Spritesheet.blocks.uniformSize(), Spritesheet.blocks.uniformSize(), 
				Spritesheet.blocks.uniformSize(), Spritesheet.blocks.uniformSize(),
				3 * Spritesheet.blocks.uniformSize(), Spritesheet.blocks.uniformSize(),
				3 * Spritesheet.blocks.uniformSize(), 0,
				3 * Spritesheet.blocks.uniformSize(), Spritesheet.blocks.uniformSize(),
				3 * Spritesheet.blocks.uniformSize(), Spritesheet.blocks.uniformSize() };
	}
	@Override
	public String getName() {
		return "Grass";
	}
}
