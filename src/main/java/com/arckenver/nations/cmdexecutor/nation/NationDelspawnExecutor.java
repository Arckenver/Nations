package com.arckenver.nations.cmdexecutor.nation;

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
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;

public class NationDelspawnExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.delspawn")
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))))
				.executor(new NationDelspawnExecutor())
				.build(), "delspawn");
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
			if (!ctx.<String>getOne("name").isPresent())
			{
				src.sendMessage(Text.builder()
						.append(Text.of(TextColors.AQUA, LanguageHandler.INFO_CLICK_DELSPAWN.split("\\{SPAWNLIST\\}")[0]))
						.append(Utils.formatNationSpawns(nation, TextColors.YELLOW, "delhome"))
						.append(Text.of(TextColors.AQUA, LanguageHandler.INFO_CLICK_DELSPAWN.split("\\{SPAWNLIST\\}")[1])).build());
				return CommandResult.success();
			}
			String spawnName = ctx.<String>getOne("name").get();
			if (!nation.isStaff(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_NATIONSTAFF));
				return CommandResult.success();
			}
			if (nation.getSpawn(spawnName) == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADSPAWNNAME));
				return CommandResult.success();
			}
			nation.removeSpawn(spawnName);
			DataHandler.saveNation(nation.getUUID());
			src.sendMessage(Text.of(TextColors.AQUA, LanguageHandler.SUCCESS_DELNATION));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
