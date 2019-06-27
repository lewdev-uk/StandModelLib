package uk.lewdev.standmodels.events.custom;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import uk.lewdev.standmodels.model.Model;

public class ModelEvent extends Event {
	
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Model model;

    public ModelEvent(Model model){
        this.model = model;
    }
    
    public Model getModel() {
    	return this.model;
    }

	@Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
