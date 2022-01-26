package it.unibo.sca.multiroomaudio.shared.messages;

import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class MsgScanResult extends Msg {
    private ScanResult[] resultList;

    public MsgScanResult(ScanResult[] resultList) {
        super("AP_INFO");
        this.resultList = resultList;
    }

    public ScanResult[] getApList() {
        return resultList;
    }
}
