package org.shfloop.SimplyShadersPuzzle;

import com.github.puzzle.loader.entrypoint.interfaces.PreModInitializer;

public class ExampleModPreinit implements PreModInitializer {

    @Override
    public void onPreInit() {
        Constants.LOGGER.info("Hello From PRE-INIT");
    }
}
