package org.shfloop.SimplyShadersPuzzle.mixins;

import com.badlogic.gdx.math.Vector3;
import com.github.puzzle.game.engine.blocks.CustomTextureLoader;

import finalforeach.cosmicreach.rendering.shaders.ChunkShader;

import org.shfloop.SimplyShadersPuzzle.BlockPropertiesIDLoader;
import org.shfloop.SimplyShadersPuzzle.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static org.shfloop.SimplyShadersPuzzle.BlockPropertiesIDLoader.NUM_FLOATS_PER_FACE_UVTEXBUFF;


@Mixin(CustomTextureLoader.class)
public class CustomTextureLoaderMixin {

    @Shadow
    static Vector3 tmpVertPos;
    @Overwrite
    public static int createUBOFloatsIdx(float u, float v, Vector3 vecNormal, Vector3 faceNormal) {
        int i;
        for(i = 0; i < ChunkShader.faceTexBufFloats.size; i += NUM_FLOATS_PER_FACE_UVTEXBUFF) {
            if (ChunkShader.faceTexBufFloats.get(i) == u && ChunkShader.faceTexBufFloats.get(i + 1) == v && ChunkShader.faceTexBufFloats.get(i + 2) == vecNormal.x && ChunkShader.faceTexBufFloats.get(i + 3) == vecNormal.y && ChunkShader.faceTexBufFloats.get(i + 4) == vecNormal.z && ChunkShader.faceTexBufFloats.get(i + 5) == faceNormal.x && ChunkShader.faceTexBufFloats.get(i + 6) == faceNormal.y && ChunkShader.faceTexBufFloats.get(i + 7) == faceNormal.z && ChunkShader.faceTexBufFloats.get(i + 8) == tmpVertPos.x && ChunkShader.faceTexBufFloats.get(i + 9) == tmpVertPos.y && ChunkShader.faceTexBufFloats.get(i + 10) == tmpVertPos.z
                    && ChunkShader.faceTexBufFloats.get(i + 11) == BlockPropertiesIDLoader.shaderBlockGroupId) { //
                return i / NUM_FLOATS_PER_FACE_UVTEXBUFF;
            }
        }

        i = ChunkShader.faceTexBufFloats.size / NUM_FLOATS_PER_FACE_UVTEXBUFF;
        ChunkShader.faceTexBufFloats.add(u);
        ChunkShader.faceTexBufFloats.add(v);
        ChunkShader.faceTexBufFloats.add(vecNormal.x);
        ChunkShader.faceTexBufFloats.add(vecNormal.y);
        ChunkShader.faceTexBufFloats.add(vecNormal.z);
        ChunkShader.faceTexBufFloats.add(faceNormal.x);
        ChunkShader.faceTexBufFloats.add(faceNormal.y);
        ChunkShader.faceTexBufFloats.add(faceNormal.z);
        ChunkShader.faceTexBufFloats.add(tmpVertPos.x);
        ChunkShader.faceTexBufFloats.add(tmpVertPos.y);
        ChunkShader.faceTexBufFloats.add(tmpVertPos.z);
        ChunkShader.faceTexBufFloats.add(BlockPropertiesIDLoader.shaderBlockGroupId); // this must run after blockstates are made??
        return i;
    }

}
