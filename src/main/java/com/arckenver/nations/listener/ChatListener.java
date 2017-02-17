package com.arckenver.nations.listener;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.channel.NationMessageChannel;
import com.arckenver.nations.object.Nation;

@Plugin(id = "spongenationtag", name = "Sponge Nation Chat Tag", version = "1.1",
description = "Towny like chat formating", authors = {"Carrot"})
public class ChatListener
{

	@Listener(order = Order.LATE)
	public void onPlayerChat(MessageChannelEvent.Chat e, @First Player p)
	{
		Nation nation = DataHandler.getNationOfPlayer(p.getUniqueId());
		if (nation == null)
		{
			return;
		}
		MessageChannel chan = MessageChannel.TO_ALL;
		Optional<MessageChannel> channel = e.getChannel();
		if (channel.isPresent())
		{
			chan = channel.get();
		}
		
		if (chan.equals(MessageChannel.TO_ALL) && ConfigHandler.getNode("others", "enableNationTag").getBoolean(true))
		{
			e.setMessage(Text.of(TextColors.WHITE, " [", TextColors.DARK_AQUA, nation.getName(), TextColors.WHITE,  "] "), e.getMessage());
		}
		else if (chan instanceof NationMessageChannel)
		{
			e.setMessage(Text.of(TextColors.WHITE, " {", TextColors.YELLOW, nation.getName(), TextColors.WHITE,  "} "), Text.of(TextColors.YELLOW, e.getMessage()));
			DataHandler.getSpyChannel().send(p, Text.of(TextColors.WHITE, " [", TextColors.RED, "SpyChat", TextColors.WHITE,  "]", TextColors.RESET, e.getMessage()));
		}
	}
}
