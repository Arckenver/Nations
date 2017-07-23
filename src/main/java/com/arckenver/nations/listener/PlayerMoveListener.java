package com.arckenver.nations.listener;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.serializer.TextSerializers;
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
	public void onPlayerMove(MoveEntityEvent event, @First Player player)
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

		String toast;

		if (nation == null) {
			toast = ConfigHandler.getNode("toast", "wild").getString();
		} else {
			toast = (zone == null ? ConfigHandler.getNode("toast", "nation").getString() : ConfigHandler.getNode("toast", "zone").getString());
		}
		
		String formatPresident = "";
		
		if (nation != null && !nation.isAdmin()) {
			formatPresident = ConfigHandler.getNode("toast", "formatPresident").getString()
					.replaceAll("\\{TITLE\\}", DataHandler.getCitizenTitle(nation.getPresident()))
					.replaceAll("\\{NAME\\}", DataHandler.getPlayerName(nation.getPresident()));
		}
		
		String formatZoneName = "";
		String formatZoneOwner = "";
		String formatZonePrice = "";
		
		if (zone != null) {
			if (zone.isNamed())
				formatZoneName = ConfigHandler.getNode("toast", "formatZoneName").getString().replaceAll("\\{ARG\\}", zone.getName()) + " ";
			if (zone.isOwned())
				formatZoneOwner = ConfigHandler.getNode("toast", "formatZoneOwner").getString().replaceAll("\\{ARG\\}", DataHandler.getPlayerName(zone.getOwner())) + " ";
			if (zone.isForSale())
				formatZonePrice = ConfigHandler.getNode("toast", "formatZonePrice").getString().replaceAll("\\{ARG\\}", Utils.formatPricePlain(zone.getPrice())) + " ";
		}
		
		String formatPvp;
		
		if (DataHandler.getFlag("pvp", loc)) {
			formatPvp = ConfigHandler.getNode("toast", "formatPvp").getString().replaceAll("\\{ARG\\}", LanguageHandler.TOAST_PVP);
		} else {
			formatPvp = ConfigHandler.getNode("toast", "formatNoPvp").getString().replaceAll("\\{ARG\\}", LanguageHandler.TOAST_NOPVP);
		}
		
		if (nation != null) {
			toast = toast.replaceAll("\\{NATION\\}", nation.getName());
		} else {
			toast = toast.replaceAll("\\{WILD\\}", LanguageHandler.TOAST_WILDNAME);
		}


		Text finalToast = TextSerializers.FORMATTING_CODE.deserialize(toast
				.replaceAll("\\{FORMATPRESIDENT\\}", formatPresident)
				.replaceAll("\\{FORMATZONENAME\\}", formatZoneName)
				.replaceAll("\\{FORMATZONEOWNER\\}", formatZoneOwner)
				.replaceAll("\\{FORMATZONEPRICE\\}", formatZonePrice)
				.replaceAll("\\{FORMATPVP\\}", formatPvp));

		player.sendMessage(ChatTypes.ACTION_BAR, finalToast);
		MessageChannel.TO_CONSOLE.send(Text.of(player.getName(), " entered area ", finalToast));
	}
}
