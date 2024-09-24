package ch.bitagent.bitcoin.java.network;

import ch.bitagent.bitcoin.java.ecc.Int;
import ch.bitagent.bitcoin.java.helper.Bytes;
import ch.bitagent.bitcoin.java.helper.Varint;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GetDataMessage implements Message {

    public static final String COMMAND = "getdata";

    private final List<GetDataType> data = new ArrayList<>();

    @Override
    public byte[] getCommand() {
        return COMMAND.getBytes();
    }

    public void addData(GetDataType data) {
        this.data.add(data);
    }

    @Override
    public byte[] serialize() {
        var result = new ByteArrayOutputStream();
        // start with the number of items as a varint
        result.writeBytes(Varint.encode(Int.parse(this.data.size())));
        // loop through each tuple (data_type, identifier) in self.data
        for (GetDataType dataType : this.data) {
            // data type is 4 bytes Little-Endian
            result.writeBytes(Int.parse(dataType.getDataType()).toBytesLittleEndian(4));
            // identifier needs to be in Little-Endian
            result.writeBytes(Bytes.changeOrder(dataType.getIdentifier()));
        }
        return result.toByteArray();
    }
}
