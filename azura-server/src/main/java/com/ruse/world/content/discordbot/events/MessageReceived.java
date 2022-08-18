package com.ruse.world.content.discordbot.events;

import com.ruse.world.content.discordbot.command.Commands;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

/*
 * @project Vanity-Server
 * @author Patrity - https://github.com/Patrity
 * Created on - 4/13/2020
 */
public class MessageReceived extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) {
            return;
        }

        Commands command = Commands.isCommand(e);

        if (Objects.isNull(command)) {
            return;
        }

        command.getAdapter().onGuildMessageReceived(e);

    }
}
