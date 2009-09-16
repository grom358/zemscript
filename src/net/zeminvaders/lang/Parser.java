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
package net.zeminvaders.lang;

import java.util.LinkedList;
import java.util.List;

import net.zeminvaders.lang.ast.AddOpNode;
import net.zeminvaders.lang.ast.AndOpNode;
import net.zeminvaders.lang.ast.ArrayNode;
import net.zeminvaders.lang.ast.AssignNode;
import net.zeminvaders.lang.ast.BlockNode;
import net.zeminvaders.lang.ast.ConcatOpNode;
import net.zeminvaders.lang.ast.DictionaryEntryNode;
import net.zeminvaders.lang.ast.DictionaryNode;
import net.zeminvaders.lang.ast.DivideOpNode;
import net.zeminvaders.lang.ast.EqualsOpNode;
import net.zeminvaders.lang.ast.FalseNode;
import net.zeminvaders.lang.ast.ForeachNode;
import net.zeminvaders.lang.ast.FunctionCallNode;
import net.zeminvaders.lang.ast.FunctionNode;
import net.zeminvaders.lang.ast.GreaterEqualOpNode;
import net.zeminvaders.lang.ast.GreaterThenOpNode;
import net.zeminvaders.lang.ast.Node;
import net.zeminvaders.lang.ast.IfNode;
import net.zeminvaders.lang.ast.LessEqualOpNode;
import net.zeminvaders.lang.ast.LessThenOpNode;
import net.zeminvaders.lang.ast.LookupNode;
import net.zeminvaders.lang.ast.ModOpNode;
import net.zeminvaders.lang.ast.MultiplyOpNode;
import net.zeminvaders.lang.ast.NegateOpNode;
import net.zeminvaders.lang.ast.NotEqualsOpNode;
import net.zeminvaders.lang.ast.NotOpNode;
import net.zeminvaders.lang.ast.NumberNode;
import net.zeminvaders.lang.ast.OrOpNode;
import net.zeminvaders.lang.ast.PowerOpNode;
import net.zeminvaders.lang.ast.ReturnNode;
import net.zeminvaders.lang.ast.RootNode;
import net.zeminvaders.lang.ast.StringNode;
import net.zeminvaders.lang.ast.SubtractOpNode;
import net.zeminvaders.lang.ast.TrueNode;
import net.zeminvaders.lang.ast.VariableNode;
import net.zeminvaders.lang.ast.WhileNode;

/**
 * Check the syntax and convert the Token stream into Abstract Syntax Tree.
 *
 * @author <a href="mailto:grom@zeminvaders.net">Cameron Zemek</a>
 */
public class Parser {
    // Look ahead buffer for reading tokens from the lexer
    TokenBuffer lookAheadBuffer;

    public Parser(Lexer lexer) {
        lookAheadBuffer = new TokenBuffer(lexer, 2);
    }

    private TokenType lookAhead(int i) {
        if (lookAheadBuffer.isEmpty() || i > lookAheadBuffer.size()) {
            return null; // EOF
        }
        Token token = lookAheadBuffer.getToken(i - 1); // 1-based index
        return token.getType();
    }

    private Token match(TokenType tokenType) {
        Token token = lookAheadBuffer.readToken();
        if (token == null) {
            throw new ParserException("Expecting type " + tokenType + " but didn't get a token");
        }
        if (token.getType() != tokenType) {
            throw new ParserException("Expecting type " + tokenType + " but got " + token.getType(), token.getPosition());
        }
        return token;
    }

    public RootNode program() {
        List<Node> script = new LinkedList<Node>();
        while (lookAhead(1) != null) {
            script.add(statement());
        }
        return new RootNode(script);
    }

    private BlockNode block() {
        // LBRACE! statement* RBRACE!
        match(TokenType.LBRACE);
        List<Node> block = new LinkedList<Node>();
        while (lookAhead(1) != TokenType.RBRACE) {
            block.add(statement());
        }
        match(TokenType.RBRACE);
        return new BlockNode(block);
    }

    private Node statement() {
        // | (ID LPAREN) => functionCall  END_STATEMENT
        // | VARIABLE! ASSIGN! expression END_STATEMENT
        // | RETURN expression END_STATEMENT
        // | IF | WHILE | FOR_EACH
        TokenType type = lookAhead(1);
        if (type == TokenType.VARIABLE && lookAhead(2) == TokenType.LPAREN) {
            Node funcCall = functionCall();
            match(TokenType.END_STATEMENT);
            return funcCall;
        } else if (type == TokenType.VARIABLE) {
            Node var = variable();
            match(TokenType.ASSIGN);
            Node value = expression();
            match(TokenType.END_STATEMENT);
            return new AssignNode(var, value);
        } else if (type == TokenType.RETURN) {
            match(TokenType.RETURN);
            Node expression = expression();
            match(TokenType.END_STATEMENT);
            return new ReturnNode(expression);
        } else if (type == TokenType.IF) {
            return _if();
        } else if (type == TokenType.WHILE) {
            return _while();
        } else if (type == TokenType.FOR_EACH) {
            return foreach();
        } else {
            // We only get here if there is token from the lexer
            // that is not handled by parser yet.
            throw new ParserException("Unknown token type " + type);
        }
    }

    private Node condition() {
        match(TokenType.LPAREN);
        Node test = booleanExpression();
        match(TokenType.RPAREN);
        return test;
    }

    private Node _if() {
        // IF! condition block else?
        match(TokenType.IF);
        Node test = condition();
        BlockNode thenBlock = block();
        Node elseBlock = null;
        if (lookAhead(1) == TokenType.ELSE) {
            elseBlock = _else();
        }
        return new IfNode(test, thenBlock, elseBlock);
    }

    private Node _else() {
        // ELSE! (if | block)!
        match(TokenType.ELSE);
        if (lookAhead(1) == TokenType.IF) {
            return _if();
        } else {
            return block();
        }
    }

    private Node _while() {
        // WHILE! condition block
        match(TokenType.WHILE);
        Node test = condition();
        Node loopBlock = block();
        return new WhileNode(test, loopBlock);
    }

    private Node foreach() {
        // FOREACH! LPAREN! VARIABLE! AS! VARIABLE! (^COLON VARIABLE!) RPAREN!
        // LBRACE! block RBRACE!
        match(TokenType.FOR_EACH);
        match(TokenType.LPAREN);
        VariableNode onEach = new VariableNode(match(TokenType.VARIABLE).getText());
        match(TokenType.AS);
        VariableNode value = new VariableNode(match(TokenType.VARIABLE).getText());
        Node as = value;
        if (lookAhead(1) == TokenType.COLON) {
            match(TokenType.COLON);
            VariableNode key = value;
            value = new VariableNode(match(TokenType.VARIABLE).getText());
            as = new DictionaryEntryNode(key, value);
        }
        match(TokenType.RPAREN);
        Node loopBlock = block();
        return new ForeachNode(onEach, as, loopBlock);
    }

    private Node array() {
        // LBRACKET! (expression (COMMA^ expression)*)? RBRACKET!
        match(TokenType.LBRACKET);
        List<Node> elements = new LinkedList<Node>();
        if (lookAhead(1) != TokenType.RBRACKET) {
            elements.add(expression());
            while (lookAhead(1) == TokenType.COMMA) {
                match(TokenType.COMMA);
                elements.add(expression());
            }
        }
        match(TokenType.RBRACKET);
        return new ArrayNode(elements);
    }

    private DictionaryNode dictionary() {
        // LBRACE! (keyValue (COMMA^ keyValue)*)? RBRACE!
        match(TokenType.LBRACE);
        List<DictionaryEntryNode> elements = new LinkedList<DictionaryEntryNode>();
        if (lookAhead(1) != TokenType.RBRACE) {
            elements.add(keyValue());
            while (lookAhead(1) == TokenType.COMMA) {
                match(TokenType.COMMA);
                elements.add(keyValue());
            }
        }
        match(TokenType.RBRACE);
        return new DictionaryNode(elements);
    }

    private DictionaryEntryNode keyValue() {
        // key COLON! expression
        Node key = key();
        match(TokenType.COLON);
        Node value = expression();
        return new DictionaryEntryNode(key, value);
    }

    private Node key() {
        // STRING_LITERAL | NUMBER
        if (lookAhead(1) == TokenType.STRING_LITERAL) {
            return new StringNode(match(TokenType.STRING_LITERAL).getText());
        } else {
            return new NumberNode(match(TokenType.NUMBER).getText());
        }
    }

    private FunctionNode function() {
        // FUNCTION! LPAREN! parameterList? RPAREN!
        // LBRACE! block() RBRACE!
        match(TokenType.FUNCTION);
        match(TokenType.LPAREN);
        List<Node> paramList = FunctionNode.NO_PARAMETERS;
        if (lookAhead(1) != TokenType.RPAREN) {
            paramList = parameterList();
        }
        match(TokenType.RPAREN);
        Node body = block();
        return new FunctionNode(paramList, body);
    }

    private List<Node> parameterList() {
        // (parameter (COMMA! parameter)* )?
        List<Node> parameters = new LinkedList<Node>();
        parameters.add(parameter());
        while (lookAhead(1) == TokenType.COMMA) {
            match(TokenType.COMMA);
            parameters.add(parameter());
        }
        return parameters;
    }

    private Node parameter() {
        // variable (ASSIGN^ expression)?
        VariableNode var = new VariableNode(match(TokenType.VARIABLE).getText());
        if (lookAhead(1) == TokenType.ASSIGN) {
            match(TokenType.ASSIGN);
            Node e = expression();
            return new AssignNode(var, e);
        }
        return var;
    }

    private Node expression() {
        TokenType type = lookAhead(1);
        if (type == TokenType.FUNCTION) {
            return function();
        } else if (type == TokenType.LBRACKET) {
            return array();
        } else if (type == TokenType.LBRACE) {
            return dictionary();
        } else {
            // An expression can result in a string, boolean or number
            return stringExpression();
        }
    }

    private Node sumExpression() {
        // term ((PLUS^|MINUS^) term)*
        Node termExpression = term();
        while (lookAhead(1) == TokenType.PLUS ||
                lookAhead(1) == TokenType.MINUS) {
            if (lookAhead(1) == TokenType.PLUS) {
                match(TokenType.PLUS);
                termExpression = new AddOpNode(termExpression, term());
            } else if (lookAhead(1) == TokenType.MINUS) {
                match(TokenType.MINUS);
                termExpression = new SubtractOpNode(termExpression, term());
            }
        }
        return termExpression;
    }

    private Node term() {
        // factor ((MUL^|DIV^|MOD^) factor)*
        Node factorExpression = factor();
        while (lookAhead(1) == TokenType.MULTIPLY ||
                lookAhead(1) == TokenType.DIVIDE ||
                lookAhead(1) == TokenType.MOD) {
            if (lookAhead(1) == TokenType.MULTIPLY) {
                match(TokenType.MULTIPLY);
                factorExpression = new MultiplyOpNode(factorExpression, factor());
            } else if (lookAhead(1) == TokenType.DIVIDE) {
                match(TokenType.DIVIDE);
                factorExpression = new DivideOpNode(factorExpression, factor());
            } else if (lookAhead(1) == TokenType.MOD) {
                match(TokenType.MOD);
                factorExpression = new ModOpNode(factorExpression, factor());
            }
        }
        return factorExpression;
    }

    private Node factor() {
        // signExpr (POW^ signExpr)*
        Node expression = signExpression();
        while (lookAhead(1) == TokenType.POWER) {
            match(TokenType.POWER);
            expression = new PowerOpNode(expression, signExpression());
        }
        return expression;
    }

    private Node signExpression() {
        // (MINUS^|PLUS^)? value
        Token signToken = null;
        if (lookAhead(1) == TokenType.MINUS) {
            signToken = match(TokenType.MINUS);
        } else if (lookAhead(1) == TokenType.PLUS) {
            match(TokenType.PLUS);
        }
        Node value = value();
        if (signToken != null) {
            return new NegateOpNode(value);
        } else {
            return value;
        }
    }

    private Node value() {
        // (ID LPAREN) => functionCall | atom
        if (lookAhead(1) == TokenType.VARIABLE && lookAhead(2) == TokenType.LPAREN) {
            return functionCall();
        } else {
            return atom();
        }
    }

    private Node functionCall() {
        // f:ID^ LPAREN! argumentList RPAREN!
        String functionName = match(TokenType.VARIABLE).getText();
        match(TokenType.LPAREN);
        List<Node> arguments = FunctionCallNode.NO_ARGUMENTS;
        if (lookAhead(1) != TokenType.RPAREN) {
            arguments = argumentList();
        }
        match(TokenType.RPAREN);
        return new FunctionCallNode(functionName, arguments);
    }

    private List<Node> argumentList() {
        // (expression (COMMA! expression)* )?
        List<Node> arguments = new LinkedList<Node>();
        arguments.add(expression());
        while (lookAhead(1) == TokenType.COMMA) {
            match(TokenType.COMMA);
            arguments.add(expression());
        }
        return arguments;
    }

    private Node atom() {
        // NUMBER
        // | TRUE | FALSE
        // | LPAREN^ sumExpr RPAREN!
        // | variable
        TokenType type = lookAhead(1);
        if (type == TokenType.NUMBER) {
            return new NumberNode(match(TokenType.NUMBER).getText());
        } else if (type == TokenType.TRUE) {
            match(TokenType.TRUE);
            return new TrueNode();
        } else if (type == TokenType.FALSE) {
            match(TokenType.FALSE);
            return new FalseNode();
        } else if (type == TokenType.LPAREN) {
            match(TokenType.LPAREN);
            Node atom = expression();
            match(TokenType.RPAREN);
            return atom;
        } else {
            return variable();
        }
    }

    private Node variable() {
        Node varNode = new VariableNode(match(TokenType.VARIABLE).getText());
        if (lookAhead(1) == TokenType.LBRACKET) {
            match(TokenType.LBRACKET);
            Node key = expression();
            match(TokenType.RBRACKET);
            return new LookupNode((VariableNode) varNode, key);
        } else {
            return varNode;
        }
    }

    private Node booleanExpression() {
        // booleanTerm (OR^ booleanExpression)?
        Node boolTerm = booleanTerm();
        if (lookAhead(1) == TokenType.OR) {
            match(TokenType.OR);
            return new OrOpNode(boolTerm, booleanExpression());
        }
        return boolTerm;
    }

    private Node booleanTerm() {
        // booleanFactor (AND^ booleanTerm)?
        Node boolFactor = booleanFactor();
        if (lookAhead(1) == TokenType.AND) {
            match(TokenType.AND);
            return new AndOpNode(boolFactor, booleanTerm());
        }
        return boolFactor;
    }

    private Node booleanFactor() {
        // (NOT^)? relation
        if (lookAhead(1) == TokenType.NOT) {
            match(TokenType.NOT);
            return new NotOpNode(booleanRelation());
        }
        return booleanRelation();
    }

    private Node booleanRelation() {
        // sumExpr ((LE^ | LT^ | GE^ | GT^ | EQUAL^ | NOT_EQUAL^) sumExpr)?
        Node sumExpr = sumExpression();
        TokenType type = lookAhead(1);
        if (type == TokenType.LESS_EQUAL) {
            match(TokenType.LESS_EQUAL);
            return new LessEqualOpNode(sumExpr, sumExpression());
        } else if (type == TokenType.LESS_THEN) {
            match(TokenType.LESS_THEN);
            return new LessThenOpNode(sumExpr, sumExpression());
        } else if (type == TokenType.GREATER_EQUAL) {
            match(TokenType.GREATER_EQUAL);
            return new GreaterEqualOpNode(sumExpr, sumExpression());
        } else if (type == TokenType.GREATER_THEN) {
            match(TokenType.GREATER_THEN);
            return new GreaterThenOpNode(sumExpr, sumExpression());
        } else if (type == TokenType.EQUAL) {
            match(TokenType.EQUAL);
            return new EqualsOpNode(sumExpr, sumExpression());
        } else if (type == TokenType.NOT_EQUAL) {
            match(TokenType.NOT_EQUAL);
            return new NotEqualsOpNode(sumExpr, sumExpression());
        }
        return sumExpr;
    }

    private Node stringExpression() {
        // string (CONC^ stringExpr)?
        Node stringNode = string();
        if (lookAhead(1) == TokenType.CONCAT) {
            match(TokenType.CONCAT);
            return new ConcatOpNode(stringNode, stringExpression());
        }
        return stringNode;
    }

    private Node string() {
        // STRING_LITERAL | boolExpr
        if (lookAhead(1) == TokenType.STRING_LITERAL) {
            return new StringNode(match(TokenType.STRING_LITERAL).getText());
        } else {
            return booleanExpression();
        }
    }
}
