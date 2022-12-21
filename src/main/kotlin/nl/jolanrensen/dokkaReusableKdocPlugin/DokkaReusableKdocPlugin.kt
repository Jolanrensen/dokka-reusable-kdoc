package nl.jolanrensen.dokkaReusableKdocPlugin

import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.transformers.documentables.DocumentableReplacerTransformer
import org.jetbrains.dokka.model.DAnnotation
import org.jetbrains.dokka.model.DClass
import org.jetbrains.dokka.model.DClasslike
import org.jetbrains.dokka.model.DEnum
import org.jetbrains.dokka.model.DInterface
import org.jetbrains.dokka.model.DObject
import org.jetbrains.dokka.model.DProperty
import org.jetbrains.dokka.model.SourceSetDependent
import org.jetbrains.dokka.model.doc.A
import org.jetbrains.dokka.model.doc.Author
import org.jetbrains.dokka.model.doc.B
import org.jetbrains.dokka.model.doc.Big
import org.jetbrains.dokka.model.doc.BlockQuote
import org.jetbrains.dokka.model.doc.Br
import org.jetbrains.dokka.model.doc.Caption
import org.jetbrains.dokka.model.doc.Cite
import org.jetbrains.dokka.model.doc.CodeBlock
import org.jetbrains.dokka.model.doc.CodeInline
import org.jetbrains.dokka.model.doc.Constructor
import org.jetbrains.dokka.model.doc.CustomDocTag
import org.jetbrains.dokka.model.doc.CustomTagWrapper
import org.jetbrains.dokka.model.doc.Dd
import org.jetbrains.dokka.model.doc.Deprecated
import org.jetbrains.dokka.model.doc.Description
import org.jetbrains.dokka.model.doc.Dfn
import org.jetbrains.dokka.model.doc.Dir
import org.jetbrains.dokka.model.doc.Div
import org.jetbrains.dokka.model.doc.Dl
import org.jetbrains.dokka.model.doc.DocTag
import org.jetbrains.dokka.model.doc.DocumentationLink
import org.jetbrains.dokka.model.doc.DocumentationNode
import org.jetbrains.dokka.model.doc.Dt
import org.jetbrains.dokka.model.doc.Em
import org.jetbrains.dokka.model.doc.Font
import org.jetbrains.dokka.model.doc.Footer
import org.jetbrains.dokka.model.doc.Frame
import org.jetbrains.dokka.model.doc.FrameSet
import org.jetbrains.dokka.model.doc.H1
import org.jetbrains.dokka.model.doc.H2
import org.jetbrains.dokka.model.doc.H3
import org.jetbrains.dokka.model.doc.H4
import org.jetbrains.dokka.model.doc.H5
import org.jetbrains.dokka.model.doc.H6
import org.jetbrains.dokka.model.doc.Head
import org.jetbrains.dokka.model.doc.Header
import org.jetbrains.dokka.model.doc.HorizontalRule
import org.jetbrains.dokka.model.doc.Html
import org.jetbrains.dokka.model.doc.I
import org.jetbrains.dokka.model.doc.IFrame
import org.jetbrains.dokka.model.doc.Img
import org.jetbrains.dokka.model.doc.Index
import org.jetbrains.dokka.model.doc.Input
import org.jetbrains.dokka.model.doc.Li
import org.jetbrains.dokka.model.doc.Link
import org.jetbrains.dokka.model.doc.Listing
import org.jetbrains.dokka.model.doc.Main
import org.jetbrains.dokka.model.doc.Menu
import org.jetbrains.dokka.model.doc.Meta
import org.jetbrains.dokka.model.doc.Nav
import org.jetbrains.dokka.model.doc.NoFrames
import org.jetbrains.dokka.model.doc.NoScript
import org.jetbrains.dokka.model.doc.Ol
import org.jetbrains.dokka.model.doc.P
import org.jetbrains.dokka.model.doc.Param
import org.jetbrains.dokka.model.doc.Pre
import org.jetbrains.dokka.model.doc.Property
import org.jetbrains.dokka.model.doc.Receiver
import org.jetbrains.dokka.model.doc.Return
import org.jetbrains.dokka.model.doc.Sample
import org.jetbrains.dokka.model.doc.Script
import org.jetbrains.dokka.model.doc.Section
import org.jetbrains.dokka.model.doc.See
import org.jetbrains.dokka.model.doc.Since
import org.jetbrains.dokka.model.doc.Small
import org.jetbrains.dokka.model.doc.Span
import org.jetbrains.dokka.model.doc.Strikethrough
import org.jetbrains.dokka.model.doc.Strong
import org.jetbrains.dokka.model.doc.Sub
import org.jetbrains.dokka.model.doc.Sup
import org.jetbrains.dokka.model.doc.Suppress
import org.jetbrains.dokka.model.doc.TBody
import org.jetbrains.dokka.model.doc.TFoot
import org.jetbrains.dokka.model.doc.THead
import org.jetbrains.dokka.model.doc.Table
import org.jetbrains.dokka.model.doc.Td
import org.jetbrains.dokka.model.doc.Text
import org.jetbrains.dokka.model.doc.Th
import org.jetbrains.dokka.model.doc.Throws
import org.jetbrains.dokka.model.doc.Title
import org.jetbrains.dokka.model.doc.Tr
import org.jetbrains.dokka.model.doc.Tt
import org.jetbrains.dokka.model.doc.U
import org.jetbrains.dokka.model.doc.Ul
import org.jetbrains.dokka.model.doc.Var
import org.jetbrains.dokka.model.doc.Version
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.plugability.DokkaPlugin

class DokkaReusableKdocPlugin : DokkaPlugin() {
    val myFilterExtension by extending {
        plugin<DokkaBase>().preMergeDocumentableTransformer providing ::DokkaReusableKdocTransformer
    }
}

class DokkaReusableKdocTransformer(context: DokkaContext) : DocumentableReplacerTransformer(context) {


    override fun processProperty(dProperty: DProperty): AnyWithChanges<DProperty> {
        return super.processProperty(dProperty) // TODO
    }

    // TODO: process all other documentable types

    override fun processClassLike(classlike: DClasslike): AnyWithChanges<DClasslike> {
        val (dClassLike, changes) = super.processClassLike(classlike)
        if (dClassLike == null) return AnyWithChanges(null, changes)

        val documentation = processDocumentation(dClassLike.documentation)
        return AnyWithChanges(
            target = when (dClassLike) {
                is DClass -> dClassLike.copy(documentation = documentation)
                is DEnum -> dClassLike.copy(documentation = documentation)
                is DInterface -> dClassLike.copy(documentation = documentation)
                is DObject -> dClassLike.copy(documentation = documentation)
                is DAnnotation -> dClassLike.copy(documentation = documentation)
            },
            changed = true,
        )
    }

    private fun processDocumentation(documentation: SourceSetDependent<DocumentationNode>): SourceSetDependent<DocumentationNode> =
        documentation.mapValues { (_, it) ->
            DocumentationNode(
                it.children.map { child ->
                    val root = processDocTag(child.root)
                    when (child) {
                        is Description -> child.copy(root = root)
                        is See -> child.copy(root = root)
                        is Param -> child.copy(root = root)
                        is Throws -> child.copy(root = root)
                        is Sample -> child.copy(root = root)
                        is Property -> child.copy(root = root)
                        is CustomTagWrapper -> child.copy(root = root)
                        is Author -> child.copy(root = root)
                        is Version -> child.copy(root = root)
                        is Since -> child.copy(root = root)
                        is Return -> child.copy(root = root)
                        is Receiver -> child.copy(root = root)
                        is Constructor -> child.copy(root = root)
                        is Deprecated -> child.copy(root = root)
                        is Suppress -> child.copy(root = root)
                    }
                }
            )
        }

    private fun processText(text: String): String = text
        .replace('a', 'e')
        .replace('A', 'E')

    private fun processDocTag(docTag: DocTag): DocTag {
        val children = docTag.children.map { processDocTag(it) }
        return when (docTag) {
            is Text -> docTag.copy(
                body = processText(docTag.body),
                children = children,
            )

            is A -> docTag.copy(children = children)
            is Big -> docTag.copy(children = children)
            is B -> docTag.copy(children = children)
            is BlockQuote -> docTag.copy(children = children)
            Br -> docTag
            is Cite -> docTag.copy(children = children)
            is CodeInline -> docTag.copy(children = children)
            is CodeBlock -> docTag.copy(children = children)
            is CustomDocTag -> docTag.copy(children = children)
            is Dd -> docTag.copy(children = children)
            is Dfn -> docTag.copy(children = children)
            is Dir -> docTag.copy(children = children)
            is Div -> docTag.copy(children = children)
            is Dl -> docTag.copy(children = children)
            is DocumentationLink -> docTag.copy(children = children)
            is Dt -> docTag.copy(children = children)
            is Em -> docTag.copy(children = children)
            is Font -> docTag.copy(children = children)
            is Footer -> docTag.copy(children = children)
            is Frame -> docTag.copy(children = children)
            is FrameSet -> docTag.copy(children = children)
            is H1 -> docTag.copy(children = children)
            is H2 -> docTag.copy(children = children)
            is H3 -> docTag.copy(children = children)
            is H4 -> docTag.copy(children = children)
            is H5 -> docTag.copy(children = children)
            is H6 -> docTag.copy(children = children)
            is Head -> docTag.copy(children = children)
            is Header -> docTag.copy(children = children)
            HorizontalRule -> docTag
            is Html -> docTag.copy(children = children)
            is I -> docTag.copy(children = children)
            is IFrame -> docTag.copy(children = children)
            is Img -> docTag.copy(children = children)
            is Index -> docTag.copy(children = children)
            is Input -> docTag.copy(children = children)
            is Li -> docTag.copy(children = children)
            is Link -> docTag.copy(children = children)
            is Listing -> docTag.copy(children = children)
            is Main -> docTag.copy(children = children)
            is Menu -> docTag.copy(children = children)
            is Meta -> docTag.copy(children = children)
            is Nav -> docTag.copy(children = children)
            is NoFrames -> docTag.copy(children = children)
            is NoScript -> docTag.copy(children = children)
            is Ol -> docTag.copy(children = children)
            is P -> docTag.copy(children = children)
            is Pre -> docTag.copy(children = children)
            is Script -> docTag.copy(children = children)
            is Section -> docTag.copy(children = children)
            is Small -> docTag.copy(children = children)
            is Span -> docTag.copy(children = children)
            is Strikethrough -> docTag.copy(children = children)
            is Strong -> docTag.copy(children = children)
            is Sub -> docTag.copy(children = children)
            is Sup -> docTag.copy(children = children)
            is Table -> docTag.copy(children = children)
            is TBody -> docTag.copy(children = children)
            is Td -> docTag.copy(children = children)
            is TFoot -> docTag.copy(children = children)
            is Th -> docTag.copy(children = children)
            is THead -> docTag.copy(children = children)
            is Title -> docTag.copy(children = children)
            is Tr -> docTag.copy(children = children)
            is Tt -> docTag.copy(children = children)
            is U -> docTag.copy(children = children)
            is Ul -> docTag.copy(children = children)
            is Var -> docTag.copy(children = children)
            is Caption -> docTag.copy(children = children)
        }
    }

}