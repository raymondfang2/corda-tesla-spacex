package com.template.flows;

// Add these imports:
import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.CarContract;
import com.template.contracts.IOUContract;
import com.template.states.CarState;
import com.template.states.IOUState;
import net.corda.core.contracts.Command;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

// Replace Initiator's definition with:
@InitiatingFlow
@StartableByRPC
public class ShipmentFlow extends FlowLogic<Void> {
    private String model;
    private Party owner;

    public ShipmentFlow(String model, Party owner) {
        this.model = model;
        this.owner = owner;
    }

    /**
     * The progress tracker provides checkpoints indicating the progress of
     the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();

    public ShipmentFlow() {
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    /**
     * The flow logic is encapsulated within the call() method.
     */
    @Suspendable
    @Override
    public Void call() throws FlowException {

//        if (getOurIdentity().getName().getOrganisation().equals("Tesla")) {
//            System.out.println("Manufacture verification passed!");
//        }
//        else {
//            throw new FlowException("Run from Tesla Only!");
//        }
        //1. get Notary
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        //2. Input Output
        CarState carState = new CarState(model, owner, getOurIdentity());

        //3. Transaction
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addOutputState(carState, CarContract.ID)
                .addCommand(new CarContract.Shipment(), getOurIdentity().getOwningKey());
        //4. Sign
        SignedTransaction sTx = getServiceHub().signInitialTransaction(builder);

        //5. Send to SpaceX
        FlowSession spaceXSession = initiateFlow(owner);

        //6. Finalize the Tx
        subFlow(new FinalityFlow(sTx, spaceXSession));

        return null;
    }
}
