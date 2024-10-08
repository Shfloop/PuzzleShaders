package org.shfloop.SimplyShadersPuzzle.mixins;

import com.badlogic.gdx.math.Vector3;
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
    public boolean forceUpdate = true;
    @Shadow
    protected Vector3 sunDirection;
    @Shadow protected float i;
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lcom/badlogic/gdx/math/Vector3;rotate", shift = At.Shift.AFTER))
    private void rotateSunAngle(CallbackInfo ci) {

        if (Shadows.shaders_on) {
            sunDirection.rotate(45f, 1.0f,0.0f,0.0f);
            final float UPDATES_PER_ROTATION = 360.0f / 4000f;

//            if(forceUpdate) {
//
//                lastUpdateTime = i - UPDATES_PER_ROTATION;
//                forceUpdate = false;
//            }
//            if (i < (lastUpdateTime + UPDATES_PER_ROTATION) ) {
//                return;
//            }

            lastUpdateTime = i;
            Shadows.getCamera().direction.set(new Vector3(sunDirection.x * -1 , sunDirection.y * -1, sunDirection.z * -1));
            Shadows.updateCenteredCamera(true);


        }
    }
}
