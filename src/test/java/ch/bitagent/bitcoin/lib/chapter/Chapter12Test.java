package ch.bitagent.bitcoin.lib.chapter;

import ch.bitagent.bitcoin.lib.block.Block;
import ch.bitagent.bitcoin.lib.block.BloomFilter;
import ch.bitagent.bitcoin.lib.block.MerkleBlock;
import ch.bitagent.bitcoin.lib.ecc.Hex;
import ch.bitagent.bitcoin.lib.ecc.Int;
import ch.bitagent.bitcoin.lib.ecc.PrivateKey;
import ch.bitagent.bitcoin.lib.helper.*;
import ch.bitagent.bitcoin.lib.network.*;
import ch.bitagent.bitcoin.lib.script.OpCodeNames;
import ch.bitagent.bitcoin.lib.script.Script;
import ch.bitagent.bitcoin.lib.tx.Tx;
import ch.bitagent.bitcoin.lib.tx.TxIn;
import ch.bitagent.bitcoin.lib.tx.TxOut;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class Chapter12Test {

    private static final Logger log = Logger.getLogger(Chapter12Test.class.getSimpleName());

    @Test
    void example1() {
        var bitFieldSize = Int.parse(10);
        var bitField = Bytes.initFill(bitFieldSize.intValue(), (byte) 0);
        var h = Helper.hash256(Hex.parse("hello world".getBytes()).toBytes());
        var bit = Hex.parse(h).mod(bitFieldSize).intValue();
        bitField[bit] = 1;
        var want = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
        assertArrayEquals(want, bitField);
    }

    @Test
    void example2() {
        var bitFieldSize = Int.parse(10);
        var bitField = Bytes.initFill(bitFieldSize.intValue(), (byte) 0);
        for (byte[] item : getItems()) {
            var h = Helper.hash256(item);
            var bit = Hex.parse(h).mod(bitFieldSize).intValue();
            bitField[bit] = 1;
        }
        var want = new byte[]{0, 0, 1, 0, 0, 0, 0, 0, 0, 1};
        assertArrayEquals(want, bitField);
    }

    private static List<byte[]> getItems() {
        var items = new ArrayList<byte[]>();
        items.add(Hex.parse("hello world".getBytes()).toBytes());
        items.add(Hex.parse("goodbye".getBytes()).toBytes());
        return items;
    }

    @Test
    void exercise1() {
        var bitFieldSize = Int.parse(10);
        var bitField = Bytes.initFill(bitFieldSize.intValue(), (byte) 0);
        for (byte[] item : getItems()) {
            var h = Helper.hash160(item);
            var bit = Hex.parse(h).mod(bitFieldSize).intValue();
            bitField[bit] = 1;
        }
        var want = new byte[]{1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
        assertArrayEquals(want, bitField);
    }

    @Test
    void example3() {
        var bitFieldSize = Int.parse(10);
        var bitField = Bytes.initFill(bitFieldSize.intValue(), (byte) 0);
        for (byte[] item : getItems()) {
            for (String function : getFunctions()) {
                byte[] h = executeFunction(function, item);
                var bit = Hex.parse(h).mod(bitFieldSize).intValue();
                bitField[bit] = 1;
            }
        }
        var want = new byte[]{1, 1, 1, 0, 0, 0, 0, 0, 0, 1};
        assertArrayEquals(want, bitField);
    }

    private static List<String> getFunctions() {
        return List.of("hash160", "hash256");
    }

    private byte[] executeFunction(String function, byte[] item) {
        switch (function) {
            case "hash160":
                return Helper.hash160(item);
            case "hash256":
                return Helper.hash256(item);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Test
    void example4() {
        var fieldSize = 2;
        var numFunctions = 2;
        var tweak = Int.parse(42);
        var bitFieldSize = fieldSize * 8;
        var bitField = Bytes.initFill(bitFieldSize, (byte) 0);
        for (byte[] item : getItems()) {
            for (int i = 0; i < numFunctions; i++) {
                var seed = Int.parse(i).mul(BloomFilter.BIP37_CONSTANT).add(tweak);
                var h = Murmur3.hash32(item, seed);
                var bit = h.mod(Int.parse(bitFieldSize)).intValue();
                bitField[bit] = 1;
            }
        }
        var want = new byte[]{0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0};
        assertArrayEquals(want, bitField);
    }

    @Test
    void exercise2() {
        var fieldSize = 10;
        var functionCount = 5;
        var tweak = Int.parse(99);
        var bitFieldSize = fieldSize * 8;
        var bitField = Bytes.initFill(bitFieldSize, (byte) 0);
        for (byte[] item : getItemsBig()) {
            for (int i = 0; i < functionCount; i++) {
                var seed = Int.parse(i).mul(BloomFilter.BIP37_CONSTANT).add(tweak);
                var h = Murmur3.hash32(item, seed);
                var bit = h.mod(Int.parse(bitFieldSize)).intValue();
                bitField[bit] = 1;
            }
        }
        var want = Hex.parse("4000600a080000010940");
        assertEquals(want, Hex.parse(Bytes.bitFieldToBytes(bitField)));
    }

    private static List<byte[]> getItemsBig() {
        var items = new ArrayList<byte[]>();
        items.add(Hex.parse("Hello World".getBytes()).toBytes());
        items.add(Hex.parse("Goodbye!".getBytes()).toBytes());
        return items;
    }

    @DisabledIfSystemProperty(named = "network", matches = "false", disabledReason = "needs a bitcoin node")
    @Test
    void example5() {
        var lastBlockHex = Hex.parse("00000000000538d5c2246336644f9a4956551afb44ba47278759ec55ea912e19");
        var address = "mwJn1YPMq7y5F8J3LkC5Hxg9PHyZ5K4cFv";
        var h160 = Base58.decode(address);
        var node = new SimpleNode(Properties.getBitcoinP2pHost(), Properties.getBitcoinP2pPort(), Properties.getBitcoinP2pTestnet(), false);
        var bf = new BloomFilter(30, 5, 90210);
        bf.add(h160);
        node.handshake();
        node.send(bf.filterload(null));
        var startBlock = lastBlockHex.toBytes();
        var getheaders = new GetHeadersMessage(null, null, startBlock, null);
        node.send(getheaders);
        var headers = node.waitFor(Set.of(HeadersMessage.COMMAND));
        if (headers == null) {
            Assertions.fail("no headers");
        }
        var headersMessage = HeadersMessage.parse(new ByteArrayInputStream(headers.getPayload()));
        log.fine(String.format("header with %s blocks.", headersMessage.getBlocks().length));
        boolean found = false;
        int imb = 0;
        int itx = 0;
        for (int b = 0; b < headersMessage.getBlocks().length; b++) {
            var block = headersMessage.getBlocks()[b];
            if (!block.checkPow()) {
                throw new IllegalStateException("proof of work is invalid");
            }
            var getdata = new GetDataMessage();
            getdata.addData(new GetDataType(GetDataType.FILTERED_BLOCK_DATA_TYPE, block.hash()));
            node.send(getdata);
            var message = node.waitFor(Set.of(MerkleBlock.COMMAND, Tx.COMMAND));
            if (message == null) {
                Assertions.fail("no merkleblock or tx");
            }
            if (message.isCommand(MerkleBlock.COMMAND)) {
                var mb = MerkleBlock.parse(new ByteArrayInputStream(message.getPayload()));
                if (!mb.isValid()) {
                    throw new IllegalStateException("invalid merkle proof");
                }
                imb++;
            } else if (message.isCommand(Tx.COMMAND)) {
                var tx = Tx.parse(new ByteArrayInputStream(message.getPayload()), Properties.getBitcoinP2pTestnet());
                for (int i = 0; i < tx.getTxOuts().size(); i++) {
                    var script = tx.getTxOuts().get(i).getScriptPubkey();
                    if (OpCodeNames.OP_106_RETURN.equals(script.getCmds().get(0).getOpCode())) {
                        log.fine(String.format("return found: %s", script));
                    } else if (address.equals(script.address(Properties.getBitcoinP2pTestnet()))) {
                        log.fine(String.format("found: %s:%s", tx.id(), i));
                        found = true;
                    } else {
                        log.fine(String.format("not found: %s, %s", address, tx.getTxOuts().get(i).getScriptPubkey().address(Properties.getBitcoinP2pTestnet())));
                    }
                }
                itx++;
            } else {
                throw new IllegalStateException();
            }
            log.fine(String.format("Message command %s, mb %s, tx %s, found %s", Bytes.byteArrayToString(message.getCommand()), imb, itx, found));
            if (found) {
                break;
            }
        }
        if (found) {
            log.fine(String.format("found %s", address));
        } else {
            log.warning(String.format("not found: %s", address));
        }
        node.close();
    }

    @DisabledIfSystemProperty(named = "network", matches = "false", disabledReason = "needs a bitcoin node")
    @Test
    void exercise6() {
        var lastBlockHex = Hex.parse("00000000000000a03f9432ac63813c6710bfe41712ac5ef6faab093fe2917636");
        var secret = Hex.parse(Bytes.changeOrder(Helper.hash256("Jimmy Song".getBytes())));
        var privateKey = new PrivateKey(secret);
        var addr = privateKey.getPoint().address(null, true);
        var h160 = Base58.decode(addr);
        var targetAddress = "mwJn1YPMq7y5F8J3LkC5Hxg9PHyZ5K4cFv";
        var targetH160 = Base58.decode(targetAddress);
        var targetScript = Script.p2pkhScript(targetH160);
        var fee = Int.parse(5000);  // fee in satoshis
        // connect to testnet.programmingbitcoin.com in testnet mode
        var node = new SimpleNode(Properties.getBitcoinP2pHost(), Properties.getBitcoinP2pPort(), Properties.getBitcoinP2pTestnet(), false);
        // Create a Bloom Filter of size 30 and 5 functions. Add a tweak.
        var bf = new BloomFilter(30, 5, 90210);
        // add the h160 to the Bloom Filter
        bf.add(h160);
        // complete the handshake
        node.handshake();
        // load the Bloom Filter with the filterload command
        node.send(bf.filterload(null));
        // set start block to last_block from above
        var startBlock = lastBlockHex.toBytes();
        // send a getheaders message with the starting block
        var getheaders = new GetHeadersMessage(null, null, startBlock, null);
        node.send(getheaders);
        // wait for the headers message
        var headers = node.waitFor(Set.of(HeadersMessage.COMMAND));
        if (headers == null) {
            Assertions.fail("no headers");
        }
        var headersMessage = HeadersMessage.parse(new ByteArrayInputStream(headers.getPayload()));
        // store the last block as None
        byte[] lastBlock = null;

        // initialize prev_tx, prev_index, and prev_amount to None
        Int prevTx = null;
        Int prevIndex = null;
        Int prevAmount = null;

        // loop through the blocks in the headers
        for (Block block : headersMessage.getBlocks()) {
            // check that the proof of work on the block is valid
            if (!block.checkPow()) {
                throw new IllegalStateException("proof of work is invalid");
            }
            // check that this block's prev_block is the last block
            if (lastBlock != null && !Arrays.equals(block.getPrevBlock(), lastBlock)) {
                throw new IllegalStateException("chain broken");
            }
            // initialize the GetDataMessage
            var getdata = new GetDataMessage();
            // add a new item to the getdata message
            // should be FILTERED_BLOCK_DATA_TYPE and block hash
            getdata.addData(new GetDataType(GetDataType.FILTERED_BLOCK_DATA_TYPE, block.hash()));

            // send the getdata message
            node.send(getdata);

            // wait for the merkleblock or tx commands
            var message = node.waitFor(Set.of(MerkleBlock.COMMAND, Tx.COMMAND));
            if (message == null) {
                Assertions.fail("no merkleblock or tx");
            }
            if (message.isCommand(MerkleBlock.COMMAND)) {
                // if we have the merkleblock command
                var mb = MerkleBlock.parse(new ByteArrayInputStream(message.getPayload()));
                // check that the MerkleBlock is valid
                if (!mb.isValid()) {
                    throw new IllegalStateException("invalid merkle proof");
                }
            } else if (message.isCommand(Tx.COMMAND)) {
                // else we have the tx command
                // set the tx's testnet to be True
                var tx = Tx.parse(new ByteArrayInputStream(message.getPayload()), Properties.getBitcoinP2pTestnet());
                // loop through the tx outs
                for (int i = 0; i < tx.getTxOuts().size(); i++) {
                    // if our output has the same address as our address we found it
                    if (addr.equals(tx.getTxOuts().get(i).getScriptPubkey().address(Properties.getBitcoinP2pTestnet()))) {
                        // we found our utxo; set prev_tx, prev_index, and tx
                        prevTx = Hex.parse(tx.hash());
                        prevIndex = Int.parse(i);
                        prevAmount = tx.getTxOuts().get(i).getAmount();
                        log.fine(String.format("found: %s:%s", prevTx, prevIndex));
                    }
                }
            } else {
                throw new IllegalStateException();
            }

            // set the last block to the current hash
            lastBlock = block.hash();
        }

        if (prevTx != null) {
            // create the TxIn
            var txIn = new TxIn(prevTx, prevIndex, null, null);
            // calculate the output amount (previous amount minus the fee)
            var outputAmount = prevAmount.sub(fee);
            // create a new TxOut to the target script with the output amount
            var txOut = new TxOut(outputAmount, targetScript);
            // create a new transaction with the one input and one output
            var txObj = new Tx(Int.parse(1), List.of(txIn), List.of(txOut), Int.parse(0), Properties.getBitcoinP2pTestnet(), null);
            // sign the only input of the transaction
            assertTrue(txObj.signInput(0, privateKey));
            // serialize and hex to see what it looks like
            log.fine(String.format("%s", Hex.parse(txObj.serialize())));
            // send this signed transaction on the network
            node.send(txObj);
            // wait a sec so this message goes through with time.sleep(1)
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.severe(e.getMessage());
            }
            // now ask for this transaction from the other node
            // create a GetDataMessage
            var getdata = new GetDataMessage();
            // ask for our transaction by adding it to the message
            getdata.addData(new GetDataType(GetDataType.TX_DATA_TYPE, txObj.hash()));
            // send the message
            node.send(getdata);
            // now wait for a Tx response
            var receivedTx = node.waitFor(Set.of(Tx.COMMAND));
            var receivedTxMessage = Tx.parse(new ByteArrayInputStream(receivedTx.getPayload()), Properties.getBitcoinP2pTestnet());
            // if the received tx has the same id as our tx, we are done!
            assertEquals(txObj.id(), receivedTxMessage.id());
        } else {
            log.warning(String.format("not found: %s", addr));
        }
        node.close();
    }
}
