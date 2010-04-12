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

import java.util.ArrayList;
import java.util.List;

import net.zeminvaders.lang.Interpreter;
import net.zeminvaders.lang.SourcePosition;
import net.zeminvaders.lang.runtime.ZemObject;

/**
 * Call to function.
 *
 * @author <a href="mailto:grom@zeminvaders.net">Cameron Zemek</a>
 */
public class FunctionCallNode extends Node {
    final static public List<Node> NO_ARGUMENTS = new ArrayList<Node>(0);

    private String functionName;
    private List<Node> arguments;

    public FunctionCallNode(SourcePosition pos, String functionName, List<Node> arguments) {
        super(pos);
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public ZemObject eval(Interpreter interpreter) {
        interpreter.checkFunctionExists(functionName);
        // Evaluate the arguments
        List<ZemObject> args = new ArrayList<ZemObject>(arguments.size());
        for (Node node : arguments) {
            args.add(node.eval(interpreter));
        }
        return interpreter.callFunction(functionName, args);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append(functionName);
        for (Node arg : arguments) {
            sb.append(' ');
            sb.append(arg);
        }
        sb.append(')');
        return sb.toString();
    }
}
