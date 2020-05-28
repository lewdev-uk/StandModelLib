package uk.lewdev.standmodels.model;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import uk.lewdev.standmodels.parser.ModelBuildInstruction;
import uk.lewdev.standmodels.utils.Axis;

/**
 * A model that supports movement when a player is within the required distance.
 * You can implement the movement logic by overriding {@link #doAnimationTick()}
 * 
 * @author Lewys
 * @since 28/05/2020
 */
public class AnimatedModel extends Model {

	private double animationDistance;
	
	private HashSet<Player> playersInAnimationDistance = new HashSet<>();

	public AnimatedModel(List<ModelBuildInstruction> ins, Location center, Axis facing, Axis desired,
			double renderDistance, double animationDistance) {
		super(ins, center, facing, desired, renderDistance, false);

		this.animationDistance = animationDistance;
	}

	/**
	 * Override this method to update the animation, if there is one.
	 * This method will automatically be called if a player is within the animation distance.
	 */
	protected void doAnimationTick() {
		return;
	}

	public final double getAnimationDistance() {
		return this.animationDistance;
	}

	public final void setAnimationDistance(double animationDistance) {
		this.animationDistance = animationDistance;
	}

	protected final void addPlayerInAnimDistance(Player player) {
		this.playersInAnimationDistance.add(player);
	}
	
	protected final HashSet<Player> getPlayersInAnimDistance() {
		return this.playersInAnimationDistance;
	}
	
	protected final void clearPlayersInAnimDistance() {
		this.playersInAnimationDistance.clear();
	}
}
