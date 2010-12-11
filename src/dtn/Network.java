/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dtn;

import java.util.LinkedList;
import org.apache.commons.math.MathException;

/**
 *
 * @author Renegade
 */
public class Network {

    static final int NUM_NODES = 1000;
    final double rho = 0.06;
    final int GRID_SIZE=10;
    final double GS_0=0.0316;

    static int L ;
    Node[] agentList;
    LinkedList<Node> infectedList;

    Region[][] grid;
    
    int currentTime;

    public Network(double pTurn, double pRot) {
        currentTime = 0;

        L=(int)Math.sqrt(NUM_NODES/rho);
        L=(int) (Math.ceil((double) L / 10) * 10);
        agentList = new Node[NUM_NODES];
        agentList[0] = new Node(L, 0, pTurn, pRot);
        for (int i = 1; i < agentList.length; i++) {
            agentList[i] = new Node(agentList[0], L, i, pTurn, pRot);
        }
        grid=new Region[L / GRID_SIZE + 1][L / GRID_SIZE + 1];
        /* Initializing Regions with coordinates */
        for (int i=0; i< L/GRID_SIZE; i++)
            for (int j=0; j< L/GRID_SIZE; j++)
                grid[i][j]=new Region();
        for (int i=0; i < agentList.length; i++) {
            grid[(int)(agentList[i].posX/GRID_SIZE)][(int)(agentList[i].posY/GRID_SIZE)].addAgent(agentList[i]);
            agentList[i].regionIndexX=(int)(agentList[i].posX/GRID_SIZE);
            agentList[i].regionIndexY=(int)(agentList[i].posY/GRID_SIZE);
        }
        infectedList=new LinkedList<Node>();
        infectedList.add(agentList[0]);

    }

    public void broadcast() throws MathException {

        int i, j;
        int y = 1;
        double newNeighbors;

        while(y < NUM_NODES) {
            newNeighbors=0;
            currentTime++;
            
            for (i = 0; i < NUM_NODES; i++) {
                /*Updating each node*/

                agentList[i].updatePosition();
                agentList[i].updateCurrentStateDuration();

                /*Printing*/
                System.out.println("Node " + i + ": St=" + agentList[i].state + " CSD=" + agentList[i].currentStateDuration);

                /* Updating region for current node */
                int regX=(int)(agentList[i].posX/GRID_SIZE), regY=(int)(agentList[i].posY/GRID_SIZE);
                if(regX!=agentList[i].regionIndexX || regY!=agentList[i].regionIndexY) {
                    grid[agentList[i].regionIndexX][agentList[i].regionIndexY].removeAgent(agentList[i]);
                    grid[regX][regY].addAgent(agentList[i]);
                    agentList[i].regionIndexX=regX;
                    agentList[i].regionIndexY=regY;
                }

                agentList[i].updateDirection();
                agentList[i].updateOrientation();
                /*Updating state from R to S*/
                if(agentList[i].state=='R' && agentList[i].currentStateDuration > Node.tauR) {
                    y = agentList[i].updateState('S', currentTime, y);
                    agentList[i].currentStateDuration=0;
                }
            }

            /*Updating state from I to R*/
            while(infectedList.element().currentStateDuration > Node.tauI) {
                Node remove = infectedList.remove();
                remove.updateState('R' , currentTime, y);
                remove.currentStateDuration=0;
                System.out.println("Node at index " + remove.nodeIndex + " I to R.");
            }

            /*Updating state from S to I*/
            LinkedList<Node> temp=new LinkedList<Node>();
            for(Node n : infectedList) {
                /*Infecting susceptible nodes in nearby regions*/
                int regX = n.regionIndexX, regY = n.regionIndexY;
                int regMax = L/GRID_SIZE - 1;

                for(i = ((regX == 0) ? 0 : (regX - 1)); i <= ((regX == regMax) ? regMax : (regX + 1)); i++) {
                    for(j = ((regY == 0) ? 0 : (regY - 1)); j <= ((regY == regMax) ? regMax : (regY + 1)); j++) {
                        for(Node adj: grid[i][j].occupants) {
                            if(Link.isConnected(n, adj)) {
                                if(adj.state == 'S') {
                                    y = adj.updateState('I', currentTime, y);
                                    adj.currentStateDuration=0;
                                    temp.add(adj);
                                }
                                if(!n.isDiscovered[adj.nodeIndex]){
                                    newNeighbors++;
                                    n.isDiscovered[adj.nodeIndex]=true;
                                }
                            }
                        }
                    }
                }

            }
            for(Node n : temp)
                infectedList.add(n);
            newNeighbors/=NUM_NODES;
            System.out.println("Message broadcasted to " + y + " nodes.");
        }
    }

}
