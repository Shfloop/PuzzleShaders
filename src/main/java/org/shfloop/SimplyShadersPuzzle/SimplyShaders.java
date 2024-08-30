package org.shfloop.SimplyShadersPuzzle;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.github.puzzle.core.PuzzleRegistries;
import com.github.puzzle.core.localization.ILanguageFile;
import com.github.puzzle.core.localization.LanguageManager;
import com.github.puzzle.core.localization.files.LanguageFileVersion1;
import com.github.puzzle.core.resources.ResourceLocation;

import com.github.puzzle.game.events.OnPreLoadAssetsEvent;
import com.github.puzzle.game.events.OnRegisterBlockEvent;

import com.github.puzzle.loader.entrypoint.interfaces.ModInitializer;

import finalforeach.cosmicreach.chat.commands.Command;
import org.shfloop.SimplyShadersPuzzle.commands.Commands;
import org.shfloop.SimplyShadersPuzzle.commands.CommandTime;

import org.greenrobot.eventbus.Subscribe;
import org.shfloop.SimplyShadersPuzzle.rendering.RenderFBO;

import java.io.IOException;

public class SimplyShaders implements ModInitializer {
    public static RenderFBO buffer ; //this might be a good way to go about this but im not really sure
    public static Mesh screenQuad;
    public static boolean inRender = false;
    @Override
    public void onInit() {
        PuzzleRegistries.EVENT_BUS.register(this);

        Constants.LOGGER.info("Hello From INIT");

        Command.registerCommand(CommandTime::new, "time");
        Commands.register();
        Command.registerCommand(CommandTime::new, "time");
        //System.out.println("IS IT RUNNING GL30" + Gdx.graphics.isGL30Available());
        //fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        // buildFBO(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        try {
            buffer = new RenderFBO(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    @Subscribe
    public void onEvent(OnRegisterBlockEvent event) {

    }



    @Subscribe
    public void onEvent(OnPreLoadAssetsEvent event) {
        ILanguageFile lang = null;
        try {
            lang = LanguageFileVersion1.loadLanguageFromString(new ResourceLocation(Constants.MOD_ID, "languages/en-US.json").locate().readString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LanguageManager.registerLanguageFile(lang);
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
    public static enum newShaderType {
        FRAG,
        VERT,
        IMPORTED
    }

}
