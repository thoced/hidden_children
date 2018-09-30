package com.jme.example;

import com.jme3.input.RawInputListener;
import com.jme3.input.event.*;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.HashMap;

public class JoystickEventListener implements RawInputListener {

    private boolean begin = false;

    private HashMap<Integer,Boolean> mapping;

    private boolean button00,button01,button02,button03 = false;

    private float JoyLeftX,JoyLeftY;
    private float JoyRightX,JoyRightY;

    public JoystickEventListener() {
        mapping = new HashMap<>();
    }

    public HashMap<Integer, Boolean> getMapping() {
        return mapping;
    }

    public boolean isButton00() {
        return button00;
    }

    public boolean isButton01() {
        return button01;
    }

    public boolean isButton02() {
        return button02;
    }

    public boolean isButton03() {
        return button03;
    }

    public float getJoyLeftX() {
        return JoyLeftX;
    }

    public float getJoyLeftY() {
        return JoyLeftY;
    }

    public float getJoyRightX() {
        return JoyRightX;
    }

    public float getJoyRightY() {
        return JoyRightY;
    }

    @Override
    public void beginInput() {

    }

    @Override
    public void endInput() {

    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {

        float value = evt.getValue();
        switch (evt.getAxisIndex()){

            case 0: JoyLeftX = value;break;

            case 1: JoyLeftY = value;break;

            case 2 : JoyRightX = value;break;

            case 3: JoyRightY = value;break;

        }
    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {

        boolean value = evt.isPressed();

        switch(evt.getButtonIndex()){
            case 0: button00 = value;break;

            case 1: button01 = value;break;

            case 2: button02 = value;break;

            case 3: button03 = value;break;
        }




    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {

    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {

    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {

    }

    @Override
    public void onTouchEvent(TouchEvent evt) {

    }
}
