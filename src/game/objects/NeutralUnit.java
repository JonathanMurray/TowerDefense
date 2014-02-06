package game.objects;

import game.DirectionSpriteSet;
import game.Game.Team;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import rendering.HUD;


public class NeutralUnit extends SeekerUnit {

	private String name;
	private Point[] wanderLocations;
	private int waitTime;
	private int waited;
	private boolean isWaiting;
	private static final double STUN_DURATION_MULTIPLIER = 1;

	public NeutralUnit(String name, int waitTime, Point[] wanderLocations,
			DirectionSpriteSet spriteSet, int idleFrameIndex,
			int movementCooldown, Point spawnLocation) {

		super(1, 0, spriteSet, idleFrameIndex, movementCooldown, spawnLocation,
				null, STUN_DURATION_MULTIPLIER);
		this.name = name;
		this.waitTime = waitTime;
		this.wanderLocations = wanderLocations;
		seekToNewLocation();
	}

	public void update(int delta) {
		super.update(delta);
		if (isWaiting) {
			waited += delta;
			if (waited >= waitTime) {
				seekToNewLocation();
			}
		}
	}

	@Override
	protected void handlePathIsBlocked() {
		stayAtSameLocation();
		isWaiting = true;
	}

	@Override
	protected void handleEndOfPath() {
		stopMoving();
		isWaiting = true;
	}

	private void seekToNewLocation() {
		List<Point> wanderLocationsList = Arrays.asList(wanderLocations);
		Collections.shuffle(wanderLocationsList);
		wanderLocations = wanderLocationsList.toArray(wanderLocations);
		for (Point location : wanderLocations) {
			seekToLocation(location);
			if (isSeeking()) {
				isWaiting = false;
				waited = 0;
				return;
			}
		}
	}

	@Override
	protected void renderStatBars(Graphics g) {
		/* DO nothing */
	}

	@Override
	public void renderExtraVisuals(Graphics g) {
		super.renderExtraVisuals(g);
		g.setColor(Color.black);
		if (HUD.NORMAL_FONT != null) {
			g.setFont(HUD.NORMAL_FONT);
		}

		g.drawString(name, getPixelLocation().x - 17, getPixelLocation().y - 19);
	}

	@Override
	public Team getTeam() {
		return Team.NEUTRAL;
	}

}
