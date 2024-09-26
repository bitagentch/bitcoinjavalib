package ch.bitagent.bitcoin.lib.ecc;

/**
 * Point operators on an elliptic curve
 */
public interface PointOperators {

    /**
     * <p>eq.</p>
     *
     * @param otherPoint a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     * @return a boolean
     */
    boolean eq(PointOperators otherPoint);

    /**
     * <p>ne.</p>
     *
     * @param otherPoint a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     * @return a boolean
     */
    boolean ne(PointOperators otherPoint);

    /**
     * <p>add.</p>
     *
     * @param otherPoint a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     */
    PointOperators add(PointOperators otherPoint);

    /**
     * <p>sub.</p>
     *
     * @param otherPoint a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     */
    PointOperators sub(PointOperators otherPoint);

    /**
     * <p>mul.</p>
     *
     * @param otherPoint a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     */
    PointOperators mul(PointOperators otherPoint);

    /**
     * <p>mul.</p>
     *
     * @param coefficient a int
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     */
    PointOperators mul(int coefficient);

    /**
     * <p>pow.</p>
     *
     * @param exponent a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     */
    PointOperators pow(Int exponent);

    /**
     * <p>div.</p>
     *
     * @param otherPoint a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     */
    PointOperators div(PointOperators otherPoint);

    /**
     * <p>mod.</p>
     *
     * @param divisor a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     */
    PointOperators mod(Int divisor);

    /**
     * <p>powMod.</p>
     *
     * @param exponent a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @param divisor a {@link ch.bitagent.bitcoin.lib.ecc.Int} object
     * @return a {@link ch.bitagent.bitcoin.lib.ecc.PointOperators} object
     */
    PointOperators powMod(Int exponent, Int divisor);
}
