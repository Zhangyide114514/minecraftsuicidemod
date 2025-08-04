package com.red.suicide;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;

public class SuicideCommand implements Command<ServerCommandSource> {
	@Override
	public int run(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		ServerPlayerEntity player = source.getPlayer();

		if (player == null) {
			source.sendError(Text.literal("此命令只能由玩家执行"));
			return 0;
		}
		ServerWorld world = (ServerWorld) player.getWorld();

		// 创建自定义命名的虚空伤害来源
		DamageSource suicideSource = new DamageSource(player.getDamageSources().create(DamageTypes.OUT_OF_WORLD).getTypeRegistryEntry()) {
			@Override
			public String getName() {
				return "suicide"; // 用于Mixin识别
			}
		};

		// 发送自定义死亡广播（如果配置允许）
		BroadcastUtil.broadcastToAll(player, BroadcastUtil.createDeathBroadcast(player));

		// 造成伤害
		player.damage(world, suicideSource, Float.MAX_VALUE);

		// 发送私密消息给玩家（如果配置允许）
		BroadcastUtil.sendPrivateMessage(player);

		return Command.SINGLE_SUCCESS;
	}
}