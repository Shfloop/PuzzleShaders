package org.shfloop.SimplyShadersPuzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.github.puzzle.game.engine.items.ExperimentalItemModel;
import com.github.puzzle.game.engine.items.ItemThingModel;
import com.github.puzzle.game.engine.items.model.IPuzzleItemModel;
import com.github.puzzle.game.engine.items.model.ItemModelWrapper;
import finalforeach.cosmicreach.GameSingletons;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.rendering.entities.EntityModelInstance;
import finalforeach.cosmicreach.rendering.items.ItemModel;
import finalforeach.cosmicreach.rendering.items.ItemModelBlock;

import finalforeach.cosmicreach.util.Identifier;
import finalforeach.cosmicreach.world.*;
import org.shfloop.SimplyShadersPuzzle.mixins.*;
import org.shfloop.SimplyShadersPuzzle.rendering.FinalShader;
import finalforeach.cosmicreach.GameAssetLoader;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.io.SaveLocation;
import finalforeach.cosmicreach.rendering.shaders.*;



import java.io.IOException;

import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class ShaderPackLoader {
    //called from GameShaderMixin to load the shader based off shader selection (loaded from settings)
    //needs to get the String of whatever file it gets
    //im fine with returning a string because i only want to load shaderPacks
    //can either be from GDx.classpath or Gdx.absolute (unzipped folder) or a zipped folder in mods/assets/shaders

    public static boolean shaderPackOn = false;
    public static String selectedPack;
    public static boolean isZipPack = false;
    public static Array<GameShader> shader1;
    public static final Map<String, String> shaderDefaultsMap = new HashMap<>();
    public static int compositeStartIdx =0;


    //also want this to init the shaders
    //probably be easier to keep track of shader arrays here

    public static void switchToShaderPack() {
        //check if folder is zip pack
        isZipPack = selectedPack.endsWith(".zip");

        //should init shaderpack for new array
        // useArray2 = shaderPackOn; //wont work cause shader 1 wont be used after we start using shader2
        shaderPackOn = true;
        // initShaderPack(useArray2 ? shader2 : shader1); // if shaderpackon is true when switching it means we are switching from a shader pack so ive got to use shader2 so the game doesnt crash when remeshing
        shader1 = new Array<>();
        try {
            initShaderPack(shader1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BlockPropertiesIDLoader.updateChunkTexBuf();
        //remesh?
        remeshAllRegions();
        remeashAllSkies();
        changeItemShader();
        updateEntityShader();

    }
    public static void switchToDefaultPack() {
        shaderPackOn = false;
        isZipPack = false;
        setDefaultShaders();
        remeshAllRegions();
        remeashAllSkies();
        changeItemShader();
        updateEntityShader();
        //remesh
    }
    public static void remeshAllRegions() {
        if (GameSingletons.world == null) {
            return;
        }
        //this needs to
        for (Zone zone: GameSingletons.world.getZones()) {
            for (Region reg: zone.getRegions()) {
                for (Chunk chunk: reg.getChunks()) {
                    chunk.flagForRemeshing(false); //hm setting this to true does not help makes it much worse
                }
            }

        }
    }
    public static void remeashAllSkies() {
        for (Sky sky: Sky.skyChoices) {
            sky.starMesh = null;
        }

//        Sky.skyChoices.set(0, new DynamicSky("base:dynamic_sky", "Dynamic_Sky"));
//        DynamicSky temp =   (DynamicSky) Sky.skyChoices.get(0);
//        Sky.currentSky = temp;
//        SkyInterface.getSkies().put("base:dynamic_sky", temp);
        if (Sky.currentSky instanceof DynamicSky) {
            ((DynamicSkyInterface)Sky.currentSky).setCurrentShader();
        }
        ((DynamicSkyInterface)Sky.skyChoices.first()).setCurrentShader();
    }
    public static void changeItemShader() { // i think i can do this without remeshing everything
        for(ItemModel model : ItemRendererInterfaceMixin.getModels().values()) { // this just needs to go through held items
            if (model instanceof ItemModelBlock) {
                ((ItemModelBlockInterface)model).setShader(Shadows.BLOCK_ENTITY_SHADER); //Maybe this works
            } else if (model instanceof ItemModelWrapper) {

                IPuzzleItemModel temp = ((ItemModelWrapperInterface)model).getParent();
                if (temp instanceof ItemThingModel) {
                    ((ItemThingModelInterface)temp).setProgram(com.github.puzzle.game.engine.shaders.ItemShader.DEFAULT_ITEM_SHADER);
                } else if (temp instanceof ExperimentalItemModel) {
                    ((ExperimentalItemModelInterface)temp).setProgram(com.github.puzzle.game.engine.shaders.ItemShader.DEFAULT_ITEM_SHADER);
                } else {
                    throw new RuntimeException("YOU MISSED A MODEL");
                }

            }

        } //2d items dont need to get new shader i just need to change entity shader for them to work

    }
    public static String[] tryDefualtShader(Identifier location) {

        //if the shader is in the map then try to assign a different shader
        //if it isnt in the map try loading from base shaders
        //all else fails throw runtimeerror

        String locNew = shaderDefaultsMap.get(location.getName());
        if (locNew != null) {
            System.out.println("Missing shader, defaulting to: " +  locNew);
            return loadShader(Identifier.of(locNew), true); //i want these to search through recursively
        }
        System.out.println("Missing shader, loading from base: " + location.toString());
        return loadShader(location,false);

    }
    public static void updateEntityShader() {
        if (GameSingletons.world != null) {
            for (Entity e: InGame.getLocalPlayer().getZone().getAllEntities()) {
                if (e.modelInstance instanceof EntityModelInstance) {
                    ((EntityModelInstanceInterface) e.modelInstance).setShader(EntityShader.ENTITY_SHADER);
                }
            }
        }


    }

    //not sure what it does if i call .split
    // probably be better to use an inputstream of some kind
    public static String[] loadShader(Identifier location, boolean lookInShaderPack) {
        if (lookInShaderPack) {
            Identifier temp = Identifier.of("shaderpacks/" + selectedPack, location.getName());
            //in case of shaders from pack for now i dont have defaulting so ill just crash
            try {
                return loadFromZipOrUnzipShaderPack(temp);
            } catch (InvalidPathException e) { //TODO not sure if zip packs will throw the same error if file isnt found
                //if it falls back to a default shader but doesnt find it it should crash
                return tryDefualtShader(location);
            }
            //TOdo make an assets map so packs dont keep loading the same common files that already have been found

        } else {
            return  GameAssetLoader.loadAsset(location).readString().split("\n");
        }


    }
    public static String[] loadFromZipOrUnzipShaderPack(String fileName) throws  InvalidPathException {
        Identifier location = Identifier.of("shaderpacks/" + selectedPack, fileName);
        return loadFromZipOrUnzipShaderPack(location);
    }
    public static String[] loadFromZipOrUnzipShaderPack(Identifier location) throws InvalidPathException {
        if (isZipPack) {
            Path zipFilePath = Paths.get(SaveLocation.getSaveFolderLocation(),"mods" ,location.getNamespace()); // in case of shaderpacks namespace will be shaderpacks/PACKNAME
            try (FileSystem fs = FileSystems.newFileSystem(zipFilePath, (ClassLoader) null)) {
                Path path = fs.getPath(location.getName());
                //System.out.println(path);
                return Files.readString(path).split("\n");
            }  catch (IOException e) {
                throw new InvalidPathException(e.getMessage(), "Could not read the file in zip: " );

            }
        } else {
            FileHandle handle = GameAssetLoader.loadAsset(location);
            if (handle == null) {
                throw new InvalidPathException(location.toPath(), " File Not Found"); //Game Asset Loader already prints error so i can just catch and continue
            }
            return handle.readString().split("\n");
        }
    }





    private static void setDefaultShaders() {
        Array<GameShader> allShaders = GameShaderInterface.getShader();
        ChunkShader.DEFAULT_BLOCK_SHADER = (ChunkShader) allShaders.get(0);
        ChunkShader.WATER_BLOCK_SHADER = (ChunkShader) allShaders.get(1);
        SkyStarShader.SKY_STAR_SHADER = (SkyStarShader) allShaders.get(2);
        SkyShader.SKY_SHADER = (SkyShader) allShaders.get(3);

        EntityShader.ENTITY_SHADER = (EntityShader) allShaders.get(4);
        //for now dont f with death screen (5)
        ItemShader.DEFAULT_ITEM_SHADER = (ItemShader) allShaders.get(6);

        FinalShader.DEFAULT_FINAL_SHADER = (FinalShader) allShaders.get(7);
        com.github.puzzle.game.engine.shaders.ItemShader.DEFAULT_ITEM_SHADER = (com.github.puzzle.game.engine.shaders.ItemShader) allShaders.get(8);
        Shadows.BLOCK_ENTITY_SHADER = (ChunkShader) ChunkShader.DEFAULT_BLOCK_SHADER;
    }

    //create the new array based onthe shaderpack folder
    //
    private static void initShaderPack(Array<GameShader> packShaders) throws IOException {

        Array<GameShader> allShaders = GameShaderInterface.getShader();


        ChunkShader.DEFAULT_BLOCK_SHADER = new ChunkShader(Identifier.of("shaders/chunk.vert.glsl"), Identifier.of("shaders/chunk.frag.glsl"));
        packShaders.add(allShaders.pop()); //i dont want to infinitly add shaders to allshaders

        ChunkShader.WATER_BLOCK_SHADER = new ChunkShader(Identifier.of("shaders/chunk-water.vert.glsl"), Identifier.of("shaders/chunk-water.frag.glsl"));
        packShaders.add(allShaders.pop());

        SkyStarShader.SKY_STAR_SHADER = new SkyStarShader(Identifier.of("shaders/sky-star.vert.glsl"), Identifier.of("shaders/sky-star.frag.glsl"));
        packShaders.add(allShaders.pop());

        SkyShader.SKY_SHADER =  new SkyShader(Identifier.of("shaders/sky.vert.glsl"), Identifier.of("shaders/sky.frag.glsl"));
        packShaders.add(allShaders.pop());

        EntityShader.ENTITY_SHADER =  new EntityShader(Identifier.of("shaders/entity.vert.glsl"), Identifier.of("shaders/entity.frag.glsl"));
        packShaders.add(allShaders.pop());

        packShaders.add(allShaders.get(5)); //TODO

        com.github.puzzle.game.engine.shaders.ItemShader.DEFAULT_ITEM_SHADER = new com.github.puzzle.game.engine.shaders.ItemShader(Identifier.of("shaders/item_shader.vert.glsl"), Identifier.of("shaders/item_shader.frag.glsl"));
        packShaders.add(allShaders.pop());



        FinalShader.DEFAULT_FINAL_SHADER =  new FinalShader(Identifier.of("shaders/final.vert.glsl"), Identifier.of("shaders/final.frag.glsl"),  false);
        packShaders.add(allShaders.pop());

        Shadows.BLOCK_ENTITY_SHADER = new ChunkShader(Identifier.of("shaders/blockEntity.vert.glsl"), Identifier.of("shaders/blockEntity.frag.glsl"));
        packShaders.add(allShaders.pop());

        //add the rest from the pack  shadow , shadowentity, ? composite0-8 as many as given
        if (BlockPropertiesIDLoader.packEnableShadows) {

            Shadows.SHADOW_CHUNK = new ChunkShader(Identifier.of("shaders/shadowChunk.vert.glsl"), Identifier.of("shaders/shadowChunk.frag.glsl"));
            packShaders.add(allShaders.pop());

            Shadows.SHADOW_ENTITY = new EntityShader(Identifier.of("shaders/shadowEntity.vert.glsl"), Identifier.of("shaders/shadowEntity.frag.glsl"));
            packShaders.add(allShaders.pop());
        }
        //load composite and settings here maybe
        //composite shaders start at 9
        compositeStartIdx = packShaders.size ;
        System.out.println("Composite start IDX " + compositeStartIdx);

        if (isZipPack) {
            Path zipFilePath = Paths.get(SaveLocation.getSaveFolderLocation(), "/mods/shaderpacks/" + selectedPack);
            try (FileSystem fs = FileSystems.newFileSystem(zipFilePath, (ClassLoader) null)) {
                for (int i = 0; i < 8; i++) {
                    String compositeName = "shaders/composite" + i;
                    Path path = fs.getPath( compositeName + ".frag.glsl");
                    //will cause invalid pathexception if it doesnt exits
                    if (Files.exists(path)) {
                        new FinalShader(Identifier.of(compositeName + ".vert.glsl"), Identifier.of(compositeName + ".frag.glsl"), true);
                        packShaders.add(allShaders.pop());
                    }else {
                        break;
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException("ZIP fs cant be created");
            }
            catch (InvalidPathException e) {
                //means composite ended
                //safely exit
            }
        } else {
            for (int i = 0; i < 8; i++ ) {
                String compositeName = "shaders/composite" + i;
                //System.out.println(compositeName);
                FileHandle compositeTest = Gdx.files.absolute(SaveLocation.getSaveFolderLocation() + "/mods/shaderpacks/" + ShaderPackLoader.selectedPack +  "/" + compositeName + ".frag.glsl");

                if (compositeTest.exists()) {
                    new FinalShader(Identifier.of(compositeName + ".vert.glsl"), Identifier.of(compositeName + ".frag.glsl"), true);
                    packShaders.add(allShaders.pop());
                } else {

                    break;
                }
            }

        }

    }
    static {
        shaderDefaultsMap.put("shaders/blockEntity.frag.glsl","shaders/chunk.frag.glsl");
        shaderDefaultsMap.put("shaders/blockEntity.vert.glsl","shaders/chunk.vert.glsl");
    }
}
