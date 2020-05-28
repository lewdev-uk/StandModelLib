package uk.lewdev.standmodels.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import uk.lewdev.standmodels.StandModelLib;

public class AsyncModelUpdater extends BukkitRunnable {
	
	private StandModelLib lib;
	protected boolean inTick = false;
	
	public AsyncModelUpdater(StandModelLib lib, long updateTickSpeed) {
		this.lib = lib;
		this.runTaskTimerAsynchronously(lib.getPlugin(), 0L, updateTickSpeed);
	}

	@Override
	public void run() {
		if (this.inTick) { return; }

		this.inTick = true;

		this.lib.getModelManager().getModels().forEach(model -> {
			model.resetForUpdate();
			Location center = model.getCenter();

			for (Player player : center.getWorld().getPlayers()) {
				if(player.getWorld() != model.getCenter().getWorld()) {
					continue;
				}
				
				double distance = player.getLocation().distanceSquared(model.getCenter());

				if (distance <= model.getRenderDistance()) {
					model.setPlayerInRenderDistance(true);
				}

				if (model.isAnimated() && distance <= model.getAnimationDistance()) {
					model.addPlayerInAnimDistance(player);
				}
			}
		});

		Bukkit.getScheduler().runTask(lib.getPlugin(), () -> {
			for (Model m : this.lib.getModelManager().getModels()) {
				m.updateTick();
			}
			
			this.inTick = false;
		});
	}
}
