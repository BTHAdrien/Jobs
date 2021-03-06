package com.gamingmesh.jobs.hooks.MythicMobs;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.MMKillInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;

public class MythicMobs4Listener implements Listener {

    private Jobs plugin;

    public MythicMobs4Listener(Jobs plugin) {
	this.plugin = plugin;
    }

    @EventHandler
    public void OnMythicMobDeath(MythicMobDeathEvent event) {
	// make sure plugin is enabled
	if (!plugin.isEnabled())
	    return;

	//disabling plugin in world
	if (event.getEntity() != null && !Jobs.getGCManager().canPerformActionInWorld(event.getEntity().getWorld()))
	    return;

	// Entity that died must be living
	if (!(event.getEntity() instanceof LivingEntity))
	    return;

	Player pDamager = null;

	// Checking if killer is player
	Entity ent = null;
	if (event.getKiller() instanceof Player)
	    pDamager = (Player) event.getKiller();
	// Checking if killer is tamed animal
	else if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
	    ent = ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
	} else
	    return;

	if (pDamager == null)
	    return;

	// check if in creative
	if (pDamager.getGameMode().equals(GameMode.CREATIVE) && !pDamager.hasPermission("jobs.paycreative") && !Jobs.getGCManager().payInCreative())
	    return;

	if (!Jobs.getPermissionHandler().hasWorldPermission(pDamager, pDamager.getLocation().getWorld().getName()))
	    return;

	// pay
	JobsPlayer jDamager = Jobs.getPlayerManager().getJobsPlayer(pDamager);
	if (jDamager == null)
	    return;

	MythicMob lVictim = event.getMobType();
	if (lVictim == null) {
	    return;
	}

	Jobs.action(jDamager, new MMKillInfo(lVictim.getInternalName(), ActionType.MMKILL), ent);
    }
}
