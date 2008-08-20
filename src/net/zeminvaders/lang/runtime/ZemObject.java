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

import net.zeminvaders.lang.InvalidTypeException;
import net.zeminvaders.lang.TypeMismatchException;

/**
 *
 *
 * @author <a href="mailto:grom@zeminvaders.net">Cameron Zemek</a>
 */
public abstract class ZemObject implements Comparable<ZemObject> {
    public ZemNumber toNumber() {
        if (this instanceof ZemNumber) {
            return (ZemNumber) this;
        }
        throw new InvalidTypeException("Expecting number");
    }

    public ZemBoolean toBoolean() {
        if (this instanceof ZemBoolean) {
            return (ZemBoolean) this;
        }
        throw new InvalidTypeException("Expecting boolean");
    }

    public ZemString toZString() {
    /*
        if (this instanceof ZemString) {
            return (ZemString) this;
        }
        throw new InvalidTypeException("Expecting string");
     */
        if (this instanceof ZemString)
            return (ZemString) this;
        // Implicit converting of types to string
        return new ZemString(this.toString());
    }

    protected void checkTypes(ZemObject left, ZemObject right) {
        if (!left.getClass().equals(right.getClass())) {
            throw new TypeMismatchException();
        }
    }
}
