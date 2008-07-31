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
package net.zeminvaders.lang;

import java.io.*;

import net.zeminvaders.lang.ast.*;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:grom@zeminvaders.net">Cameron Zemek</a>
 */
public class ParserTest {
    /**
     * Test that assignment works.
     */
    @Test
    public void testAssignment() {
        try {
            Lexer lexer = new Lexer(new StringReader("n = 0;"));
            Parser parser = new Parser(lexer);
            RootNode node = parser.program();
            AssignNode assignment = (AssignNode) node.get(0);
            assertTrue(assignment.getClass() == AssignNode.class);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Helper method to test that the right-hand side of the assignment
     * is a certain node type.
     *
     * @param snippet Script snippet to check
     * @param type    The type of the right-hand side of the assignment
     */
    private void assertType(String snippet, Class<? extends Object> type) {
        try {
            Lexer lexer = new Lexer(new StringReader(snippet));
            Parser parser = new Parser(lexer);
            RootNode node = parser.program();
            AssignNode assignment = (AssignNode) node.get(0);
            assertTrue(assignment.getRight().getClass() == type);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testConstants() {
        assertType("n = 3;", NumberNode.class);
        assertType("n = 'hello';", StringNode.class);
        assertType("n = true;", TrueNode.class);
        assertType("n = false;", FalseNode.class);
    }

    @Test
    public void testArithmeticOperators() {
        assertType("n = 1 + 1;", AddOpNode.class);
        assertType("n = 3 - 2;", SubtractOpNode.class);
        assertType("n = 2 * 2;", MultiplyOpNode.class);
        assertType("n = 4 / 2;", DivideOpNode.class);
        assertType("n = 4 % 3;", ModOpNode.class);
        assertType("n = 2 ^ 2;", PowerOpNode.class);
    }

    /**
     * Helper method to test that the parser can process a script.
     *
     * @param snippet Script snippet to test
     */
    private void assertSyntax(String snippet) {
        try {
            Lexer lexer = new Lexer(new StringReader(snippet));
            Parser parser = new Parser(lexer);
            RootNode node = parser.program();
            assertNotNull(node);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Helper method to test that the script input generates the
     * correct syntax tree by converting the syntax tree into S-Expression.
     *
     * @param script Script input
     * @param sexpr  The expected S-Expression
     */
    private void assertSExpr(String script, String sexpr) {
        try {
            Lexer lexer = new Lexer(new StringReader(script));
            Parser parser = new Parser(lexer);
            RootNode node = parser.program();
            assertEquals(sexpr, node.toString());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testOperatorChain() {
        assertSyntax("n = 2 + 2 - 2 + 2;");
        assertSyntax("n = 2 * 2 / 2 * 2;");
        assertSyntax("n = 2 ^ 2 ^ 2;");
        assertSyntax("n = true && true && false || false || true;");
    }

    @Test
    public void testOperatorPrecedence() {
        assertSExpr("n = 1 - -2;", "(set! n (- 1 (- 2)))");
        assertSExpr("n = 2 + 2 * 3;", "(set! n (+ 2 (* 2 3)))");
        assertSExpr("n = 2 - 4 / 2;", "(set! n (- 2 (/ 4 2)))");
        assertSExpr("n = 2 + 1 * 2 ^ 2;", "(set! n (+ 2 (* 1 (^ 2 2))))");
        assertSExpr("n = 2 ^ -2;", "(set! n (^ 2 (- 2)))");
        assertSExpr("n = -2 ^ 2;", "(set! n (^ (- 2) 2))");
        assertSExpr("n = a || b && c;", "(set! n (or a (and b c)))");
        assertSExpr("n = a && b || c;", "(set! n (or (and a b) c))");
        assertSExpr("n = a && b || c && d;", "(set! n (or (and a b) (and c d)))");
        assertSExpr("n = a && b || c && d || e;", "(set! n (or (and a b) (or (and c d) e)))");
        assertSExpr("n = !a && b;", "(set! n (and (not a) b))");
        assertSExpr("n = a && !b;", "(set! n (and a (not b)))");

        assertSExpr("n = (2 + 2) * 3;", "(set! n (* (+ 2 2) 3))");
        assertSExpr("n = !(a && b);", "(set! n (not (and a b)))");

        assertSExpr("n = 1 + 1 <= 2 || 3 * 2 > 5 && 5 * 1 > 4;", "(set! n (or (<= (+ 1 1) 2) (and (> (* 3 2) 5) (> (* 5 1) 4))))");
    }

    @Test
    public void testControlStructures() {
        assertSExpr("if (cond) { then(); } else { somethingElse(); }", "(if cond ((then)) ((somethingElse)))");
        assertSExpr("while (cond) { body(); }", "(while cond ((body)))");
        assertSExpr("foreach (on_var as element) { process(element); }", "(foreach on_var element ((process element)))");
        assertSExpr("foreach (on_var as key : value) { process(key, value); }", "(foreach on_var (key value) ((process key value)))");
    }

    @Test
    public void testFunction() {
        assertSExpr("add = function(a, b) { return a + b; };", "(set! add (function (a b) ((return (+ a b)))))");
    }
}
