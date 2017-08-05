package com.arckenver.nations.cmdexecutor.nation;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.channel.NationMessageChannel;
import com.arckenver.nations.object.Nation;

public class NationChatExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		CommandSpec chatCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.chat")
				.arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("msg"))))
				.executor(new NationChatExecutor())
				.build();
		
		cmd.child(chatCmd, "chat", "c");
		
		Sponge.getCommandManager().register(NationsPlugin.getInstance(), chatCmd, "nationchat", "nc");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			if (nation == null)
			{
				player.setMessageChannel(MessageChannel.TO_ALL);
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NONATION));
				return CommandResult.success();
			}
			NationMessageChannel channel = nation.getMessageChannel();

			if (!ctx.<String>getOne("msg").isPresent())
			{
				if (player.getMessageChannel().equals(channel)) {
					player.setMessageChannel(MessageChannel.TO_ALL);
					src.sendMessage(Text.of(TextColors.YELLOW, LanguageHandler.INFO_NATIONCHAT_OFF));
				} else {
					player.setMessageChannel(channel);
					src.sendMessage(Text.of(TextColors.YELLOW, LanguageHandler.INFO_NATIONCHATON_ON));
				}
			}
			else
			{
				Text header = TextSerializers.FORMATTING_CODE.deserialize(ConfigHandler.getNode("others", "nationChatFormat").getString().replaceAll("\\{NATION\\}", nation.getTag()).replaceAll("\\{TITLE\\}", DataHandler.getCitizenTitle(player.getUniqueId())));
				
				Text msg = Text.of(header, TextColors.RESET, player.getName(), TextColors.WHITE, ": ", TextColors.YELLOW, ctx.<String>getOne("msg").get());
				channel.send(player, msg);
				DataHandler.getSpyChannel().send(Text.of(TextSerializers.FORMATTING_CODE.deserialize(ConfigHandler.getNode("others", "nationSpyChatTag").getString()), TextColors.RESET, msg));
			}

		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
