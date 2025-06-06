package dev.siebrenvde.doylcraft.addons;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberJoinEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.user.update.UserUpdateNameEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class DiscordSRVAddon {

    public static final String GLOBAL_CHANNEL = "global";

    private static final DiscordSRV discord = DiscordSRV.getPlugin();

    public static void init() {
        DiscordSRV.api.subscribe(new Listeners());
    }

    /**
     * Send a message to a text channel
     * @param textChannel the channel to send the message to
     * @param message the message
     */
    public static void sendMessage(String textChannel, String message) {
        TextChannel tc = discord.getDestinationTextChannelForGameChannelName(textChannel);
        tc.sendMessage(message).queue();
    }

    /**
     * Send a {@link Component} to a text channel
     * @param textChannel the channel to send the message to
     * @param message the message
     */
    public static void sendMessage(String textChannel, Component message) {
        sendMessage(textChannel, PlainTextComponentSerializer.plainText().serialize(message));
    }

    /**
     * Send an embed to a text channel
     * @param textChannel the channel to send the message to
     * @param embed the embed
     */
    public static void sendEmbed(String textChannel, EmbedBuilder embed) {
        TextChannel tc = discord.getDestinationTextChannelForGameChannelName(textChannel);
        tc.sendMessageEmbeds(embed.build()).queue();
    }

    /**
     * Creates an EmbedBuilder with a player head
     * @param player the player whose head to use
     * @param message the message
     * @return a new EmbedBuilder
     */
    public static EmbedBuilder playerEmbed(Player player, String message) {
        return new EmbedBuilder().setAuthor(message, null, DiscordSRV.getAvatarUrl(player));
    }

    /**
     * Creates an EmbedBuilder with a player head
     * @param player the player whose head to use
     * @param message the message
     * @return a new EmbedBuilder
     */
    public static EmbedBuilder playerEmbed(Player player, Component message) {
        return playerEmbed(player, PlainTextComponentSerializer.plainText().serialize(message));
    }

    /**
     * {@return a list of members in the main guild}
     */
    public static List<Member> getMembers() {
        return discord.getMainGuild().getMembers();
    }

    /**
     * {@return the invite link}
     */
    public static String getInviteLink() {
        return DiscordSRV.config().getString("DiscordInviteLink");
    }

    private static class Listeners extends ListenerAdapter {

        @Subscribe
        public void onDiscordReady(DiscordReadyEvent ignored) {
            discord.getJda().addEventListener(this);
        }

        @Override
        public void onGuildMemberJoin(GuildMemberJoinEvent event) {
            List<String> name = List.of("@" + event.getMember().getEffectiveName());
            Bukkit.getOnlinePlayers().forEach(player -> player.addCustomChatCompletions(name));
        }

        @Override
        public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
            List<String> name = List.of(
                "@" + (event.getMember() != null
                    ? event.getMember().getEffectiveName()
                    : event.getUser().getEffectiveName())
            );
            Bukkit.getOnlinePlayers().forEach(player -> player.removeCustomChatCompletions(name));
        }

        @Override
        public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {
            updateNameCompletions(
                event.getOldNickname() != null
                    ? event.getOldNickname()
                    : event.getUser().getEffectiveName(),
                event.getNewNickname() != null
                    ? event.getNewNickname()
                    : event.getMember().getEffectiveName()
            );
        }

        @Override
        public void onUserUpdateName(UserUpdateNameEvent event) {
            Member member = discord.getMainGuild().getMember(event.getUser());
            if (member == null || member.getNickname() != null) return;
            updateNameCompletions(
                event.getOldName(),
                event.getNewName()
            );
        }

        private void updateNameCompletions(String nameOld, String nameNew) {
            List<String> oldName = List.of("@" + nameOld);
            List<String> newName = List.of("@" + nameNew);
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.removeCustomChatCompletions(oldName);
                player.addCustomChatCompletions(newName);
            });
        }

    }

}
