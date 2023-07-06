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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Predicate;

import javax.lang.model.element.Element;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.SpecTree;
import com.sun.source.util.DocTreePath;

import jdk.javadoc.internal.doclets.formats.html.Contents;
import jdk.javadoc.internal.doclets.formats.html.HtmlConfiguration;
import jdk.javadoc.internal.doclets.formats.html.markup.ContentBuilder;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlTree;
import jdk.javadoc.internal.doclets.formats.html.markup.Text;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.taglets.SpecTaglet;
import jdk.javadoc.internal.doclets.toolkit.taglets.TagletWriter;
import jdk.javadoc.internal.doclets.toolkit.util.CommentHelper;

public class HtmlSpecTaglet extends SpecTaglet {
    // Threshold for length of @spec tag label for switching from inline to block layout.
    private static final int TAG_LIST_ITEM_MAX_INLINE_LENGTH = 30; // FIXME: dup in HtmlSeeTaglet

    private final Contents contents;

    HtmlSpecTaglet(HtmlConfiguration config) {
        super(config);
        this.contents = config.contents;
    }

    @Override
    public Content specTagOutput(Element holder, List<? extends SpecTree> specTags, TagletWriter writer) {
        if (specTags.isEmpty()) {
            return Text.EMPTY;
        }

        List<Content> links = specTags.stream()
                .map(st -> specTagToContent(holder, st, writer)).toList();

        // Use a different style if any link label is longer than 30 chars or contains commas.
        boolean hasLongLabels = links.stream().anyMatch(this::isLongOrHasComma);
        var specList = HtmlTree.UL(hasLongLabels ? HtmlStyle.tagListLong : HtmlStyle.tagList);
        links.stream()
                .filter(Predicate.not(Content::isEmpty))
                .forEach(item -> specList.add(HtmlTree.LI(item)));

        return new ContentBuilder(
                HtmlTree.DT(contents.externalSpecifications),
                HtmlTree.DD(specList));
    }

    private Content specTagToContent(Element holder, SpecTree specTree, TagletWriter writer) {
        TagletWriterImpl w = (TagletWriterImpl) writer;
        String specTreeURL = specTree.getURL().getBody();
        List<? extends DocTree> specTreeLabel = specTree.getTitle();
        Content label = w.getHtmlWriter().commentTagsToContent(holder, specTreeLabel, writer.context.isFirstSentence);
        return getExternalSpecContent(holder, specTree, specTreeURL,
                w.textOf(specTreeLabel).replaceAll("\\s+", " "), label,
                w);
    }

    Content getExternalSpecContent(Element holder, DocTree docTree, String url, String searchText, Content title, TagletWriterImpl w) {
        URI specURI;
        try {
            // Use the canonical title of the spec if one is available
            specURI = new URI(url);
        } catch (URISyntaxException e) {
            CommentHelper ch = utils.getCommentHelper(holder);
            DocTreePath dtp = ch.getDocTreePath(docTree);
            w.getHtmlWriter().messages.error(dtp, "doclet.Invalid_URL", e.getMessage());
            specURI = null;
        }

        Content titleWithAnchor = w.createAnchorAndSearchIndex(holder,
                searchText,
                title,
                resources.getText("doclet.External_Specification"),
                docTree);

        if (specURI == null) {
            return titleWithAnchor;
        } else {
            return HtmlTree.A(w.getHtmlWriter().resolveExternalSpecURI(specURI), titleWithAnchor);
        }

    }


    private boolean isLongOrHasComma(Content c) {
        String s = c.toString()
                .replaceAll("<.*?>", "")              // ignore HTML
                .replaceAll("&#?[A-Za-z0-9]+;", " ")  // entities count as a single character
                .replaceAll("\\R", "\n");             // normalize newlines
        return s.length() > TAG_LIST_ITEM_MAX_INLINE_LENGTH || s.contains(",");
    }
}
