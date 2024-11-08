package org.shfloop.SimplyShadersPuzzle;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import com.github.puzzle.core.loader.launch.provider.mod.entrypoint.impls.ClientModInitializer;
import com.github.puzzle.core.localization.ILanguageFile;
import com.github.puzzle.core.localization.LanguageManager;
import com.github.puzzle.core.localization.files.LanguageFileVersion1;



import com.github.puzzle.game.events.OnPreLoadAssetsEvent;
import com.github.puzzle.game.events.OnRegisterBlockEvent;



import finalforeach.cosmicreach.util.Identifier;

import org.shfloop.SimplyShadersPuzzle.rendering.RenderFBO;

import java.io.IOException;
import java.util.Objects;

public class SimplyShaders implements ClientModInitializer {
    public static RenderFBO buffer ; //this might be a good way to go about this but im not really sure
    public static Mesh screenQuad = null;
    public static boolean inRender = false;
    @Override
    public void onInit() {
    }

    public static void genMesh() {
        float[] verts = new float[20];
        int i = 0;

        verts[i++] = -1; // x1
        verts[i++] = -1; // y1
        verts[i++] = 0;
        verts[i++] = 0f; // u1
        verts[i++] = 0f; // v1

        verts[i++] = 1f; // x2
        verts[i++] = -1; // y2
        verts[i++] = 0;
        verts[i++] = 1f; // u2
        verts[i++] = 0f; // v2

        verts[i++] = 1f; // x3
        verts[i++] = 1f; // y3
        verts[i++] = 0;
        verts[i++] = 1f; // u3
        verts[i++] = 1f; // v3

        verts[i++] = -1; // x4
        verts[i++] = 1f; // y4
        verts[i++] = 0;
        verts[i++] = 0f; // u4
        verts[i++] = 1f; // v4

        screenQuad = new Mesh(true, 4,0,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
        screenQuad.setVertices(verts);

    }

    public static void resize(){
        if (buffer != null) {
            buffer.dispose();
            try {
                buffer = new RenderFBO(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
    public  enum newShaderType {
        FRAG,
        VERT,
        IMPORTED
    }

}
