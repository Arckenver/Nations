package com.arckenver.nations.cmdexecutor.nation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;

public class NationListExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.list")
				.arguments()
				.executor(new NationListExecutor())
				.build(), "list", "l");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		List<Text> contents = new ArrayList<>();
		Iterator<Nation> iter = DataHandler.getNations().values().iterator();
		if (!iter.hasNext())
		{
			contents.add(Text.of(TextColors.YELLOW, LanguageHandler.ERROR_NONATIONYET));
		}
		else
		{
			while (iter.hasNext())
			{
				Nation nation = iter.next();
				if (!nation.isAdmin() || src.hasPermission("nations.admin.nation.listall"))
				{
					contents.add(Text.of(Utils.nationClickable(TextColors.YELLOW, nation.getRealName()), TextColors.GOLD, " [" + nation.getNumCitizens() + "]"));
				}
			}
		}
		PaginationList.builder()
		.title(Text.of(TextColors.GOLD, "{ ", TextColors.YELLOW, LanguageHandler.HEADER_NATIONLIST, TextColors.GOLD, " }"))
		.contents(contents)
		.padding(Text.of("-"))
		.sendTo(src);
		return CommandResult.success();
	}
}
