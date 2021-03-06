package com.jme.example;

import com.jme3.system.AppSettings;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The starter class.
 */
public class Starter extends Application {

    public static void main(final String[] args) {

        final AppSettings settings = new AppSettings(true);
        settings.setResolution(1920  ,    1080);
        settings.setFullscreen(true);
        settings.setGammaCorrection(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL45);
        settings.setFrameRate(90);
        settings.setUseJoysticks(true);

        final GameApplication application = new GameApplication();
        application.setSettings(settings);
        application.setShowSettings(false);
        application.start();
    }

    @Override
    public void start(final Stage primaryStage) {
    }
}
