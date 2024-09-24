package ch.bitagent.bitcoin.java.script;

import ch.bitagent.bitcoin.java.ecc.Hex;
import ch.bitagent.bitcoin.java.ecc.Int;
import ch.bitagent.bitcoin.java.ecc.S256Point;
import ch.bitagent.bitcoin.java.ecc.Signature;
import ch.bitagent.bitcoin.java.helper.Bytes;
import ch.bitagent.bitcoin.java.helper.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.logging.Logger;

public class Op {

    private static final Logger log = Logger.getLogger(Op.class.getSimpleName());

    private Op() {}

    static byte[] encodeNum(int num) {
        var result = new byte[0];
        if (num == 0) {
            return result;
        }
        var absNum = Math.abs(num);
        var negative = num < 0;
        while (absNum != 0) {
            result = Bytes.add(result, new byte[]{(byte) (absNum & 0xff)});
            absNum >>= 8;
        }
        // if the top bit is set,
        // for negative numbers we ensure that the top bit is set
        // for positive numbers we ensure that the top bit is not set
        if ((result[result.length - 1] & 0x80) != 0) {
            if (negative) {
                result = Bytes.add(result, new byte[]{(byte) 0x80});
            } else {
                result = Bytes.add(result, new byte[]{(byte) 0x00});
            }
        } else if (negative) {
            result[result.length - 1] |= (byte) 0x80;
        }
        return result;
    }

    static int decodeNum(byte[] element) {
        if (Arrays.equals(element, new byte[0])) {
            return 0;
        }
        // reverse for big endian
        var bigEndian = Bytes.changeOrder(element);
        // top bit being 1 means it's negative
        boolean negative;
        int result;
        if ((bigEndian[0] & 0x80) != 0) {
            negative = true;
            result = bigEndian[0] & 0x7f;
        } else {
            negative = false;
            result = bigEndian[0];
        }
        for (int c = 1; c < bigEndian.length; c++) {
            result <<= 8;
            result += c;
        }
        if (negative) {
            return -result;
        } else {
            return result;
        }
    }

    public static boolean op0(Deque<byte[]> stack) {
        stack.push(encodeNum(0));
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    public static boolean op1(Deque<byte[]> stack) {
        stack.push(encodeNum(1));
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    public static boolean op2(Deque<byte[]> stack) {
        stack.push(encodeNum(2));
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    public static boolean op6(Deque<byte[]> stack) {
        stack.push(encodeNum(6));
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    public static boolean op105Verify(Deque<byte[]> stack) {
        if (stack.isEmpty()) {
            return false;
        }
        byte[] element = stack.pop();
        if (decodeNum(element) == 0) {
            return false;
        }
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    public static boolean op1102Dup(Deque<byte[]> stack) {
        if (stack.size() < 2) {
            return false;
        }
        var element1 = stack.pop();
        var element2 = stack.pop();
        stack.push(element2);
        stack.push(element1);
        stack.push(element2);
        stack.push(element1);
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    static boolean op118Dup(Deque<byte[]> stack) {
        if (stack.isEmpty()) {
            return false;
        }
        byte[] element = stack.pop();
        stack.push(element);
        stack.push(element);
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    public static boolean op124Swap(Deque<byte[]> stack) {
        if (stack.size() < 2) {
            return false;
        }
        var element1 = stack.pop();
        var element2 = stack.pop();
        stack.push(element1);
        stack.push(element2);
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    public static boolean op135Equal(Deque<byte[]> stack) {
        if (stack.size() < 2) {
            return false;
        }
        Int element1 = Hex.parse(stack.pop());
        Int element2 = Hex.parse(stack.pop());
        if (element1.eq(element2)) {
            stack.push(encodeNum(1));
        } else {
            stack.push(encodeNum(0));
        }
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    public static boolean op136EqualVerify(Deque<byte[]> stack) {
        return op135Equal(stack) && op105Verify(stack);
    }

    public static boolean op145Not(Deque<byte[]> stack) {
        if (stack.isEmpty()) {
            return false;
        }
        var element = stack.pop();
        if (decodeNum(element) == 0) {
            stack.push(encodeNum(1));
        } else {
            stack.push(encodeNum(0));
        }
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    public static boolean op147Add(Deque<byte[]> stack) {
        if (stack.size() < 2) {
            return false;
        }
        Int element1 = Hex.parse(stack.pop());
        Int element2 = Hex.parse(stack.pop());
        stack.push(encodeNum(element1.add(element2).intValue()));
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    static boolean op167Sha1(Deque<byte[]> stack) {
        if (stack.isEmpty()) {
            return false;
        }
        byte[] element = stack.pop();
        stack.push(Helper.sha1(element));
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    static boolean op169Hash160(Deque<byte[]> stack) {
        if (stack.isEmpty()) {
            return false;
        }
        byte[] element = stack.pop();
        stack.push(Helper.hash160(element));
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    static boolean op170Hash256(Deque<byte[]> stack) {
        if (stack.isEmpty()) {
            return false;
        }
        byte[] element = stack.pop();
        stack.push(Helper.hash256(element));
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    static boolean op172Checksig(Deque<byte[]> stack, Int z) {
        // check that there are at least 2 elements on the stack
        if (stack.size() < 2) {
            return false;
        }
        // the top element of the stack is the SEC pubkey
        var secPubkey = stack.pop();
        // the next element of the stack is the DER signature
        // take off the last byte of the signature as that's the hash_type
        //    der_signature = stack.pop()[:-1]
        var derSignatureWithHashType = stack.pop();
        var derSignature = Arrays.copyOf(derSignatureWithHashType, derSignatureWithHashType.length - 1);
        // parse the serialized pubkey and signature into objects
        S256Point point;
        Signature sig;
        try {
            point = S256Point.parse(secPubkey);
            sig = Signature.parse(derSignature);
        } catch (Exception e) {
            log.severe(String.format("Exception %s", e.getMessage()));
            return false;
        }
        // verify the signature using S256Point.verify()
        // push an encoded 1 or 0 depending on whether the signature verified
        if (point.verify(z, sig)) {
            stack.push(encodeNum(1));
        } else {
            stack.push(encodeNum(0));
        }
        log.fine(String.format("%s", printStack(stack)));
        return true;
    }

    static boolean op174Checkmultisig(Deque<byte[]> stack, Int z) {
        if (stack.isEmpty()) {
            return false;
        }
        var n = decodeNum(stack.pop());
        if (stack.size() < n + 1) {
            return false;
        }
        var secPubkeys = new ArrayList<byte[]>();
        for (int i = 0; i < n; i++) {
            secPubkeys.add(stack.pop());
        }
        var m = decodeNum(stack.pop());
        if (stack.size() < m + 1) {
            return false;
        }
        var derSignatures = new ArrayList<byte[]>();
        for (int i = 0; i < m; i++) {
            // signature is assumed to be using SIGHASH_ALL
            var derSignature = stack.pop();
            derSignatures.add(Arrays.copyOf(derSignature, derSignature.length - 1));
        }
        // OP_CHECKMULTISIG bug
        stack.pop();
        // parse all the points
        var points = new ArrayList<S256Point>();
        for (byte[] secPubkey : secPubkeys) {
            points.add(S256Point.parse(secPubkey));
        }
        // parse all the signatures
        var sigs = new ArrayList<Signature>();
        for (byte[] derSignature : derSignatures) {
            sigs.add(Signature.parse(derSignature));
        }
        // loop through the signatures
        for (Signature sig : sigs) {
            // if we have no more points, signatures are no good
            if (points.isEmpty()) {
                log.severe("signatures no good or not in right order");
                return false;
            }
            // we loop until we find the point which works with this signature
            for (S256Point point : points) {
                // get the current point from the list of points
                points.remove(point);
                // # we check if this signature goes with the current point
                if (point.verify(z, sig)) {
                    break;
                }
            }
        }
        // the signatures are valid, so push a 1 to the stack
        stack.add(encodeNum(1));
        return true;
    }

    public static String printStack(Deque<byte[]> stack) {
        StringBuilder stackBuilder = new StringBuilder();
        stackBuilder.append("[");
        String sep = "";
        for (byte[] bytes : stack) {
            stackBuilder.append(sep);
            String hexString = Bytes.byteArrayToHexString(bytes);
            if (hexString.length() >= 10) {
                stackBuilder.append(Helper.maskString(hexString, 4));
            } else {
                stackBuilder.append(hexString);
            }
            sep = " : ";
        }
        stackBuilder.append("]");
        return stackBuilder.toString();
    }
}
