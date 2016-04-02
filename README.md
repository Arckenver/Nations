# Nations

*See official sponge forums thread [here]().*

Nations is a minecraft plugin for [SpongeAPI](https://github.com/SpongePowered/SpongeAPI), similar to [Towny Advanced](http://towny.palmergames.com/). It enables player to create nations, (what was called towns in Towny), invite players to join their nations, claim land etc.

Nation has a president, who can appoint ministers with same permissions as him. President and ministers can create zones that are buyable by citizens of the nation. The owner of a zone has all permissions on it and can have coowners.

This plugin's specificity is that land can be claimed by selecting an area with a golden axe (right/left click). Selection is considered to be vertically expanded. Thanks to that, nation's territory and zones are precisely defined and not subject to plot constraints.

## Prerelease and issues

The plugin is yet in early developpement phase and many things have yet to be done. Please report any issue in the [official sponge forums thread]().

Grab latest release [here](https://github.com/Arckenver/Nations/releases).

## Commands

Here is the full list of commands
```
/na
/na setpres <nation> <president>
/na setname <oldname> <newname>
/nation (or /n)
/n info [nation]
/n here
/n list
/n citizen <player>
/n create <name>
/n deposit <amount>
/n withdraw <amount>
/n claim
/n claim outpost
/n unclaim
/n invite <player>
/n join <nation>
/n kick <player>
/n leave
/n resign <successor>
/n spawn <spawn>
/n setspawn <spawn>
/n delspawn <spawn>
/n buyextra <amount>
/n minister <add|remove> <player>
/n perm <type> <perm> <true|false>
/n flag <flag> <true|false>
/zone (or /z)
/z info [zone]
/z list
/z create <name>
/z coowner <add|remove> <player>
/z setowner <player>
/z delowner
/z perm <type> <perm> <true|false>
/z flag <flag> <true|false>
/z sell <amount>
/z buy
```

## Permissions

Here is the full list of permissions
```
nations.admin.bypass.perm.build
nations.admin.bypass.perm.interact
nations.admin.zone.listall
nations.command.nationadmin
nations.command.nationadmin.setpres
nations.command.nationadmin.setname
nations.command.nation
nations.command.nation.info
nations.command.nation.here
nations.command.nation.list
nations.command.nation.citizen
nations.command.nation.create
nations.command.nation.deposit
nations.command.nation.withdraw
nations.command.nation.claim
nations.command.nation.claim.outpost
nations.command.nation.unclaim
nations.command.nation.invite
nations.command.nation.join
nations.command.nation.kick
nations.command.nation.leave
nations.command.nation.resign
nations.command.nation.spawn
nations.command.nation.setspawn
nations.command.nation.delspawn
nations.command.nation.buyextra
nations.command.nation.minister
nations.command.nation.perm
nations.command.nation.flag
nations.command.zone
nations.command.zone.info
nations.command.zone.list
nations.command.zone.create
nations.command.zone.coowner
nations.command.zone.setowner
nations.command.zone.delowner
nations.command.zone.perm
nations.command.zone.flag
nations.command.zone.sell
nations.command.zone.buy
```

Those are the permissions that should be given to standard players
```
nations.command.nation
nations.command.nation.*
nations.command.zone
nations.command.zone.*
```


