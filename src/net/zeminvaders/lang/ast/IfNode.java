/**
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

import net.zeminvaders.lang.Interpreter;
import net.zeminvaders.lang.runtime.ZemBoolean;
import net.zeminvaders.lang.runtime.ZemObject;

/**
 * if control structure.
 *
 * @author <a href="mailto:grom@zeminvaders.net">Cameron Zemek</a>
 */
public class IfNode extends Node {
    private Node testCondition;
    private Node thenBlock;
    private Node elseBlock;

    public IfNode(Node testCondition, Node thenBlock, Node elseBlock) {
        this.testCondition = testCondition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    public Node getTestCondition() {
        return testCondition;
    }

    public Node getThenBlock() {
        return thenBlock;
    }

    public Node getElseBlock() {
        return elseBlock;
    }

    @Override
    public ZemObject eval(Interpreter interpreter) {
        boolean test = testCondition.eval(interpreter).toBoolean().booleanValue();
        if (test) {
            return thenBlock.eval(interpreter);
        } else if (elseBlock != null) {
            return elseBlock.eval(interpreter);
        }
        return ZemBoolean.FALSE;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('(');
        sb.append("if ");
        sb.append(testCondition);
        sb.append(' ');
        sb.append(thenBlock);
        if (elseBlock != null) {
            sb.append(' ');
            sb.append(elseBlock);
        }
        sb.append(')');
        return sb.toString();
    }
}
