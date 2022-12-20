/*
 * Copyright (c) 2012, 2019, Oracle and/or its affiliates. All rights reserved.
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
 * @bug 8000418 8024288 8196202 8243113
 * @summary Verify that files use a common Generated By string
 * @library ../../lib
 * @modules jdk.javadoc/jdk.javadoc.internal.tool
 * @build javadoc.tester.*
 * @run main TestGeneratedBy
 */

import javadoc.tester.JavadocTester;

public class TestGeneratedBy extends JavadocTester {

    public static void main(String... args) throws Exception {
        var tester = new TestGeneratedBy();
        tester.runTests();
    }

    @Test
    public void testTimestamp() {
        javadoc("-d", "out-timestamp",
            "-sourcepath", testSrc,
            "pkg");
        checkExit(Exit.OK);
        checkFiles(false, "allclasses-noframe.html");

        checkTimestamps(true);
    }

    @Test
    public void testNoTimestamp() {
        javadoc("-d", "out-notimestamp",
            "-notimestamp",
            "-sourcepath", testSrc,
            "pkg");
        checkExit(Exit.OK);
        checkFiles(false, "allclasses-noframe.html");

        checkTimestamps(false);
    }

    void checkTimestamps(boolean timestamp) {
        checkTimestamps(timestamp,
        "pkg/MyClass.html",
        "pkg/package-summary.html",
        "pkg/package-tree.html",
        "constant-values.html",
        "overview-tree.html",
        "deprecated-list.html",
        "serialized-form.html",
        "help-doc.html",
        "index-all.html",
        "index.html");

    }

    void checkTimestamps(boolean timestamp, String... files) {
        String version = System.getProperty("java.specification.version");
        String genBy = "Generated by javadoc (" + version + ")";
        if (timestamp) genBy += " on ";

        for (String file: files) {
            // genBy is the current standard "Generated by" text
            checkOutput(file, true, genBy);

            // These are older versions of the "Generated by" text
            checkOutput(file, false,
                    "Generated by javadoc (version",
                    "Generated by javadoc on");
        }
    }
}

