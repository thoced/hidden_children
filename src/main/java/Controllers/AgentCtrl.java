package Controllers;

import com.jme3.ai.navmesh.Path;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AgentCtrl extends AbstractControl implements AnimEventListener {

    private Path paths;

    private Queue<Vector3f> fifo = new LinkedList<Vector3f>();

    private boolean isBusy = false;

    private final float SPEED = 1.6f ;

    private Vector3f nextPos = null;

    private AnimChannel channel;
    private AnimControl control;

    private Spatial spatialAgent;

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        spatialAgent = ((Node)spatial).getChild("spatialAgent");
        control = spatialAgent.getControl(AnimControl.class);
        control.addListener(this);
        channel = control.createChannel();
        channel.setAnim("CEDZOMBIE_IDLE");

    }

    @Override
    protected void controlUpdate(float tpf) {

      if(fifo != null && !fifo.isEmpty() && nextPos == null){
          RigidBodyControl rc = spatial.getControl(RigidBodyControl.class);
          nextPos = fifo.poll();
          isBusy = true;
          rc.clearForces();
      }

      if(nextPos != null){
          RigidBodyControl rc = this.spatial.getControl(RigidBodyControl.class);
          Vector3f spatialPos = rc.getPhysicsLocation();
          Vector3f diff = nextPos.subtract(spatialPos);
          diff.normalizeLocal();


          Vector3f spatialZero = spatialPos.clone();
          spatialZero.setY(0f);

          Vector3f nextPosZero = nextPos.clone();
          nextPosZero.setY(0f);

          Vector3f dir = nextPosZero.subtract(spatialZero);
          dir.normalizeLocal();


        Quaternion quaternion = new Quaternion();
        quaternion.lookAt(dir,Vector3f.UNIT_Y);


        Quaternion quatCurrent = spatialAgent.getWorldRotation();
        quatCurrent.slerp(quaternion,0.075f);
        //quatCurrent.slerp(quaternion,0.1f);
        spatialAgent.setLocalRotation(quatCurrent);



        //rc.setPhysicsRotation(quaternion);
       // rc.clearForces();
        rc.setLinearVelocity(diff.mult(SPEED));






      }

      if(nextPos != null && this.spatial.getLocalTranslation().distance(nextPos) <= 0.5f){
          nextPos = null;
          if(fifo.isEmpty()) {
              isBusy = false;

          }
      }




    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public Path getPaths() {
        return paths;
    }

    public void setPaths(Path paths) {
        this.paths = paths;
        nextPos = null;
        fifo.clear();
        List<Path.Waypoint> list = paths.getWaypoints();
        for(Path.Waypoint waypoint : list){
            fifo.add(waypoint.getPosition());
        }
        isBusy = true;
        channel.setAnim("CEDZOMBIE_RUN");
        channel.setSpeed(0.8f);
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {

        if(animName.equals("CEDZOMBIE_RUN") && !isBusy)
            channel.setAnim("CEDZOMBIE_IDLE");

        if(animName.equals("CEDZOMBIE_IDLE") && isBusy)
            channel.setAnim("CEDZOMBIE_RUN");

    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {

        channel.setSpeed(0.8f);

    }
}
