/*
 * Copyright (c) 2010 Cameron Zemek
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.zeminvaders.lang.runtime.ZemObject;

/**
 * A mapping of variable names and their values. The values are accessed via
 * a pointer class (Variable) so that symbol tables can share variables.
 */
public class SymbolTable {
    /**
     * Reference to global SymbolTable. Null if this is the global SymbolTable
     */
	private SymbolTable global;
	
	/**
	 * Variables in use by this symbol table.
	 */
	private Map<String, Variable> bindings;

	/**
	 * Create global symbol table
	 */
	public SymbolTable() {
	    global = null;
	    bindings = new HashMap<String, Variable>();
	}

	/**
	 * Create a symbol table from parent. This is basically a copy constructor,
	 * except we don't copy from the global table.
	 * @param global Global symbol table
	 * @param parent Symbol table to copy from
	 */
	public SymbolTable(SymbolTable global, SymbolTable parent) {
	    this.global = global;
        // Import variables from parent, unless the parent is the global symbol
	    // table. We don't copy from the global symbol table since the
	    // global table is always used for finding a variable if no
	    // local binding is found
        if (parent != null && parent != global) {
            bindings = new HashMap<String, Variable>(parent.bindings);
        } else {
            bindings = new HashMap<String, Variable>();
        }
	}

	/**
	 * Create a symbol table with captured variables. Used to create a closure.
	 * @param global Global symbol table
	 * @param parent Symbol table of parent
	 * @param upvals Names of variables that are captured by this closure
	 */
	public SymbolTable(SymbolTable global, SymbolTable parent, Collection<String> upvals) {
	    this.global = global;
	    bindings = new HashMap<String, Variable>();
	    if (parent != null && parent != global) {
            for (String variableName : upvals) {
                if (parent.bindings.containsKey(variableName)) {
                    bindings.put(variableName, parent.bindings.get(variableName));
                }
            }
        }
	}

	public void set(String name, ZemObject value) {
        if (bindings.containsKey(name)) {
            bindings.get(name).setValue(value);
        } else {
            bindings.put(name, new Variable(value));
        }
	}

	public ZemObject get(String name, SourcePosition pos) {
        if (!bindings.containsKey(name)) {
            if (global != null) {
                return global.get(name, pos);
            }
            throw new UnsetVariableException(name, pos);
        }
        return bindings.get(name).getValue();
	}

	public Collection<String> getNames() {
	    return bindings.keySet();
	}

	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    for (Map.Entry<String, Variable> entry : bindings.entrySet()) {
	        sb.append(entry.getKey());
	        sb.append(" => ");
	        sb.append(entry.getValue());
	        sb.append('\n');
	    }
	    return sb.toString();
    }
}
