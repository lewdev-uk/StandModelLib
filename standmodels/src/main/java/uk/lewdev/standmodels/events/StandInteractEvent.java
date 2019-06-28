package uk.lewdev.standmodels.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import uk.lewdev.standmodels.StandModelLib;
import uk.lewdev.standmodels.events.custom.ModelInteractEvent;
import uk.lewdev.standmodels.model.Model;
import uk.lewdev.standmodels.parser.ModelBuildInstruction;

public class StandInteractEvent implements Listener {

	private StandModelLib lib;

	public StandInteractEvent(StandModelLib lib) {
		this.lib = lib;
	}

	@EventHandler
	public void onStandManip(PlayerArmorStandManipulateEvent event) {
		event.setCancelled(this.handleInteract(event.getRightClicked(), event.getPlayer()));
	}

	@EventHandler
	public void onStandInteract(PlayerInteractEntityEvent event) {
		
		event.setCancelled(this.handleInteract(event.getRightClicked(), event.getPlayer()));
	}

	/**
	 * 
	 * @param e
	 * @param p
	 * @return True if event should cancel, else False.
	 */
	protected boolean handleInteract(Entity e, Player p) {
		if (e instanceof ArmorStand && e.getName().equals(ModelBuildInstruction.MODEL_PART_NAME)) {
			ArmorStand stand = (ArmorStand) e;
			Model m = this.lib.getModelManager().getModel(stand);
			if (m != null) {
				ModelInteractEvent event = new ModelInteractEvent(m, p);
				Bukkit.getPluginManager().callEvent(event);
				return ! m.isItemsTakeable();
			}
		}
		return false;
	}
}
