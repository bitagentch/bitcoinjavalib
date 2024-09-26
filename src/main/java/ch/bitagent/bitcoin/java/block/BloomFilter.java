package ch.bitagent.bitcoin.java.block;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;
import ch.bitagent.bitcoin.java.helper.Bytes;
import ch.bitagent.bitcoin.java.helper.Murmur3;
import ch.bitagent.bitcoin.java.helper.Varint;
import ch.bitagent.bitcoin.java.network.GenericMessage;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * <p>BloomFilter class.</p>
 */
public class BloomFilter {

    /** Constant <code>BIP37_CONSTANT</code> */
    public static final Int BIP37_CONSTANT = Hex.parse("fba4c795");

    private final int size;
    private final byte[] bitField;
    private final int functionCount;
    private final int tweak;

    /**
     * <p>Constructor for BloomFilter.</p>
     *
     * @param size a int
     * @param functionCount a int
     * @param tweak a int
     */
    public BloomFilter(int size, int functionCount, int tweak) {
        this.size = size;
        this.bitField = Bytes.initFill(this.size * 8, (byte) 0);
        this.functionCount = functionCount;
        this.tweak = tweak;
    }

    /**
     * Add an item to the filter
     *
     * @param item an array of {@link byte} objects
     */
    public void add(byte[] item) {
        // iterate self.function_count number of times
        for (int i = 0; i < functionCount; i++) {
            // BIP0037 spec seed is i*BIP37_CONSTANT + self.tweak
            var seed = Int.parse(i).mul(BIP37_CONSTANT).add(Int.parse(this.tweak));
            // get the murmur3 hash given that seed
            var h = Murmur3.hash32(item, seed);
            // set the bit at the hash mod the bitfield size (self.size*8)
            var bit = h.mod(Int.parse(this.size * 8)).intValue();
            // set the bit field at bit to be 1
            this.bitField[bit] = 1;
        }
    }

    /**
     * <p>filterBytes.</p>
     *
     * @return an array of {@link byte} objects
     */
    public byte[] filterBytes() {
        return Bytes.bitFieldToBytes(this.bitField);
    }

    /**
     * Return the filterload message
     *
     * @param flag a {@link ch.bitagent.bitcoin.java.ecc.Int} object
     * @return a {@link ch.bitagent.bitcoin.java.network.GenericMessage} object
     */
    public GenericMessage filterload(Int flag) {
        flag = Objects.requireNonNullElse(flag, Int.parse(1));

        var payload = new ByteArrayOutputStream();
        // start the payload with the size of the filter in bytes
        payload.writeBytes(Varint.encode(Int.parse(this.size)));
        // next add the bit field using self.filter_bytes()
        payload.writeBytes(this.filterBytes());
        // function count is 4 bytes little endian
        payload.writeBytes(Int.parse(this.functionCount).toBytesLittleEndian(4));
        // tweak is 4 bytes little endian
        payload.writeBytes(Int.parse(this.tweak).toBytesLittleEndian(4));
        // flag is 1 byte little endian
        payload.writeBytes(flag.toBytesLittleEndian(1));
        // return a GenericMessage whose command is b'filterload'
        // and payload is what we've calculated
        return new GenericMessage("filterload", payload.toByteArray());
    }
}
