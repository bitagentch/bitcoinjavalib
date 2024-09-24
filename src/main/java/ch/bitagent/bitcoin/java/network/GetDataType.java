package ch.bitagent.bitcoin.java.network;

public class GetDataType {

    public static final int TX_DATA_TYPE = 1;
    public static final int BLOCK_DATA_TYPE = 2;
    public static final int FILTERED_BLOCK_DATA_TYPE = 3;
    public static final int COMPACT_BLOCK_DATA_TYPE = 4;

    private final int dataType;
    private final byte[] identifier;

    public GetDataType(int dataType, byte[] identifier) {
        this.dataType = dataType;
        this.identifier = identifier;
    }

    public int getDataType() {
        return dataType;
    }

    public byte[] getIdentifier() {
        return identifier;
    }
}
