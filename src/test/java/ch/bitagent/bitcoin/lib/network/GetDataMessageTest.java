package ch.bitagent.bitcoin.lib.network;

import ch.bitagent.bitcoin.lib.ecc.Hex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetDataMessageTest {

    @Test
    void serialize() {
        var hexMsg = Hex.parse("020300000030eb2540c41025690160a1014c577061596e32e426b712c7ca00000000000000030000001049847939585b0652fba793661c361223446b6fc41089b8be00000000000000").toBytes();
        var getData = new GetDataMessage();
        var block1 = Hex.parse("00000000000000cac712b726e4326e596170574c01a16001692510c44025eb30").toBytes();
        getData.addData(new GetDataType(GetDataType.FILTERED_BLOCK_DATA_TYPE, block1));
        var block2 = Hex.parse("00000000000000beb88910c46f6b442312361c6693a7fb52065b583979844910").toBytes();
        getData.addData(new GetDataType(GetDataType.FILTERED_BLOCK_DATA_TYPE, block2));
        assertArrayEquals(hexMsg, getData.serialize());
    }
}