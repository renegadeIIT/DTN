package gui;

import dtn.Network;
import dtn.Node;
import java.awt.Color;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.MathException;

/**
 *
 * @author Renegade
 */
public class SimulationPanel extends javax.swing.JPanel implements Runnable {


    /** Creates new form SimulationPanel */
    public SimulationPanel(SimulationFrame parent) {
        this.parent = parent;
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));
        
    }// </editor-fold>

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (network != null) {
            for (Node n : network.agentList) {

                switch (n.state) {
                    case 'S':
                        g.setColor(Color.green);
                        break;
                    case 'I':
                        g.setColor(Color.red);
                        break;
                    case 'R':
                        g.setColor(Color.blue);
                        break;
                }

                if (n.GAMMA_T < 360) {
                    g.fillOval((int) (3 * n.posX), (int) (3 * n.posY), 5, 5);
                } else {
                    g.fillOval((int) (3 * n.posX), (int) (3 * n.posY), 3, 3);
                }

                for (int i = 0; i < n.isNeighbor.length; i++) {
                    if (n.isNeighbor[i]) {
                        g.drawLine((int) (3 * n.posX) + 1, (int) (3 * n.posY) + 1, (int) (3 * network.agentList[i].posX) + 1, (int) (3 * network.agentList[i].posY) + 1);
                    }
                }

            }
        }
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void simulate() {
        runner = new Thread(this);

        /* Disable buttons and text fields */
        parent.getStartButton().setEnabled(false);
        parent.getNumNodes().setEditable(false);
        parent.getLength().setEditable(false);
        parent.getAgentDensity().setEditable(false);
        parent.getVelocity().setEditable(false);
        parent.getPTurn().setEditable(false);
        parent.getPRot().setEditable(false);
        parent.getPercentageDA().setEditable(false);
        parent.getGamma().setEditable(false);
        parent.getGenerateButton().setEnabled(false);
        parent.getGraphType().setEnabled(false);
        parent.getBinSize().setEditable(false);

        runner.start();
    }

    public void stop () {
        runner.interrupt();
        runner = null;
    }

    public void run() {
        try {
            network.broadcast();
        } catch (MathException ex) {
            Logger.getLogger(SimulationPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimulationPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        /* Enable buttons and text fields */
        parent.getStartButton().setEnabled(true);
        parent.getNumNodes().setEditable(true);
        parent.getLength().setEditable(true);
        parent.getAgentDensity().setEditable(true);
        parent.getVelocity().setEditable(true);
        parent.getPTurn().setEditable(true);
        parent.getPRot().setEditable(true);
        parent.getPercentageDA().setEditable(true);
        parent.getGamma().setEditable(true);
        parent.getGenerateButton().setEnabled(true);
        parent.getGraphType().setEnabled(true);
        if(parent.getGraphType().getSelectedItem().toString().equalsIgnoreCase("Average")) {
            parent.getBinSize().setEditable(true);
        }

    }

    // Variables declaration - do not modify
    private Network network;
    private Thread runner;
    private SimulationFrame parent;
    // End of variables declaration

}
