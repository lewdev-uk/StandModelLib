package uk.lewdev.standmodels;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class StandModelPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		Bukkit.getLogger().log(Level.INFO, "[StandModelLib] has been enabled. You should not have to run this as a plugin - Developers can add this as a maven dependency.");
	}
	
	@Override
	public void onDisable() {
		Bukkit.getLogger().log(Level.INFO, "[StandModelLib] has been disabled.");
	}
}