package org.shfloop.SimplyShadersPuzzle.mixins;

import com.github.puzzle.game.engine.blocks.BlockLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockLoader.class)
public abstract class BlockLoaderMixin {
    @Inject(method = "registerFinalizers", at = @At(value = "INJECT", target = "Lcom/github/puzzle/game/engine/PuzzleRegistries;store(Lfinalforeach/cosmicreach/util/Identifier;Lcom/github/puzzle/game/engine/block/modelsPuzzleBlockModel;)V"))
    private void injectgetblockName(CallbackInfo ci) {
        System.out.println("HELFHWES:LFKHS:ELKF:SLKEF");
    }
}
