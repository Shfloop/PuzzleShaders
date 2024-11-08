package org.shfloop.SimplyShadersPuzzle.mixins;

import com.llamalad7.mixinextras.sugar.Local;

import finalforeach.cosmicreach.blocks.Block;
import finalforeach.cosmicreach.blocks.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockState.class)
public class BlockStateMixin {
    @Inject(method = "initialize", at = @At("HEAD"))
    private void testInject(CallbackInfo ci, @Local Block block) {

    }
}
