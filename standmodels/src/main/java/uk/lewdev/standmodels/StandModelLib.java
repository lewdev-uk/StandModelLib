package uk.lewdev.standmodels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import uk.lewdev.standmodels.events.StandInteractEvent;
import uk.lewdev.standmodels.model.AsyncModelUpdater;
import uk.lewdev.standmodels.model.ModelAnimationTicker;
import uk.lewdev.standmodels.model.ModelManager;
import uk.lewdev.standmodels.parser.ModelBuildInstruction;

public class StandModelLib {

	private final JavaPlugin plugin;
	private ModelManager modelManager;
	private List<BukkitRunnable> tasks = new ArrayList<>();
	private List<Listener> events = new ArrayList<>();

	/**
	 * @param plugin
	 * @param updateTickSpeed    How many server ticks between the render state and
	 *                           player within the animation distance is updated.
	 *                           This is performed asynchronous and will never
	 *                           overlap if an update task is still running.
	 * @param animationTickSpeed How many server ticks between animations updating.
	 *                           If speed is lower than 1, then animation ticking
	 *                           will be disabled.
	 */
	public StandModelLib(JavaPlugin plugin, long updateTickSpeed, long animationTickSpeed) {
		this.plugin = plugin;
		this.modelManager = new ModelManager(plugin);

		this.initTasks(updateTickSpeed, animationTickSpeed);
		this.initEvents();
	}

	/**
	 * Creates the lib, without animations.
	 * 
	 * @param plugin
	 * @param updateTickSpeed How many server ticks between the render state and
	 *                        player within the animation distance is updated. This
	 *                        is performed asynchronous and will never overlap if an
	 *                        update task is still running.
	 */
	public StandModelLib(JavaPlugin plugin, long updateTickSpeed) {
		this(plugin, updateTickSpeed, -1);
	}
	
	/**
	 * Creates the lib with the default parameters:
	 * - updateTickSpeed 15
	 * - animationTicks disabled
	 * 
	 * @param plugin
	 */
	public StandModelLib(JavaPlugin plugin) {
		this(plugin, 15, -1);
	}

	private void initTasks(long updateTickSpeed, long animationTickSpeed) {
		this.tasks.add(new AsyncModelUpdater(this, updateTickSpeed));

		if (animationTickSpeed > 0) {
			this.tasks.add(new ModelAnimationTicker(this, animationTickSpeed));
		}
	}

	private void initEvents() {
		StandInteractEvent event = new StandInteractEvent(this);
		this.events.add(event);
	}

	public JavaPlugin getPlugin() {
		return this.plugin;
	}

	public ModelManager getModelManager() {
		if (this.modelManager == null) {
			throw new IllegalStateException("Cannot continue after StandModelLib has been destroyed.");
		}

		return this.modelManager;
	}

	/**
	 * Stop all tasks and unregister all events.
	 * Then removes all stands from the world.
	 */
	public void destroy() {
		this.tasks.forEach(task -> task.cancel());
		this.tasks = null;

		this.events.forEach(event -> HandlerList.unregisterAll(event));
		this.events = null;

		this.modelManager.getModels().forEach(model -> this.getModelManager().removeModel(model));
		this.modelManager = null;
	}

	/**
	 * Remove all models parts, from all worlds. Should only be used if something
	 * has gone wrong.
	 * 
	 * Plugins should call {@link StandModelLib#destroy()} onDisable();
	 */
	public static void cleanupStands() {
		for (World world : Bukkit.getWorlds()) {
			for (Entity ent : world.getEntities()) {
				if (ent.getType() == EntityType.ARMOR_STAND) {
					if (ent.getName().equals(ModelBuildInstruction.MODEL_PART_NAME)) {
						ent.remove();
					}
				}
			}
		}
	}
}
