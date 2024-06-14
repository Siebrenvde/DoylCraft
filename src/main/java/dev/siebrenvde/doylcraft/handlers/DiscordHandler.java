package dev.siebrenvde.doylcraft.handlers;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.List;

public class DiscordHandler {

    private final DiscordSRV discord = DiscordSRV.getPlugin();

    public void sendDiscordMessage(String textChannel, String message) {
        TextChannel tc = discord.getDestinationTextChannelForGameChannelName(textChannel);
        tc.sendMessage(message).queue();
    }

    public void sendDiscordMessage(String textChannel, TextComponent message) {
        sendDiscordMessage(textChannel, PlainTextComponentSerializer.plainText().serialize(message));
    }

    public void sendDiscordEmbed(String textChannel, EmbedBuilder embed) {
        TextChannel tc = discord.getDestinationTextChannelForGameChannelName(textChannel);
        tc.sendMessageEmbeds(embed.build()).queue();
    }

    public List<Member> getMembers() {
        return discord.getMainGuild().getMembers();
    }

}
