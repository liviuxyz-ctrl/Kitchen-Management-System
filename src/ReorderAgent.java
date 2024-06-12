import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ReorderAgent extends Agent {
    protected void setup() {
        addBehaviour(new ReorderRequestBehaviour());
    }

    private class ReorderRequestBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                String content = msg.getContent();
                System.out.println("Reorder requested: " + content);
                doWait(10000);
                System.out.println("Reorder delivered: " + content);
                ACLMessage deliveryMsg = new ACLMessage(ACLMessage.INFORM);
                deliveryMsg.setContent(content);
                deliveryMsg.addReceiver(new AID("inventory-agent", AID.ISLOCALNAME));
                send(deliveryMsg);
            } else {
                block();
            }
        }
    }
}
