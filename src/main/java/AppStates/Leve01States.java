package AppStates;

import Controllers.PlayerCtrl;
import com.jme.example.GameApplication;
import com.jme.example.JoystickEventListener;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.Joystick;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.ss.editor.extension.util.JmbExtUtils;

public class Leve01States extends AbstractAppState {

    private SimpleApplication simpleApp;
    private AppStateManager manager;

    private BulletAppState bulletAppState;

    private JoystickEventListener joystickEventListener;

    private Visitor visitor;



    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        simpleApp = (SimpleApplication)app;
        manager = stateManager;

        final Spatial scene = simpleApp.getAssetManager().loadModel("Scenes/zoneScene.j3s");

        simpleApp.getRootNode().attachChild(scene);

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        visitor = new Visitor(bulletAppState,this.simpleApp);
        simpleApp.getRootNode().depthFirstTraversal(visitor);




        // initialisation joystick
        Joystick[] joysticks = simpleApp.getInputManager().getJoysticks();
        if (joysticks == null)
            throw new IllegalStateException("Cannot find any joysticks!");

        joystickEventListener = new JoystickEventListener();
        simpleApp.getInputManager().addRawInputListener(joystickEventListener);

        JmbExtUtils.resetPhysicsControlPositions(scene);



    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        visitor.getPlayer().getControl(PlayerCtrl.class).setAxeLeft(joystickEventListener.getJoyAxeLeft());
        visitor.getPlayer().getControl(PlayerCtrl.class).setAxeRight(joystickEventListener.getJoyAxeRight().negate());



    }

    public Visitor getVisitor() {
        return visitor;
    }
}
