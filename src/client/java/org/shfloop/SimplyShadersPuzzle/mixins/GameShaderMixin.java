package org.shfloop.SimplyShadersPuzzle.mixins;


import com.badlogic.gdx.graphics.glutils.ShaderProgram;


import finalforeach.cosmicreach.util.Identifier;
import org.shfloop.SimplyShadersPuzzle.Constants;
import org.shfloop.SimplyShadersPuzzle.ShaderPackLoader;
import org.shfloop.SimplyShadersPuzzle.Shadows;
import org.shfloop.SimplyShadersPuzzle.SimplyShaders;
import org.shfloop.SimplyShadersPuzzle.rendering.FinalShader;
import org.shfloop.SimplyShadersPuzzle.rendering.RenderFBO;

import finalforeach.cosmicreach.RuntimeInfo;

import finalforeach.cosmicreach.rendering.shaders.ChunkShader;

import finalforeach.cosmicreach.rendering.shaders.GameShader;

import org.lwjgl.opengl.GL32;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(GameShader.class)
public abstract class GameShaderMixin   {



    @Inject(method = "initShaders()V", at = @At("TAIL")) // making it head should populate the mods assets with the replacement shaders
    static private void addShadowPassShaders(CallbackInfo ci) {
        FinalShader.initFinalShader();
        Shadows.BLOCK_ENTITY_SHADER = ChunkShader.DEFAULT_BLOCK_SHADER;
    }
    @Overwrite
    public static void reloadAllShaders() {
       Constants.LOGGER.info("Reloading all Shaders");
        if (ShaderPackLoader.shaderPackOn) {
         return;
        }
        for (GameShader shader: GameShaderInterface.getShader()) {
            shader.reload();
        }

       Constants.LOGGER.info("Reloaded all Shaders");
    }

    @Inject(method = "bind", at = @At("TAIL"))
    private void bindDrawBuffers(CallbackInfo ci) {
        //bind the appropriate outbuffers based on what the shader loaded from file
        if (SimplyShaders.inRender &&!Arrays.equals(RenderFBO.lastDrawBuffers, shaderDrawBuffers)) {
            GL32.glDrawBuffers(shaderDrawBuffers);
            RenderFBO.lastDrawBuffers = shaderDrawBuffers;
        }
    }

///mixin to start of gameshaderinit shaders so i can initialize shaderpackloader




    @Shadow
    protected Identifier vertexShaderId;
    @Shadow Identifier fragShaderId;

    @Overwrite
    public void verifyShaderHasNoBannedKeywords(Identifier shaderId, String shaderText) {

    }
    @Overwrite
    public void reload() {
        GameShader tempThis = ((GameShader) (Object)this); //maybe this works

        if (tempThis.shader != null) {
            tempThis.shader.dispose();
        }

        if (RuntimeInfo.isMac) {
            ShaderProgram.prependVertexCode = "";
            ShaderProgram.prependFragmentCode = "";
        }

        String vert = loadShaderFile(this.vertexShaderId, SimplyShaders.newShaderType.VERT); //preprocess doesnt do anything atm
        String frag = loadShaderFile(this.fragShaderId, SimplyShaders.newShaderType.FRAG);
        tempThis.validateShader(this.vertexShaderId, vert, this.fragShaderId, frag);
        ShaderProgram.pedantic = true;
        tempThis.shader = new ShaderProgram(vert, frag);
       Constants.LOGGER.info("Compiling shader(" + this.vertexShaderId + ", " + this.fragShaderId + ")...");
        if (!tempThis.shader.isCompiled()) {
            String log = tempThis.shader.getLog();
            throw new RuntimeException(this.getClass().getSimpleName() + " is not compiled!\nShader files: " + this.vertexShaderId + ", " + this.fragShaderId + "\n" + log + " \nVERT--------------\n " + vert + " \nFRAG------------\n" + frag);
        } else {
            for(String u : tempThis.shader.getUniforms()) {
                if (u.contains(".")) {
                    int blockIndex = GL32.glGetUniformBlockIndex(tempThis.shader.getHandle(), u.split("\\.")[0]);
                   Constants.LOGGER.info("Loaded uniform: " + tempThis.getUniformTypeName(u) + " " + u + " at location=" + blockIndex);
                } else {
                   Constants.LOGGER.info("Loaded uniform: " + tempThis.getUniformTypeName(u) + " " + u + " at location=" + tempThis.shader.getUniformLocation(u));
                }
            }

           Constants.LOGGER.info(tempThis.shader.getLog());
            if (RuntimeInfo.isMac) {
                ShaderProgram.prependVertexCode = GameShader.macOSPrependVertVer;
                ShaderProgram.prependFragmentCode = GameShader.macOSPrependFragVer;
            }
        }


    }
    //make better error reporting

    //adding field to each GameShader
    private int[] shaderDrawBuffers;

    private String loadShaderFile(Identifier shaderId, SimplyShaders.newShaderType shaderType) {
       // String[] rawShaderLines = GameAssetLoader.loadAsset("shaders/" + shaderName).readString().split("\n"); //
        String[] rawShaderLines = ShaderPackLoader.loadShader( shaderId, ShaderPackLoader.shaderPackOn);
        StringBuilder sb = new StringBuilder();
        String version = "";
        //Puzzle difference


        String define = shaderId.getName().replaceAll("[-/. ()]", "_");
        sb.append("#ifndef " + define + "\n");
        sb.append("#define " + define + "\n");
        boolean foundDrawBuffer = false;
        for(String shaderLine : rawShaderLines) {
            String trimmed = shaderLine.trim(); // Fix CRLF causing crashes

            if (shaderLine.startsWith("#version")) {
                version = shaderLine + "\n";
                if (RuntimeInfo.isMac) {
                    switch(shaderType.ordinal()) {
                        case 0:
                            version = version + GameShader.macOSPrependFrag;
                            break;
                        case 1:
                            version = version + GameShader.macOSPrependVert;
                    }
                }
            } else if (trimmed.startsWith("#import \"") && trimmed.endsWith("\"")) {
                String importedShaderName = trimmed.replaceFirst("#import \"", "").replace("\\", "/");
                importedShaderName = importedShaderName.substring(0, importedShaderName.length() - 1);
                Identifier importedId = Identifier.of(importedShaderName);
                sb.append(loadShaderFile(importedId, SimplyShaders.newShaderType.IMPORTED) + "\n");
            } else if (trimmed.startsWith("/*") && trimmed.endsWith("*/")) {
               foundDrawBuffer = findDrawBuffers(trimmed);
            }else {
                sb.append(shaderLine + "\n");
            }
        }
        //only want to apply drawbuffer if its a frag shader
        if (!foundDrawBuffer && shaderType.ordinal() == 0) {
            shaderDrawBuffers = new int[] {GL32.GL_COLOR_ATTACHMENT0}; //default
           Constants.LOGGER.info("Default drawBuffer main");
        }

        sb.append("#endif //" + define + "\n");
        return version + sb.toString();
    }
    private boolean findDrawBuffers(String shaderLine) {
        int indexOfDrawBuffers = shaderLine.indexOf(':'); // maybe change this so it actually looks for drawbuffers
        if (indexOfDrawBuffers > 0) {
            indexOfDrawBuffers++; //want the int after
            //found it
            int[] tempdrawBuffers = new int[8];
            int drawBufferLength = 0;
            //look for only 8 drawbuffers
            for (int i = 0; i <  8; i++ ) {
                //dont want index outofbound
                if (i + indexOfDrawBuffers >= shaderLine.length()) {
                    break;
                }
                char testValue = shaderLine.charAt(i + indexOfDrawBuffers);
                if (testValue>= 48 && testValue <= 55) { // its a character between 0-7
                    for (int y = 0; y <= drawBufferLength; y++) {
                        //loop through the temp buffer to check if the values already been written
                        if (tempdrawBuffers[y] == testValue) {
                            //duplicate drawbuffer should not compile
                            throw new RuntimeException("Duplicate DrawBuffer"); //TODO add error handling
                        }
                    }
                    tempdrawBuffers[drawBufferLength++] = testValue; //if it finds a number add it to tempdrawbuffers
                } else {
                    break; // break if its not a number
                }

            }
            if (drawBufferLength == 0) {
                //found drawbuffers directive but
                //no drawbuffers defined
                throw new RuntimeException("NO drawbuffers elements defined"); // could probably just make the default array
            }
            shaderDrawBuffers = new int[drawBufferLength];//drawbufer points to next open spot so should be fine
            //System.out.print("Defined DrawBuffers: {");
            StringBuilder output = new StringBuilder("Defined DrawBuffers: {");
            for (int i = 0; i < drawBufferLength; i++) {
                shaderDrawBuffers[i] = tempdrawBuffers[i] + GL32.GL_COLOR_ATTACHMENT0 -48; //48 to convert the ascii back to 0-7
                output.append(tempdrawBuffers[i] - 48).append(", ");
                //copy over the data with a new sized array and the appropriate colorattachment value
            }
            output.append("}");
           Constants.LOGGER.info(output);

            return true;
        }
        return false;
    }




}
