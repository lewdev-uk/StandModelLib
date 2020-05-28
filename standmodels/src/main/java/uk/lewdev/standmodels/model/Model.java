package uk.lewdev.standmodels.model;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import uk.lewdev.standmodels.parser.ModelBuildInstruction;
import uk.lewdev.standmodels.utils.Axis;

public class Model {

	private Location center;

	private float rotation;

	private double renderDistance;
	private double animationDistance;

	private long lastUpdate = System.currentTimeMillis(); // TimeMillis

	private final List<ModelBuildInstruction> instructions;
	private HashSet<ArmorStand> stands = new HashSet<>();

	private boolean playerInRenderDistance = false;
	protected HashSet<Player> playersInAnimationDistance = new HashSet<>();
	
	private boolean itemsTakeable;
	private boolean isAnimated;
	
	public Model(List<ModelBuildInstruction> ins, Location center, Axis facing, Axis desired,
			double renderDistance, double animationDistance, boolean itemsTakeable, boolean isAnimated) {
		this.center = center;
		this.instructions = ins;
		this.renderDistance = renderDistance;
		this.animationDistance = animationDistance;
		this.itemsTakeable = itemsTakeable;

		this.rotation = Axis.calcRightRotationAngle(facing, desired);
		this.isAnimated = isAnimated;
	}
	
	public Model(List<ModelBuildInstruction> ins, Location center, Axis facing, Axis desired,
			double renderDistance, double animationDistance, boolean isAnimated) {
		this(ins, center, facing, desired, renderDistance, animationDistance, false, isAnimated);
	}

	public Model(List<ModelBuildInstruction> ins, Location center, Axis facing, Axis desired,
			double renderDistance, double animationDistance) {
		this(ins, center, facing, desired, renderDistance, animationDistance, false, false);
	}

	/**
	 * User can override this method to update the animation, if there is one.
	 * This method will only be called if a player is within the animation distance.
	 */
	protected void doAnimationTick() {
		return;
	}
	
	public void setAnimated(boolean animated) {
		this.isAnimated = animated;
	}
	
	public boolean isAnimated() {
		return this.isAnimated;
	}

	public boolean isRendered() {
		return (this.stands != null && !this.stands.isEmpty());
	}

	public final void rotate(float rightAngle) {
		if (this.isRendered()) {
			this.stands.forEach(stand -> {
				this.rotation += rightAngle;
				stand.teleport(Axis.rotateRight(this.center, stand.getLocation(), rightAngle));
			});
		} else {
			this.rotation += rightAngle;
		}
	}

	public boolean isStandPart(ArmorStand stand) {
		return this.stands.contains(stand);
	}

	public Location getCenter() {
		return this.center.clone();
	}
	
	public void setCenter(Location loc) {
		loc = loc.clone();
		
		double xDiff = loc.getX() - this.center.getX();
		double yDiff = loc.getY() - this.center.getY();
		double zDiff = loc.getZ() - this.center.getZ();
		
		this.stands.stream().forEach(stand -> {
			stand.teleport(stand.getLocation().add(xDiff, yDiff, zDiff));
		});
		
		this.center = loc;
	}

	public Long getLastUpdated() {
		return this.lastUpdate;
	}
	
	public boolean isItemsTakeable() {
		return this.itemsTakeable;
	}

	public double getRenderDistance() {
		return this.renderDistance;
	}

	public void setRenderDistance(double renderDistance) {
		this.renderDistance = renderDistance;
	}

	public double getAnimationDistance() {
		return this.animationDistance;
	}

	public void setAnimationDistance(double animationDistance) {
		this.animationDistance = animationDistance;
	}

	protected boolean shouldRender() {
		return this.playerInRenderDistance;
	}

	protected boolean shouldAnimate() {
		return this.isAnimated && this.playersInAnimationDistance.size() >= 1;
	}
	
	protected final void setPlayerInRenderDistance(boolean withinDistance) {
		this.playerInRenderDistance = withinDistance;
	}
	
	/**
	 * Reset the players within animation and render distance, ready
	 * to be updated again by {@link AsyncModelUpdater}
	 */
	protected final void resetForUpdate() {
		this.playersInAnimationDistance.clear();
		this.playerInRenderDistance = false;
	}
	
	protected final void addPlayerInAnimDistance(Player player) {
		this.playersInAnimationDistance.add(player);
	}
	
	protected final HashSet<Player> getPlayersInAnimDistance() {
		return this.playersInAnimationDistance;
	}

	protected final void updateTick() {
		this.lastUpdate = System.currentTimeMillis();

		// Render Update
		if (this.shouldRender() && !this.isRendered()) {
			this.render();
		}

		else if (!this.shouldRender() && this.isRendered()) {
			this.unRender();
		}
	}

	protected final void render() {
		if (isRendered()) {
			return;
		}

		this.stands = new HashSet<ArmorStand>();
		this.instructions.forEach(ins -> this.stands.add(ins.spawnStand(this.center.clone(), this.rotation)));
	}

	public final void unRender() {
		if (!this.isRendered()) {
			return;
		}
		
		this.stands.forEach(stand -> stand.remove());
		this.stands = null;
	}
}
