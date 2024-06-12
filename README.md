# Kitchen Management System

## Overview

The Kitchen Management System is a Java-based simulation designed to demonstrate the capabilities of multi-agent systems within a kitchen environment. Utilizing the Java Agent DEvelopment Framework (JADE), this application simulates various kitchen operations such as order processing, delivery, inventory management, and customer interactions through a series of interconnected agents.



## Features
- **Multi-Agent Coordination**: Implements several agents that manage different aspects of kitchen operations, coordinating seamlessly to simulate a real kitchen environment.
- **Real-Time Inventory Management**: Tracks inventory in real-time and automatically orders new supplies when stocks are low.
- **Order Processing and Delivery**: Simulates the receipt, preparation, and delivery of orders.
- **Interactive GUI**: Provides a graphical user interface for real-time monitoring and management of kitchen operations.
- **Logging and Monitoring**: Includes comprehensive logging of actions and a monitoring agent that oversees system operations.

## Gui Example

![image](https://github.com/liviuxyz-ctrl/Kitchen-Management-System/assets/70070368/feec7fdb-20d8-4514-a7d1-421f0d7f1e89)

## Prerequisites
- Java JDK 11 or higher
- Eclipse, IntelliJ IDEA, or any compatible Java IDE
- JADE (Java Agent DEvelopment Framework)

## Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/liviuxyz-ctrl/Kitchen-Management-System
   ```
   
2. **Navigate to the Project Directory**
   ```bash
   cd kitchen-management-system
   ```

3. **Set Up JADE**
   Ensure JADE is installed and configured in your IDE. [JADE Setup Guide](https://jade.tilab.com/documentation/tutorials-guides/).

## System Architecture

The system integrates multiple Java classes and agents, each handling specific aspects of kitchen management:

### Main Entry
- **Main.java**: Initializes and starts all agents and the GUI component, setting the operational stage for the system.

### Agents
- **ClientAgent**: Manages order processing including time calculations and inventory deductions.
- **ConsoleOutputStream**: Redirects console output to the GUI for real-time monitoring.
- **DeliveryAgent**: Simulates the delivery process of orders.
- **InventoryAgent**: Monitors and manages inventory levels, and triggers restocking.
- **KitchenManagementGUI**: Facilitates interaction with the system through a graphical user interface.
- **MonitorAgent**: Provides system status updates and monitoring.
- **OrderGeneratorAgent**: Randomly generates new orders or based on specified criteria.
- **OrderProcessorAgent**: Processes incoming orders and coordinates with inventory and delivery.
- **ReorderAgent**: Automates reordering of low-stock items.
- **UserInterfaceAgent**: Bridges the GUI with the backend agents for command processing and data retrieval.

### Communication
- Agents communicate using ACL messages to manage tasks and share information efficiently.

## Usage

1. **Start the System**
   - Open your IDE and load the project.
   - Run `Main.java` to start all agents and the GUI.

2. **Interact with the GUI**
   - Use the GUI to initiate new orders, view inventory levels, and monitor ongoing processes.

3. **Shutdown**
   - Close the GUI window or stop the IDE's running process to terminate the application.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

