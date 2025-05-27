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
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public class DiscordSRVAddon {

    public static final String GLOBAL_CHANNEL = "global";

    @Nullable private static DiscordSRVAddon instance;

    private final DiscordSRV discord = DiscordSRV.getPlugin();

    public DiscordSRVAddon() {
        instance = this;
        DiscordSRV.api.subscribe(new Listeners(discord));
    }

    /**
     * Send a message to a text channel
     * @param textChannel the channel to send the message to
     * @param message the message
     */
    public void sendMessage(String textChannel, String message) {
        TextChannel tc = discord.getDestinationTextChannelForGameChannelName(textChannel);
        tc.sendMessage(message).queue();
    }

    /**
     * Send a {@link Component} to a text channel
     * @param textChannel the channel to send the message to
     * @param message the message
     */
    public void sendMessage(String textChannel, Component message) {
        sendMessage(textChannel, PlainTextComponentSerializer.plainText().serialize(message));
    }

    /**
     * Send an embed to a text channel
     * @param textChannel the channel to send the message to
     * @param embed the embed
     */
    public void sendEmbed(String textChannel, EmbedBuilder embed) {
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
    public List<Member> getMembers() {
        return discord.getMainGuild().getMembers();
    }

    /**
     * {@return the invite link}
     */
    public String getInviteLink() {
        return DiscordSRV.config().getString("DiscordInviteLink");
    }

    private static class Listeners extends ListenerAdapter {

        private final DiscordSRV discord;

        public Listeners(DiscordSRV discord) {
            this.discord = discord;
        }

        @Subscribe
        public void onDiscordReady(DiscordReadyEvent ignored) {
            discord.getJda().addEventListener(this);
        }

        // Temporarily? disable
        // Might fix this later
        /*@SubscribeEvent
        public void onMessageReceived(MessageReceivedEvent event) {
            if(!event.isFromType(ChannelType.TEXT)) return;

            if(event.getAuthor().getId().equals("864703528496398356")) {

                String[] strings = event.getMessage().getContentRaw().split("\n");

                String streamer = strings[0].split(" ")[0].replace("\\", "");

                Bukkit.broadcast(
                    text().color(Colours.TWITCH)
                        .append(text("[Twitch] "), text(streamer), text(" is now live!"))
                        .hoverEvent(HoverEvent.showText(text(strings[1]))) // Show title
                        .clickEvent(ClickEvent.openUrl(strings[2])) // Open stream on click
                        .build()
                );

            }
        }*/

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

    public static DiscordSRVAddon get() {
        return Objects.requireNonNull(instance);
    }

}
