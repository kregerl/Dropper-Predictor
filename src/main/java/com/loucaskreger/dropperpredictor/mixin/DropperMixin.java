package com.loucaskreger.dropperpredictor.mixin;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.loucaskreger.dropperpredictor.command.DropperCommands;
import net.minecraft.tileentity.DispenserTileEntity;

@Mixin(DispenserTileEntity.class)
public class DropperMixin {

	@Shadow
	private static Random RNG;

	private static final Logger LOGGER = LogManager.getLogger();

	@Inject(method = "<init>", at = @At("RETURN"))
	private void chooseSlot(CallbackInfo ci) {

		if (DropperCommands.seed == 0) {
			try {
				Field field = Random.class.getDeclaredField("seed");
				field.setAccessible(true);
				AtomicLong scrambledSeed = (AtomicLong) field.get(RNG);
				DropperCommands.seed = scrambledSeed.get();
				DropperCommands.random = new Random(DropperCommands.seed ^ 0x5deece66dL);
			} catch (Exception e) {
				LOGGER.error("Could not get the dropper seed: " + e.toString());
			}
		}

	}

	@Inject(method = "getDispenseSlot", at = @At("HEAD"))
	private void getDispenseSlot(CallbackInfoReturnable<Integer> ci) {
		int[] contents = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		nextRandom(DropperCommands.random, contents);

	}

	private static void nextRandom(Random random, int[] contents) {
		int j = 1;

		for (int k = 0; k < contents.length; ++k) {
			if (random.nextInt(j++) == 0) {
			}
		}

	}

}
