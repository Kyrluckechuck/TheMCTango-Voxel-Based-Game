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
	
	public static HashMap<Byte, Tile> tileMap = new HashMap<Byte, Tile>();
	
	public static Tile Air = new TileAir();
	public static Tile Void = new TileVoid();
	public static Tile Grass = new TileGrass();
	public static Tile CrackedStone = new TileCrackedStone();
	public static Tile Dirt = new TileDirt();
	public static Tile Sand = new TileSand();
	public static Tile Brick = new TileBrick();
	public static Tile Glass = new TileGlass();

	public abstract byte getId();
	public abstract Color4f getColor();
	public abstract float[] getTexCoords();
	
	public static Tile getTile(byte id) {
		return tileMap.get(id);
	}
	
	public static void createTileMap() {
		tileMap.put((byte) 0, Air);
		tileMap.put((byte) 1, Void);
		tileMap.put((byte) 2, Grass);
		tileMap.put((byte) 3, CrackedStone);
		tileMap.put((byte) 4, Dirt);
		tileMap.put((byte) 5, Sand);
		tileMap.put((byte) 6, Brick);
		tileMap.put((byte) 7, Glass);
	}
}
