package org.black_ixx.playerpoints.commands;

import java.util.EnumMap;
import java.util.UUID;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.LocalizeNode;
import org.black_ixx.playerpoints.config.RootConfig;
import org.black_ixx.playerpoints.models.Flag;
import org.black_ixx.playerpoints.permissions.PermissionHandler;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.services.PointsCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the give command.
 * 
 * @author Mitsugaru
 */
public class GiveCommand implements PointsCommand {

    @Override
    public boolean execute(PlayerPoints plugin, CommandSender sender,
            Command command, String label, String[] args,
            EnumMap<Flag, String> info) {
        if(!PermissionHandler.has(sender, PermissionNode.GIVE)) {
            info.put(Flag.EXTRA, PermissionNode.GIVE.getNode());
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.PERMISSION_DENY, info));
            return true;
        }
        if(args.length < 2) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.COMMAND_GIVE, info));
            return true;
        }
        try {
            final int anzahl = Integer.parseInt(args[1]);
            String playerName = null;
            if(plugin.getModuleForClass(RootConfig.class).autocompleteOnline) {
                playerName = plugin.expandName(args[0]);
            }
            if(playerName == null) {
                playerName = args[0];
            }
            UUID id = plugin.translateNameToUUID(playerName);
            if(plugin.getAPI().give(id, anzahl)) {
                info.put(Flag.PLAYER, playerName);
                info.put(Flag.AMOUNT, "" + plugin.getAPI().look(id));
                sender.sendMessage(LocalizeConfig.parseString(
                        LocalizeNode.POINTS_SUCCESS, info));
                final Player target = Bukkit.getServer().getPlayer(id);
                if(target != null && target.isOnline()) {
                    info.put(Flag.PLAYER, sender.getName());
                    info.put(Flag.AMOUNT, "" + anzahl);
                    target.sendMessage(LocalizeConfig.parseString(
                            LocalizeNode.POINTS_PAY_RECEIVE, info));
                }
            } else {
                sender.sendMessage(LocalizeConfig.parseString(
                        LocalizeNode.POINTS_FAIL, info));
            }
        } catch(NumberFormatException notnumber) {
            info.put(Flag.EXTRA, args[1]);
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.NOT_INTEGER, info));
        }
        return true;
    }

}
