package uk.lewdev.standmodels.model;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import uk.lewdev.standmodels.StandModelLib;

/**
 * 
 */
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

		List<Model> models = this.lib.getModelManager().getModels();

		for (Model m : models) {
			Location center = m.getCenter();

			m.setPlayerInRenderDistance(false);

			HashSet<Player> playerInAnimDis = new HashSet<Player>();

			for (Player player : center.getWorld().getPlayers()) {
				double distance = player.getLocation().distance(m.getCenter());

				if (distance <= m.getRenderDistance()) {
					m.setPlayerInRenderDistance(true);
				}

				if (m.isAnimated() && distance <= m.getAnimationDistance()) {
					playerInAnimDis.add(player);
				}
			}
			
			if(m.isAnimated()) {
				m.setPlayersInAnimationDistance(playerInAnimDis);
			}
		}

		Bukkit.getScheduler().runTask(lib.getPlugin(), () -> {
			for (Model m : models) {
				m.updateTick();
			}
			
			this.inTick = false;
		});
	}
}
