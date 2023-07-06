/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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

package jdk.javadoc.internal.doclets.formats.html.taglets;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

import com.sun.source.doctree.DocTree;

import jdk.javadoc.doclet.Taglet;
import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.doclets.formats.html.markup.ContentBuilder;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlTree;
import jdk.javadoc.internal.doclets.formats.html.markup.RawHtml;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.taglets.SimpleTaglet;
import jdk.javadoc.internal.doclets.toolkit.taglets.TagletWriter;

public class HtmlSimpleTaglet extends SimpleTaglet {


    /**
     * Constructs a {@code HtmlSimpleTaglet}.
     *
     * @param tagName   the name of this tag
     * @param header    the header to output
     * @param locations the possible locations that this tag can appear in
     *                  The string can contain 'p' for package, 't' for type,
     *                  'm' for method, 'c' for constructor and 'f' for field.
     *                  See {@link #getLocations(String) getLocations} for the
     *                  complete list.
     */
    HtmlSimpleTaglet(HtmlConfiguration config, String tagName, String header, String locations) {
        super(config, tagName, header, getLocations(locations), isEnabled(locations));
    }

    /**
     * Constructs a {@code HtmlSimpleTaglet}.
     *
     * @param tagKind   the kind of this tag
     * @param header    the header to output
     * @param locations the possible locations that this tag can appear in
     */
    HtmlSimpleTaglet(HtmlConfiguration config, DocTree.Kind tagKind, String header, Set<Taglet.Location> locations) {
        super(config, tagKind, header, locations, true);
    }

    /**
     * Constructs a {@code HtmlSimpleTaglet}.
     *
     * @param tagName   the name of this tag
     * @param header    the header to output
     * @param locations the possible locations that this tag can appear in
     */
    HtmlSimpleTaglet(HtmlConfiguration config, String tagName, String header, Set<Taglet.Location> locations) {
        super(config, tagName, header, locations, true);
    }

    /**
     * Constructs a {@code HtmlSimpleTaglet}.
     *
     * @param tagKind   the kind of this tag
     * @param header    the header to output
     * @param locations the possible locations that this tag can appear in
     */
    HtmlSimpleTaglet(HtmlConfiguration config, DocTree.Kind tagKind, String header, Set<Taglet.Location> locations, boolean enabled) {
        super(config, tagKind, header, locations, enabled);
    }

    private static Set<Taglet.Location> getLocations(String locations) {
        Set<Taglet.Location> set = EnumSet.noneOf(Taglet.Location.class);
        for (int i = 0; i < locations.length(); i++) {
            switch (locations.charAt(i)) {
                case 'a':  case 'A':
                    return EnumSet.allOf(Taglet.Location.class);
                case 'c':  case 'C':
                    set.add(Taglet.Location.CONSTRUCTOR);
                    break;
                case 'f':  case 'F':
                    set.add(Taglet.Location.FIELD);
                    break;
                case 'm':  case 'M':
                    set.add(Taglet.Location.METHOD);
                    break;
                case 'o':  case 'O':
                    set.add(Taglet.Location.OVERVIEW);
                    break;
                case 'p':  case 'P':
                    set.add(Taglet.Location.PACKAGE);
                    break;
                case 's':  case 'S':        // super-packages, anyone?
                    set.add(Taglet.Location.MODULE);
                    break;
                case 't':  case 'T':
                    set.add(Taglet.Location.TYPE);
                    break;
                case 'x':  case 'X':
                    break;
            }
        }
        return set;
    }

    private static boolean isEnabled(String locations) {
        return locations.matches("[^Xx]*");
    }

    @Override
    public Content simpleBlockTagOutput(Element element,
                                        List<? extends DocTree> simpleTags,
                                        String header,
                                        TagletWriter writer) {
        TagletWriterImpl w = (TagletWriterImpl) writer;
        var ch = utils.getCommentHelper(element);
        var htmlWriter = w.getHtmlWriter();
        var context = w.getContext();

        ContentBuilder body = new ContentBuilder();
        boolean many = false;
        for (DocTree simpleTag : simpleTags) {
            if (many) {
                body.add(", ");
            }
            List<? extends DocTree> bodyTags = ch.getBody(simpleTag);
            body.add(htmlWriter.commentTagsToContent(element, bodyTags, context.within(simpleTag)));
            many = true;
        }
        return new ContentBuilder(
                HtmlTree.DT(RawHtml.of(header)),
                HtmlTree.DD(body));
    }
}
