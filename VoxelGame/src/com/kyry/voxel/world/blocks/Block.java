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
	
	public static HashMap<Short, Block> tileMap = new HashMap<Short, Block>();
	
	public static Block Air = new BlockAir();
	public static Block Void = new BlockVoid();
	public static Block Grass = new BlockGrass();
	public static Block CrackedStone = new BlockCrackedStone();
	public static Block Dirt = new BlockDirt();
	public static Block Sand = new BlockSand();
	public static Block Brick = new BlockBrick();
	public static Block Glass = new BlockGlass();
	public static Block Wireframe = new BlockWireframe();
	public static Block Adamantium = new BlockAdamantium();
	public static Block TransparentGray = new TileTransparentGray();
	public static Block Crosshair = new Crosshair();
	public static Block Skybox1 = new BlockSkybox1();
	public static Block Skybox2 = new BlockSkybox1();
	public static Block Skybox3 = new BlockSkybox1();
	public static Block Skybox4 = new BlockSkybox1();
	public static Block Skybox5 = new BlockSkybox1();
	public static Block Skybox6 = new BlockSkybox1();


	public abstract short getId();
	public abstract Color4f getColor();
	public abstract float[] getTexCoords();
	
	public boolean isDestroyable() {
		return true;
	}	
	
	public static Block getTile(short id) {
		return tileMap.get(id);
	}
	
	public static void createTileMap() {
		tileMap.put((short) 0, Air);
		tileMap.put((short) 1, Void);
		tileMap.put((short) 2, Grass);
		tileMap.put((short) 3, CrackedStone);
		tileMap.put((short) 4, Dirt);
		tileMap.put((short) 5, Sand);
		tileMap.put((short) 6, Brick);
		tileMap.put((short) 7, Glass);
		tileMap.put((short) 8, Wireframe);
		tileMap.put((short) 9, Adamantium);
		tileMap.put((short) 16, TransparentGray);
		tileMap.put((short) 17, Crosshair);
		tileMap.put((short) 209, Skybox1);
		tileMap.put((short) 224, Skybox2);
		tileMap.put((short) 225, Skybox3);
		tileMap.put((short) 226, Skybox4);
		tileMap.put((short) 227, Skybox5);
		tileMap.put((short) 241, Skybox6);
	}
}
