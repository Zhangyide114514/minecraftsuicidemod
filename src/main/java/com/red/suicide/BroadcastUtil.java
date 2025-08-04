package com.red.suicide;

import com.red.suicide.config.SuicideConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BroadcastUtil {
    private static SuicideConfig getConfig() {
        return SuicideConfig.getInstance();
    }

    // 创建死亡广播消息
    public static Text createDeathBroadcast(ServerPlayerEntity player) {
        SuicideConfig config = getConfig();
        if (!config.deathBroadcast.enabled) {
            return null; // 广播被禁用
        }

        String rawMessage = config.deathBroadcast.getRandomMessage();
        if (rawMessage == null) {
            return null; // 没有可用的消息
        }

        // 替换变量
        String message = rawMessage
                .replace("{player}", player.getDisplayName().getString())
                .replace("{world}", player.getWorld().getRegistryKey().getValue().getPath());

        // 解析自定义格式
        return parseCustomFormat(message);
    }

    // 发送私密消息给玩家
    public static void sendPrivateMessage(ServerPlayerEntity player) {
        SuicideConfig config = getConfig();
        if (config.privateMessage == null) {
            return; // 私密消息被禁用
        }

        Text formattedMessage = parseCustomFormat(config.privateMessage);
        player.sendMessage(formattedMessage, false);
    }

    // 解析自定义格式（支持颜色代码和格式化）
    private static Text parseCustomFormat(String message) {
        MutableText result = Text.literal("");
        String[] parts = message.split("&");

        if (parts.length > 0) {
            result.append(Text.literal(parts[0]));
        }

        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;

            char code = parts[i].charAt(0);
            String text = parts[i].substring(1);

            Formatting formatting = Formatting.byCode(code);
            if (formatting != null) {
                // 处理特殊格式
                switch (formatting) {
                    case OBFUSCATED:
                    case BOLD:
                    case STRIKETHROUGH:
                    case UNDERLINE:
                    case ITALIC:
                        // 这些格式可以直接应用
                        result.append(Text.literal(text).formatted(formatting));
                        break;
                    default:
                        // 颜色格式需要重置之前的格式
                        result = Text.literal("").append(result).formatted(formatting);
                        result.append(Text.literal(text));
                        break;
                }
            } else {
                result.append(Text.literal("&" + parts[i]));
            }
        }

        return result;
    }

    // 发送广播给所有玩家
    public static void broadcastToAll(ServerPlayerEntity player, Text message) {
        if (message == null || player.getServer() == null) {
            return; // 没有消息或服务器不可用
        }

        player.getServer().getPlayerManager().broadcast(
                message,
                false // 不发送操作栏消息
        );
    }
}