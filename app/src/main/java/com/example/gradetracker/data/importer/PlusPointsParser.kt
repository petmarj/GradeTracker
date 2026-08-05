package com.example.gradetracker.data.importer

import java.io.InputStream
import java.io.StringReader
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import org.xml.sax.InputSource
import org.w3c.dom.Element
import org.w3c.dom.Node

data class PlusPointsSemester(
    val name: String,
    val subjects: List<PlusPointsSubject>
)

data class PlusPointsSubject(
    val name: String,
    val exams: List<PlusPointsExam>
)

data class PlusPointsExam(
    val name: String,
    val mark: Double,
    val weight: Double,
    val date: Long?
)

object PlusPointsParser {

    fun parse(inputStream: InputStream): PlusPointsSemester {
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = false
            isExpandEntityReferences = false
        }
        listOf(
            XMLConstants.FEATURE_SECURE_PROCESSING to true,
            "http://xml.org/sax/features/external-general-entities" to false,
            "http://xml.org/sax/features/external-parameter-entities" to false,
            "http://apache.org/xml/features/nonvalidating/load-external-dtd" to false
        ).forEach { (feature, enabled) ->
            runCatching { factory.setFeature(feature, enabled) }
        }

        val builder = factory.newDocumentBuilder().apply {
            setEntityResolver { _, _ -> InputSource(StringReader("")) }
        }
        val document = builder.parse(inputStream)
        val plist = document.documentElement
        require(plist.tagName == "plist") {
            "Die ausgewählte Datei ist keine Property-List."
        }

        val root = plist.childElements().firstOrNull { it.tagName == "dict" }
            ?.asDictionary()
            ?: throw IllegalArgumentException("Die Property-List enthält keine Daten.")
        val data = (root["data"] as? PlistValue.Dictionary)?.values
            ?: throw IllegalArgumentException("Das PlusPoints-Semester fehlt.")
        val name = data.string("name")?.trim().orEmpty()
        require(name.isNotEmpty()) { "Das Semester hat keinen Namen." }

        val subjects = data.array("subjects").mapNotNull { value ->
            val subject = (value as? PlistValue.Dictionary)?.values
                ?: return@mapNotNull null
            val subjectName = subject.string("name")?.trim().orEmpty()
            if (subjectName.isEmpty()) return@mapNotNull null

            val exams = subject.array("exams").mapNotNull examLoop@{ examValue ->
                val exam = (examValue as? PlistValue.Dictionary)?.values
                    ?: return@examLoop null
                val examName = exam.string("name")?.trim().orEmpty()
                val mark = exam.number("mark") ?: return@examLoop null
                val importedWeight = exam.number("weight") ?: 1.0
                val counted = exam.number("counted")?.toInt() != 0

                PlusPointsExam(
                    name = examName.ifEmpty { "Prüfung" },
                    mark = mark,
                    weight = if (counted) importedWeight else 0.0,
                    date = exam.number("dAtEaTtr:date")?.toLong()
                )
            }

            PlusPointsSubject(
                name = subjectName,
                exams = exams
            )
        }

        return PlusPointsSemester(name = name, subjects = subjects)
    }

    private sealed interface PlistValue {
        data class Dictionary(val values: Map<String, PlistValue>) : PlistValue
        data class ArrayValue(val values: List<PlistValue>) : PlistValue
        data class Text(val value: String) : PlistValue
        data class Number(val value: Double) : PlistValue
        data class BooleanValue(val value: Boolean) : PlistValue
    }

    private fun Element.asValue(): PlistValue = when (tagName) {
        "dict" -> PlistValue.Dictionary(asDictionary())
        "array" -> PlistValue.ArrayValue(childElements().map { it.asValue() })
        "integer", "real" -> PlistValue.Number(
            textContent.trim().toDoubleOrNull()
                ?: throw IllegalArgumentException("Ungültiger Zahlenwert in der PlusPoints-Datei.")
        )
        "true" -> PlistValue.BooleanValue(true)
        "false" -> PlistValue.BooleanValue(false)
        else -> PlistValue.Text(textContent.orEmpty())
    }

    private fun Element.asDictionary(): Map<String, PlistValue> {
        val children = childElements()
        val result = linkedMapOf<String, PlistValue>()
        var index = 0
        while (index < children.size) {
            val keyElement = children[index]
            if (keyElement.tagName != "key" || index + 1 >= children.size) {
                index++
                continue
            }
            result[keyElement.textContent] = children[index + 1].asValue()
            index += 2
        }
        return result
    }

    private fun Element.childElements(): List<Element> {
        val result = mutableListOf<Element>()
        for (index in 0 until childNodes.length) {
            val node = childNodes.item(index)
            if (node.nodeType == Node.ELEMENT_NODE) result += node as Element
        }
        return result
    }

    private fun Map<String, PlistValue>.string(key: String): String? =
        (this[key] as? PlistValue.Text)?.value

    private fun Map<String, PlistValue>.number(key: String): Double? = when (val value = this[key]) {
        is PlistValue.Number -> value.value
        is PlistValue.Text -> value.value.toDoubleOrNull()
        is PlistValue.BooleanValue -> if (value.value) 1.0 else 0.0
        else -> null
    }

    private fun Map<String, PlistValue>.array(key: String): List<PlistValue> =
        (this[key] as? PlistValue.ArrayValue)?.values.orEmpty()
}
