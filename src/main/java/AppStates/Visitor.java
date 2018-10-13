package AppStates;

import Controllers.AgentCtrl;
import Controllers.PlayerCtrl;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightProbeBlendingStrategy;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.LightNode;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.PointLightShadowRenderer;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public class Visitor implements SceneGraphVisitor {

    private BulletAppState bulletAppState;
    private SimpleApplication simpleApplication;

    private Spatial NodeSpatials;

    private Spatial NavMesh;

    private Spatial agent;
    private Spatial player;

    private Node entity;

    private Node entity_bonhomme;

    private List<Node> nodesPositionNavMesh = new ArrayList<Node>();
    private FogFilter fog;


    public Visitor(BulletAppState bulletAppState, SimpleApplication simpleApplication) {
        this.bulletAppState = bulletAppState;
        this.simpleApplication = simpleApplication;
    }

    @Override
    public void visit(Spatial spatial) {

        if(spatial.getName().equals("ROOT_SCENE")){

            DirectionalLight dl = (DirectionalLight) spatial.getLocalLightList().get(0);
            final int SHADOWMAP_SIZE=2048;
            DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(simpleApplication.getAssetManager(), SHADOWMAP_SIZE, 4);
            dlsr.setLight(dl);
            simpleApplication.getViewPort().addProcessor(dlsr);


            PointLight pl = (PointLight) spatial.getLocalLightList().get(1);
            PointLightShadowRenderer plsr = new PointLightShadowRenderer(simpleApplication.getAssetManager(), SHADOWMAP_SIZE);
            plsr.setShadowIntensity(0.1f);
            plsr.setShadowZFadeLength(3f);
            plsr.setShadowZExtend(6f);
            plsr.setRenderBackFacesShadows(true);
            plsr.setLight(pl);
            simpleApplication.getViewPort().addProcessor(plsr);



        }

        if(spatial.getName().equals("navmesh")) {
            spatial.setCullHint(Spatial.CullHint.Always);
            NavMesh = spatial;
        }

        if(spatial.getName().equals("NodesPosition")){

            List<Spatial> listNodes = ((Node)spatial).getChildren();
            for(Spatial node : listNodes){
                nodesPositionNavMesh.add((Node)node);

            }
        }

        if(spatial.getName().equals("agent")){
            agent = spatial;

            CapsuleCollisionShape capsuleCollisionShape = new CapsuleCollisionShape(0.32f,0.4f,1 );
            RigidBodyControl rigidBodyControl = new RigidBodyControl(1f);
            rigidBodyControl.setCollisionShape(capsuleCollisionShape);
            rigidBodyControl.setKinematic(false);
            rigidBodyControl.setAngularFactor(new Vector3f(0,1,0));
            rigidBodyControl.setFriction(0f);
            rigidBodyControl.setDamping(0.95f,1f);
            agent.addControl(rigidBodyControl);
            bulletAppState.getPhysicsSpace().add(rigidBodyControl);

            // ajout du control de mouvement
            AgentCtrl agentCtrl = new AgentCtrl();
            agent.addControl(agentCtrl);

            // creation du GhostControl pour le système d'évitement
            Node nodeChost = new Node("NodeGhost");
            Spatial spatialAgent = ((Node)agent).getChild("spatialAgent");
            ((Node)spatialAgent).attachChild(nodeChost);
            nodeChost.move(0,80,100f);
            // creation du ghostctrl
            CapsuleCollisionShape capsuleGhost = new CapsuleCollisionShape(0.40f,0.4f,1);
            GhostControl ghostControl = new GhostControl();
            ghostControl.setCollisionShape(capsuleGhost);
            nodeChost.addControl(ghostControl);
            bulletAppState.getPhysicsSpace().add(ghostControl);


        }

        if(spatial.getName().equals("Scene")){
            List<Spatial> listScenes = ((Node)spatial).getChildren();
            for(Spatial scene : listScenes){
                RigidBodyControl rigidBodyControl = new RigidBodyControl();
                scene.addControl(rigidBodyControl);
                rigidBodyControl.setMass(0f);
                rigidBodyControl.setKinematic(false);
                rigidBodyControl.setRestitution(0.9f);
                rigidBodyControl.setGravity(Vector3f.ZERO);
                CollisionShape shape = CollisionShapeFactory.createMeshShape(scene);
                rigidBodyControl.setCollisionShape(shape);
                bulletAppState.getPhysicsSpace().add(rigidBodyControl);
            }
        }


        if(spatial.getName().equals("Trees")){
            List<Spatial> listTrees = ((Node)spatial).getChildren();
            for(Spatial tree : listTrees){
                tree.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                RigidBodyControl rigidBodyControl = new RigidBodyControl();
                tree.addControl(rigidBodyControl);
                rigidBodyControl.setMass(0f);
                rigidBodyControl.setKinematic(true);
                rigidBodyControl.setRestitution(0.9f);
                rigidBodyControl.setGravity(Vector3f.ZERO);
                CylinderCollisionShape cylinderCollisionShape = new CylinderCollisionShape(new Vector3f(0.32f,2,0.32f),1);
                rigidBodyControl.setCollisionShape(cylinderCollisionShape);
                bulletAppState.getPhysicsSpace().add(rigidBodyControl);
            }
        }

        if(spatial.getName().equals("player")){
            player = spatial;

            RigidBodyControl rigidBodyControl = new RigidBodyControl(1f);
            player.addControl(rigidBodyControl);
            rigidBodyControl.setGravity(new Vector3f(0,-9.81f,0));
            rigidBodyControl.setAngularFactor(new Vector3f(0,1,0));
            rigidBodyControl.setDamping(0.95f,1f);
            rigidBodyControl.setKinematic(false);
            rigidBodyControl.setRestitution(0.1f);
            rigidBodyControl.setFriction(0f);
            CapsuleCollisionShape capsuleCollisionShape = new CapsuleCollisionShape(0.27f,0.4f,1);
            rigidBodyControl.setCollisionShape(capsuleCollisionShape);
            bulletAppState.getPhysicsSpace().add(rigidBodyControl);

            // PlayerCtrl
            PlayerCtrl playerCtrl = new PlayerCtrl(simpleApplication.getCamera());
            player.addControl(playerCtrl);


        }



/*
        // creation de la physique
        if(spatial.getName().equals("Spatials")){

            NodeSpatials = spatial;

            List<Spatial> listSpatial = ((Node)spatial).getChildren();
            for(Spatial sp : listSpatial){
                RigidBodyControl rigidBodyControl = new RigidBodyControl();
                sp.addControl(rigidBodyControl);
                rigidBodyControl.setMass(0f);
                rigidBodyControl.setKinematic(false);
                rigidBodyControl.setRestitution(0.5f);
                rigidBodyControl.setFriction(1f);
                rigidBodyControl.setGravity(Vector3f.ZERO);
                CollisionShape shape = CollisionShapeFactory.createMeshShape(sp);
                rigidBodyControl.setCollisionShape(shape);
                bulletAppState.getPhysicsSpace().add(rigidBodyControl);

            }

        }

        if(spatial.getName().equals("NavMesh")){
            NavMesh = spatial;
        }

        if(spatial.getName().equals("Entity")) {
            entity = (Node) spatial;
            SphereCollisionShape sphereCollisionShape = new SphereCollisionShape(1f);
            CollisionShape shape = CollisionShapeFactory.createMeshShape(spatial);
            RigidBodyControl rigidBodyControl = new RigidBodyControl(sphereCollisionShape, 1.0f);
            spatial.addControl(rigidBodyControl);
            rigidBodyControl.setRestitution(0.5f);
            rigidBodyControl.setFriction(1f);
            bulletAppState.getPhysicsSpace().add(rigidBodyControl);

            // ajout du ghost
            SphereCollisionShape sphereGhostShape = new SphereCollisionShape(1);
            GhostControl ghostControl = new GhostControl(sphereGhostShape);

            Node nodeGhost = new Node();
            nodeGhost.setLocalTranslation(2, 0, 0);
            ((Node) spatial).attachChild(nodeGhost);
            nodeGhost.addControl(ghostControl);


            bulletAppState.getPhysicsSpace().add(ghostControl);
        }

        if(spatial.getName().equals("entity_homme")){
            entity_bonhomme = (Node) spatial;
            CapsuleCollisionShape capsuleCollisionShape = new CapsuleCollisionShape(1,2,1);
            RigidBodyControl rigidBodyControl = new RigidBodyControl(1f);
            rigidBodyControl.setCollisionShape(capsuleCollisionShape);
            rigidBodyControl.setKinematic(false);
            rigidBodyControl.setAngularFactor(new Vector3f(0,1,0));
            //rigidBodyControl.setFriction(1f);
            rigidBodyControl.setDamping(0.1f,1f);


            spatial.addControl(rigidBodyControl);
            bulletAppState.getPhysicsSpace().add(rigidBodyControl);


        }
*/
        // debug
        bulletAppState.setDebugEnabled(false);


    }

    public Spatial getNodeSpatials() {
        return NodeSpatials;
    }

    public Spatial getNavMesh() {
        return NavMesh;
    }

    public Node getEntity() {
        return entity;
    }

    public Node getEntity_bonhomme() {
        return entity_bonhomme;
    }

    public List<Node> getNodesPositionNavMesh() {
        return nodesPositionNavMesh;
    }

    public Spatial getAgent() {
        return agent;
    }

    public Spatial getPlayer() {
        return player;
    }
}
