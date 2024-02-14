package dev.siebrenvde.doylcraft.utils;

import dev.siebrenvde.doylcraft.handlers.DiscordHandler;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import org.json.JSONObject;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class Requests {

    DiscordHandler discordHandler;

    public Requests(DiscordHandler discordHandler) {
        this.discordHandler = discordHandler;
    }

    public JSONObject get(String url) {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        try {
            return new JSONObject(response.thenApply(HttpResponse::body).get());
        } catch(Exception e) {
            discordHandler.sendDiscordMessage("errors", "<@213752213879783425>");
            discordHandler.sendDiscordEmbed("global", new EmbedBuilder().setTitle(e.getClass().getSimpleName()).setDescription(e.getMessage()).setColor(Color.decode("#E3242B")));
            return null;
        }

    }

}
