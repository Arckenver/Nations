package com.arckenver.nations.cmdelement;

import java.util.ArrayList;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.Text;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.object.Nation;

public class AccountOwnerElement extends PatternMatchingCommandElement
{
	public AccountOwnerElement(Text key)
	{
		super(key);
	}
	
	@Override
	protected Object parseValue(CommandSource src, CommandArgs args) throws ArgumentParseException
	{
		return args.next();
	}
	
	@Override
	protected Iterable<String> getChoices(CommandSource src)
	{
		ArrayList<String> list = new ArrayList<String>();
		for (Nation nation : DataHandler.getNations().values())
		{
			list.add(nation.getRealName());
		}
		list.addAll(DataHandler.getPlayerNames());
		return list;
	}

	@Override
	protected Object getValue(String choice) throws IllegalArgumentException
	{
		return choice;
	}

	public Text getUsage(CommandSource src)
	{
		return Text.EMPTY;
	}
}
