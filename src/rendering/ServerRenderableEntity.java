package rendering;

import game.Direction;

import java.awt.Point;

import messages.ClientEntityMessage;
import messages.Message;
import multiplayer.ModifyEntityMessage;
import multiplayer.Server;
import multiplayer.SetAutoUpdateSpriteMessage;
import multiplayer.SetIntPropertyMessage;

import org.newdawn.slick.Graphics;

public class ServerRenderableEntity implements RenderableEntity {

	private Server server;
	private int entityID;

	public ServerRenderableEntity(int entityID, Server server) {
		this.entityID = entityID;
		this.server = server;
	}

	@Override
	public void render(Graphics g) {
	}

	@Override
	public void renderExtraVisuals(Graphics g) {
	}

	@Override
	public void setDirection(Direction direction) {
		server.sendMessageToClients(new Message(ClientEntityMessage.ENTITY_SET_DIRECTION, new SetIntPropertyMessage(entityID, direction.ordinal())));
	}

	@Override
	public void setAutoUpdateSprite(boolean autoUpdate) {
		server.sendMessageToClients(new Message(ClientEntityMessage.ENTITY_SET_AUTO_UPDATE_SPRITE, new SetAutoUpdateSpriteMessage(entityID, autoUpdate)));
	}

	@Override
	public void setPercentHealth(int percentHealth) {
		server.sendMessageToClients(new Message(ClientEntityMessage.ENTITY_SET_PERCENT_HEALTH, new SetIntPropertyMessage(entityID, percentHealth)));
	}

	@Override
	public void setPercentMana(int percentMana) {
		server.sendMessageToClients(new Message(ClientEntityMessage.ENTITY_SET_PERCENT_MANA, new SetIntPropertyMessage(entityID, percentMana)));
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLocationAndSpeed(Point pixelLocation, Point movementSpeed) {
		server.sendMessageToClients(new Message(ClientEntityMessage.ENTITY_SET_LOCATION_SPEED, new ModifyEntityMessage(entityID, pixelLocation.x,
				pixelLocation.y, movementSpeed.x, movementSpeed.y)));
	}

}
