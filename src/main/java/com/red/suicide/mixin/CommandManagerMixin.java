package com.red.suicide.mixin;

import com.red.suicide.SuicideCommand;
import com.red.suicide.ReloadConfigCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void registerSuicideCommand(CommandManager.RegistrationEnvironment environment,
                                        CommandRegistryAccess commandRegistryAccess,
                                        CallbackInfo ci) {
        CommandDispatcher<ServerCommandSource> dispatcher = ((CommandManager) (Object) this).getDispatcher();

        dispatcher.register(
                CommandManager.literal("suicide")
                        .requires(source -> source.hasPermissionLevel(0)) // 所有玩家可用
                        .executes(new SuicideCommand())
                        .then(CommandManager.literal("reload")
                                .requires(source -> source.hasPermissionLevel(2)) // 仅限OP使用
                                .executes(new ReloadConfigCommand())
                        ));
    }
}