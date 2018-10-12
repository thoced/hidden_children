package AppStates;

import Controllers.AgentCtrl;
import com.jme3.ai.navmesh.Cell;
import com.jme3.ai.navmesh.NavMesh;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.ai.navmesh.Path;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LogicAgentAppState extends AbstractAppState {

    private Leve01States leve01States;

    private NavMeshPathfinder pathfinder;

    private float waitNode = 0f;

    private final float TIMEWAITNODE = 10f;

    private AgentCtrl agentCtrl;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        leve01States = stateManager.getState(Leve01States.class);
        agentCtrl = leve01States.getVisitor().getAgent().getControl(AgentCtrl.class);


        if(leve01States != null){
            Visitor visitor = leve01States.getVisitor();
            Spatial nodeMesh= visitor.getNavMesh();

          NavMesh navMesh = new NavMesh(((Geometry)nodeMesh).getMesh());

          pathfinder = new NavMeshPathfinder(navMesh);
        }



    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        waitNode += tpf;
        if(waitNode >= TIMEWAITNODE && !agentCtrl.isBusy()){
            waitNode = 0f;
            // selection d'une position de node
            Random random = new Random();
            int valueRandom = (int) getRandom(0,5);
            System.out.println("random: " + valueRandom);
            Node postionNavMesh = leve01States.getVisitor().getNodesPositionNavMesh().get(valueRandom);

            pathfinder.clearPath();
            pathfinder.setPosition(leve01States.getVisitor().getAgent().getLocalTranslation());
            pathfinder.computePath(postionNavMesh.getLocalTranslation());

            List<Path.Waypoint> listWayPoint = pathfinder.getPath().getWaypoints();
            for(Path.Waypoint waypoint : listWayPoint){
                System.out.println("waypoint: " + waypoint.getPosition());
            }

            leve01States.getVisitor().getAgent().getControl(AgentCtrl.class).setPaths(pathfinder.getPath());

        }


    }

    private double getRandom(int min,int max){
        double x = (Math.random() * ((max-min)+1))+min;
        return x;
    }
}
