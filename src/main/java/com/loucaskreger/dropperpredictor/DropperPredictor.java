package com.loucaskreger.dropperpredictor;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class DropperPredictor implements ModInitializer {

	public static Random random;
	public static long seed = 0;

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(literal("dropper").then(literal("seed").executes(getSeedCommand)));

			dispatcher.register(literal("dropper")
					.then(literal("predict").then(argument("predictions", integer(0, 20)).executes(predictCommand))));

		});

	}

	private static int broadcast(ServerCommandSource source, UUID uuid, String message) {
		final Text text = new LiteralText(message);

		source.getMinecraftServer().getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, uuid);
		return Command.SINGLE_SUCCESS;
	}

	public static List<Integer> nextDrops(int numberOfDrops) {
		List<Integer> nextPredictions = new ArrayList<Integer>();
		int[] contents = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Random thisRand = new Random(getNewSeed() ^ 0x5deece66dL);

		for (int i = 0; i < numberOfDrops; i++) {
			nextPredictions.add(getNext(thisRand, contents));
		}
		return nextPredictions;
	}

	private static long getNewSeed() {
		long seed = 0;
		try {
			Field field = Random.class.getDeclaredField("seed");
			field.setAccessible(true);
			AtomicLong scrambledSeed = (AtomicLong) field.get(random);
			seed = scrambledSeed.get();
		} catch (Exception e) {
		}
		return seed;
	}

	private static int getNext(Random random, int[] contents) {
		int i = -1;
		int j = 1;

		for (int k = 0; k < contents.length; ++k) {
			if (random.nextInt(j++) == 0) {
				i = k;
			}
		}

		return contents[i];
	}

	protected static final Command<ServerCommandSource> getSeedCommand = new Command<ServerCommandSource>() {

		@Override
		public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			UUID uuid = context.getSource().getPlayer().getUuid();
			broadcast(context.getSource(), uuid, String.valueOf(seed));

			return 1;
		}

	};

	protected static final Command<ServerCommandSource> predictCommand = new Command<ServerCommandSource>() {

		@Override
		public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			int numPredictions = context.getArgument("predictions", Integer.class);
			UUID uuid = context.getSource().getPlayer().getUuid();
			List<Integer> nextPredictions = nextDrops(numPredictions);
			int size = nextPredictions.size();

			String initialMessage = String.format(size == 1 ? "The next drop is:" : "The next %d drops are:", size);
			broadcast(context.getSource(), uuid, initialMessage);
			for (int i = 0; i < size; i++) {
				String message = String.format("%d: %d", i + 1, nextPredictions.get(i));
				broadcast(context.getSource(), uuid, message);
			}

			return 1;
		}

	};

}
