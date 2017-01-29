package com.arckenver.nations.listener;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.management.monitor.MonitorSettingException;

/**
 * Created by Allen Truman on 1/28/2017.
 */

public class MobEnterListener {
    @Listener
    public void onMoveEntityEvent(MoveEntityEvent event) {
        if (!ConfigHandler.getNode("worlds").getNode(event.getTargetEntity().getWorld().getName()).getNode("enabled").getBoolean() || !ConfigHandler.getNode("others", "preventMobEnter").getBoolean())
        {
            return;
        }

        if (event.getTargetEntity() instanceof Monster)
        {
            if (!DataHandler.getFlag("mobs", event.getTargetEntity().getLocation()))
            {
                event.getTargetEntity().remove();
            }
        }
    }
}
