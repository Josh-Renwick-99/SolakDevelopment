package com.ruse.world.content.discordbot.command.impl;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;


public class PlayerFile extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        String[] params = e.getMessage().getContentRaw().toLowerCase().split("=");
        if (params == null || params.length != 2) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage("Invalid entry").queue());
            return;
        }
        String name = params[1];
        File file = new File("./data/saves/characters/" + name + ".json");
        if (file.exists()) {
            user.openPrivateChannel().queue((channel) -> {
                channel.sendMessage("Here you go :)").queue();
                channel.sendFile(file).queue();
            });
            e.getMessage().getTextChannel().sendMessage("Private messaged you bro!").queue();
        } else {
            user.openPrivateChannel().queue((channel) ->
                    channel.sendMessage("Invalid entry").queue());
            e.getMessage().getTextChannel().sendMessage("This players log doesn't exist").queue();
        }
    }

}
