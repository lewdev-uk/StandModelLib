package uk.lewdev.standmodels.model;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

public class ModelManager {
	
	private final JavaPlugin plugin;
	
	private List<Model> models = new ArrayList<Model>();
	
	public ModelManager(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	public JavaPlugin getPlugin() {
		return this.plugin;
	}
	
	public void spawnModel(Model model) {
		this.models.add(model);
	}
	
	public void removeModel(Model model) {
		this.models.remove(model);
		model.unRender();
	}
	
	public List<Model> getModels() {
		return this.models;
	}
	
	public Model getModel(ArmorStand stand) {
		for (Model m : this.models) {
			if(m.isStandPart(stand)) {
				return m;
			}
		}
		return null;
	}
}