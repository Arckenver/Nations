package com.arckenver.nations.cmdexecutor.nationadmin;

import java.math.BigDecimal;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.cmdelement.NationNameElement;
import com.arckenver.nations.object.Nation;
import com.google.common.collect.ImmutableMap;

public class NationadminEcoExecutor implements CommandExecutor
{
	public static void create(CommandSpec.Builder cmd) {
		cmd.child(CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.eco")
				.arguments(
						GenericArguments.optional(GenericArguments.choices(Text.of("give|take|set"),
								ImmutableMap.<String, String> builder()
										.put("give", "give")
										.put("take", "take")
										.put("set", "set")
										.build())),
						GenericArguments.optional(new NationNameElement(Text.of("nation"))),
						GenericArguments.optional(GenericArguments.doubleNum(Text.of("amount"))))
				.executor(new NationadminEcoExecutor())
				.build(), "eco");
	}

	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.<String>getOne("nation").isPresent() || !ctx.<String>getOne("give|take|set").isPresent() || !ctx.<String>getOne("amount").isPresent())
		{
			src.sendMessage(Text.of(TextColors.YELLOW, "/na eco <give|take|set> <nation> <amount>"));
			return CommandResult.success();
		}
		String nationName = ctx.<String>getOne("nation").get();
		BigDecimal amount = BigDecimal.valueOf(ctx.<Double>getOne("amount").get());
		String operation = ctx.<String>getOne("give|take|set").get();
		
		Nation nation = DataHandler.getNation(nationName);
		if (nation == null)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADNATIONNAME));
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
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_ECONOACCOUNT));
			return CommandResult.success();
		}
		TransactionResult result;
		if (operation.equalsIgnoreCase("give"))
		{
			result = optAccount.get().deposit(NationsPlugin.getEcoService().getDefaultCurrency(), amount, NationsPlugin.getCause());
		}
		else if (operation.equalsIgnoreCase("take"))
		{
			result = optAccount.get().withdraw(NationsPlugin.getEcoService().getDefaultCurrency(), amount, NationsPlugin.getCause());
		}
		else if (operation.equalsIgnoreCase("set"))
		{
			result = optAccount.get().setBalance(NationsPlugin.getEcoService().getDefaultCurrency(), amount, NationsPlugin.getCause());
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADARG_GTS));
			return CommandResult.success();
		}
		if (result.getResult() != ResultType.SUCCESS)
		{
			src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_ECOTRANSACTION));
			return CommandResult.success();
		}
		src.sendMessage(Text.of(TextColors.GREEN, LanguageHandler.SUCCESS_GENERAL));
		return CommandResult.success();
	}
}
