/**
 *   Copyright 2014 Prasanth Jayachandran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.bitagent.bitcoin.lib.helper;

import ch.bitagent.bitcoin.lib.ecc.Int;

/**
 * https://github.com/prasanthj/hasher/blob/master/src/main/java/hasher/Murmur3.java
 * https://en.wikipedia.org/wiki/MurmurHash
 */
public class Murmur3 {

  private Murmur3() {}

  // Constants for 32 bit variant
  private static final int C1 = 0xcc9e2d51;
  private static final int C2 = 0x1b873593;
  private static final int R1 = 15;
  private static final int R2 = 13;
  private static final int M = 5;
  private static final int N = 0xe6546b64;

  /**
   * Murmur3 32-bit variant.
   */
  private static int hash32(byte[] data, int seed) {
    int hash = seed;
    int length = data.length;
    final int nblocks = length >> 2;

    // body
    for (int i = 0; i < nblocks; i++) {
      int i4 = i << 2;
      int k = (data[i4] & 0xff)
          | ((data[i4 + 1] & 0xff) << 8)
          | ((data[i4 + 2] & 0xff) << 16)
          | ((data[i4 + 3] & 0xff) << 24);

      // mix functions
      k *= C1;
      k = Integer.rotateLeft(k, R1);
      k *= C2;
      hash ^= k;
      hash = Integer.rotateLeft(hash, R2) * M + N;
    }

    // tail
    int idx = nblocks << 2;
    int k1 = 0;
    switch (length - idx) {
      case 3:
        k1 ^= data[idx + 2] << 16;
      case 2:
        k1 ^= data[idx + 1] << 8;
      case 1:
        k1 ^= data[idx];

        // mix functions
        k1 *= C1;
        k1 = Integer.rotateLeft(k1, R1);
        k1 *= C2;
        hash ^= k1;
    }

    // finalization
    hash ^= length;
    hash ^= (hash >>> 16);
    hash *= 0x85ebca6b;
    hash ^= (hash >>> 13);
    hash *= 0xc2b2ae35;
    hash ^= (hash >>> 16);

    return hash;
  }

  /**
   * <p>hash32.</p>
   *
   * @param data an array of {@link byte} objects
   * @param seed a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
   * @return a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
   */
  public static Int hash32(byte[] data, Int seed) {
    var hash = hash32(data, seed.intValue());
    return Int.parse(toUnsignedInt(hash));
  }

  /**
   * Signed int to Unsigned int in a long
   */
  private static long toUnsignedInt(int signedInt) {
    return signedInt & 0xffffffffL;
  }
}
