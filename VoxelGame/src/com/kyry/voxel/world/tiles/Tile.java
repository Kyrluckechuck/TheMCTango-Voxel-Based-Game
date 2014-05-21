package com.kyry.voxel.world.tiles;

import java.util.HashMap;

import com.nishu.utils.Color4f;

public abstract class Tile {
	
	/* 
	 * Texture coords for multi-textured tiles
	 * bottom - first
	 * top - second
	 * front - third
	 * back - fourth
	 * left - fifth
	 * right - sixth
	 */
	
	public static HashMap<Short, Tile> tileMap = new HashMap<Short, Tile>();
	
	public static Tile Air = new TileAir();
	public static Tile Void = new TileVoid();
	public static Tile Grass = new TileGrass();
	public static Tile CrackedStone = new TileCrackedStone();
	public static Tile Dirt = new TileDirt();
	public static Tile Sand = new TileSand();
	public static Tile Brick = new TileBrick();
	public static Tile Glass = new TileGlass();
	public static Tile Skybox1 = new TileSkybox1();
	public static Tile Skybox2 = new TileSkybox1();
	public static Tile Skybox3 = new TileSkybox1();
	public static Tile Skybox4 = new TileSkybox1();
	public static Tile Skybox5 = new TileSkybox1();
	public static Tile Skybox6 = new TileSkybox1();

	public abstract short getId();
	public abstract Color4f getColor();
	public abstract float[] getTexCoords();
	
	public static Tile getTile(short id) {
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
		tileMap.put((short) 209, Skybox1);
		tileMap.put((short) 224, Skybox2);
		tileMap.put((short) 225, Skybox3);
		tileMap.put((short) 226, Skybox4);
		tileMap.put((short) 227, Skybox5);
		tileMap.put((short) 241, Skybox6);
	}
}
