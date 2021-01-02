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

import com.loucaskreger.dropperpredictor.DropperPredictor;

import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Mixin(DispenserBlockEntity.class)
public class DropperMixin {

	@Shadow
	private static Random RANDOM;

	@Shadow
	private DefaultedList<ItemStack> inventory;

	private static final Logger LOGGER = LogManager.getLogger();

	@Inject(method = "<init>", at = @At("RETURN"))
	private void chooseSlot(CallbackInfo ci) {

		if (DropperPredictor.seed == 0) {
			try {
				Field field = Random.class.getDeclaredField("seed");
				field.setAccessible(true);
				AtomicLong scrambledSeed = (AtomicLong) field.get(RANDOM);
				DropperPredictor.seed = scrambledSeed.get();
				DropperPredictor.random = new Random(DropperPredictor.seed ^ 0x5deece66dL);
			} catch (Exception e) {
				LOGGER.error("Could not get the dropper seed: " + e.toString());
			}
		}


	}

	@Inject(method = "chooseNonEmptySlot", at = @At("HEAD"))
	private void selectSlot(CallbackInfoReturnable<Integer> ci) {
		int[] contents = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		nextRandom(DropperPredictor.random, contents);

	}

	private static void nextRandom(Random random, int[] contents) {
		int j = 1;

		for (int k = 0; k < contents.length; ++k) {
			if (random.nextInt(j++) == 0) {
			}
		}

	}

}
