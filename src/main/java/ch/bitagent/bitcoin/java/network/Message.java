package ch.bitagent.bitcoin.java.network;

public interface Message {

    byte[] getCommand();

    byte[] serialize();
}
