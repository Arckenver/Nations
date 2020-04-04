package com.arckenver.nations.listener;

import java.util.Optional;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Point;
import com.arckenver.nations.object.Rect;

public class GoldenAxeListener
{
	@Listener
	public void onPlayerRightClick(InteractBlockEvent.Secondary.MainHand event, @First Player player)
	{
		if (ConfigHandler.getNode("others", "enableGoldenAxe").getBoolean(true) == false)
		{
			return ;
		}
		Optional<ItemStack> optItem = player.getItemInHand(HandTypes.MAIN_HAND);
		if (!optItem.isPresent())
		{
			return;
		}
		if (optItem.get().getType().equals(ItemTypes.GOLDEN_AXE))
		{
			event.setCancelled(true);
			Optional<Location<World>> optLoc = event.getTargetBlock().getLocation();
			if (!optLoc.isPresent())
			{
				return;
			}
			Point secondPoint = new Point(optLoc.get().getExtent(), optLoc.get().getBlockX(), optLoc.get().getBlockZ());
			DataHandler.setSecondPoint(player.getUniqueId(), secondPoint);
			Point firstPoint = DataHandler.getFirstPoint(player.getUniqueId());
			if (firstPoint != null && !firstPoint.getWorld().equals(secondPoint.getWorld()))
			{
				firstPoint = null;
				DataHandler.setFirstPoint(player.getUniqueId(), firstPoint);
			}
			
			String coord = secondPoint.getX() + " " + secondPoint.getY() + ")" + ((firstPoint != null) ? " (" + new Rect(firstPoint, secondPoint).size() + ")" : "");
			player.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.AXE_SECOND.replaceAll("\\{COORD\\}", coord)));
		}
	}
	
	@Listener
	public void onPlayerLeftClick(InteractBlockEvent.Primary.MainHand event, @First Player player)
	{
		if (ConfigHandler.getNode("others", "enableGoldenAxe").getBoolean(true) == false)
		{
			return ;
		}
		Optional<ItemStack> optItem = player.getItemInHand(HandTypes.MAIN_HAND);
		if (!optItem.isPresent())
		{
			return;
		}
		if (optItem.get().getType().equals(ItemTypes.GOLDEN_AXE))
		{
			event.setCancelled(true);
			Optional<Location<World>> optLoc = event.getTargetBlock().getLocation();
			if (!optLoc.isPresent())
			{
				return;
			}
			Point firstPoint = new Point(optLoc.get().getExtent(), optLoc.get().getBlockX(), optLoc.get().getBlockZ());
			DataHandler.setFirstPoint(player.getUniqueId(), firstPoint);
			Point secondPoint = DataHandler.getSecondPoint(player.getUniqueId());
			if (secondPoint != null && !secondPoint.getWorld().equals(firstPoint.getWorld()))
			{
				secondPoint = null;
				DataHandler.setSecondPoint(player.getUniqueId(), secondPoint);
			}
			
			String coord = firstPoint.getX() + " " + firstPoint.getY() + ")" + ((secondPoint != null) ? " (" + new Rect(secondPoint, firstPoint).size() + ")" : "");
			player.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.AXE_FIRST.replaceAll("\\{COORD\\}", coord)));
		}
	}
}
