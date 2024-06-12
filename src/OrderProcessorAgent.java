import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.Map;

public class OrderProcessorAgent extends Agent {
    private Map<String, Map<String, Integer>> recipes;
    private Map<String, Integer> processingTimes; // in milliseconds
    private Map<String, Long> totalProcessingTimes; // to track total time for each order type
    private Map<String, Integer> orderCounts; // to track count of each order type

    protected void setup() {
        recipes = new HashMap<>();
        processingTimes = new HashMap<>();
        totalProcessingTimes = new HashMap<>();
        orderCounts = new HashMap<>();

        // Define recipes
        addRecipe("Pizza", Map.of("Tomato", 2, "Cheese", 1, "Dough", 1), 5000);
        addRecipe("Pasta", Map.of("Tomato", 3, "Cheese", 2), 4000);
        addRecipe("Salad", Map.of("Tomato", 1, "Lettuce", 2), 3000);
        addRecipe("Sandwich", Map.of("Tomato", 1, "Cheese", 1, "Chicken", 2), 6000);
        addRecipe("Burger", Map.of("Bread", 1, "Lettuce", 1, "Tomato", 1, "Cheese", 1, "Chicken", 1), 7000);

        addBehaviour(new OrderProcessingBehaviour());
        addBehaviour(new OrdersRequestBehaviour());
    }

    private void addRecipe(String dish, Map<String, Integer> ingredients, int processingTime) {
        recipes.put(dish, ingredients);
        processingTimes.put(dish, processingTime);
        totalProcessingTimes.put(dish, 0L);
        orderCounts.put(dish, 0);
    }

    private class OrderProcessingBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            if (msg != null) {
                String dish = msg.getContent();
                Map<String, Integer> ingredients = recipes.get(dish);
                Integer processingTime = processingTimes.get(dish);
                if (ingredients != null && processingTime != null) {
                    long startTime = System.currentTimeMillis();
                    addBehaviour(new OneShotBehaviour() {
                        public void action() {
                            try {
                                // Simulate processing time
                                Thread.sleep(processingTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
                                ACLMessage checkStockMsg = new ACLMessage(ACLMessage.INFORM);
                                checkStockMsg.setContent(entry.getKey() + ":" + -entry.getValue());
                                checkStockMsg.addReceiver(new AID("inventory-agent", AID.ISLOCALNAME));
                                send(checkStockMsg);
                            }
                            long endTime = System.currentTimeMillis();
                            long timeTaken = endTime - startTime;
                            totalProcessingTimes.put(dish, totalProcessingTimes.get(dish) + timeTaken);
                            orderCounts.put(dish, orderCounts.get(dish) + 1);

                            System.out.println("Order processed: " + dish);

                            // Inform the UserInterfaceAgent about the processed order
                            ACLMessage informUI = new ACLMessage(ACLMessage.INFORM);
                            informUI.setContent("Order processed: " + dish + " in " + timeTaken + " ms");
                            informUI.addReceiver(new AID("user-interface-agent", AID.ISLOCALNAME));
                            send(informUI);
                        }
                    });
                }
            } else {
                block();
            }
        }
    }

    private class OrdersRequestBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
            if (msg != null) {
                if ("view-orders".equals(msg.getContent())) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Orders processed: " + orderCounts.toString());
                    send(reply);

                    // Inform the UserInterfaceAgent about the orders processed and average times
                    StringBuilder statsBuilder = new StringBuilder();
                    for (String dish : totalProcessingTimes.keySet()) {
                        long totalTime = totalProcessingTimes.get(dish);
                        int count = orderCounts.get(dish);
                        long averageTime = count == 0 ? 0 : totalTime / count;

                        statsBuilder.append("Dish: ").append(dish).append("\n");
                        statsBuilder.append("  Total orders: ").append(count).append("\n");
                        statsBuilder.append("  Average time: ").append(averageTime).append(" ms\n");
                    }

                    ACLMessage statsMsg = new ACLMessage(ACLMessage.INFORM);
                    statsMsg.setContent(statsBuilder.toString());
                    statsMsg.addReceiver(new AID("user-interface-agent", AID.ISLOCALNAME));
                    send(statsMsg);
                }
            } else {
                block();
            }
        }
    }
}
