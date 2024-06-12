import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserInterfaceAgent extends Agent {
    private KitchenManagementGUI gui;
    private AgentController userInterfaceAgentController;

    protected void setup() {
        try {
            userInterfaceAgentController = getContainerController().getAgent(getLocalName());
            gui = new KitchenManagementGUI(userInterfaceAgentController);
            gui.createAndShowGUI();
        } catch (ControllerException e) {
            e.printStackTrace();
        }
        addBehaviour(new UserInteractionBehaviour());
        addBehaviour(new ResponseBehaviour());
    }

    private class UserInteractionBehaviour extends CyclicBehaviour {
        public void action() {
            Object receivedObject = getO2AObject();
            if (receivedObject instanceof ACLMessage) {
                ACLMessage msg = (ACLMessage) receivedObject;
                send(msg);
            } else {
                block();
            }
        }
    }

    private class ResponseBehaviour extends CyclicBehaviour {
        private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.INFORM) {
                    String content = msg.getContent();
                    String timestamp = dateFormat.format(new Date());
                    if (content.startsWith("INVENTORY_UPDATE:")) {
                        gui.updateStockDisplay("[" + timestamp + "] " + content.substring("INVENTORY_UPDATE:".length()));
                    } else if (content.startsWith("Order processed:")) {
                        String formattedMessage = "[" + timestamp + "] " + content;
                        gui.updateOrdersDisplay(formattedMessage);
                    } else if (content.startsWith("Dish:")) {
                        gui.updateStatistics(content);
                    } else {
                        String formattedMessage = "[" + timestamp + "] " + content;
                        gui.updateLog(formattedMessage);
                    }
                }
            } else {
                block();
            }
        }
    }
}
