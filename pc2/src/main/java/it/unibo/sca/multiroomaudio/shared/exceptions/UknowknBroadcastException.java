package it.unibo.sca.multiroomaudio.shared.exceptions;

public class UknowknBroadcastException extends Exception {

    public UknowknBroadcastException(String s){
        super(s);
    }
    public void printMessage(){
        System.out.println(super.getMessage());
    }
}
