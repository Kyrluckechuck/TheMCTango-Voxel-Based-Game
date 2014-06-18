package com.kyry.voxel.world.blocks;

import com.kyry.voxel.utilities.Globals;
import com.kyry.voxel.utilities.Spritesheet;
import com.nishu.utils.Color4f;

public class BlockAdamantium extends Block {
	/* Override ID */
	@Override
	public byte getId() {
		return 9;
	}
	/* Override Colour */
	@Override
	public Color4f getColor() {
		return Color4f.DEFAULT;
	}
	/* Override Tex Coords */
	@Override
	public float[] getTexCoords() {
		float actualSize = Globals.TextureSize;
		float size = Spritesheet.blocks.uniformSize();
		int height = (int) (getId() / (float)actualSize);
		return new float[] { (getId()-(size*height)) * size, height * size };
	}
	/* Override Whether destroyable */
	@Override
	public boolean isDestroyable() {
		return false;
	}	
	/* Override Name of Block*/
	@Override
	public String getName() {
		return "Adamantium";
	}
}
