import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

public class DeliveryAgent extends Agent {
    protected void setup() {
        addBehaviour(new DeliveryBehaviour());
    }

    private class DeliveryBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                String content = msg.getContent();
                System.out.println("Delivery received: " + content);
                ACLMessage updateMsg = new ACLMessage(ACLMessage.INFORM);
                updateMsg.setContent(content);
                updateMsg.addReceiver(new AID("inventory-agent", AID.ISLOCALNAME));
                send(updateMsg);
            } else {
                block();
            }
        }
    }
}
