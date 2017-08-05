package com.arckenver.nations.cmdexecutor.nation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.cmdelement.NationNameElement;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Request;

public class NationJoinExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.join")
				.arguments(GenericArguments.optional(new NationNameElement(Text.of("nation"))))
				.executor(new NationJoinExecutor())
				.build(), "join", "apply");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (src instanceof Player)
		{
			Player guestPlayer = (Player) src;
			if (!ctx.<String>getOne("nation").isPresent())
			{
				src.sendMessage(Text.of(TextColors.YELLOW, "/n join <nation>"));
				return CommandResult.success();
			}
			if (DataHandler.getNationOfPlayer(guestPlayer.getUniqueId()) != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NEEDLEAVE));
				return CommandResult.success();
			}
			String nationName = ctx.<String>getOne("nation").get();
			Nation nation = DataHandler.getNation(nationName);
			if (nation == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADNATIONNAME));
				return CommandResult.success();
			}
			
			Request req = DataHandler.getJoinRequest(nation.getUUID(), guestPlayer.getUniqueId());
			if (req != null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_ALREADYASKED));
				return CommandResult.success();
			}
			req = DataHandler.getInviteRequest(nation.getUUID(), guestPlayer.getUniqueId());
			if (nation.getFlag("open") || req != null)
			{
				if (req != null)
				{
					DataHandler.removeInviteRequest(req);
				}
				for (UUID uuid : nation.getCitizens())
				{
					Optional<Player> optPlayer = Sponge.getServer().getPlayer(uuid);
					if (optPlayer.isPresent())
						optPlayer.get().sendMessage(Text.of(TextColors.GREEN, LanguageHandler.INFO_JOINNATIONANNOUNCE.replaceAll("\\{PLAYER\\}", guestPlayer.getName())));
				}
				nation.addCitizen(guestPlayer.getUniqueId());
				guestPlayer.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.INFO_JOINNATION.replaceAll("\\{NATION\\}", nation.getName())));
				DataHandler.saveNation(nation.getUUID());
				return CommandResult.success();
			}
			ArrayList<UUID> nationStaff = nation.getStaff();
			List<Player> nationStaffPlayers = nationStaff
					.stream()
					.filter(uuid -> Sponge.getServer().getPlayer(uuid).isPresent())
					.map(uuid -> Sponge.getServer().getPlayer(uuid).get())
					.collect(Collectors.toList());
			
			if (nationStaffPlayers.isEmpty())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOSTAFFONLINE));
				return CommandResult.success();
			}
			DataHandler.addJoinRequest(new Request(nation.getUUID(), guestPlayer.getUniqueId()));
			for (Player p : nationStaffPlayers)
			{
				String str = LanguageHandler.INFO_CLICK_JOINREQUEST.replaceAll("\\{PLAYER\\}", guestPlayer.getName());
				p.sendMessage(Text.builder()
						.append(Text.of(TextColors.AQUA, str.split("\\{CLICKHERE\\}")[0]))
						.append(Text.builder(LanguageHandler.CLICKME)
								.onClick(TextActions.runCommand("/nation invite " + guestPlayer.getName()))
								.color(TextColors.DARK_AQUA)
								.build())
						.append(Text.of(TextColors.AQUA, str.split("\\{CLICKHERE\\}")[1])).build());
			}
			src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.INFO_INVITSEND.replaceAll("\\{RECEIVER\\}", nationName)));
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
