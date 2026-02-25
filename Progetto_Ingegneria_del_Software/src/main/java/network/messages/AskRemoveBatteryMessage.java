package network.messages;

import model.GameModel;
import network.structure.NetworkView;
import view.View;

public class AskRemoveBatteryMessage extends Message {

    public AskRemoveBatteryMessage(String clientId) {
        super(clientId);
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.setRemoveBattery();
    }

}
