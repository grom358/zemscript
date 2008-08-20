/*
 * Copyright (c) 2008 Cameron Zemek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package net.zeminvaders.lang.runtime;

import java.math.BigDecimal;

/**
 *
 *
 * @author <a href="mailto:grom@zeminvaders.net">Cameron Zemek</a>
 */
final public class ZemNumber extends ZemObject {
    private BigDecimal value;

    public ZemNumber(String value) {
        this.value = new BigDecimal(value);
    }

    protected ZemNumber(BigDecimal value) {
        this.value = value;
    }

    protected ZemNumber(int value) {
        this.value = new BigDecimal(value);
    }

    public ZemNumber add(ZemNumber augend) {
        return new ZemNumber(value.add(augend.value));
    }

    public ZemNumber subtract(ZemNumber subtrahend) {
        return new ZemNumber(value.subtract(subtrahend.value));
    }

    public ZemNumber multiply(ZemNumber multiplicand) {
        return new ZemNumber(value.multiply(multiplicand.value));
    }

    public ZemNumber divide(ZemNumber divisor) {
        return new ZemNumber(value.divide(divisor.value));
    }

    public ZemNumber remainder(ZemNumber divisor) {
        return new ZemNumber(value.remainder(divisor.value));
    }

    public ZemNumber power(ZemNumber n) {
        return new ZemNumber(value.pow(n.value.intValueExact()));
    }

    public ZemNumber negate() {
        return new ZemNumber(value.negate());
    }

    public int compareTo(ZemObject object) {
        checkTypes(this, object);
        ZemNumber number = (ZemNumber) object;
        return value.compareTo(number.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object object) {
        return compareTo((ZemObject) object) == 0;
    }
}
