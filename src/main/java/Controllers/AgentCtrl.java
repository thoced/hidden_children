package Controllers;

import com.jme3.ai.navmesh.Path;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
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

public class AgentCtrl extends AbstractControl implements AnimEventListener, PhysicsCollisionListener {

    private Path paths;

    private Queue<Vector3f> fifo = new LinkedList<Vector3f>();

    private boolean isBusy = false;

    private final float SPEED = 1.55f ;

    private Vector3f nextPos = null;

    private AnimChannel channel;
    private AnimControl control;

    private Spatial spatialAgent;

    private Vector3f forceEvitement = new Vector3f(0,0,0);
    private Vector3f vAgentToPoint = new Vector3f(0,0,0);

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        spatialAgent = ((Node)spatial).getChild("spatialAgent");
        control = spatialAgent.getControl(AnimControl.class);
        control.addListener(this);
        channel = control.createChannel();
        channel.setAnim("ALIEN_IDLE");

        RigidBodyControl rigidBodyControl = spatial.getControl(RigidBodyControl.class);
        rigidBodyControl.getPhysicsSpace().addCollisionListener(this);

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

          Vector3f ortho = new Vector3f(1,1,1);
          Vector3f force;
          if(vAgentToPoint.length() > 0f) {
              ortho = vAgentToPoint.cross(Vector3f.UNIT_Y);
              ortho.projectLocal(diff);
              force = (diff.add(ortho)).mult(SPEED);
          }else
              force = diff.mult(SPEED);


          rc.setLinearVelocity(force);
          vAgentToPoint.set(0,0,0);


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
        channel.setAnim("ALIEN_RUN");
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

        if(animName.equals("ALIEN_RUN") && !isBusy)
            channel.setAnim("ALIEN_IDLE");

        if(animName.equals("ALIEN_IDLE") && isBusy)
            channel.setAnim("ALIEN_RUN");

    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {

        channel.setSpeed(0.8f);

    }

    @Override
    public void collision(PhysicsCollisionEvent event) {



        if(event.getNodeA().getName().equals("NodeGhost") && event.getNodeB().getName().equals("tree01")){
            Vector3f contactPoint = event.getPositionWorldOnB();
            vAgentToPoint = (contactPoint.subtract(spatialAgent.getWorldTranslation())).normalize();


        }



        if(event.getNodeB().getName().equals("NodeGhost") && event.getNodeA().getName().equals("tree01")) {
            Vector3f contactPoint = event.getPositionWorldOnA();
            vAgentToPoint = (contactPoint.subtract(spatialAgent.getWorldTranslation())).normalize();
        }


    }
}
