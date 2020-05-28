package uk.lewdev.standmodels.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import uk.lewdev.standmodels.StandModelLib;

public class AsyncModelUpdater extends BukkitRunnable {
	
	private StandModelLib lib;
	private ModelManager modelManager;
	protected boolean inTick = false;
	
	public AsyncModelUpdater(StandModelLib lib, long updateTickSpeed) {
		this.lib = lib;
		this.modelManager = lib.getModelManager();
		this.runTaskTimerAsynchronously(lib.getPlugin(), 0L, updateTickSpeed);
	}

	@Override
	public void run() {
		if (this.inTick) { return; }

		this.inTick = true;
		
		this.modelManager.getStaticModels().forEach(model -> {
			model.setPlayerInRenderDistance(false);
			Location center = model.getCenter();

			for (Player player : center.getWorld().getPlayers()) {
				double distance = player.getLocation().distanceSquared(model.getCenter());

				if (distance <= square(model.getRenderDistance())) {
					model.setPlayerInRenderDistance(true);
					break;
				}
			}
		});
		
		this.modelManager.getAnimatedModels().forEach(model -> {
			model.setPlayerInRenderDistance(false);
			model.clearPlayersInAnimDistance();
			
			Location center = model.getCenter();

			for (Player player : center.getWorld().getPlayers()) {
				double distance = player.getLocation().distanceSquared(model.getCenter());

				if (distance <= square(model.getRenderDistance())) {
					model.setPlayerInRenderDistance(true);
				}
				
				if (distance <= square(model.getAnimationDistance())) {
					model.addPlayerInAnimDistance(player);
				}
			}
		});

		// Swap back to main thread to update entities
		Bukkit.getScheduler().runTask(lib.getPlugin(), () -> {
			this.modelManager.getStaticModels().forEach(model -> model.updateTick());
			this.modelManager.getAnimatedModels().forEach(model -> model.updateTick());
			
			this.inTick = false;
		});
	}
	
	protected double square(double num) {
		return num*num;
	}
}
