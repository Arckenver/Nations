package com.arckenver.nations.cmdexecutor;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;

public class NationadminFlagExecutor implements CommandExecutor
{
	@Override
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("nation").isPresent() || !ctx.<String>getOne("flag").isPresent() || !ctx.<String>getOne("bool").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na flag <nation> <flag> <true|false>"));
			return CommandResult.success();
		}
		String nationName = ctx.<String>getOne("nation").get();
		Nation nation = DataHandler.getNation(nationName);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CB));
			return CommandResult.success();
		}
		String flag = ctx.<String>getOne("flag").get();
		boolean bool = (ctx.<Boolean>getOne("bool").isPresent()) ? ctx.<Boolean>getOne("bool").get() : !nation.getFlag(flag);
		nation.setFlag(flag, bool);
		DataHandler.saveNation(nation.getUUID());
		src.sendMessage(Utils.formatNationDescription(nation, false));
		return CommandResult.success();
	}
}
