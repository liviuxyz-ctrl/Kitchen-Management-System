import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class MonitorAgent extends Agent {
    private int systemStatusChecks = 0;

    protected void setup() {
        addBehaviour(new MonitorBehaviour());
        addBehaviour(new StatusRequestBehaviour());
    }

    private class MonitorBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                String content = msg.getContent();
                System.out.println("Alert: " + content);
                systemStatusChecks++;
            } else {
                block();
            }
        }
    }

    private class StatusRequestBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
            if (msg != null) {
                if ("view-status".equals(msg.getContent())) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("System status checks: " + systemStatusChecks);
                    send(reply);

                    // Inform the UserInterfaceAgent about the status checks
                    ACLMessage informUI = new ACLMessage(ACLMessage.INFORM);
                    informUI.setContent("System status checks: " + systemStatusChecks);
                    informUI.addReceiver(new AID("user-interface-agent", AID.ISLOCALNAME));
                    send(informUI);
                }
            } else {
                block();
            }
        }
    }
}
