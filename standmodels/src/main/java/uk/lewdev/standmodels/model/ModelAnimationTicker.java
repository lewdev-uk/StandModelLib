package uk.lewdev.standmodels.model;

import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import uk.lewdev.standmodels.StandModelLib;

public class ModelAnimationTicker extends BukkitRunnable {

	private StandModelLib lib;
	private boolean inTick = false;

	public ModelAnimationTicker(StandModelLib lib, long animationTickSpeed) {
		this.lib = lib;
		this.runTaskTimer(lib.getPlugin(), 10L, animationTickSpeed);
	}
	
	@Override
	public void run() {
		if (this.inTick) { return; }

		this.inTick = true;

		List<Model> models = this.lib.getModelManager().getModels();
		
		for (Model m : models) {
			if(m.shouldAnimate()) {
				m.animationTick();
			}
		}
		
		this.inTick = false;
	}
}
