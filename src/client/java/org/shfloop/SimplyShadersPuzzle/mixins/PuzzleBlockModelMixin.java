package org.shfloop.SimplyShadersPuzzle.mixins;


import com.github.puzzle.game.engine.blocks.models.PuzzleBlockModel;
import finalforeach.cosmicreach.rendering.blockmodels.BlockModel;
import org.shfloop.SimplyShadersPuzzle.BlockPropertiesIDLoader;
import org.shfloop.SimplyShadersPuzzle.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PuzzleBlockModel.class)
public abstract class PuzzleBlockModelMixin extends BlockModel {

    @Inject(method = "initialize", at = @At("HEAD"))
    private void showmethevariables(CallbackInfo ci) {
        //String temp = ((PuzzleBlockModel)(Object)this).parent;
        String model = ((PuzzleBlockModel)(Object)this).modelName;
        int tempidx = model.indexOf('[');
        if (tempidx != -1) {
            String blockID = model.substring(0 ,tempidx);
            //GameLoader.LOGGER.info(blockName);

            Integer temp = BlockPropertiesIDLoader.baseGeneratedBlockIDMap.getOrDefault(blockID, -1);
            if (temp == -1) {
                int generatedBlockID = BlockPropertiesIDLoader.baseGeneratedBlockID++;
                if (generatedBlockID > 32766) {
                    throw new RuntimeException("TOO MANY BASE BLOCK IDS");
                }
                Constants.LOGGER.info(blockID + " " + generatedBlockID);
                BlockPropertiesIDLoader.baseGeneratedBlockIDMap.put(blockID, generatedBlockID);
                BlockPropertiesIDLoader.baseGeneratedBlockIDArray.add(blockID);
                BlockPropertiesIDLoader.shaderBlockGroupId = (float) (generatedBlockID << 8);
                //BlockPropertiesIDLoader.shaderBlockGroupId = Float.intBitsToFloat(generatedBlockID << 16);
            } else {
                BlockPropertiesIDLoader.shaderBlockGroupId = (float) (temp << 8);
            }
        } else {
            Constants.LOGGER.info("NOT IN INDEX " + model);
        }
        //Constants.LOGGER.info(temp + " " + model);
    }
}
