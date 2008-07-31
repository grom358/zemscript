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
public class PeekReaderTest {
    private PeekReader in;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        in = new PeekReader(new StringReader("hello"), 3);
    }

    @Test
    public void testPeek() {
        try {
            assertTrue(in.peek(1) == 'h'); // peek: h
            assertTrue(in.peek(2) == 'e'); // peek: e
            assertTrue(in.peek(3) == 'l'); // peek: l

            assertTrue(in.read() == 'h'); // h
            assertTrue(in.peek(1) == 'e'); // peek: e
            assertTrue(in.peek(2) == 'l'); // peek: l
            assertTrue(in.peek(3) == 'l'); // peek: l

            assertTrue(in.read() == 'e');
            assertTrue(in.peek(1) == 'l'); // peek: l
            assertTrue(in.peek(2) == 'l'); // peek: l
            assertTrue(in.peek(3) == 'o'); // peek: o

            assertTrue(in.read() == 'l');
            assertTrue(in.peek(1) == 'l'); // peek: l
            assertTrue(in.peek(2) == 'o'); // peek: o
            assertTrue(in.peek(3) == -1); // peek: -1 (End of stream)

            assertTrue(in.read() == 'l');
            assertTrue(in.read() == 'o');
            assertTrue(in.read() == -1);
        } catch (IOException e) {
            fail();
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void exceedMinPeek() {
        in.peek(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void exceedMaxPeek() {
        in.peek(4);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        in.close();
    }
}
