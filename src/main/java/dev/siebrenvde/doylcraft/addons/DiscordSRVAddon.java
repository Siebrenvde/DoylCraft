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
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;

import java.util.List;

public class DiscordSRVAddon {

    private final DiscordSRV discord = DiscordSRV.getPlugin();

    public DiscordSRVAddon() {
        DiscordSRV.api.subscribe(new Listeners(discord));
    }

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

    private static class Listeners extends ListenerAdapter {

        private final DiscordSRV discord;

        public Listeners(DiscordSRV discord) {
            this.discord = discord;
        }

        @Subscribe
        public void onDiscordReady(DiscordReadyEvent event) {
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
            if(member == null || member.getNickname() != null) return;
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
