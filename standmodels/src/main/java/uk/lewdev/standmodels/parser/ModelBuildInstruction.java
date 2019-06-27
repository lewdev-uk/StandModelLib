package uk.lewdev.standmodels.parser;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import uk.lewdev.standmodels.utils.Axis;

public class ModelBuildInstruction {

	public static final String MODEL_PART_NAME = "StandModelLib-Model-Part";

	private double relativeX;
	private double relativeY;
	private double relativeZ;

	private boolean small = false;

	private ItemStack[] armour = new ItemStack[4];

	private EulerAngle headAngle;
	private EulerAngle bodyAngle;
	private EulerAngle leftArmAngle;
	private EulerAngle rightArmAngle;
	private EulerAngle leftLegAngle;
	private EulerAngle rightLegAngle;

	public ModelBuildInstruction() { }

	public ModelBuildInstruction setRelativeX(double relativeX) {
		this.relativeX = relativeX;
		return this;
	}

	public ModelBuildInstruction setRelativeY(double relativeY) {
		this.relativeY = relativeY;
		return this;
	}

	public ModelBuildInstruction setRelativeZ(double relativeZ) {
		this.relativeZ = relativeZ;
		return this;
	}

	public ModelBuildInstruction setArmour(ItemStack[] armour) {
		this.armour = armour;
		return this;
	}

	public ModelBuildInstruction setHeadAngle(EulerAngle headAngle) {
		this.headAngle = headAngle;
		return this;
	}

	public ModelBuildInstruction setBodyAngle(EulerAngle bodyAngle) {
		this.bodyAngle = bodyAngle;
		return this;
	}

	public ModelBuildInstruction setLeftArmAngle(EulerAngle leftArmAngle) {
		this.leftArmAngle = leftArmAngle;
		return this;
	}

	public ModelBuildInstruction setRightArmAngle(EulerAngle rightArmAngle) {
		this.rightArmAngle = rightArmAngle;
		return this;
	}

	public ModelBuildInstruction setLeftLegAngle(EulerAngle leftLegAngle) {
		this.leftLegAngle = leftLegAngle;
		return this;
	}

	public ModelBuildInstruction setRightLegAngle(EulerAngle rightLegAngle) {
		this.rightLegAngle = rightLegAngle;
		return this;
	}

	public ModelBuildInstruction setSmall() {
		this.small = true;
		return this;
	}

	public ArmorStand spawnStand(Location center, float rotation) {
		center = center.clone();
		Location standPos = center.clone().add(new Location(center.getWorld(), relativeX, relativeY, relativeZ));

		if (rotation > 0) {
			standPos = Axis.rotateRight(center.clone(), standPos, rotation);
		}

		ArmorStand stand = (ArmorStand) center.getWorld().spawnEntity(standPos, EntityType.ARMOR_STAND);
		// Default Setup
		stand.setVisible(false);
		stand.setGravity(false);
		stand.setCustomName(MODEL_PART_NAME);
		stand.setCustomNameVisible(false);
		stand.setInvulnerable(true);
		stand.setAI(false);
		stand.setArms(false);
		stand.setSmall(this.small);
		stand.setCollidable(true);
		stand.setCanPickupItems(false);
		stand.setRemoveWhenFarAway(false); // We handle this

		// Angle Settings
		if (this.headAngle != null) {
			stand.setHeadPose(this.headAngle);
		}

		if (this.bodyAngle != null) {
			stand.setBodyPose(this.bodyAngle);
		}

		if (this.leftArmAngle != null) {
			stand.setLeftArmPose(this.leftArmAngle);
		}

		if (this.rightArmAngle != null) {
			stand.setRightArmPose(this.rightArmAngle);
		}

		if (this.leftLegAngle != null) {
			stand.setLeftLegPose(this.leftLegAngle);
		}

		if (this.rightLegAngle != null) {
			stand.setRightLegPose(this.rightLegAngle);
		}

		// Armour setting
		stand.setBoots(this.armour[0]);
		stand.setLeggings(this.armour[1]);
		stand.setChestplate(this.armour[2]);
		stand.setHelmet(this.armour[3]);

		return stand;
	}
}