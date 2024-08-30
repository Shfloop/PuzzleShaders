package org.shfloop.SimplyShadersPuzzle.mixins;

import com.badlogic.gdx.math.Vector3;
import com.github.puzzle.game.engine.blocks.CustomTextureLoader;
import com.llamalad7.mixinextras.sugar.Local;
import org.shfloop.SimplyShadersPuzzle.Shadows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomTextureLoader.class)
public class CustomTextureLoaderMixin {

    @Inject(method = "setNormal", at = @At("TAIL"))
    private  static void overwriteNormal(CallbackInfo ci, @Local Vector3 tmpNormal) {

        tmpNormal.set(Shadows.tmpNormalVec);

    }

}
