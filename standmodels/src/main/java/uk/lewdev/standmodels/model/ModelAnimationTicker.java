package uk.lewdev.standmodels.model;

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

		this.lib.getModelManager().getAnimatedModels().stream()
			.filter(model -> model.shouldAnimate())
			.forEach(model -> model.doAnimationTick());
		
		this.inTick = false;
	}
}
