package uk.lewdev.standmodels.events.custom;

import org.bukkit.entity.Player;

import uk.lewdev.standmodels.model.Model;

public class ModelInteractEvent extends ModelEvent {
	
	private final Player interactor;

	public ModelInteractEvent(Model model, Player interactor) {
		super(model);
		this.interactor = interactor;
	}
	
	public Player getInteractor() {
		return this.interactor;
	}
}