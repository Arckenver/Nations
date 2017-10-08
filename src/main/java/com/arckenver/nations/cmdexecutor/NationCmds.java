package com.arckenver.nations.cmdexecutor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.cmdexecutor.nation.NationInfoExecutor;
import com.arckenver.nations.cmdexecutor.nationadmin.NationadminExecutor;
import com.arckenver.nations.cmdexecutor.nationworld.NationworldExecutor;
import com.arckenver.nations.cmdexecutor.zone.ZoneExecutor;

public class NationCmds {

	public static void create(NationsPlugin plugin)
	{

		CommandSpec.Builder nationCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nation.execute")
				.executor(new NationInfoExecutor());

		CommandSpec.Builder nationadminCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationadmin.execute")
				.executor(new NationadminExecutor());

		CommandSpec.Builder zoneCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.zone.execute")
				.executor(new ZoneExecutor());

		CommandSpec.Builder nationworldCmd = CommandSpec.builder()
				.description(Text.of(""))
				.permission("nations.command.nationworld.execute")
				.executor(new NationworldExecutor());

		createCmds(nationCmd, "com.arckenver.nations.cmdexecutor.nation");
		createCmds(nationadminCmd, "com.arckenver.nations.cmdexecutor.nationadmin");
		createCmds(zoneCmd, "com.arckenver.nations.cmdexecutor.zone");
		createCmds(nationworldCmd, "com.arckenver.nations.cmdexecutor.nationworld");

		Sponge.getCommandManager().register(plugin, nationadminCmd.build(), "nationadmin", "na", "nationsadmin");
		Sponge.getCommandManager().register(plugin, nationCmd.build(), "nation", "n", "nations");
		Sponge.getCommandManager().register(plugin, zoneCmd.build(), "zone", "z");
		Sponge.getCommandManager().register(plugin, nationworldCmd.build(), "nationworld", "nw");
	}

	private static void createCmds(CommandSpec.Builder cmd, String path)
	{
		path = path.concat(".");
		try {
			JarFile jarFile = new JarFile(URLDecoder.decode(NationsPlugin.class.getProtectionDomain().getCodeSource().getLocation().toString().split("!")[0].replaceFirst("jar:file:", ""), "UTF-8"));
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements())
			{
				JarEntry entry = entries.nextElement();
				if (entry.getName().replace("/", ".").replace("\\", ".").startsWith(path) && entry.getName().endsWith(".class") && !entry.getName().contains("$")) {
					String className = path.concat(entry.getName().substring(path.length()).replace(".class", ""));
					try {
						Class<?> cl = Class.forName(className);
						cl.getMethod("create", CommandSpec.Builder.class).invoke(null, cmd);
					} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						System.out.println(className);
						e.printStackTrace();
					}
				}
			}
			jarFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
