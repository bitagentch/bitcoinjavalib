package ch.bitagent.bitcoin.lib.wallet;

public class AddressChangeIndex {

    private int change;
    private int index;

    public AddressChangeIndex(int change, int addressIndex) {
        this.change = change;
        this.index = addressIndex;
    }

    public int getChange() {
        return change;
    }

    public int getIndex() {
        return index;
    }
}
