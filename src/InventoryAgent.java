import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.Map;

public class InventoryAgent extends Agent {
    private Map<String, Integer> inventory;
    private final int storageLimit = 50; // Reduced storage limit
    private final int restockAmount = 20; // Restock amount

    protected void setup() {
        // Initialize inventory
        inventory = new HashMap<>();
        inventory.put("Tomato", 25);
        inventory.put("Cheese", 15);
        inventory.put("Dough", 10);
        inventory.put("Lettuce", 20);
        inventory.put("Chicken", 15);

        // Add behavior to handle inventory updates and reordering
        addBehaviour(new InventoryUpdateBehaviour());
        addBehaviour(new StockCheckBehaviour());
        addBehaviour(new StockRequestBehaviour());

        // Add a ticker behaviour to send stock updates periodically
        addBehaviour(new StockUpdateBehaviour(this, 1000)); // 1-second interval
    }

    private class InventoryUpdateBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            if (msg != null) {
                String content = msg.getContent();
                String[] parts = content.split(":");
                for (int i = 0; i < parts.length; i += 2) {
                    String ingredient = parts[i];
                    int quantity = Integer.parseInt(parts[i + 1]);
                    updateInventory(ingredient, quantity);
                }
            } else {
                block();
            }
        }
    }

    private void updateInventory(String ingredient, int quantity) {
        int currentQuantity = inventory.getOrDefault(ingredient, 0);
        if (currentQuantity + quantity <= storageLimit) {
            inventory.put(ingredient, currentQuantity + quantity);
            System.out.println("Inventory updated: " + ingredient + " = " + inventory.get(ingredient));
        } else {
            System.out.println("Storage limit reached for: " + ingredient);
        }
    }

    private class StockCheckBehaviour extends CyclicBehaviour {
        public void action() {
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                if (entry.getValue() < 10) {
                    ACLMessage reorderMsg = new ACLMessage(ACLMessage.REQUEST);
                    reorderMsg.setContent(entry.getKey() + ":" + restockAmount);
                    reorderMsg.addReceiver(new AID("reorder-agent", AID.ISLOCALNAME));
                    send(reorderMsg);
                }
            }
            block(10000);
        }
    }

    private class StockRequestBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(inventory.toString());
                send(reply);

                // Inform the UserInterfaceAgent about the inventory
                ACLMessage informUI = new ACLMessage(ACLMessage.INFORM);
                informUI.setContent("INVENTORY_UPDATE:" + inventory.toString());
                informUI.addReceiver(new AID("user-interface-agent", AID.ISLOCALNAME));
                send(informUI);
            } else {
                block();
            }
        }
    }

    private class StockUpdateBehaviour extends CyclicBehaviour {
        public StockUpdateBehaviour(Agent a, long period) {
            super(a);
        }

        public void action() {
            ACLMessage stockUpdate = new ACLMessage(ACLMessage.INFORM);
            stockUpdate.setContent("INVENTORY_UPDATE:" + inventory.toString());
            stockUpdate.addReceiver(new AID("user-interface-agent", AID.ISLOCALNAME));
            send(stockUpdate);
            block(1000); // Update every second
        }
    }
}
