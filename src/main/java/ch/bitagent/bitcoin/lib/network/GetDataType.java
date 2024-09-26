package ch.bitagent.bitcoin.lib.network;

/**
 * <p>GetDataType class.</p>
 */
public class GetDataType {

    /** Constant <code>TX_DATA_TYPE=1</code> */
    public static final int TX_DATA_TYPE = 1;
    /** Constant <code>BLOCK_DATA_TYPE=2</code> */
    public static final int BLOCK_DATA_TYPE = 2;
    /** Constant <code>FILTERED_BLOCK_DATA_TYPE=3</code> */
    public static final int FILTERED_BLOCK_DATA_TYPE = 3;
    /** Constant <code>COMPACT_BLOCK_DATA_TYPE=4</code> */
    public static final int COMPACT_BLOCK_DATA_TYPE = 4;

    private final int dataType;
    private final byte[] identifier;

    /**
     * <p>Constructor for GetDataType.</p>
     *
     * @param dataType a int
     * @param identifier an array of {@link byte} objects
     */
    public GetDataType(int dataType, byte[] identifier) {
        this.dataType = dataType;
        this.identifier = identifier;
    }

    /**
     * <p>Getter for the field <code>dataType</code>.</p>
     *
     * @return a int
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * <p>Getter for the field <code>identifier</code>.</p>
     *
     * @return an array of {@link byte} objects
     */
    public byte[] getIdentifier() {
        return identifier;
    }
}
