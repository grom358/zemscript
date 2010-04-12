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
package net.zeminvaders.lang;

import net.zeminvaders.lang.runtime.Dictionary;
import net.zeminvaders.lang.runtime.ZemArray;
import net.zeminvaders.lang.runtime.ZemBoolean;
import net.zeminvaders.lang.runtime.ZemNumber;
import net.zeminvaders.lang.runtime.ZemString;

/**
 * Types don't match.
 *
 * @author <a href="mailto:grom@zeminvaders.net">Cameron Zemek</a>
 */
public class TypeMismatchException extends ZemException {
    private static final long serialVersionUID = 9115378805326306069L;

    static private String toString(Class type) {
        if (type == Dictionary.class) {
            return "dictionary";
        } else if (type == ZemArray.class) {
            return "array";
        } else if (type == ZemBoolean.class) {
            return "boolean";
        } else if (type == ZemNumber.class) {
            return "number";
        } else if (type == ZemString.class) {
            return "string";
        } else {
            return type.getName();
        }
    }

    public TypeMismatchException(SourcePosition pos, Class expect, Class actual) {
        super("Type mismatch - Excepted type '" + toString(expect) + "' but got type '" + toString(actual) + "'", pos);
    }
}
