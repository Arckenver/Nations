package com.arckenver.nations.cmdexecutor.nation;

import java.math.BigDecimal;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.Utils;
import com.arckenver.nations.object.Nation;

public class NationBuyextraExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.buyextra")
				.arguments(GenericArguments.optional(GenericArguments.integer(Text.of("amount"))))
				.executor(new NationBuyextraExecutor())
				.build(), "buyextra");
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
			if (!nation.isStaff(player.getUniqueId()))
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_NATIONPRES));
				return CommandResult.success();
			}
			if (!ctx.<String>getOne("amount").isPresent())
			{
				src.sendMessage(Text.of(TextColors.YELLOW, "/n buyextra <amount>"));
				return CommandResult.success();
			}
			int n = ctx.<Integer>getOne("amount").get();
			int maxToBuy = ConfigHandler.getNode("others", "maxExtra").getInt() - nation.getExtras();
			if (n > maxToBuy)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOMOREBLOCK.replaceAll("\\{NUM\\}", Integer.toString(maxToBuy))));
				return CommandResult.success();
			}

			if (NationsPlugin.getEcoService() == null)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOECO));
				return CommandResult.success();
			}
			Optional<Account> optAccount = NationsPlugin.getEcoService().getOrCreateAccount("nation-" + nation.getUUID().toString());
			if (!optAccount.isPresent())
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_ECONONATION));
				return CommandResult.success();
			}
			BigDecimal price = BigDecimal.valueOf(n * ConfigHandler.getNode("prices", "extraPrice").getDouble());
			TransactionResult result = optAccount.get().withdraw(NationsPlugin.getEcoService().getDefaultCurrency(), price, NationsPlugin.getCause());
			if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS)
			{
				String[] splited = LanguageHandler.ERROR_NEEDMONEY.split("\\{AMOUNT\\}");
				src.sendMessage(Text.builder()
						.append(Text.of(TextColors.RED, (splited.length > 0) ? splited[0] : ""))
						.append(Utils.formatPrice(TextColors.RED, price))
						.append(Text.of(TextColors.RED, (splited.length > 1) ? splited[1] : "")).build());
				return CommandResult.success();
			}
			else if (result.getResult() != ResultType.SUCCESS)
			{
				src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_ECOTRANSACTION));
				return CommandResult.success();
			}

			nation.addExtras(n);
			DataHandler.saveNation(nation.getUUID());
			String[] splited2 = LanguageHandler.SUCCESS_ADDBLOCKS.replaceAll("\\{NUM\\}", Integer.toString(n)).split("\\{AMOUNT\\}");
			src.sendMessage(Text.builder()
					.append(Text.of(TextColors.AQUA, (splited2.length > 0) ? splited2[0] : ""))
					.append(Utils.formatPrice(TextColors.AQUA, price))
					.append(Text.of(TextColors.AQUA, (splited2.length > 1) ? splited2[1] : "")).build());
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NOPLAYER));
		}
		return CommandResult.success();
	}
}
