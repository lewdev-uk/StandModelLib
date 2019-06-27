package uk.lewdev.standmodels.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import uk.lewdev.standmodels.utils.UMaterial;

/**
 * Parses generated command block String from https://mrgarretto.com/armorstand/
 * <br>
 * <b>Note:</b> When using the website: the command must be generated as
 * "Single" and only use "Medium" or "Large" pieces. That means NO part can be
 * "Small" or "Solid" otherwise it will break the plugin. <br>
 * <b>Note 2:</b> This Parser is a quick and dirty way to extract armour stand
 * summon commands from a specific string. It is not a full command parser. I
 * use Regex to extract the data we need through out the process.
 */
public class ModelSpawnCommandParser {

	private String command;

	List<ModelBuildInstruction> instructions;

	public ModelSpawnCommandParser(String command) {
		this.command = command;
		this.instructions = this.parse();
	}

	public List<ModelBuildInstruction> getInstructions() {
		return this.instructions;
	}

	private List<ModelBuildInstruction> parse() {

		List<ModelBuildInstruction> instructions = new ArrayList<>();

		// Selects the commands out of the string, which would of been inserted into
		// command blocks.

		Matcher m = Pattern.compile("\\\".*?\\\"").matcher(this.command);

		while (m.find()) {
			String subCmd = m.group();
			subCmd = subCmd.replace('"', Character.MIN_VALUE).trim();

			String[] args = subCmd.split(" ");

			if (args.length >= 6) {
				String cmd = args[0];
				if (!cmd.equals("summon")) {
					continue;
				}

				String entity = args[1];
				if (!entity.equals("armor_stand")) {
					continue;
				}

				ModelBuildInstruction ins = new ModelBuildInstruction();

				double relativeX = Double.parseDouble(args[2].replace('~', Character.MIN_VALUE));
				double relativeY = Double.parseDouble(args[3].replace('~', Character.MIN_VALUE)) + 3;
				double relativeZ = Double.parseDouble(args[4].replace('~', Character.MIN_VALUE));

				ins.setRelativeX(relativeX).setRelativeY(relativeY).setRelativeZ(relativeZ);

				String entityData = args[5];

				parseEntityData(entityData, ins);

				instructions.add(ins);
			}
		}

		return instructions;
	}

	private void parseEntityData(String entityData, ModelBuildInstruction ins) {
		if (entityData.startsWith("{")) {
			entityData = entityData.replaceFirst("\\{", "");
		}
		
		String[] attributes = entityData.split(firstLevelSplit(','));

		for (String att : attributes) {
			// Format is now
			// Pose:{Head:[-60f,0f,0f],Leg:[0f,0f,0f]}
			// ArmorItems:[{},{},{},{Count:1,id:end_rod}]}
			// DisabledSlots:4096

			String[] attArr = att.split(firstLevelSplit(':'));
			String attType = attArr[0];
			String attData = attArr[1];

			if (attType.equals("Small")) {
				if (attData.equals("1")) {
					ins.setSmall();
				}
			}

			if (attType.equals("Pose")) {
				parsePoseData(attData, ins);
			}

			if (attType.equals("ArmorItems")) {
				parseArmourData(attData, ins);
			}

			// We don't care about other attributes, as they are the same for every spawn
			// and hard-coded in the spawn method.
		}
	}

	private void parsePoseData(String data, ModelBuildInstruction ins) {
		data = removeAll(data, '{', '}');
		
		// Data is in format
		// Head:[-60f,0f,0f]
		// Head:[-60f,0f,0f],Leg:[0f,0f,0f]
		String[] poses = data.split(firstLevelSplit(','));

		for (int i = 0; i < poses.length; i++) {
			// Format= Head:[-60f,0f,0f]

			String[] poseArgs = poses[i].split(":");

			String standPart = poseArgs[0].trim(); // Format= Head

			String anglesStr = removeAll(poseArgs[1], '[', ']', 'f'); // Format= -60f,0f,0f

			String[] angleStrArray = anglesStr.split(","); // We now have individual floats as strings
			
			double x = Double.parseDouble(angleStrArray[0]);
			double y = Double.parseDouble(angleStrArray[1]);
			double z = Double.parseDouble(angleStrArray[2]);
			
			//Minecraft uses degrees, where as Spigot uses radians.
			x = Math.toRadians(x);
			y = Math.toRadians(y);
			z = Math.toRadians(z);

			EulerAngle angle = new EulerAngle(x,y,z);
			
			if (standPart.equals("Head")) {
				ins.setHeadAngle(angle);
			}

			else if (standPart.equals("Body")) {
				ins.setBodyAngle(angle);
			}

			else if (standPart.equals("LeftArm")) {
				ins.setLeftArmAngle(angle);
			}

			else if (standPart.equals("RightArm")) {
				ins.setRightArmAngle(angle);
			}

			else if (standPart.equals("LeftLeg")) {
				ins.setLeftLegAngle(angle);
			}

			else if (standPart.equals("RightLeg")) {
				ins.setRightLegAngle(angle);
			}
		}
	}

	private void parseArmourData(String data, ModelBuildInstruction ins) {
		data = removeAll(data, '[', ']');

		String[] armourPartData = data.split(firstLevelSplit(','));
		ItemStack[] armour = new ItemStack[4];

		int i = 0;
		for (String section : armourPartData) {
			section = section.replace('{', Character.MIN_VALUE).replace('}', Character.MIN_VALUE);

			if (section.contains(",")) {
				String[] itemData = section.split(",");

				String matName = null;
				byte mData = 0;
				int amount = 1;

				for (String attribute : itemData) {
					String attName = attribute.split(":")[0].trim();
					String attValue = attribute.split(":")[1].trim();

					if (attName.equals("id")) {
						matName = attValue;
					} else if (attName.equals("Damage")) {
						mData = Byte.parseByte(attValue);
					} else if (attName.equals("Count")) {
						amount = Integer.parseInt(attValue);
					}
				}
				
				@SuppressWarnings("deprecation")
				ItemStack item = UMaterial.valueOf(matName, mData);
				item.setAmount(amount);
				armour[i] = item;
			}
			i++;
		}

		ins.setArmour(armour);
	}

	/**
	 * Splits on character that are not inside of an array [x,x] or a brace {....}
	 * i.e. "First level split", without going deeper.
	 * 
	 * @param c char to split on
	 * @return regex string
	 */
	private String firstLevelSplit(char c) {
		return c + "+(?![^{]*})+(?![^\\[]*\\])";
	}

	private String removeAll(String str, char... cs) {
		String regex = "";
		for (char c : cs) {
			if (!regex.isEmpty()) {
				regex += "|";
			}
			regex += Pattern.quote(c + "");
		}
		str = str.replaceAll(regex, "");
		return str;
	}
}
