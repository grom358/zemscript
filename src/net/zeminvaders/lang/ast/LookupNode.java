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
package net.zeminvaders.lang.ast;

import java.util.Map;

import net.zeminvaders.lang.Interpreter;
import net.zeminvaders.lang.InvalidTypeException;
import net.zeminvaders.lang.SourcePosition;
import net.zeminvaders.lang.runtime.Dictionary;
import net.zeminvaders.lang.runtime.ZemArray;
import net.zeminvaders.lang.runtime.ZemObject;

/**
 * foreach control structure.
 *
 * @author <a href="mailto:grom@zeminvaders.net">Cameron Zemek</a>
 */
public class LookupNode extends Node {
    private VariableNode varNode;
    private Node keyNode;

    public LookupNode(SourcePosition pos, VariableNode varNode, Node keyNode) {
        super(pos);
        this.varNode = varNode;
        this.keyNode = keyNode;
    }

    public ZemObject get(Interpreter interpreter) {
        ZemObject var = interpreter.getVariable(varNode.getName());
        ZemObject ret = null;
        if (var instanceof ZemArray) {
            int index = keyNode.eval(interpreter).toNumber(keyNode.getPosition()).intValue();
            return ((ZemArray) var).get(index);
        } else if (var instanceof Dictionary) {
            ZemObject key = keyNode.eval(interpreter);
            return ((Dictionary) var).get(key);
        }
        throw new InvalidTypeException("lookup expects an array or dictionary.", getPosition());
    }

    public void set(Interpreter interpreter, ZemObject result) {
        ZemObject var = interpreter.getVariable(varNode.getName());
        ZemObject ret = null;
        if (var instanceof ZemArray) {
            int index = keyNode.eval(interpreter).toNumber(keyNode.getPosition()).intValue();
            ((ZemArray) var).set(index, result);
            return;
        } else if (var instanceof Dictionary) {
            ZemObject key = keyNode.eval(interpreter);
            ((Dictionary) var).set(key, result);
            return;
        }
        throw new InvalidTypeException("lookup expects an array or dictionary.", getPosition());
    }

    @Override
    public ZemObject eval(Interpreter interpreter) {
        return get(interpreter);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('(');
        sb.append("lookup ");
        sb.append(varNode);
        sb.append(' ');
        sb.append(keyNode);
        sb.append(')');
        return sb.toString();
    }
}
