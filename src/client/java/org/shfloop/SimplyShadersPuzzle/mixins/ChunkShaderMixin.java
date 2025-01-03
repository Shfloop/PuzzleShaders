package org.shfloop.SimplyShadersPuzzle.mixins;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.math.Matrix4;
import com.llamalad7.mixinextras.sugar.Local;
import finalforeach.cosmicreach.GameSingletons;
import org.shfloop.SimplyShadersPuzzle.Shadows;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.rendering.shaders.ChunkShader;
import finalforeach.cosmicreach.rendering.shaders.GameShader;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ChunkShader.class)
public abstract class ChunkShaderMixin extends GameShader {
    private String shaderType;

    //TODO i should add an entity shader mixin to add normals to entities




    @Inject(method = "bind(Lcom/badlogic/gdx/graphics/Camera;)V", at = @At("TAIL"))//value = "INVOKE", target = "Lfinalforeach/cosmicreach/rendering/shaders/GameShader;bindOptionalTextureBuffer(Ljava/lang/String;,  Lfinalforeach/cosmicreach/rendering/TextureBuffer; I)V")) // Lfinalforeach/cosmicreach/rendering/shaders/GameShader;bindOptionalTextureBuffer(Ljava/lang/String;,  Lfinalforeach/cosmicreach/rendering/TextureBuffer; I)V
    private void injectShaderParam(CallbackInfo ci ,@Local int texNum) {


        if (Shadows.shaders_on && GameSingletons.world != null) { //should find a better way to do this

            this.bindOptionalUniformMatrix("lightSpaceMatrix", Shadows.getCamera().combined);
            //FIXME This is redundant i only need to bind the shadowmap texture once when the shader is created because it doesnchange
            this.bindOptionalTextureI("shadowMap", Shadows.shadow_map.getDepthMapTexture().id, texNum); // i think i should try and change this so it matches how texture numbers are handled in chunk shader but idk
            //this.bindOptionalUniform3f("lightPos", Shadows.getCamera().position); // no longer used
            this.bindOptionalUniform3f("lightDir", Shadows.getCamera().direction);// to compare with normal

        }
    }
    private void bindOptionalUniformMatrix(String uniform_name, Matrix4 mat) {
        int u = this.shader.getUniformLocation(uniform_name);
        if (u != -1) {
            this.shader.setUniformMatrix(uniform_name,mat);

        }
    }

//may want to move these out of the mixin but i have no idea if its a bad thing
private int bindOptionalTextureI(String uniform_name, int id,int texNum) {
    int u = this.shader.getUniformLocation(uniform_name);

    if (u != -1) {
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + texNum);//just for right now this works i thinki
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, id);
        this.shader.setUniformi(u, texNum);
        return texNum + 1;
    } else {
        return texNum;
    }
}
}
