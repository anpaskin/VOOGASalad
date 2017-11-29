package engine.Actions;

import java.util.List;

import engine.Action;
import engine.GameObject;
import engine.GameLayer;
import engine.World;

public class ChangeWorld implements Action {

	private World newWorld;
	private int newPlayerX;
	private int newPlayerY;
	
	public ChangeWorld(World newWorld, int newPlayerX, int newPlayerY) {
		this.newWorld = newWorld;
		this.newPlayerX = newPlayerX;
		this.newPlayerY = newPlayerY;
	}
	
	@Override
	public void execute(GameObject asking, World world) {
		List<GameObject> players = world.getWithTag(GameLayer.PLAYER_TAG);
		world.removeGameObjects(players);
		MoveTo moveTo = new MoveTo(newPlayerX, newPlayerY);
		for(GameObject player : players) {
			moveTo.execute(player, newWorld);
		}
		newWorld.addGameObjects(players);
		world.setNextWorld(newWorld);
	}

}
