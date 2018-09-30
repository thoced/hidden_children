package AppStates;

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

        Vector3f forwardCam = simpleApp.getCamera().getDirection();
        forwardCam.setY(0f);
        forwardCam.normalizeLocal();
        Vector3f leftCam = simpleApp.getCamera().getLeft();
        leftCam.setY(0f);
        leftCam.normalizeLocal();

       /* RigidBodyControl rigidBodyControl =  visitor.getEntity_bonhomme().getControl(RigidBodyControl.class);



        if(Math.abs(joystickEventListener.getJoyLeftX()) > 0.05f || Math.abs(joystickEventListener.getJoyLeftY()) > 0.05f  ) {

            forwardCam.multLocal(-joystickEventListener.getJoyLeftY());
            leftCam.multLocal(-joystickEventListener.getJoyLeftX());
            Vector3f currentVelocity = rigidBodyControl.getLinearVelocity().clone();
            currentVelocity.setX(0f);
            currentVelocity.setZ(0f);
            currentVelocity.addLocal((forwardCam.add(leftCam).mult(32f)));

            rigidBodyControl.setLinearVelocity(currentVelocity);

            Quaternion currentRot = rigidBodyControl.getPhysicsRotation();
            Quaternion q = new Quaternion();
            q.lookAt((forwardCam.add(leftCam)).normalize(),Vector3f.UNIT_Y);

            Quaternion applyRot = q.slerp(currentRot,q,0.1f);

            rigidBodyControl.setPhysicsRotation(applyRot);



        }*/







    }

    public Visitor getVisitor() {
        return visitor;
    }
}
