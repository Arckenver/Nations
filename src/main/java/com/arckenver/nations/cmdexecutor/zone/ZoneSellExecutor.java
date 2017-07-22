package com.arckenver.nations.cmdexecutor.zone;

import java.math.BigDecimal;

import org.spongepowered.api.Sponge;
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
import com.arckenver.nations.object.Zone;

public class ZoneSellExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			Nation nation = DataHandler.getNation(player.getLocation());
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDSTANDNATION));
				return CommandResult.success();
			}
			Zone zone = nation.getZone(player.getLocation());
			if (zone == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDSTANDZONESELF));
				return CommandResult.success();
			}
			if ((!zone.isOwner(player.getUniqueId()) || nation.isAdmin()) && !nation.isStaff(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOOWNER));
				return CommandResult.success();
			}
			if (!ctx.<Double>getOne("price").isPresent())
			{
				src.sendMessage(Text.of(TextColors.YELLOW, "/z sell <price>"));
				return CommandResult.success();
			}
			BigDecimal price = BigDecimal.valueOf(ctx.<Double>getOne("price").get());
			if (price.compareTo(BigDecimal.ZERO) == -1)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADARG_P));
				return CommandResult.success();
			}
			zone.setPrice(price);
			DataHandler.saveNation(nation.getUUID());
			nation.getCitizens().forEach(
				uuid -> Sponge.getServer().getPlayer(uuid).ifPresent(
						p -> {
							String str = LanguageHandler.INFO_ZONEFORSALE.replaceAll("\\{PLAYER\\}",  player.getName()).replaceAll("\\{ZONE\\}", zone.getName());
							String[] splited = str.split("\\{AMOUNT\\}");
							src.sendMessage(Text.builder()
									.append(Text.of(TextColors.AQUA, (splited.length > 0) ? splited[0] : ""))
									.append(Utils.formatPrice(TextColors.AQUA, price))
									.append(Text.of(TextColors.AQUA, (splited.length > 1) ? splited[1] : "")).build());
						}));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
