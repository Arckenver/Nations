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

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.object.Point;
import com.arckenver.nations.object.Rect;

public class GoldenAxeListener
{
	@Listener
	public void onPlayerRightClick(InteractBlockEvent.Secondary event, @First Player player)
	{
		Optional<ItemStack> optItem = player.getItemInHand(HandTypes.MAIN_HAND);
		if (!optItem.isPresent())
		{
			return;
		}
		if (optItem.get().getItem().equals(ItemTypes.GOLDEN_AXE))
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
			player.sendMessage(Text.of(
					TextColors.AQUA,
					"Second position set to (" + secondPoint.getX() + " " + secondPoint.getY() + ")" +
					((firstPoint != null) ? " (" + new Rect(firstPoint, secondPoint).size() + ")" : "")));
		}
	}
	
	@Listener
	public void onPlayerLeftClick(InteractBlockEvent.Primary event, @First Player player)
	{
		Optional<ItemStack> optItem = player.getItemInHand(HandTypes.MAIN_HAND);
		if (!optItem.isPresent())
		{
			return;
		}
		if (optItem.get().getItem().equals(ItemTypes.GOLDEN_AXE))
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
				DataHandler.setFirstPoint(player.getUniqueId(), secondPoint);
			}
			player.sendMessage(Text.of(
					TextColors.AQUA,
					"First position set to (" + firstPoint.getX() + " " + firstPoint.getY() + ")" +
					((secondPoint != null) ? " (" + new Rect(firstPoint, secondPoint).size() + ")" : "")));
		}
	}
}
