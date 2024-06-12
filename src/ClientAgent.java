import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.Map;

public class ClientAgent extends Agent {
    private Map<String, Integer> preparationTimes;

    protected void setup() {
        preparationTimes = new HashMap<>();
        preparationTimes.put("Pizza", 2000);
        preparationTimes.put("Pasta", 1500);
        preparationTimes.put("Salad", 1000);

        addBehaviour(new OrderProcessingBehaviour());
    }

    private class OrderProcessingBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            if (msg != null) {
                String dish = msg.getContent();
                int preparationTime = preparationTimes.getOrDefault(dish, 1000);
                System.out.println("Preparing " + dish + " for " + preparationTime + "ms");
                doWait(preparationTime);
                System.out.println(dish + " prepared");
                ACLMessage updateMsg = new ACLMessage(ACLMessage.INFORM);
                updateMsg.setContent("Tomato:-1:Cheese:-1:Dough:-1");
                updateMsg.addReceiver(new AID("inventory-agent", AID.ISLOCALNAME));
                send(updateMsg);
            } else {
                block();
            }
        }
    }
}
