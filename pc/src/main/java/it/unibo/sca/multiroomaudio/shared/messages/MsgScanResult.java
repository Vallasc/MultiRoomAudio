package it.unibo.sca.multiroomaudio.shared.messages;

import io.github.vallasc.APInfo;

public class MsgApInfo extends Msg {
    private APInfo[] apList;

    public MsgApInfo(APInfo[] apList) {
        super("AP_INFO");
        this.apList = apList;
    }

    public APInfo[] getApList() {
        return apList;
    }
}
