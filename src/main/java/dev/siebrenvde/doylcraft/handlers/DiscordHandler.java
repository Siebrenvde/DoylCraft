package dev.siebrenvde.doylcraft.handlers;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;

import java.util.List;

public class DiscordHandler {

    private DiscordSRV discord = DiscordSRV.getPlugin();

    public void sendDiscordMessage(String textChannel, String message) {
        TextChannel tc = discord.getDestinationTextChannelForGameChannelName(textChannel);
        tc.sendMessage(message).queue();
    }

    public void sendDiscordEmbed(String textChannel, EmbedBuilder embed) {
        TextChannel tc = discord.getDestinationTextChannelForGameChannelName(textChannel);
        tc.sendMessage(embed.build()).queue();
    }

    public List<Member> getMembers() {
        return discord.getMainGuild().getMembers();
    }

}
