package org.shfloop.SimplyShadersPuzzle.mixins;

import com.badlogic.gdx.math.Vector3;
import finalforeach.cosmicreach.rendering.shaders.SkyShader;
import org.shfloop.SimplyShadersPuzzle.DynamicSkyInterface;
import org.shfloop.SimplyShadersPuzzle.Shadows;
import finalforeach.cosmicreach.world.DynamicSky;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(DynamicSky.class)
public abstract  class DynamicSkyMixin implements DynamicSkyInterface {
    public float lastUpdateTime;

    @Override
    public void setLastUpdateTime() {
        forceUpdate = true;

    }
    @Override
    public void setCurrentShader() {
        skyShader = SkyShader.SKY_SHADER;
    }
    @Shadow protected SkyShader skyShader;
    public boolean forceUpdate = true;
    @Shadow
    protected Vector3 sunDirection;
    @Shadow protected float i;
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lcom/badlogic/gdx/math/Vector3;rotate", shift = At.Shift.AFTER))
    private void rotateSunAngle(CallbackInfo ci) {

        if (Shadows.shaders_on) {
            sunDirection.rotate(45f, 1.0f,0.0f,0.0f);
            Vector3 cameraDirection = Shadows.getCamera().direction;
            cameraDirection.x = sunDirection.x * -1;
            cameraDirection.y = sunDirection.y * -1;
            cameraDirection.z = sunDirection.z * -1;
            cameraDirection.nor();
            Shadows.getCamera().update();
        }
    }
}
