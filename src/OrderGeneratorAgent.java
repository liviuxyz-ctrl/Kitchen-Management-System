import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class OrderGeneratorAgent extends Agent {
    private List<String> dishes;

    protected void setup() {
        dishes = Arrays.asList("Pizza", "Pasta", "Salad");
        addBehaviour(new OrderGenerationBehaviour());
    }

    private class OrderGenerationBehaviour extends TickerBehaviour {
        public OrderGenerationBehaviour() {
            super(OrderGeneratorAgent.this, 5000);
        }

        protected void onTick() {
            String dish = dishes.get(new Random().nextInt(dishes.size()));
            ACLMessage orderMsg = new ACLMessage(ACLMessage.INFORM);
            orderMsg.setContent(dish);
            orderMsg.addReceiver(new AID("order-processor-agent", AID.ISLOCALNAME));
            send(orderMsg);
            System.out.println("Order generated: " + dish);
        }
    }
}
