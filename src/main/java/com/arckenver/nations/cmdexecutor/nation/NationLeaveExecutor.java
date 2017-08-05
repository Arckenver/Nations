package com.arckenver.nations.cmdexecutor.nation;

import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Nation;

public class NationLeaveExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.leave")
				.arguments()
				.executor(new NationLeaveExecutor())
				.build(), "leave", "quit");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NONATION));
				return CommandResult.success();
			}
			if (nation.isPresident(player.getUniqueId()))
			{
				if (nation.getNumCitizens() > 1)
				{
					src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDRESIGN));
					return CommandResult.success();
				}
				nation.removeCitizen(player.getUniqueId());
				src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.SUCCESS_LEAVENATION));
				DataHandler.removeNation(nation.getUUID());
				MessageChannel.TO_ALL.send(Text.of(TextColors.AQUA, LanguageHandler.INFO_NATIONFALL.replaceAll("\\{NATION\\}", nation.getName())));
				return CommandResult.success();
			}
			nation.removeCitizen(player.getUniqueId());
			DataHandler.saveNation(nation.getUUID());
			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.SUCCESS_LEAVENATION));
			for (UUID citizen : nation.getCitizens())
			{
				Sponge.getServer().getPlayer(citizen).ifPresent(
						p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.INFO_LEAVENATION.replaceAll("\\{PLAYER\\}", player.getName()))));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
