package com.ruse.world.content.discordbot.command.impl;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;


public class GiveItem extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        String[] params = e.getMessage().getContentRaw().toLowerCase().split("=");
        if (params == null || params.length != 4) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage("Invalid entry").queue());
            return;
        }
        String name = params[1];
        int id = Integer.parseInt(params[2]);
        int amount = Integer.parseInt(params[3]);

        Player target = World.getPlayerByName(name);
        if (target == null) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage(name + " must be online to give them stuff!").queue());
        } else {
            target.getInventory().add(id, amount);
            user.openPrivateChannel().queue((channel) -> channel.sendMessage( "Gave " + amount + "x " + ItemDefinition.forId(id).getName() + " to " + name + ".").queue());
        }
    }

}
