package com.arckenver.nations.cmdexecutor.nation;

import static org.spongepowered.api.util.SpongeApiTranslationHelper.t;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;

public class NationFlagExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CI));
				return CommandResult.success();
			}
			if (!nation.isStaff(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CK));
				return CommandResult.success();
			}
			String flag = ctx.<String>getOne("flag").get();
			if (!player.hasPermission("nations.command.nation.flag." + flag))
			{
				player.sendMessage(t("You do not have permission to use this command!"));
				return CommandResult.success();
			}
			boolean bool = (ctx.<Boolean>getOne("bool").isPresent()) ? ctx.<Boolean>getOne("bool").get() : !nation.getFlag(flag);
			nation.setFlag(flag, bool);
			DataHandler.saveNation(nation.getUUID());
			int clicker = Utils.CLICKER_DEFAULT;
			if (src.hasPermission("nations.command.nationadmin"))
			{
				clicker = Utils.CLICKER_ADMIN;
			}
			src.sendMessage(Utils.formatNationDescription(nation, clicker));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.CA));
		}
		return CommandResult.success();
	}
}
