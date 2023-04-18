/*
 * Copyright (c) 2022, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 8298405
 * @summary Markdown support in the standard doclet
 * @modules jdk.compiler/com.sun.tools.javac.api
 *          jdk.compiler/com.sun.tools.javac.file
 *          jdk.compiler/com.sun.tools.javac.tree
 *          jdk.compiler/com.sun.tools.javac.util
 * @build DocCommentTester
 * @run main DocCommentTester MarkdownTest.java
 */

/*
 * Test for handling Markdown content.
 *
 * In the tests for code spans and code blocks, "@dummy" is used as a dummy inline
 * or block tag to verify that it is skipped as part of the code span or code block.
 * In other words, "@dummy" should appear as a literal part of the Markdown content.
 * Conversely, standard tags are used to verify that a fragment of text is not being
 * skipped as a code span or code block. In other words, they should be recognized as tags
 * and not skipped as part of any Markdown content.
 *
 * "@dummy" is also known to DocCommentTester and will not have any preceding whitespace
 * removed during normalization.
 */

class MarkdownTest {
    /**md
     * abc < def & ghi {@code 123} jkl {@unknown} mno.
     */
    void descriptionMix() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 5
    RawText[MARKDOWN, pos:4, abc_<_def_&_ghi_]
    Literal[CODE, pos:20, 123]
    RawText[MARKDOWN, pos:31, _jkl_]
    UnknownInlineTag[UNKNOWN_INLINE_TAG, pos:36
      tag:unknown
      content: 1
        Text[TEXT, pos:45]
    ]
    RawText[MARKDOWN, pos:46, _mno.]
  body: empty
  block tags: empty
]
*/

    /**md
     * @since abc < def & ghi {@code 123} jkl {@unknown} mno.
     */
    void blockTagMix() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: empty
  body: empty
  block tags: 1
    Since[SINCE, pos:4
      body: 5
        RawText[MARKDOWN, pos:11, abc_<_def_&_ghi_]
        Literal[CODE, pos:27, 123]
        RawText[MARKDOWN, pos:38, _jkl_]
        UnknownInlineTag[UNKNOWN_INLINE_TAG, pos:43
          tag:unknown
          content: 1
            Text[TEXT, pos:52]
        ]
        RawText[MARKDOWN, pos:53, _mno.]
    ]
]
*/

    /**md
     * 123 {@link Object abc < def & ghi {@code 123} jkl {@unknown} mno} 456.
     */
    void inlineTagMix() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 3
    RawText[MARKDOWN, pos:4, 123_]
    Link[LINK, pos:8
      reference:
        Reference[REFERENCE, pos:15, Object]
      body: 5
        RawText[MARKDOWN, pos:22, abc_<_def_&_ghi_]
        Literal[CODE, pos:38, 123]
        RawText[MARKDOWN, pos:49, _jkl_]
        UnknownInlineTag[UNKNOWN_INLINE_TAG, pos:54
          tag:unknown
          content: 1
            Text[TEXT, pos:63]
        ]
        RawText[MARKDOWN, pos:64, _mno]
    ]
    RawText[MARKDOWN, pos:69, _456.]
  body: empty
  block tags: empty
]
*/

    /**md
     * 123 `abc` 456.
     */
    void simpleCodeSpan() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123_`abc`_456.]
  body: empty
  block tags: empty
]
*/

    /**md
     * 123 ```abc``` 456.
     */
    void mediumCodeSpan() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123_```abc```_456.]
  body: empty
  block tags: empty
]
*/

    /**md
     * 123 ```abc`def``` 456.
     */
    void mediumCodeSpanWithBackTicks() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123_```abc`def```_456.]
  body: empty
  block tags: empty
]
*/

    /**md
     * 123 ```abc{@dummy ...}def``` 456.
     */
    void mediumCodeSpanWithNotInlineTag() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123_```abc{@dummy_...}def```_456.]
  body: empty
  block tags: empty
]
*/

    /**md
     * 123 ```abc
     * @dummy def``` 456.
     */
    void mediumCodeSpanWithNotBlockTag() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123_```abc|_@dummy_def```_456.]
  body: empty
  block tags: empty
]
*/

    /**md
     * 123.
     * ```
     * abc
     * ```
     * 456.
     */
    void simpleFencedCodeBlock_backtick() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 1
    RawText[MARKDOWN, pos:10, ```|_abc|_```|_456.]
  block tags: empty
]
*/

    /**md
     * 123.
     * ~~~
     * abc
     * {@dummy ...}
     * ~~~
     * 456.
     */
    void simpleFencedCodeBlock_tilde() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 1
    RawText[MARKDOWN, pos:10, ~~~|_abc|_{@dummy_...}|_~~~|_456.]
  block tags: empty
]
*/

    /**md
     * 123.
     * ```
     * abc {@dummy def} ghi
     * ```
     * 456.
     */
    void fencedCodeBlockWithInlineTag_backtick() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 1
    RawText[MARKDOWN, pos:10, ```|_abc_{@dummy_def}_ghi|_```|_456.]
  block tags: empty
]
*/

    /**md
     * 123.
     * ```
     * abc ``` ghi
     * {@dummy ...}
     * ```
     * 456.
     */
    void fencedCodeBlockWithBackTicks_backtick() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 1
    RawText[MARKDOWN, pos:10, ```|_abc_```_ghi|_{@dummy_...}|_```|_456.]
  block tags: empty
]
*/

    /**md
     * 123.
     * ```abc`def``` 456.
     */
    void codeSpanNotCodeBlock() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 1
    RawText[MARKDOWN, pos:10, ```abc`def```_456.]
  block tags: empty
]
*/

    /**md
     * 123.
     * ```
     * {@code ...}
     * ~~~
     * 456.
     */
    void mismatchedFences() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 3
    RawText[MARKDOWN, pos:10, ```|_]
    Literal[CODE, pos:15, ...]
    RawText[MARKDOWN, pos:26, |_~~~|_456.]
  block tags: empty
]
*/

    /**md
     * 123.
     * `````
     * ``` ghi
     * {@dummy ...}
     * `````
     * 456.
     */
    void fencedCodeBlockWithShortFence_backtick() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 1
    RawText[MARKDOWN, pos:10, `````|_```_ghi|_{@dummy_...}|_`````|_456.]
  block tags: empty
]
*/

    /**md
     * 123.
     *
     *     abc {@dummy ...}
     *     @dummy
     *     def
     *
     * 456 {@code ...}.
     */
    void indentedCodeBlock_afterBlank() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 3
    RawText[MARKDOWN, pos:15, abc_{@dummy_...}|_____@dummy|_____def||_456_]
    Literal[CODE, pos:59, ...]
    RawText[MARKDOWN, pos:70, .]
  block tags: empty
]
*/

    /**md
     * 123.
     * ### heading
     *     abc {@dummy ...}
     *     @dummy
     *     def
     * 456 {@code ...}.
     */
    void indentedCodeBlock_afterATX() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 3
    RawText[MARKDOWN, pos:10, ###_heading|_____abc_{@dummy_...}|_____@dummy|_____def|_456_]
    Literal[CODE, pos:70, ...]
    RawText[MARKDOWN, pos:81, .]
  block tags: empty
]
*/

    /**md
     * 123.
     * Heading
     * -------
     *     abc {@dummy ...}
     *     @dummy
     *     def
     * 456 {@code ...}.
     */
    void indentedCodeBlock_afterSetext() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 3
    RawText[MARKDOWN, pos:10, Heading|_-------|_____abc_{@dummy_...}|_____@dummy|_____def|_456_]
    Literal[CODE, pos:75, ...]
    RawText[MARKDOWN, pos:86, .]
  block tags: empty
]
*/

    /**md
     * 123.
     * - - - - -
     *     abc {@dummy ...}
     *     @dummy
     *     def
     * 456 {@code ...}.
     */
    void indentedCodeBlock_afterThematicBreak() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 3
    RawText[MARKDOWN, pos:10, -_-_-_-_-|_____abc_{@dummy_...}|_____@dummy|_____def|_456_]
    Literal[CODE, pos:68, ...]
    RawText[MARKDOWN, pos:79, .]
  block tags: empty
]
*/

    /**md
     * 123.
     * ```
     * abc
     * {@dummy}
     * def
     * ```
     *     abc {@dummy ...}
     *     @dummy
     *     def
     * 456 {@code ...}.
     */
    void indentedCodeBlock_afterFencedCodeBlock() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 3
    RawText[MARKDOWN, pos:10, ```|_abc|_{@dummy}|_def|_```|___..._...}|_____@dummy|_____def|_456_]
    Literal[CODE, pos:87, ...]
    RawText[MARKDOWN, pos:98, .]
  block tags: empty
]
*/

    /**md
     * 123.
     *
     * ```
     * public class HelloWorld {
     *     @dummy
     *     public static void main(String... args) {
     *         System.out.println("Hello World");
     *     }
     * }
     * ```
     * 456 {@code ...}.
     */
    void fencedHelloWorld() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 3
    RawText[MARKDOWN, pos:11, ```|_public_class_HelloWorld_{|_...lo_World");|_____}|_}|_```|_456_]
    Literal[CODE, pos:165, ...]
    RawText[MARKDOWN, pos:176, .]
  block tags: empty
]
*/

    /**md
     * 123.
     *
     *     public class HelloWorld {
     *         @dummy
     *         public static void main(String... args) {
     *             System.out.println("Hello World");
     *         }
     *     }
     *
     * 456 {@code ...}.
     */
    void indentedHelloWorld() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, 123.]
  body: 3
    RawText[MARKDOWN, pos:15, public_class_HelloWorld_{|______...orld");|_________}|_____}||_456_]
    Literal[CODE, pos:180, ...]
    RawText[MARKDOWN, pos:191, .]
  block tags: empty
]
*/

    /**md
     * {@summary abc ``code-span {@dummy ...}`` def {@code ...} }
     * rest.
     */
    void codeSpanInInlineTag() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    Summary[SUMMARY, pos:4
      summary: 3
        RawText[MARKDOWN, pos:14, abc_``code-span_{@dummy_...}``_def_]
        Literal[CODE, pos:49, ...]
        RawText[MARKDOWN, pos:60, _]
    ]
  body: 1
    RawText[MARKDOWN, pos:62, |_rest.]
  block tags: empty
]
*/

    /**md
     * {@summary abc
     * ```code-block
     *   {@dummy ...}
     * ```
     * def {@code ...} }
     * rest.
     */
    void codeBlockInInlineTag() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    Summary[SUMMARY, pos:4
      summary: 3
        RawText[MARKDOWN, pos:14, abc|_```code-block|___{@dummy_...}|_```|_def_]
        Literal[CODE, pos:59, ...]
        RawText[MARKDOWN, pos:70, _]
    ]
  body: 1
    RawText[MARKDOWN, pos:72, |_rest.]
  block tags: empty
]
*/

    /**md
     * abc `
     * def
     */
    void unmatchedBackTick() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    RawText[MARKDOWN, pos:4, abc_`|_def]
  body: empty
  block tags: empty
]
*/

    /**md
     * {@summary abc `
     * def}
     * rest
     */
    void unmatchedBackTickInInline() { }
/*
DocComment[DOC_COMMENT, pos:0
  firstSentence: 1
    Summary[SUMMARY, pos:4
      summary: 1
        RawText[MARKDOWN, pos:14, abc_`|_def]
    ]
  body: 1
    RawText[MARKDOWN, pos:25, |_rest]
  block tags: empty
]
*/

// While this is an important test case, it is also a negative one.
// Note how the backticks "match" across the end of the inline tag.
// That's unfortunate, but cannot reasonably be detected without
// examining the contents of a code span.
// Not surprisingly, most of the checks fail for this (bad) test case.
//    /**md
//     * {@summary abc `
//     * def}
//     * rest `more`
//     */
//    void unmatchedBackTickInInline2() { }
///*
//DocComment[DOC_COMMENT, pos:0
//  firstSentence: 1
//    Summary[SUMMARY, pos:4
//      summary: 1
//        Erroneous[ERRONEOUS, pos:14, prefPos:37
//          code: compiler.err.dc.unterminated.inline.tag
//          body: abc_`|_def}|_rest_`more`
//        ]
//    ]
//  body: empty
//  block tags: empty
//]
//*/

}
