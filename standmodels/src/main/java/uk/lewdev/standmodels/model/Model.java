package uk.lewdev.standmodels.model;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import uk.lewdev.standmodels.parser.ModelBuildInstruction;
import uk.lewdev.standmodels.utils.Axis;

public class Model {

	private Location center;

	private float rotation;
	
	private double renderDistance;

	private long lastUpdate = System.currentTimeMillis(); // TimeMillis

	private final List<ModelBuildInstruction> instructions;
	private HashSet<ArmorStand> stands = new HashSet<>();

	private boolean playerInRenderDistance = false;
	
	private boolean itemsTakeable;
	
	public Model(List<ModelBuildInstruction> ins, Location center, Axis facing, Axis desired,
			double renderDistance, boolean itemsTakeable) {
		this.center = center;
		this.instructions = ins;
		this.renderDistance = renderDistance;
		this.itemsTakeable = itemsTakeable;

		this.rotation = Axis.calcRightRotationAngle(facing, desired);
	}
	
	public final boolean isRendered() {
		return ! this.stands.isEmpty();
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

	public final boolean isStandPart(ArmorStand stand) {
		return this.stands.contains(stand);
	}

	public final Location getCenter() {
		return this.center.clone();
	}
	
	public final void setCenter(Location loc) {
		loc = loc.clone();
		
		double xDiff = loc.getX() - this.center.getX();
		double yDiff = loc.getY() - this.center.getY();
		double zDiff = loc.getZ() - this.center.getZ();
		
		this.stands.stream().forEach(stand -> {
			stand.teleport(stand.getLocation().add(xDiff, yDiff, zDiff));
		});
		
		this.center = loc;
	}

	public final Long getLastUpdated() {
		return this.lastUpdate;
	}
	
	public final boolean isItemsTakeable() {
		return this.itemsTakeable;
	}

	public final double getRenderDistance() {
		return this.renderDistance;
	}

	public final void setRenderDistance(double renderDistance) {
		this.renderDistance = renderDistance;
	}

	protected final boolean shouldRender() {
		return this.playerInRenderDistance;
	}
	
	protected final void setPlayerInRenderDistance(boolean withinDistance) {
		this.playerInRenderDistance = withinDistance;
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

		this.instructions.forEach(ins -> this.stands.add(ins.spawnStand(this.center.clone(), this.rotation)));
	}

	public final void unRender() {
		if (!this.isRendered()) {
			return;
		}
		
		this.stands.forEach(stand -> stand.remove());
		this.stands.clear();
	}
}
