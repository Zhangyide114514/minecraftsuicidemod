package com.red.suicide;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.red.suicide.config.SuicideConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadConfigCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try {
            // 重新加载配置
            SuicideConfig config = SuicideConfig.load();

            // 发送成功消息
            source.sendFeedback(() -> Text.literal("§a[自杀模组] §7配置已成功重新加载！")
                    .append("\n§8- 广播消息: §7" + config.deathBroadcast.messages.size() + "条")
                    .append("\n§8- 私密消息: §7" + (config.privateMessage != null ? "已启用" : "已禁用")), true);
            return SINGLE_SUCCESS;
        } catch (Exception e) {
            // 发送错误消息
            source.sendError(Text.literal("§c[自杀模组] §7配置重新加载失败: " + e.getMessage()));
            return 0;
        }
    }
}