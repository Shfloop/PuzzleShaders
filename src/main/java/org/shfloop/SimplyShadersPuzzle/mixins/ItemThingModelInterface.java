package org.shfloop.SimplyShadersPuzzle.mixins;


import com.github.puzzle.game.engine.items.ItemThingModel;
import com.github.puzzle.game.engine.items.model.IPuzzleItemModel;

import finalforeach.cosmicreach.rendering.shaders.GameShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemThingModel.class)
public abstract interface ItemThingModelInterface  {

    @Accessor("program")
    public void setProgram(GameShader program);
}
