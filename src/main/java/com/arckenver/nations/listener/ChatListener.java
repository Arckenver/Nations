package com.arckenver.nations.listener;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent.MessageFormatter;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.channel.NationMessageChannel;
import com.arckenver.nations.object.Nation;

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
		
		MessageFormatter formater = e.getFormatter();
		
		if (chan.equals(MessageChannel.TO_ALL) && ConfigHandler.getNode("others", "enableNationTag").getBoolean(true))
		{
			e.setMessage(Text.of(TextSerializers.FORMATTING_CODE.deserialize(ConfigHandler.getNode("others", "publicChatFormat").getString().replaceAll("\\{NATION\\}", nation.getTag()).replaceAll("\\{TITLE\\}", DataHandler.getCitizenTitle(p.getUniqueId()))), formater.getHeader().toText()), formater.getBody().toText());
		}
		else if (chan instanceof NationMessageChannel)
		{
			e.setMessage(Text.of(TextSerializers.FORMATTING_CODE.deserialize(ConfigHandler.getNode("others", "nationChatFormat").getString().replaceAll("\\{NATION\\}", nation.getTag()).replaceAll("\\{TITLE\\}", DataHandler.getCitizenTitle(p.getUniqueId()))), formater.getHeader().toText()), Text.of(TextColors.YELLOW, formater.getBody().toText()));
			DataHandler.getSpyChannel().send(p, Text.of(TextSerializers.FORMATTING_CODE.deserialize(ConfigHandler.getNode("others", "nationSpyChatTag").getString()), TextColors.RESET, e.getMessage()));
		}
	}
}
