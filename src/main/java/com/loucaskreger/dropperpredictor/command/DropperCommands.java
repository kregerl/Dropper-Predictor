package com.loucaskreger.dropperpredictor.command;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;


public class DropperCommands {

	public static Random random;
	public static long seed = 0;

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

	public static class PredictCommand {
		public static void register(CommandDispatcher<CommandSource> dispatcher) {
			dispatcher.register(Commands.literal("dropper").requires((source) -> source.hasPermissionLevel(0))
					.then(Commands.literal("predict").then(
							Commands.argument("predictions", IntegerArgumentType.integer(0, 20)).executes((context) -> {

								CommandSource source = context.getSource();
								int numPredictions = context.getArgument("predictions", Integer.class);
								List<Integer> nextPredictions = nextDrops(numPredictions);
								int size = nextPredictions.size();

								String initialMessage = String
										.format(size == 1 ? "The next drop is:" : "The next %d drops are:", size);
								source.sendFeedback(new StringTextComponent(initialMessage), true);
								for (int i = 0; i < size; i++) {
									String message = String.format("%d: %d", i + 1, nextPredictions.get(i));
									source.sendFeedback(new StringTextComponent(message), true);
								}

								return 1;
							}))));
		}

	}

	public static class SeedCommand {
		public static void register(CommandDispatcher<CommandSource> dispatcher) {
			dispatcher.register(Commands.literal("dropper").requires((source) -> source.hasPermissionLevel(0))
					.then(Commands.literal("seed").executes((context) -> {
						CommandSource source = context.getSource();
						source.sendFeedback(new StringTextComponent(String.valueOf(seed)), true);
						return 1;
					})));
		}
	}

}
