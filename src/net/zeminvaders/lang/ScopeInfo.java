package net.zeminvaders.lang;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class is used to track information about a function's scope. It is
 * used as part of finding what variables are captured by the closure.
 */
public class ScopeInfo {	
	private Collection<String> upvals; // External local variables being used
	private Collection<String> global;
	private Collection<String> outer;
	private Collection<String> local;
	
	public ScopeInfo(Collection<String> global, Collection<String> variables) {
		upvals = new HashSet<String>();
		this.global = global;
		this.outer = variables;
		local = new HashSet<String>();
	}
	
	public ScopeInfo(ScopeInfo parent) {
		upvals = new HashSet<String>();
		// Propagate global to inner scope
		global = new HashSet<String>(parent.global);
		// Propagate outer scope to inner scope
		outer = new HashSet<String>(parent.outer);
		outer.addAll(parent.local);
		// Setup empty local scope
		local = new HashSet<String>();
	}
	
	public Collection<String> getUpvals() {
		return upvals;
	}
	
	/**
	 * End the scope and propagate information about used upvals
	 * to the outer (this) scope
	 * @param scope Inner scope
	 */
	public void endScope(ScopeInfo scope) {
		for (String name : scope.upvals) {
			if (!local.contains(name)) {
				upvals.add(name);
			}
		}
	}
	
	public void markGlobal(String name) {
		global.add(name);
	}
	
	public void markLocal(String name) {
		local.add(name);
	}
	
	public void readVariable(String name) {
		if (global.contains(name)) {
			// Global is accessible by all levels of scope, do nothing
		} else if (local.contains(name)) {
			// Local variable, do nothing
		} else if (outer.contains(name)){
			// Capture as an upval
			upvals.add(name);
		} else {
			// Variable has not be defined at this point. At runtime if the
			// variable is not at global scope then it will result in an error.
		}
	}
	
	public void writeVariable(String name) {
		if (global.contains(name)) {
			// Do nothing
		} else if (outer.contains(name)) {
			// Capture as an upval
			upvals.add(name);
		} else {
			// New variable
			local.add(name);
		}
	}
}
