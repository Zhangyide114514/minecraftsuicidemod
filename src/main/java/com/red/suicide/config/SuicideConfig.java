package com.red.suicide.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SuicideConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("suicide-mod.json");
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(BroadcastSettings.class, new BroadcastSettingsAdapter())
            .create();

    // 单例实例
    private static SuicideConfig INSTANCE;

    public BroadcastSettings deathBroadcast = new BroadcastSettings();
    public String privateMessage = "&c[自杀] &7你选择了结束自己的生命。";

    // 广播设置类
    public static class BroadcastSettings {
        public boolean enabled = true;
        public List<String> messages = new ArrayList<>();

        public BroadcastSettings() {
            // 默认消息
            messages.add("&4[死亡] &e{player} &7选择了自我了断");
            messages.add("&4[死亡] &e{player} &7觉得活着没意思");
            messages.add("&4[死亡] &e{player} &7离开了这个世界");
        }

        // 获取随机消息
        public String getRandomMessage() {
            if (messages.isEmpty()) return null;
            Random random = new Random();
            return messages.get(random.nextInt(messages.size()));
        }
    }

    // 自定义JSON适配器
    private static class BroadcastSettingsAdapter implements JsonSerializer<BroadcastSettings>, JsonDeserializer<BroadcastSettings> {
        @Override
        public JsonElement serialize(BroadcastSettings src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) return JsonNull.INSTANCE;

            JsonObject obj = new JsonObject();
            obj.addProperty("enabled", src.enabled);

            JsonArray messagesArray = new JsonArray();
            for (String msg : src.messages) {
                messagesArray.add(msg);
            }
            obj.add("messages", messagesArray);

            return obj;
        }

        @Override
        public BroadcastSettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null || json.isJsonNull()) {
                BroadcastSettings settings = new BroadcastSettings();
                settings.enabled = false;
                return settings;
            }

            if (json.isJsonPrimitive() && json.getAsString().equalsIgnoreCase("null")) {
                BroadcastSettings settings = new BroadcastSettings();
                settings.enabled = false;
                return settings;
            }

            if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                BroadcastSettings settings = new BroadcastSettings();

                if (obj.has("enabled")) {
                    settings.enabled = obj.get("enabled").getAsBoolean();
                }

                if (obj.has("messages")) {
                    settings.messages.clear();
                    JsonArray messagesArray = obj.getAsJsonArray("messages");
                    for (JsonElement element : messagesArray) {
                        if (!element.isJsonNull()) {
                            settings.messages.add(element.getAsString());
                        }
                    }
                }

                return settings;
            }

            // 兼容旧格式
            if (json.isJsonArray()) {
                BroadcastSettings settings = new BroadcastSettings();
                JsonArray messagesArray = json.getAsJsonArray();
                for (JsonElement element : messagesArray) {
                    if (!element.isJsonNull()) {
                        settings.messages.add(element.getAsString());
                    }
                }
                return settings;
            }

            return new BroadcastSettings();
        }
    }

    public static SuicideConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(reader, SuicideConfig.class);
                return INSTANCE;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 创建默认配置
        INSTANCE = new SuicideConfig();
        INSTANCE.save();
        return INSTANCE;
    }

    // 获取当前配置实例
    public static SuicideConfig getInstance() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}