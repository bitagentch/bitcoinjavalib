package ch.bitagent.bitcoin.java.chapter;

import ch.bitagent.bitcoin.java.block.MerkleTree;
import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.helper.Bytes;
import ch.bitagent.bitcoin.java.helper.Helper;
import ch.bitagent.bitcoin.java.helper.Merkle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Chapter11Test {

    private static final Logger log = Logger.getLogger(Chapter11Test.class.getSimpleName());

    @Test
    void example1() {
        var hash0 = Hex.parse("c117ea8ec828342f4dfb0ad6bd140e03a50720ece40169ee38bdc15d9eb64cf5");
        var hash1 = Hex.parse("c131474164b412e3406696da1ee20ab0fc9bf41c8f05fa8ceea7a08d672d7cc5");
        var parent = Merkle.merkleParent(hash0.toBytes(), hash1.toBytes());
        assertEquals(Hex.parse("8b30c5ba100f6f2e5ad1e2a742e5020491240f8eb514fe97c713c31718ad7ecd"), Hex.parse(parent));
    }

    @Test
    void example2() {
        var hexHashes = new ArrayList<byte[]>();
        hexHashes.add(Hex.parse("c117ea8ec828342f4dfb0ad6bd140e03a50720ece40169ee38bdc15d9eb64cf5").toBytes());
        hexHashes.add(Hex.parse("c131474164b412e3406696da1ee20ab0fc9bf41c8f05fa8ceea7a08d672d7cc5").toBytes());
        hexHashes.add(Hex.parse("f391da6ecfeed1814efae39e7fcb3838ae0b02c02ae7d0a5848a66947c0727b0").toBytes());
        hexHashes.add(Hex.parse("3d238a92a94532b946c90e19c49351c763696cff3db400485b813aecb8a13181").toBytes());
        hexHashes.add(Hex.parse("10092f2633be5f3ce349bf9ddbde36caa3dd10dfa0ec8106bce23acbff637dae").toBytes());
        var parentLevel = Merkle.merkleParentLevel(hexHashes);
        assertEquals("8b30c5ba100f6f2e5ad1e2a742e5020491240f8eb514fe97c713c31718ad7ecd", Hex.parse(parentLevel.get(0)).toString());
        assertEquals("7f4e6f9e224e20fda0ae4c44114237f97cd35aca38d83081c9bfd41feb907800", Hex.parse(parentLevel.get(1)).toString());
        assertEquals("3ecf6115380c77e8aae56660f5634982ee897351ba906a6837d15ebc3a225df0", Hex.parse(parentLevel.get(2)).toString());
    }

    @Test
    void example3() {
        var hexHashes = new ArrayList<byte[]>();
        hexHashes.add(Hex.parse("c117ea8ec828342f4dfb0ad6bd140e03a50720ece40169ee38bdc15d9eb64cf5").toBytes());
        hexHashes.add(Hex.parse("c131474164b412e3406696da1ee20ab0fc9bf41c8f05fa8ceea7a08d672d7cc5").toBytes());
        hexHashes.add(Hex.parse("f391da6ecfeed1814efae39e7fcb3838ae0b02c02ae7d0a5848a66947c0727b0").toBytes());
        hexHashes.add(Hex.parse("3d238a92a94532b946c90e19c49351c763696cff3db400485b813aecb8a13181").toBytes());
        hexHashes.add(Hex.parse("10092f2633be5f3ce349bf9ddbde36caa3dd10dfa0ec8106bce23acbff637dae").toBytes());
        hexHashes.add(Hex.parse("7d37b3d54fa6a64869084bfd2e831309118b9e833610e6228adacdbd1b4ba161").toBytes());
        hexHashes.add(Hex.parse("8118a77e542892fe15ae3fc771a4abfd2f5d5d5997544c3487ac36b5c85170fc").toBytes());
        hexHashes.add(Hex.parse("dff6879848c2c9b62fe652720b8df5272093acfaa45a43cdb3696fe2466a3877").toBytes());
        hexHashes.add(Hex.parse("b825c0745f46ac58f7d3759e6dc535a1fec7820377f24d4c2c6ad2cc55c0cb59").toBytes());
        hexHashes.add(Hex.parse("95513952a04bd8992721e9b7e2937f1c04ba31e0469fbe615a78197f68f52b7c").toBytes());
        hexHashes.add(Hex.parse("2e6d722e5e4dbdf2447ddecc9f7dabb8e299bae921c99ad5b0184cd9eb8e5908").toBytes());
        hexHashes.add(Hex.parse("b13a750047bc0bdceb2473e5fe488c2596d7a7124b4e716fdd29b046ef99bbf0").toBytes());
        var wantHexHash = Hex.parse("acbcab8bcc1af95d8d563b77d24c3d19b18f1486383d75a5085c4e86c86beed6").toBytes();
        assertArrayEquals(wantHexHash, Merkle.merkleRoot(hexHashes));
    }

    @Test
    void example4() {
        var txHexHashes = new ArrayList<byte[]>();
        txHexHashes.add(Hex.parse("42f6f52f17620653dcc909e58bb352e0bd4bd1381e2955d19c00959a22122b2e").toBytes());
        txHexHashes.add(Hex.parse("94c3af34b9667bf787e1c6a0a009201589755d01d02fe2877cc69b929d2418d4").toBytes());
        txHexHashes.add(Hex.parse("959428d7c48113cb9149d0566bde3d46e98cf028053c522b8fa8f735241aa953").toBytes());
        txHexHashes.add(Hex.parse("a9f27b99d5d108dede755710d4a1ffa2c74af70b4ca71726fa57d68454e609a2").toBytes());
        txHexHashes.add(Hex.parse("62af110031e29de1efcad103b3ad4bec7bdcf6cb9c9f4afdd586981795516577").toBytes());
        txHexHashes.add(Hex.parse("766900590ece194667e9da2984018057512887110bf54fe0aa800157aec796ba").toBytes());
        txHexHashes.add(Hex.parse("e8270fb475763bc8d855cfe45ed98060988c1bdcad2ffc8364f783c98999a208").toBytes());
        var hashes = txHexHashes.stream().map(Bytes::changeOrder).collect(Collectors.toList());
        assertArrayEquals(Hex.parse("654d6181e18e4ac4368383fdc5eead11bf138f9b7ac1e15334e4411b3c4797d9").toBytes(), Bytes.changeOrder(Merkle.merkleRoot(hashes)));
    }

    @Test
    void example5() {
        printMerkleTree(16);
    }

    @Test
    void exercise5() {
        printMerkleTree(27);
    }

    private static void printMerkleTree(int total) {
        var maxDepth = (int) Math.ceil(Helper.logWithBase(total, 2));
        var merkleTree = new ArrayList<byte[]>();
        for (int depth = 0; depth < maxDepth+1; depth++) {
            var numItems = (int) Math.ceil(total / Math.pow(2, maxDepth - depth));
            var levelHashes = Bytes.initFill(numItems, (byte) numItems);
            merkleTree.add(levelHashes);
        }
        for (byte[] levelHash : merkleTree) {
            log.fine(String.format("levelHash %s", Hex.parse(levelHash)));
        }
    }

    @Test
    void example6() {
        var hexHashes = new ArrayList<byte[]>();
        hexHashes.add(Hex.parse("9745f7173ef14ee4155722d1cbf13304339fd00d900b759c6f9d58579b5765fb").toBytes());
        hexHashes.add(Hex.parse("5573c8ede34936c29cdfdfe743f7f5fdfbd4f54ba0705259e62f39917065cb9b").toBytes());
        hexHashes.add(Hex.parse("82a02ecbb6623b4274dfcab82b336dc017a27136e08521091e443e62582e8f05").toBytes());
        hexHashes.add(Hex.parse("507ccae5ed9b340363a0e6d765af148be9cb1c8766ccc922f83e4ae681658308").toBytes());
        hexHashes.add(Hex.parse("a7a4aec28e7162e1e9ef33dfa30f0bc0526e6cf4b11a576f6c5de58593898330").toBytes());
        hexHashes.add(Hex.parse("bb6267664bd833fd9fc82582853ab144fece26b7a8a5bf328f8a059445b59add").toBytes());
        hexHashes.add(Hex.parse("ea6d7ac1ee77fbacee58fc717b990c4fcccf1b19af43103c090f601677fd8836").toBytes());
        hexHashes.add(Hex.parse("457743861de496c429912558a106b810b0507975a49773228aa788df40730d41").toBytes());
        hexHashes.add(Hex.parse("7688029288efc9e9a0011c960a6ed9e5466581abf3e3a6c26ee317461add619a").toBytes());
        hexHashes.add(Hex.parse("b1ae7f15836cb2286cdd4e2c37bf9bb7da0a2846d06867a429f654b2e7f383c9").toBytes());
        hexHashes.add(Hex.parse("9b74f89fa3f93e71ff2c241f32945d877281a6a50a6bf94adac002980aafe5ab").toBytes());
        hexHashes.add(Hex.parse("b3a92b5b255019bdaf754875633c2de9fec2ab03e6b8ce669d07cb5b18804638").toBytes());
        hexHashes.add(Hex.parse("b5c0b915312b9bdaedd2b86aa2d0f8feffc73a2d37668fd9010179261e25e263").toBytes());
        hexHashes.add(Hex.parse("c9d52c5cb1e557b92c84c52e7c4bfbce859408bedffc8a5560fd6e35e10b8800").toBytes());
        hexHashes.add(Hex.parse("c555bc5fc3bc096df0a0c9532f07640bfb76bfe4fc1ace214b8b228a1297a4c2").toBytes());
        hexHashes.add(Hex.parse("f9dbfafc3af3400954975da24eb325e326960a25b87fffe23eef3e7ed2fb610e").toBytes());
        var tree = new MerkleTree(hexHashes.size());
        tree.getNodes().get(4).setItems(hexHashes);
        assertEquals("{9745f717..., 5573c8ed..., 82a02ecb..., 507ccae5..., a7a4aec2..., bb626766..., ea6d7ac1..., 45774386..., 76880292..., b1ae7f15..., 9b74f89f..., b3a92b5b..., b5c0b915..., c9d52c5c..., c555bc5f..., f9dbfafc...}", tree.getNodes().get(4).toString());
        tree.getNodes().get(3).setItems(Merkle.merkleParentLevel(tree.getNodes().get(4).getItems()));
        assertEquals("{272945ec..., 9a38d037..., 4a64abd9..., ec7c95e1..., 3b67006c..., 850683df..., d40d268b..., 8636b7a3...}", tree.getNodes().get(3).toString());
        tree.getNodes().get(2).setItems(Merkle.merkleParentLevel(tree.getNodes().get(3).getItems()));
        assertEquals("{3ba6c080..., 8e894862..., 7ab01bb6..., 3df760ac...}", tree.getNodes().get(2).toString());
        tree.getNodes().get(1).setItems(Merkle.merkleParentLevel(tree.getNodes().get(2).getItems()));
        assertEquals("{6382df3f..., 87cf8fa3...}", tree.getNodes().get(1).toString());
        tree.getNodes().get(0).setItems(Merkle.merkleParentLevel(tree.getNodes().get(1).getItems()));
        assertEquals("{*597c4baf.*}", tree.getNodes().get(0).toString());
        log.fine(tree.toString());
    }

    @Test
    void example7() {
        var hexHashes = new ArrayList<byte[]>();
        hexHashes.add(Hex.parse("9745f7173ef14ee4155722d1cbf13304339fd00d900b759c6f9d58579b5765fb").toBytes());
        hexHashes.add(Hex.parse("5573c8ede34936c29cdfdfe743f7f5fdfbd4f54ba0705259e62f39917065cb9b").toBytes());
        hexHashes.add(Hex.parse("82a02ecbb6623b4274dfcab82b336dc017a27136e08521091e443e62582e8f05").toBytes());
        hexHashes.add(Hex.parse("507ccae5ed9b340363a0e6d765af148be9cb1c8766ccc922f83e4ae681658308").toBytes());
        hexHashes.add(Hex.parse("a7a4aec28e7162e1e9ef33dfa30f0bc0526e6cf4b11a576f6c5de58593898330").toBytes());
        hexHashes.add(Hex.parse("bb6267664bd833fd9fc82582853ab144fece26b7a8a5bf328f8a059445b59add").toBytes());
        hexHashes.add(Hex.parse("ea6d7ac1ee77fbacee58fc717b990c4fcccf1b19af43103c090f601677fd8836").toBytes());
        hexHashes.add(Hex.parse("457743861de496c429912558a106b810b0507975a49773228aa788df40730d41").toBytes());
        hexHashes.add(Hex.parse("7688029288efc9e9a0011c960a6ed9e5466581abf3e3a6c26ee317461add619a").toBytes());
        hexHashes.add(Hex.parse("b1ae7f15836cb2286cdd4e2c37bf9bb7da0a2846d06867a429f654b2e7f383c9").toBytes());
        hexHashes.add(Hex.parse("9b74f89fa3f93e71ff2c241f32945d877281a6a50a6bf94adac002980aafe5ab").toBytes());
        hexHashes.add(Hex.parse("b3a92b5b255019bdaf754875633c2de9fec2ab03e6b8ce669d07cb5b18804638").toBytes());
        hexHashes.add(Hex.parse("b5c0b915312b9bdaedd2b86aa2d0f8feffc73a2d37668fd9010179261e25e263").toBytes());
        hexHashes.add(Hex.parse("c9d52c5cb1e557b92c84c52e7c4bfbce859408bedffc8a5560fd6e35e10b8800").toBytes());
        hexHashes.add(Hex.parse("c555bc5fc3bc096df0a0c9532f07640bfb76bfe4fc1ace214b8b228a1297a4c2").toBytes());
        hexHashes.add(Hex.parse("f9dbfafc3af3400954975da24eb325e326960a25b87fffe23eef3e7ed2fb610e").toBytes());
        var tree = new MerkleTree(hexHashes.size());
        tree.getNodes().get(4).setItems(hexHashes);
        while (tree.root() == null) {
            if (tree.isLeaf()) {
                tree.up();
            } else {
                var leftHash = tree.getLeftNode();
                var rightHash = tree.getRightNode();
                if (leftHash == null) {
                    tree.left();
                } else if (rightHash == null) {
                    tree.right();
                } else {
                    tree.setCurrentNode(Merkle.merkleParent(leftHash, rightHash));
                    tree.up();
                }
            }
        }
        log.fine(tree.toString());
        assertEquals("{*597c4baf.*}", tree.getNodes().get(0).toString());
        assertEquals("{6382df3f..., 87cf8fa3...}", tree.getNodes().get(1).toString());
        assertEquals("{3ba6c080..., 8e894862..., 7ab01bb6..., 3df760ac...}", tree.getNodes().get(2).toString());
        assertEquals("{272945ec..., 9a38d037..., 4a64abd9..., ec7c95e1..., 3b67006c..., 850683df..., d40d268b..., 8636b7a3...}", tree.getNodes().get(3).toString());
        assertEquals("{9745f717..., 5573c8ed..., 82a02ecb..., 507ccae5..., a7a4aec2..., bb626766..., ea6d7ac1..., 45774386..., 76880292..., b1ae7f15..., 9b74f89f..., b3a92b5b..., b5c0b915..., c9d52c5c..., c555bc5f..., f9dbfafc...}", tree.getNodes().get(4).toString());
    }

    @Test
    void example8() {
        var hexHashes = new ArrayList<byte[]>();
        hexHashes.add(Hex.parse("9745f7173ef14ee4155722d1cbf13304339fd00d900b759c6f9d58579b5765fb").toBytes());
        hexHashes.add(Hex.parse("5573c8ede34936c29cdfdfe743f7f5fdfbd4f54ba0705259e62f39917065cb9b").toBytes());
        hexHashes.add(Hex.parse("82a02ecbb6623b4274dfcab82b336dc017a27136e08521091e443e62582e8f05").toBytes());
        hexHashes.add(Hex.parse("507ccae5ed9b340363a0e6d765af148be9cb1c8766ccc922f83e4ae681658308").toBytes());
        hexHashes.add(Hex.parse("a7a4aec28e7162e1e9ef33dfa30f0bc0526e6cf4b11a576f6c5de58593898330").toBytes());
        hexHashes.add(Hex.parse("bb6267664bd833fd9fc82582853ab144fece26b7a8a5bf328f8a059445b59add").toBytes());
        hexHashes.add(Hex.parse("ea6d7ac1ee77fbacee58fc717b990c4fcccf1b19af43103c090f601677fd8836").toBytes());
        hexHashes.add(Hex.parse("457743861de496c429912558a106b810b0507975a49773228aa788df40730d41").toBytes());
        hexHashes.add(Hex.parse("7688029288efc9e9a0011c960a6ed9e5466581abf3e3a6c26ee317461add619a").toBytes());
        hexHashes.add(Hex.parse("b1ae7f15836cb2286cdd4e2c37bf9bb7da0a2846d06867a429f654b2e7f383c9").toBytes());
        hexHashes.add(Hex.parse("9b74f89fa3f93e71ff2c241f32945d877281a6a50a6bf94adac002980aafe5ab").toBytes());
        hexHashes.add(Hex.parse("b3a92b5b255019bdaf754875633c2de9fec2ab03e6b8ce669d07cb5b18804638").toBytes());
        hexHashes.add(Hex.parse("b5c0b915312b9bdaedd2b86aa2d0f8feffc73a2d37668fd9010179261e25e263").toBytes());
        hexHashes.add(Hex.parse("c9d52c5cb1e557b92c84c52e7c4bfbce859408bedffc8a5560fd6e35e10b8800").toBytes());
        hexHashes.add(Hex.parse("c555bc5fc3bc096df0a0c9532f07640bfb76bfe4fc1ace214b8b228a1297a4c2").toBytes());
        hexHashes.add(Hex.parse("f9dbfafc3af3400954975da24eb325e326960a25b87fffe23eef3e7ed2fb610e").toBytes());
        hexHashes.add(Hex.parse("38faf8c811988dff0a7e6080b1771c97bcc0801c64d9068cffb85e6e7aacaf51").toBytes());
        var tree = new MerkleTree(hexHashes.size());
        tree.getNodes().get(5).setItems(hexHashes);
        while (tree.root() == null) {
            if (tree.isLeaf()) {
                tree.up();
            } else {
                var leftHash = tree.getLeftNode();
                if (leftHash == null) {
                    tree.left();
                } else if (tree.rightExists()) {
                    var rightHash = tree.getRightNode();
                    if (rightHash == null) {
                        tree.right();
                    } else {
                        tree.setCurrentNode(Merkle.merkleParent(leftHash, rightHash));
                        tree.up();
                    }
                } else {
                    tree.setCurrentNode(Merkle.merkleParent(leftHash, leftHash));
                    tree.up();
                }
            }
        }
        log.fine(tree.toString());
        assertEquals("{*0a313864.*}", tree.getNodes().get(0).toString());
        assertEquals("{597c4baf..., 6f8a8190...}", tree.getNodes().get(1).toString());
        assertEquals("{6382df3f..., 87cf8fa3..., 5647f416...}", tree.getNodes().get(2).toString());
        assertEquals("{3ba6c080..., 8e894862..., 7ab01bb6..., 3df760ac..., 28e93b98...}", tree.getNodes().get(3).toString());
        assertEquals("{272945ec..., 9a38d037..., 4a64abd9..., ec7c95e1..., 3b67006c..., 850683df..., d40d268b..., 8636b7a3..., ce26d40b...}", tree.getNodes().get(4).toString());
        assertEquals("{9745f717..., 5573c8ed..., 82a02ecb..., 507ccae5..., a7a4aec2..., bb626766..., ea6d7ac1..., 45774386..., 76880292..., b1ae7f15..., 9b74f89f..., b3a92b5b..., b5c0b915..., c9d52c5c..., c555bc5f..., f9dbfafc..., 38faf8c8...}", tree.getNodes().get(5).toString());
    }
}
