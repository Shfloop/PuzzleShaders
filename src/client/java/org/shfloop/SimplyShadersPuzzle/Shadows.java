package org.shfloop.SimplyShadersPuzzle;

import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.rendering.shaders.GameShader;
import org.shfloop.SimplyShadersPuzzle.mixins.GameShaderInterface;
import finalforeach.cosmicreach.rendering.shaders.ChunkShader;
import finalforeach.cosmicreach.rendering.shaders.EntityShader;

public class Shadows {
    public static  ChunkShader BLOCK_ENTITY_SHADER;
    public static ChunkShader SHADOW_CHUNK;
    public static EntityShader SHADOW_ENTITY;
    public static Vector3 lastUsedCameraPos;
    public static boolean shaders_on = false;
    public static int time_of_day = 0;
    public static boolean updateTime = false;
    public static int cycleLength = 38400;
    public static boolean doDaylightCycle = true;

    private static OrthographicCamera sunCamera;
    public static ShadowMap shadow_map;
    public static Vector3 tmpNormalVec;
    //nolonger used in .1.44
//    public static VertexAttribute posAttrib = VertexAttribute.Position();
//    public static VertexAttribute uvIdxAttrib = new VertexAttribute(32, 1, 5126, false, "a_uvIdx");
//    public static VertexAttribute lightingAttrib = new VertexAttribute(4, 4, "a_lighting");
//
//    public static VertexAttribute normal_attrib = new VertexAttribute(32, 1, "as_normal_dir");

    private static Vector3 lastCameraPos = new Vector3(0,0,0);
    public static boolean shadowPass = false;
    public static boolean initalized = false;
    private static final String[] SHADERS_TO_COPY = {"chunk.frag.glsl","chunk.vert.glsl", "shadowpass.frag.glsl","shadowpass.vert.glsl", "shadowEntity.frag.glsl", "shadowEntity.vert.glsl", "final.vert.glsl", "final.frag.glsl", "composite0.vert.glsl", "composite0.frag.glsl"};

    static {
        //not sure what viewport size i should be using
        sunCamera =  new OrthographicCamera(256, 256); // should change this to be initialized on the player instead
        sunCamera.near = -512.0f; //TODO make this a shaderpack variable
        sunCamera.far = 256.0F;

        //calcSunDirection();
    }

    public static void reloadShaders() { //dont need to call reload shaders because that happens after this gets called by chunkshader
        if (shaders_on) {
            cleanup();
            try {
                turnShadowsOn(); //this should reset the sky time
            } catch (Exception e) {

                throw new RuntimeException(e);

            }
        }
    }
    public static void turnShadowsOn()  {
       Constants.LOGGER.info("Turning Shaders On");

        try {
            ShaderPackLoader.switchToShaderPack();
        } catch (RuntimeException e) {

            ShaderPackLoader.switchToDefaultPack();
            Constants.LOGGER.info("ERROR in Shader pack loading");
            Constants.LOGGER.info(e.getMessage());
            Chat.MAIN_CLIENT_CHAT.addMessage( null, e.getMessage());
            Array<GameShader> defaultShaders = GameShaderInterface.getShader();
            if(defaultShaders.size > 7) { //default shaders should only be size 7 // if shaderpack loading fails it can add a shader too it without removing it
                //This shouldnt be necessary but is an easy solution to shader loading without redoing the entire thing
                defaultShaders.pop();
            }
            Shadows.shaders_on = false;
            initalized = false;
            return;
        }
       Constants.LOGGER.info("creating Shadow map");
        try { shadow_map= new ShadowMap();}
        catch (Exception e){
            cleanup();
            e.printStackTrace();
            System.out.print("ERROR   ");

           Constants.LOGGER.info(e);
            Shadows.shaders_on = false;
            initalized = false;
            return;
        }
        initalized = true;

       Constants.LOGGER.info("Finished Loading Shaders");
        //after shaders are loaded bind the render textures
        if (SimplyShaders.buffer != null) {
            //RenderFBO.bindRenderTextures();

        } else {
           Constants.LOGGER.info("Render Textures NOT BOUND");

        }

       // ChunkShader.reloadAllShaders();
    }



    public static OrthographicCamera getCamera() {
        // needs to check the current view frustrum how do i differ it from Menu cam vs player cam does matter cause shadow map will be different needs a way to take the current camera

        return sunCamera;
    }
    public static void updateCenteredCamera() { // can just be called every render
        float dist_traveled = lastUsedCameraPos.dst(lastCameraPos);
        if(dist_traveled > 2.0) { //look at a another way to do this iris seems to calc when crossing block borders
            lastCameraPos.set(lastUsedCameraPos);
            Shadows.sunCamera.position.set(lastCameraPos);
            Shadows.sunCamera.update();
        }
    }
    public static void calcSunDirection() {
//        float temp_time = time_of_day - 960  ;
////        if (time_of_day > 1500) {
////            temp_time = 1500 - time_of_day;
////        }
//        sunCamera.position.x = temp_time;
//        sunCamera.position.y = -1.0f / 1850.0f * temp_time * temp_time + 1850; // give it a little more height
//        sunCamera.position.z = -1.0f / 1850.0f * temp_time * temp_time + 1850; //
//        sunCamera.lookAt(0,0,0);
//        sunCamera.up.set(0,1,0);
        float dayPerc =   360.0f * (float)time_of_day / cycleLength;
        sunCamera.direction.set(-0.514496f, -0.857493f, -0.0f).rotate(dayPerc, 1.0F, 0.0F, 1.0F);
        sunCamera.update();

         // whenever the sun changes the next render pass will force update the new camera with new direction
    }

    // for shader files all i need to do is switch the default static shaders for each shader type
    public static void cleanup()  {
        //if copy base shaders fails the game need to stop for good isnt much i can doi to recover
       Constants.LOGGER.info("Turning Shaders OFF");

        ShaderPackLoader.switchToDefaultPack();

        //TODO add other shaders
        if (shadow_map != null) {
            shadow_map.cleanup(); //:(
        }
        initalized = false;


    }

}
