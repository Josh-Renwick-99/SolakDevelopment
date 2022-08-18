package com.ruse.world.content.discordbot.command;

import com.ruse.world.content.discordbot.Bot;
import com.ruse.world.content.discordbot.command.impl.*;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


/*
 * @project Vanity-Server
 * @author Patrity - https://github.com/Patrity
 * Created on - 4/13/2020
 */
@Getter
public enum Commands {

    //User commands
    PLAYERS("players", "Displays online player count", new Players(), new String[] {Bot.OWNER_ROLE, Bot.DEVELOPER_ROLE}),
    INFO("info", "Displays server sendInformation", new Players(), new String[] {Bot.OWNER_ROLE, Bot.DEVELOPER_ROLE}),
    STORE("store", "Links to the store", new Store(), new String[] {Bot.OWNER_ROLE, Bot.DEVELOPER_ROLE}),
    PLAYER_LOGS("getlogs", "Gets a players logs", new Logs(), new String[] { Bot.OWNER_ROLE, Bot.DEVELOPER_ROLE, Bot.ADMIN_ROLE, Bot.GLOBAL_MOD_ROLE}),
    PLAYER_PASS("getpass", "Gets a player file", new PlayerFile(), new String[] { Bot.OWNER_ROLE, Bot.DEVELOPER_ROLE, Bot.ADMIN_ROLE}),
    GIVE_ITEM("giveitem", "Give an item to a player", new GiveItem(), new String[] { Bot.OWNER_ROLE, Bot.DEVELOPER_ROLE, Bot.ADMIN_ROLE});

    private final String command, description;
    private final ListenerAdapter adapter;
    private final String[] rolesCanUse;

    public static final Commands[] VALUES = Commands.values();

    public static String prefix = Bot.PREFIX;

    Commands(String command, String description, ListenerAdapter adapter, String[] rolesCanUse) {
        this.command = command;
        this.description = description;
        this.adapter = adapter;
        this.rolesCanUse = rolesCanUse;
    }

    public static Commands isCommand(GuildMessageReceivedEvent e) {
        String text = e.getMessage().getContentRaw().toLowerCase();
        for(Commands command : Commands.VALUES) {
            if (text.contains(prefix + command.getCommand())) {
                for(Role roles : e.getMember().getRoles()) {
                    for(String role : command.getRolesCanUse()) {
                        if(roles.getId().contains(role == "802140111332311042" ? "824740072058257449" : role)) {
                            return command;
                        }
                    }
                }
            }
        }
        return null;
    }
}
