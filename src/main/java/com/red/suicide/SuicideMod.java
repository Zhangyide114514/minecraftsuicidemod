package com.red.suicide;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuicideMod implements ModInitializer {
    public static final String MOD_ID = "Suicide";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Suicide Mod initialized. Ready to accept /suicide commands.");
    }
}