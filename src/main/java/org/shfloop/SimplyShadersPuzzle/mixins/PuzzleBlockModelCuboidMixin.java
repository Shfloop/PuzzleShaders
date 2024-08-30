package org.shfloop.SimplyShadersPuzzle.mixins;

import com.badlogic.gdx.math.Vector3;
import com.github.puzzle.game.engine.blocks.models.PuzzleBlockModelCuboid;
import com.llamalad7.mixinextras.sugar.Local;

import org.shfloop.SimplyShadersPuzzle.Shadows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PuzzleBlockModelCuboid.class)
public class PuzzleBlockModelCuboidMixin {
    @Inject(method = "initialize", at = @At(value = "INVOKE", target = "Lcom/github/puzzle/game/engine/blocks/CustomTextureLoader;setNormal(Lcom/badlogic/gdx/math/Vector3;FFF)V")) // i think this should only target the first one
    private void getNewNormalIdx(CallbackInfo ci, @Local PuzzleBlockModelCuboid.Face f) {
        //System.out.println(f.vertexIndexD);
        Shadows.tmpNormalVec = switch (f.vertexIndexD) {
            //Everything is wack but seems to be getting case 4 the most
            //dirt is getting one for everything
            //glass only gets -x
            case 4 -> new Vector3(-1, 0, 0); //negx
            case 3 -> new Vector3(1, 0, 0);
            case 1 -> new Vector3(0, -1, 0); //neg y
            case 6 -> new Vector3(0, 1, 0);
            case 2 -> new Vector3(0, 0, -1);//neg z
            case 5 -> new Vector3(0, 0, 1);
            default -> new Vector3(1, 1, 1); //shouldnt happen but still need it
        };
    }
}
