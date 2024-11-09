package org.shfloop.SimplyShadersPuzzle.mixins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import finalforeach.cosmicreach.rendering.BatchedZoneRenderer;
import org.shfloop.SimplyShadersPuzzle.Shadows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BatchedZoneRenderer.class)
public class BatchedZoneRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lfinalforeach/cosmicreach/rendering/BatchedZoneRenderer;requestMeshes()V"))
    private void injectSwitchToFrontFace(CallbackInfo ci) {
        if(Shadows.shadowPass) {
            //Gdx.gl.glCullFace(GL20.GL_FRONT);
            //only like 30 fps drop in a test might have worse cases but im not sure how else to fix shadows
            Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        }

    }
}
