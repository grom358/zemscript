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

import java.util.LinkedList;

/**
 * Buffer of Tokens. Used to provide lookahead into the stream from the lexer.
 * Also filters out comment tokens.
 * 
 * @author <a href="mailto:grom@zeminvaders.net">Cameron Zemek</a>
 */
public class TokenBuffer {
    private LinkedList<Token> tokenQueue;
    private Lexer lexer;

    public TokenBuffer(Lexer lexer, int size) {
        this.lexer = lexer;
        tokenQueue = new LinkedList<Token>();

        // init queue
        for (int i = 0; i < size; i++) {
            Token token = nextToken();
            if (token == null) {
                break;
            }
            tokenQueue.addLast(token);
        }
    }

    private Token nextToken() {
        Token token = lexer.getNextToken();
        while (token != null && token.getType() == TokenType.COMMENT) {
            token = lexer.getNextToken();
        }
        return token;
    }

    public boolean isEmpty() {
        return tokenQueue.isEmpty();
    }

    public int size() {
        return tokenQueue.size();
    }

    public Token getToken(int i) {
        return tokenQueue.get(i);
    }

    /**
     * Read the next token from the lexer
     */
    public Token readToken() {
        if (tokenQueue.isEmpty()) {
            return null;
        }
        Token token = tokenQueue.removeFirst();

        // Add another token to the queue
        Token newToken = nextToken();
        if (newToken != null) {
            tokenQueue.addLast(newToken);
        }
        return token;
    }
}
