package com.jme.example;

import AppStates.Leve01States;
import AppStates.LogicAgentAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.TechniqueDef;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.SceneProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import com.ss.editor.extension.loader.SceneLoader;
import com.ss.editor.extension.util.JmbExtUtils;

/**
 * The game application class.
 */
public class GameApplication extends SimpleApplication {

    /**
     * The post filter processor.
     */
    protected FilterPostProcessor postProcessor;
    private FogFilter fog;


    @Override
    public void simpleInitApp() {
        renderManager.setPreferredLightMode(TechniqueDef.LightMode.SinglePass);
        renderManager.setSinglePassLightBatchSize(5);

        postProcessor = new FilterPostProcessor(assetManager);
        postProcessor.initialize(renderManager, viewPort);

        // register post effects filter
        viewPort.addProcessor(postProcessor);

        // register loader of j3s files
        SceneLoader.install(this, postProcessor);

        final Camera camera = getCamera();
        camera.setLocation(new Vector3f(20.50714F, 19.356062F, 0.070957F));
        camera.setRotation(new Quaternion(-0.042982846F, 0.90933293F, -0.09716145F, -0.40227568F));

        final FlyByCamera flyByCamera = getFlyByCamera();
        flyByCamera.setMoveSpeed(8);



        final InputManager inputManager = getInputManager();
        inputManager.addMapping("enableMouse", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
            if (isPressed) {
                flyByCamera.setEnabled(!flyByCamera.isEnabled());
                inputManager.setCursorVisible(!flyByCamera.isEnabled());
            }
        }, "enableMouse");

        this.getFlyByCamera().setEnabled(false);

        this.getStateManager().attach(new Leve01States());
        this.getStateManager().attach(new LogicAgentAppState());



    }

    @Override
    public void destroy() {
        super.destroy();
        System.exit(0);
    }
}