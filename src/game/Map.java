package game;

import game.objects.enemies.Enemy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import applicationSpecific.MapEditorData;

public class Map implements TileBasedMap {

	private static Map instance;
	private static Random random = new Random();
	private static TiledMap tiledMap;

	private static Animation mushroomAnimation;

	// Keeps track of all dynamic blockages, to make collision checking fast!
	private boolean[][] blockedByEntity; // TODO

	private static ArrayList<Point> validItemSpawnLocations = new ArrayList<Point>();

	public Map() {
		try {
			tiledMap = new TiledMap(MapEditorData.MAP_FILE_PATH);
		} catch (SlickException e) {
			e.printStackTrace();
			System.exit(0);
		}
		blockedByEntity = new boolean[tiledMap.getWidth()][tiledMap.getHeight()];
		instance = this;

		setupValidItemSpawnLocations();
		mushroomAnimation = ResourceLoader.createTileScaledAnimation(true,
				"spore/spore1.png", "spore/spore2.png", "spore/spore3.png",
				"spore/spore4.png", "spore/spore5.png");
	}

	private void setupValidItemSpawnLocations() {
		for (int x = 0; x < tiledMap.getWidth(); x++) {
			for (int y = 0; y < tiledMap.getHeight(); y++) {

				if (!blockedForHero(x, y) && blockedForTowers(x, y)) {
					validItemSpawnLocations.add(new Point(x, y));
				}
			}
		}
	}

	public static Point getRandomItemSpawnLocation() {
		Random random = new Random();
		ArrayList<Point> locations = validItemSpawnLocations;

		return locations.get(random.nextInt(locations.size()));
	}

	public static Map instance() {
		if (instance == null) {
			throw new IllegalStateException(
					"Constructor should be called in Game-class");
		}
		return instance;
	}

	public void setLocationBlockedByEntity(int x, int y, boolean blocked) {
		blockedByEntity[x][y] = blocked;
	}

	public Point getTDStartPoint() {
		return MapEditorData.TD_START[new Random().nextInt(MapEditorData.TD_START.length)];
	}

	public Point[] getRandomTDCheckpoints() {
		random = new Random();
		Point[] randomCheckpoints = new Point[MapEditorData.TD_CHECKPOINTS.length];
		for (int i = 0; i < MapEditorData.TD_CHECKPOINTS.length; i++) {
			randomCheckpoints[i] = MapEditorData.TD_CHECKPOINTS[i][random.nextInt(MapEditorData.TD_CHECKPOINTS[i].length)];
		}
		return randomCheckpoints;
	}

	public Point getHeroEnemiesStartPoint() {
		random = new Random();
		List<Point> startPoints = Arrays.asList(MapEditorData.HERO_ENEMIES_START);
		Collections.shuffle(startPoints);
		for (Point potentialStartPoint : startPoints) {
			if (!blockedForHero(potentialStartPoint.x, potentialStartPoint.y)) {
				return potentialStartPoint;
			}
		}
		return getSomeValidHeroLocation(); // no room at normal spawnlocation, //TODO Should ideally spawn much closer to real spawn location ??
											// spawn somewhere else
	}

	private Point getSomeValidHeroLocation() {
		for (int x = 0; x < getWidthInTiles(); x++) {
			for (int y = 0; y < getHeightInTiles(); y++) {
				if (!blockedForHero(x, y)) {
					return new Point(x, y);
				}
			}
		}
		throw new IllegalStateException("No valid hero location.. weird");
	}

	public Point getCastleLocation() {
		return MapEditorData.CASTLE_LOCATION;
	}

	public void render(Graphics g) {
		tiledMap.render(0, 0);
		renderMushroom(g);
	}

	private void renderMushroom(Graphics g) {
		Point castle = MapEditorData.CASTLE_LOCATION;
		g.drawAnimation(mushroomAnimation, castle.x * getTileWidth(), castle.y
				* getTileHeight());
	}

	public void debugRenderEntitiesCollision(Graphics g) {
		g.setColor(new Color(100, 30, 30, 70));
		for (int x = 0; x < blockedByEntity.length; x++) {
			for (int y = 0; y < blockedByEntity[0].length; y++) {
				if (blockedByEntity[x][y]) {
					g.fillRect(x * getTileWidth(), y * getTileHeight(),
							getTileWidth(), getTileHeight());
				}
			}
		}
	}

	/**
	 * In pixels
	 * @return
	 */
	public static int getTileWidth() {
		return tiledMap.getTileWidth();
	}

	/**
	 * In pixels
	 * @return
	 */
	public static int getTileHeight() {
		return tiledMap.getTileHeight();
	}

	public static int getTileXFromMouseX(int mouseX) {
		return mouseX / tiledMap.getTileWidth();
	}

	public static int getTileYFromMouseY(int mouseY) {
		return mouseY / tiledMap.getTileHeight();
	}

	public static Point getTileLocationFromMouseLocation(Point mouseLocation) {
		return new Point(getTileXFromMouseX(mouseLocation.x),
				getTileYFromMouseY(mouseLocation.y));
	}

	@Override
	public boolean blocked(PathFindingContext context, int tx, int ty) {
		if (context.getMover() instanceof Enemy) {
			return blockedForEnemy((Enemy) context.getMover(), tx, ty);
		} else {
			return blockedForHero(tx, ty);
		}
	}

	public boolean blockedForEnemy(Enemy mover, int tx, int ty) {
		if (mover.isInTDMode()) {
			return blockedForTD(tx, ty);
		} else {
			return blockedForHero(tx, ty);
		}
	}

	private boolean blockedForTD(int x, int y) {
		if (isOutsideMap(x, y)) {
			return true;
		}
		boolean invalidTile = tiledMap.getTileId(x, y,
				tiledMap.getLayerIndex(MapEditorData.TD_ALLOWED_LAYER)) == 0;

		return invalidTile;
	}

	public static boolean blockedForTowers(int x, int y) {
		if (isOutsideMap(x, y)) {
			return true;
		}
		boolean invalidTile = tiledMap.getTileId(x, y,
				tiledMap.getLayerIndex(MapEditorData.TOWERS_ALLOWED_LAYER)) == 0;

		return invalidTile || instance.blockedByEntity[x][y];
	}

	public static boolean blockedForHero(int x, int y) {
		if (isOutsideMap(x, y)) {
			return true;
		}
		boolean invalidTile = tiledMap.getTileId(x, y,
				tiledMap.getLayerIndex(MapEditorData.HERO_ALLOWED_LAYER)) == 0;

		return invalidTile || instance.blockedByEntity[x][y];
	}

	@Override
	public float getCost(PathFindingContext context, int tx, int ty) {
		return 1;
	}

	@Override
	public int getHeightInTiles() {
		return tiledMap.getHeight();
	}

	@Override
	public int getWidthInTiles() {
		return tiledMap.getWidth();
	}

	@Override
	public void pathFinderVisited(int x, int y) {
	}

	private static boolean isOutsideMap(int x, int y) {
		return x < 0 || y < 0 || x >= tiledMap.getWidth()
				|| y >= tiledMap.getHeight();
	}

	public static void debugPrintBlockedArray() {
		for (int x = 0; x < MapEditorData.WIDTH_IN_TILES; x++) {
			System.out.print("-");
		}
		System.out.println();
		for (int y = 0; y < MapEditorData.HEIGHT_IN_TILES; y++) {
			System.out.print("|");
			for (int x = 0; x < MapEditorData.WIDTH_IN_TILES; x++) {
				System.out.print((instance.blockedByEntity[x][y] ? "x" : " "));
			}
			System.out.println("|");
		}
		for (int x = 0; x < MapEditorData.WIDTH_IN_TILES; x++) {
			System.out.print("-");
		}
	}

}
