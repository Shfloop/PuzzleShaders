package org.shfloop.SimplyShadersPuzzle.mixins;

import org.shfloop.SimplyShadersPuzzle.DynamicSkyRewrite;
import finalforeach.cosmicreach.world.Sky;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Sky.class)
public abstract class  SkyMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void overwriteOldDynamicSky(CallbackInfo ci) {
        Sky.skyChoices.set(2, new DynamicSkyRewrite("Dynamic_Sky"));
    }
}
