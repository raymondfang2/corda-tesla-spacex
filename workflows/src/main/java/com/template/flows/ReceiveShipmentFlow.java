package com.template.flows;
// Add this import:
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;

// Replace Responder's definition with:
@InitiatedBy(ShipmentFlow.class)
public class ReceiveShipmentFlow extends FlowLogic<Void> {
    private final FlowSession otherPartySession;

    public ReceiveShipmentFlow(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        subFlow(new ReceiveFinalityFlow(otherPartySession));

        return null;
    }
}

