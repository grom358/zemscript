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
import net.zeminvaders.lang.SourcePosition;
import net.zeminvaders.lang.InvalidTypeException;
import net.zeminvaders.lang.runtime.Dictionary;
import net.zeminvaders.lang.runtime.ZemArray;
import net.zeminvaders.lang.runtime.ZemObject;

/**
 * foreach control structure.
 *
 * @author <a href="mailto:grom@zeminvaders.net">Cameron Zemek</a>
 */
public class ForeachNode extends Node {
    private VariableNode onVariableNode;
    private Node asNode;
    private Node loopBody;

    public ForeachNode(SourcePosition pos, VariableNode onVariableNode, Node asNode, Node loopBody) {
        super(pos);
        this.onVariableNode = onVariableNode;
        this.asNode = asNode;
        this.loopBody = loopBody;
    }

    @Override
    public ZemObject eval(Interpreter interpreter) {
        ZemObject onVariable = interpreter.getVariable(onVariableNode.getName());
        ZemObject ret = null;
        if (onVariable instanceof ZemArray) {
            String asVariableName = asNode.toString();
            for (ZemObject element : (ZemArray) onVariable) {
                interpreter.setVariable(asVariableName, element);
                ret = loopBody.eval(interpreter);
            }
            return ret;
        } else if (onVariable instanceof Dictionary) {
            DictionaryEntryNode entryNode = (DictionaryEntryNode) asNode;
            String keyName = ((VariableNode) entryNode.getKey()).getName();
            String valueName = ((VariableNode) entryNode.getValue()).getName();
            for (Map.Entry<ZemObject, ZemObject> entry : (Dictionary) onVariable) {
                interpreter.setVariable(keyName, entry.getKey());
                interpreter.setVariable(valueName, entry.getValue());
                ret = loopBody.eval(interpreter);
            }
            return ret;
        }
        throw new InvalidTypeException("foreach expects an array or dictionary.");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append("foreach ");
        sb.append(onVariableNode);
        sb.append(' ');
        sb.append(asNode);
        sb.append(' ');
        sb.append(loopBody);
        sb.append(')');
        return sb.toString();
    }
}
