import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
        // Get a hold on JADE runtime
        Runtime rt = Runtime.instance();

        // Create a default profile
        Profile p = new ProfileImpl();
        AgentContainer mainContainer = rt.createMainContainer(p);

        try {
            // Create and start the agents
            AgentController inventoryAgent = mainContainer.createNewAgent("inventory-agent", "InventoryAgent", null);
            AgentController orderGeneratorAgent = mainContainer.createNewAgent("order-generator-agent", "OrderGeneratorAgent", null);
            AgentController orderProcessorAgent = mainContainer.createNewAgent("order-processor-agent", "OrderProcessorAgent", null);
            AgentController clientAgent = mainContainer.createNewAgent("client-agent", "ClientAgent", null);
            AgentController reorderAgent = mainContainer.createNewAgent("reorder-agent", "ReorderAgent", null);
            AgentController deliveryAgent = mainContainer.createNewAgent("delivery-agent", "DeliveryAgent", null);
            AgentController monitorAgent = mainContainer.createNewAgent("monitor-agent", "MonitorAgent", null);
            AgentController userInterfaceAgent = mainContainer.createNewAgent("user-interface-agent", "UserInterfaceAgent", null);

            // Start the agents
            inventoryAgent.start();
            orderGeneratorAgent.start();
            orderProcessorAgent.start();
            clientAgent.start();
            reorderAgent.start();
            deliveryAgent.start();
            monitorAgent.start();
            userInterfaceAgent.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
