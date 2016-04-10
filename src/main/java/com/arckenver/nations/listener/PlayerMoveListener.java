package com.arckenver.nations.listener;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Zone;

public class PlayerMoveListener
{
	@Listener
	public void onPlayerMove(DisplaceEntityEvent.TargetPlayer event, @First Player player)
	{
		if (event.getFromTransform().getLocation().getBlockX() == event.getToTransform().getLocation().getBlockX() && 
	            event.getFromTransform().getLocation().getBlockZ() == event.getToTransform().getLocation().getBlockZ())
	    {
	        return;
	    }
		if (!ConfigHandler.getNode("worlds").getNode(event.getToTransform().getExtent().getName()).getNode("enabled").getBoolean())
		{
			return;
		}
		
		Location<World> loc = event.getToTransform().getLocation();
		Nation nation = DataHandler.getNation(loc);
		Nation lastNationWalkedOn = DataHandler.getLastNationWalkedOn(player.getUniqueId());
		Zone zone = null;
		if (nation != null)
		{
			zone = nation.getZone(loc);
		}
		Zone lastZoneWalkedOn = DataHandler.getLastZoneWalkedOn(player.getUniqueId());
		if ((nation == null && lastNationWalkedOn == null) || (nation != null && lastNationWalkedOn != null && nation.getUUID().equals(lastNationWalkedOn.getUUID())))
		{
			if ((zone == null && lastZoneWalkedOn == null) || (zone != null && lastZoneWalkedOn != null && zone.getUUID().equals(lastZoneWalkedOn.getUUID())))
			{
				return;
			}
		}
		DataHandler.setLastNationWalkedOn(player.getUniqueId(), nation);
		DataHandler.setLastZoneWalkedOn(player.getUniqueId(), zone);
		
		Text.Builder builder = Text.builder("~ ").color(TextColors.GRAY);
		
		builder.append((nation == null) ? Text.of(TextColors.DARK_GREEN, LanguageHandler.IA) : Utils.nationClickable(TextColors.DARK_AQUA, nation.getName()));
		builder.append(Text.of(TextColors.GRAY, " - "));
		if (zone != null)
		{
			builder.append(Utils.zoneClickable(TextColors.GREEN, zone.getName()));
			if (zone.isForSale())
			{
				builder.append(
						Text.of(TextColors.GRAY, " - "),
						Text.of(TextColors.YELLOW, "["),
						Utils.formatPrice(TextColors.YELLOW, zone.getPrice()),
						Text.of(TextColors.YELLOW, "]")
				);
			}
			builder.append(Text.of(TextColors.GRAY, " - "));
		}
		
		builder.append((DataHandler.getFlag("pvp", loc)) ? Text.of(TextColors.DARK_RED, "(PvP)") : Text.of(TextColors.DARK_GREEN, "(No PvP)"));
		builder.append(Text.of(TextColors.GRAY, " ~"));
		
		player.sendMessage(builder.build());
	}
}
