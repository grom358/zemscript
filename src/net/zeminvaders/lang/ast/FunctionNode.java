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
import net.zeminvaders.lang.ScopeInfo;
import net.zeminvaders.lang.SourcePosition;
import net.zeminvaders.lang.runtime.Parameter;
import net.zeminvaders.lang.runtime.UserFunction;
import net.zeminvaders.lang.runtime.ZemObject;

/**
 * Function declaration.
 *
 * @author <a href="mailto:grom@zeminvaders.net">Cameron Zemek</a>
 */
public class FunctionNode extends Node {
    final static public List<Node> NO_PARAMETERS = new ArrayList<Node>(0);

    private List<Node> parameters;
    private Node body;

    public FunctionNode(SourcePosition pos, List<Node> parameters, Node body) {
        super(pos);
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public void resolveScope(ScopeInfo scope) {
        ScopeInfo functionScope = new ScopeInfo(scope);
        for (Node node : parameters) {
            if (node instanceof VariableNode) {
                String parameterName = ((VariableNode) node).getName();
                functionScope.markLocal(parameterName);
            } else if (node instanceof AssignNode) {
                String parameterName = ((VariableNode) ((AssignNode) node).getLeft()).getName();
                functionScope.markLocal(parameterName);
            } else {
                // This error should not occur
                throw new RuntimeException("Invalid function");
            }
        }
        body.resolveScope(functionScope);
        scope.endScope(functionScope);
    }

    @Override
    public ZemObject eval(Interpreter interpreter) {
        List<Parameter> params = new ArrayList<Parameter>(parameters.size());
        for (Node node : parameters) {
            // TODO clean up getting parameters
            String parameterName;
            ZemObject parameterValue;
            if (node instanceof VariableNode) {
                parameterName = ((VariableNode) node).getName();
                parameterValue = null;
            } else if (node instanceof AssignNode) {
                parameterName = ((VariableNode) ((AssignNode) node).getLeft()).getName();
                parameterValue = ((AssignNode) node).getRight().eval(interpreter);
            } else {
                // This error should not occur
                throw new RuntimeException("Invalid function");
            }
            Parameter param = new Parameter(parameterName, parameterValue);
            params.add(param);
        }
        return new UserFunction(params, body, interpreter.createSymbolTable(this));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(function (");
        boolean first = true;
        for (Node node : parameters) {
            if (first) {
                first = false;
            } else {
                sb.append(' ');
            }
            sb.append(node);
        }
        sb.append(") ");
        sb.append(body);
        sb.append(')');
        return sb.toString();
    }
}
