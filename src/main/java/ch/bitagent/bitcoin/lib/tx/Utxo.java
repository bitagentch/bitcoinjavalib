package ch.bitagent.bitcoin.lib.tx;

import ch.bitagent.bitcoin.lib.wallet.AddressChangeIndex;
import org.json.JSONObject;

public class Utxo {

    private int txPos;
    private String txHash;
    private long value;
    private int height;
    private AddressChangeIndex changeIndex;

    public Utxo(JSONObject unspent, AddressChangeIndex changeIndex) {
        this.txPos = unspent.getInt("tx_pos");
        this.txHash = unspent.getString("tx_hash");
        this.value = unspent.getLong("value");
        this.height = unspent.getInt("height");
        this.changeIndex = changeIndex;
    }

    public int getTxPos() {
        return txPos;
    }

    public String getTxHash() {
        return txHash;
    }

    public long getValue() {
        return value;
    }

    public int getHeight() {
        return height;
    }

    public AddressChangeIndex getChangeIndex() {
        return changeIndex;
    }

    @Override
    public String toString() {
        return "Utxo{" +
                "height=" + height +
                ", value=" + value +
                ", txPos=" + txPos +
                ", address='" + String.format("/%s/%s", changeIndex.getChange(), changeIndex.getIndex()) + '\'' +
                ", txHash='" + txHash + '\'' +
                '}';
    }
}
