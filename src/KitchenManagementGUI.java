import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;

public class KitchenManagementGUI {
    private JFrame frame;
    private JTextArea logArea;
    private JTextArea stockArea;
    private JTextArea ordersArea;
    private JTextArea statisticsArea;
    private JButton generateOrderButton;
    private JButton checkInventoryButton;
    private JButton viewOrdersButton;
    private JButton viewStatusButton;
    private AgentController userInterfaceAgentController;

    public KitchenManagementGUI(AgentController userInterfaceAgentController) {
        this.userInterfaceAgentController = userInterfaceAgentController;
    }

    public void createAndShowGUI() {
        frame = new JFrame("Smart Kitchen Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Stock Panel
        JPanel stockPanel = new JPanel();
        stockPanel.setLayout(new BorderLayout());

        stockArea = new JTextArea();
        stockArea.setEditable(false);
        JScrollPane stockScrollPane = new JScrollPane(stockArea);
        stockPanel.add(stockScrollPane, BorderLayout.CENTER);

        checkInventoryButton = new JButton("Check Inventory");
        checkInventoryButton.addActionListener(new CheckInventoryAction());
        stockPanel.add(checkInventoryButton, BorderLayout.SOUTH);

        tabbedPane.addTab("Stock", stockPanel);

        // Orders Panel
        JPanel ordersPanel = new JPanel();
        ordersPanel.setLayout(new BorderLayout());

        ordersArea = new JTextArea();
        ordersArea.setEditable(false);
        JScrollPane ordersScrollPane = new JScrollPane(ordersArea);
        ordersPanel.add(ordersScrollPane, BorderLayout.CENTER);

        viewOrdersButton = new JButton("View Orders");
        viewOrdersButton.addActionListener(new ViewOrdersAction());
        ordersPanel.add(viewOrdersButton, BorderLayout.SOUTH);

        tabbedPane.addTab("Orders", ordersPanel);

        // Statistics Panel
        JPanel statisticsPanel = new JPanel();
        statisticsPanel.setLayout(new BorderLayout());

        statisticsArea = new JTextArea();
        statisticsArea.setEditable(false);
        JScrollPane statisticsScrollPane = new JScrollPane(statisticsArea);
        statisticsPanel.add(statisticsScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Statistics", statisticsPanel);

        // Logger Panel (renamed from Console Output)
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        JPanel logButtonPanel = new JPanel();
        logButtonPanel.setLayout(new FlowLayout());

        generateOrderButton = new JButton("Generate Order");
        generateOrderButton.addActionListener(new GenerateOrderAction());
        logButtonPanel.add(generateOrderButton);

        viewStatusButton = new JButton("View Status");
        viewStatusButton.addActionListener(new ViewStatusAction());
        logButtonPanel.add(viewStatusButton);

        logPanel.add(logButtonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Logger", logPanel);

        frame.getContentPane().add(tabbedPane);
        frame.setVisible(true);

        // Redirect standard output and error streams to the log area
        ConsoleOutputStream consoleOutputStream = new ConsoleOutputStream(logArea);
        PrintStream ps = new PrintStream(consoleOutputStream);
        System.setOut(ps);
        System.setErr(ps);

        // Schedule periodic updates for stock levels
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateStock();
            }
        }, 0, 5000); // Update every 5 seconds
    }

    private void updateStock() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent("check-inventory");
        msg.addReceiver(new AID("inventory-agent", AID.ISLOCALNAME));
        try {
            userInterfaceAgentController.putO2AObject(msg, AgentController.ASYNC);
        } catch (StaleProxyException ex) {
            ex.printStackTrace();
        }
    }

    private class GenerateOrderAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateLog("Generating random order...");
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent("generate-order");
            msg.addReceiver(new AID("order-generator-agent", AID.ISLOCALNAME));
            try {
                userInterfaceAgentController.putO2AObject(msg, AgentController.ASYNC);
            } catch (StaleProxyException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class CheckInventoryAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateLog("Checking inventory...");
            updateStock(); // Force an immediate update
        }
    }

    private class ViewOrdersAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateLog("Viewing orders...");
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setContent("view-orders");
            msg.addReceiver(new AID("order-processor-agent", AID.ISLOCALNAME));
            try {
                userInterfaceAgentController.putO2AObject(msg, AgentController.ASYNC);
            } catch (StaleProxyException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class ViewStatusAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateLog("Viewing system status...");
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setContent("view-status");
            msg.addReceiver(new AID("monitor-agent", AID.ISLOCALNAME));
            try {
                userInterfaceAgentController.putO2AObject(msg, AgentController.ASYNC);
            } catch (StaleProxyException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void updateStockDisplay(String stock) {
        SwingUtilities.invokeLater(() -> stockArea.setText(stock));
    }

    public void updateOrdersDisplay(String orders) {
        SwingUtilities.invokeLater(() -> ordersArea.append(orders + "\n"));
    }

    public void updateStatistics(String statistics) {
        SwingUtilities.invokeLater(() -> statisticsArea.setText(statistics));
    }

    public void updateLog(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }
}
