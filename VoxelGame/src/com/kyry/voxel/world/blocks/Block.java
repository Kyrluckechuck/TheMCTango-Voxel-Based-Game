package com.kyry.voxel.world.blocks;

import java.util.HashMap;

import com.nishu.utils.Color4f;

public abstract class Block {
	
	/* 
	 * Texture coords for multi-textured tiles
	 * bottom - first
	 * top - second
	 * front - third
	 * back - fourth
	 * left - fifth
	 * right - sixth
	 */
	
	public static HashMap<Byte, Block> tileMap = new HashMap<Byte, Block>();
	
	public static Block Air = new BlockAir();
	public static Block Void = new BlockVoid();
	public static Block Grass = new BlockGrass();
	public static Block Stone = new BlockStone();
	public static Block Dirt = new BlockDirt();
	public static Block Sand = new BlockSand();
	public static Block Brick = new BlockBrick();
	public static Block Glass = new BlockGlass();
	public static Block Cobblestone = new BlockCobblestone();
	public static Block Adamantium = new BlockAdamantium();
	public static Block TransparentGray = new TileTransparentGray();
	public static Block Crosshair = new Crosshair();
	public static Block Wireframe = new BlockWireframe();
	public static Block Skybox1 = new BlockSkybox1();
	public static Block Skybox2 = new BlockSkybox1();
	public static Block Skybox3 = new BlockSkybox1();
	public static Block Skybox4 = new BlockSkybox1();
	public static Block Skybox5 = new BlockSkybox1();
	public static Block Skybox6 = new BlockSkybox1();


	public abstract byte getId();
	public abstract Color4f getColor();
	public abstract float[] getTexCoords();
	
	public boolean isDestroyable() {
		return true;
	}	
	public String getName() {
		return "Grr";
	}
	
	public static Block getTile(byte id) {
		return tileMap.get(id);
	}
	
	public static void createTileMap() {
		tileMap.put((byte) 0, Air);
		tileMap.put((byte) 1, Void);
		tileMap.put((byte) 2, Grass);
		tileMap.put((byte) 3, Stone);
		tileMap.put((byte) 4, Dirt);
		tileMap.put((byte) 5, Sand);
		tileMap.put((byte) 6, Brick);
		tileMap.put((byte) 7, Glass);
		tileMap.put((byte) 8, Cobblestone);
		tileMap.put((byte) 9, Adamantium);
		tileMap.put((byte) 48, TransparentGray);
		tileMap.put((byte) 49, Crosshair);
		tileMap.put((byte) 50, Wireframe);
		tileMap.put((byte) 81, Skybox1);
//		tileMap.put((short) 96, Skybox2);
//		tileMap.put((short) 97, Skybox3);
//		tileMap.put((short) 98, Skybox4);
//		tileMap.put((short) 99, Skybox5);
//		tileMap.put((short) 113, Skybox6);
	}
}
