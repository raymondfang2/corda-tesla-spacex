package com.template.contracts;

import com.template.states.CarState;
import com.template.states.IOUState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;
import org.bouncycastle.crypto.Signer;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class CarContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.CarContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        if (tx.getCommands().size()!=1) throw new IllegalArgumentException("Tx can only have 1 command1");

        Command command = tx.getCommand(0);
        CommandData commandData = command.getValue();
        List<PublicKey> requiredSigners = command.getSigners();

        if (commandData instanceof  Shipment) {
            //Shipment rule
            //shape rule
            if (tx.getInputStates().size()!=0||tx.getOutputStates().size()!=1) {
                throw new IllegalArgumentException("Tx can not have input state(s) and only 1 output state is allowed!");
            }

            //content rule
            ContractState outputState = tx.getOutput(0);
            if (!(outputState instanceof CarState)) {
                throw new IllegalArgumentException("Tx output state only CarState");
            }
            CarState carState = (CarState) outputState;
            if (!(carState.getModel().equals("Cybertruck"))) {
                throw new IllegalArgumentException("Must be Cybertruck!");
            }


            //signer rule
            PublicKey manufactureKey = carState.getManufacturer().getOwningKey();
            if (!(requiredSigners.contains(manufactureKey))) {
                throw new IllegalArgumentException("Manufacture Signature is required");
            }

        }
    }


    public static class Shipment implements  CommandData {}

}