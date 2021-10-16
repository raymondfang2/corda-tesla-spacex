package com.template.webserver;

import com.template.flows.ShipmentFlow;
import com.template.states.CarState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.StateMachineInfo;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.Vault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @GetMapping(value = "/templateendpoint", produces = "text/plain")
    private String templateendpoint() {
        return "Define an endpoint here.";
    }

    @GetMapping("/network")
    List<String> getNetwork() {
        List<NodeInfo> nodes = proxy.networkMapSnapshot();
        ArrayList<String> parties = new ArrayList(nodes.size());
        for (int i=0; i<nodes.size(); i++) {
            String partyName = nodes.get(i).getLegalIdentities().get(0).getName().getOrganisation();
            parties.add(partyName);
        }
        return parties;
    }

    @GetMapping("/shipmentFlow")
    String startShipmentFlow() {
        List<NodeInfo> nodes = proxy.networkMapSnapshot();
        NodeInfo spaceX = nodes.get(2);
        Party owner = spaceX.getLegalIdentities().get(0);
        proxy.startFlowDynamic(ShipmentFlow.class,"Cybertruck",owner);
        return "Start....";
    }

    @GetMapping("/getState")
    List<String> getCarState() {
        Vault.Page<CarState> pages = proxy.vaultQuery(com.template.states.CarState.class);
        List<StateAndRef<CarState>> states = pages.getStates();
        List<String> result = new ArrayList<>(states.size());
        for (int i=0; i<states.size(); i++) {
            result.add(states.get(i).getState().getData().getModel()
                    +"=>"+states.get(i).getState().getData().getOwner());
        }
        return result;
    }
}