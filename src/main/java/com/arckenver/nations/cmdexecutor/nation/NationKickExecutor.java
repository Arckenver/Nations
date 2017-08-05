package com.arckenver.nations.cmdexecutor.nation;

import java.util.UUID;

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
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.cmdelement.CitizenNameElement;
import com.arckenver.nations.object.Nation;

public class NationKickExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.kick")
				.arguments(GenericArguments.optional(new CitizenNameElement(Text.of("player"))))
				.executor(new NationKickExecutor())
				.build(), "kick");
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
			if (!nation.isPresident(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_NATIONPRES));
				return CommandResult.success();
			}
			if (!ctx.<String>getOne("player").isPresent())
			{
				src.sendMessage(Text.of(TextColors.YELLOW, "/n kick <player>"));
				return CommandResult.success();
			}
			String toKick = ctx.<String>getOne("player").get();
			UUID uuid = DataHandler.getPlayerUUID(toKick);
			if (uuid == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADPLAYERNAME));
				return CommandResult.success();
			}
			if (!nation.isCitizen(uuid))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOTINNATION));
				return CommandResult.success();
			}
			if (player.getUniqueId().equals(uuid))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOKICKSELF));
				return CommandResult.success();
			}
			if (nation.isPresident(uuid))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_KICKPRESIDENT));
				return CommandResult.success();
			}
			if (nation.isMinister(uuid) && nation.isMinister(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_KICKMINISTER));
				return CommandResult.success();
			}
			nation.removeCitizen(uuid);
			DataHandler.saveNation(nation.getUUID());
			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.SUCCESS_KICK.replaceAll("\\{PLAYER\\}", toKick)));
			Sponge.getServer().getPlayer(uuid).ifPresent(
					p -> p.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.SUCCESS_KICK.replaceAll("\\{PLAYER\\}", player.getName()))));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
