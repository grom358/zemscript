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

import java.io.IOException;
import java.io.StringReader;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:grom@zeminvaders.net">Cameron Zemek</a>
 */
public class LexerTest {
    public void assertTokenType(String code, TokenType expected) throws IOException {
        Lexer lexer = new Lexer(new StringReader(code));
        TokenType actual = lexer.getNextToken().getType();
        assertEquals(expected, actual);
    }

    @Test
    public void testAssignOp() throws IOException {
        assertTokenType("=", TokenType.ASSIGN);
    }

    @Test
    public void testMathOps() throws IOException {
        assertTokenType("+", TokenType.PLUS);
        assertTokenType("-", TokenType.MINUS);
        assertTokenType("*", TokenType.MULTIPLY);
        assertTokenType("/", TokenType.DIVIDE);
        assertTokenType("^", TokenType.POWER);
        assertTokenType("%", TokenType.MOD);
    }

    @Test
    public void testStringOps() throws IOException {
        assertTokenType("~", TokenType.CONCAT);
    }

    @Test
    public void testBooleanOps() throws IOException {
        assertTokenType("&&", TokenType.AND);
        assertTokenType("||", TokenType.OR);
        assertTokenType("!", TokenType.NOT);
    }

    @Test
    public void testBooleanRelations() throws IOException {
        assertTokenType("<", TokenType.LESS_THEN);
        assertTokenType("<=", TokenType.LESS_EQUAL);
        assertTokenType("==", TokenType.EQUAL);
        assertTokenType("!=", TokenType.NOT_EQUAL);
        assertTokenType(">=", TokenType.GREATER_EQUAL);
        assertTokenType(">", TokenType.GREATER_THEN);
    }

    @Test
    public void testNumber() throws IOException {
        assertTokenType("0", TokenType.NUMBER);
        assertTokenType("1", TokenType.NUMBER);
        assertTokenType("69", TokenType.NUMBER);
        assertTokenType("0.01", TokenType.NUMBER);
        assertTokenType("12345678901234567890.1234567890", TokenType.NUMBER);
    }

    @Test(expected = LexerException.class)
    public void testInvalidNumber() throws IOException {
        Lexer lexer = new Lexer(new StringReader("12.23.4"));
        lexer.getNextToken();
    }

    @Test
    public void testBoolean() throws IOException {
        assertTokenType("true", TokenType.TRUE);
        assertTokenType("false", TokenType.FALSE);
    }

    @Test
    public void testString() throws IOException {
        assertTokenType("''", TokenType.STRING_LITERAL);
        assertTokenType("'a'", TokenType.STRING_LITERAL);
        assertTokenType("'hello'", TokenType.STRING_LITERAL);
    }

    @Test
    public void testKeywords() throws IOException {
        assertTokenType("if", TokenType.IF);
        assertTokenType("else", TokenType.ELSE);
        assertTokenType("while", TokenType.WHILE);
        assertTokenType("foreach", TokenType.FOR_EACH);
        assertTokenType("as", TokenType.AS);
        assertTokenType("function", TokenType.FUNCTION);
        assertTokenType("return", TokenType.RETURN);
    }

    @Test
    public void testDelimiters() throws IOException {
        assertTokenType("(", TokenType.LPAREN);
        assertTokenType(")", TokenType.RPAREN);
        assertTokenType("{", TokenType.LBRACE);
        assertTokenType("}", TokenType.RBRACE);
        assertTokenType("[", TokenType.LBRACKET);
        assertTokenType("]", TokenType.RBRACKET);
        assertTokenType(",", TokenType.COMMA);
        assertTokenType(":", TokenType.COLON);
        assertTokenType(";", TokenType.END_STATEMENT);
    }

    @Test(expected = LexerException.class)
    public void testInvalidCharacter() throws IOException {
        Lexer lexer = new Lexer(new StringReader("#"));
        lexer.getNextToken();
    }

    @Test
    public void testExpression() throws IOException {
        String test = "n = (3 + 12 * 2 ^ 4 >= 0) && 3 % 4 == 3;";
        Lexer lexer = new Lexer(new StringReader(test));
        assertEquals(new Token(new SourcePosition(1, 1), TokenType.VARIABLE, "n"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 3), TokenType.ASSIGN, "="), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 5), TokenType.LPAREN, "("), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 6), TokenType.NUMBER, "3"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 8), TokenType.PLUS, "+"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 10), TokenType.NUMBER, "12"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 13), TokenType.MULTIPLY, "*"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 15), TokenType.NUMBER, "2"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 17), TokenType.POWER, "^"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 19), TokenType.NUMBER, "4"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 21), TokenType.GREATER_EQUAL, ">="), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 24), TokenType.NUMBER, "0"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 25), TokenType.RPAREN, ")"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 27), TokenType.AND, "&&"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 30), TokenType.NUMBER, "3"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 32), TokenType.MOD, "%"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 34), TokenType.NUMBER, "4"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 36), TokenType.EQUAL, "=="), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 39), TokenType.NUMBER, "3"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 40), TokenType.END_STATEMENT, ";"), lexer.getNextToken());
    }

    @Test
    public void testCompact() throws IOException {
        String test = "132.567'hello'somevar";
        Lexer lexer = new Lexer(new StringReader(test));
        assertEquals(new Token(new SourcePosition(1, 1), TokenType.NUMBER, "132.567"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 8), TokenType.STRING_LITERAL, "hello"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 15), TokenType.VARIABLE, "somevar"), lexer.getNextToken());
    }

    @Test
    public void testFunctionDeclaration() throws IOException {
        String test = "greet = function() { println('hello'); }";
        Lexer lexer = new Lexer(new StringReader(test));
        assertEquals(new Token(new SourcePosition(1, 1), TokenType.VARIABLE, "greet"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 7), TokenType.ASSIGN, "="), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 9), TokenType.FUNCTION, "function"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 17), TokenType.LPAREN, "("), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 18), TokenType.RPAREN, ")"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 20), TokenType.LBRACE, "{"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 22), TokenType.VARIABLE, "println"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 29), TokenType.LPAREN, "("), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 30), TokenType.STRING_LITERAL, "hello"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 37), TokenType.RPAREN, ")"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 38), TokenType.END_STATEMENT, ";"), lexer.getNextToken());
        assertEquals(new Token(new SourcePosition(1, 40), TokenType.RBRACE, "}"), lexer.getNextToken());
    }
}
