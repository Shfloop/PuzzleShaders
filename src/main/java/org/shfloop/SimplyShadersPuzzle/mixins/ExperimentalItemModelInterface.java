package org.shfloop.SimplyShadersPuzzle.mixins;

import com.github.puzzle.game.engine.items.ExperimentalItemModel;
import com.github.puzzle.game.engine.items.model.IPuzzleItemModel;
import finalforeach.cosmicreach.rendering.shaders.GameShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExperimentalItemModel.class)
public interface ExperimentalItemModelInterface extends IPuzzleItemModel {
    @Accessor("program")
    public void setProgram(GameShader program);
}
