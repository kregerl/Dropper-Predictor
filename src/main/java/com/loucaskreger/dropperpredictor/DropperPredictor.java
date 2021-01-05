package com.loucaskreger.dropperpredictor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.loucaskreger.dropperpredictor.command.DropperCommands;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DropperPredictor.MOD_ID)
public class DropperPredictor {
	public static final String MOD_ID = "dropperpredictor";
	public static final Logger LOGGER = LogManager.getLogger();

	public DropperPredictor() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setupCommon);
		modBus.addListener(this::setupClient);

	}

	private void setupCommon(final FMLCommonSetupEvent event) {

	}

	private void setupClient(final FMLClientSetupEvent event) {
	}

	@Mod.EventBusSubscriber(modid = DropperPredictor.MOD_ID)
	public static class EventHandler {

		@SubscribeEvent
		public static void serverStarting(final FMLServerStartingEvent event) {
			CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommandManager().getDispatcher();
			LOGGER.info("Server Starting");
			DropperCommands.SeedCommand.register(dispatcher);
			DropperCommands.PredictCommand.register(dispatcher);

		}

	}

}
