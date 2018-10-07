package Controllers;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.CameraControl;

public class PlayerCtrl extends AbstractControl {

    private Vector3f walkDirection = new Vector3f(0,0,0);

    private Vector2f axeLeft = new Vector2f(0,0);
    private Vector2f axeRight = new Vector2f(0,0);

    private Vector3f force;

    private Vector3f viewDirection = new Vector3f(0,0,0);

    private Node headNode;

    private RigidBodyControl physic;

    private Camera camera;

    private float SPEED = 6f;

    public PlayerCtrl(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        // récupération du node de tête
        headNode = (Node) ((Node)spatial).getChild("headNode");

        CameraControl cameraControl = new CameraControl(camera);
        cameraControl.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        headNode.addControl(cameraControl);

        // récupération de l'objet physic rigidbodycontrol
        physic = spatial.getControl(RigidBodyControl.class);

    }

    @Override
    protected void controlUpdate(float tpf) {

        force = physic.getLinearVelocity();

        Vector3f dir = camera.getDirection();
        Vector3f left = camera.getLeft();

        dir.normalizeLocal();
        dir.multLocal(-axeLeft.y);

        left.normalizeLocal();
        left.multLocal(-axeLeft.x);

        dir.addLocal(left);

        walkDirection = dir.mult(tpf * SPEED);


        Quaternion currentRot = headNode.getWorldRotation();
        Quaternion rotRoll = new Quaternion();

        rotRoll.fromAngleAxis(axeRight.x*tpf,Vector3f.UNIT_Y);
      //  rotPitch.fromAngleAxis(axeRight.y*tpf, Vector3f.UNIT_Z);
        currentRot.multLocal(rotRoll);
       // currentRot.multLocal(rotPitch);
        headNode.setLocalRotation(currentRot);

        // walk

        physic.setLinearVelocity(force.add(walkDirection));
        walkDirection.set(0,0,0);


    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public Vector3f getWalkDirection() {
        return walkDirection;
    }

    public void setWalkDirection(Vector3f walkDirection) {
        this.walkDirection = walkDirection;
    }

    public Vector3f getViewDirection() {
        return viewDirection;
    }

    public void setViewDirection(Vector3f viewDirection) {
        this.viewDirection = viewDirection;
    }

    public Vector2f getAxeLeft() {
        return axeLeft;
    }

    public void setAxeLeft(Vector2f axeLeft) {
        this.axeLeft = axeLeft;
    }

    public Vector2f getAxeRight() {
        return axeRight;
    }

    public void setAxeRight(Vector2f axeRight) {
        this.axeRight = axeRight;
    }
}
