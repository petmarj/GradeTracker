package ch.example.gradetracker.data.importer

import java.io.ByteArrayInputStream
import org.junit.Assert.assertEquals
import org.junit.Test

class PlusPointsParserTest {

    @Test
    fun parsesSemesterAndAppliesImportRules() {
        val xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
            <plist version="1.0">
                <dict>
                    <key>data</key>
                    <dict>
                        <key>class</key><string>Semester</string>
                        <key>name</key><string>Herbst</string>
                        <key>subjects</key>
                        <array>
                            <dict>
                                <key>name</key><string>Mathematik</string>
                                <key>weight</key><string>9.0</string>
                                <key>exams</key>
                                <array>
                                    <dict>
                                        <key>name</key><string>Test 1</string>
                                        <key>counted</key><integer>0</integer>
                                        <key>mark</key><real>9.0</real>
                                        <key>weight</key><string>2.5</string>
                                        <key>dAtEaTtr:date</key><real>1784352453965.0</real>
                                        <key>has_subexams</key><integer>1</integer>
                                        <key>subexams</key>
                                        <array>
                                            <dict>
                                                <key>name</key><string>Nicht importieren</string>
                                                <key>mark</key><real>6.0</real>
                                            </dict>
                                        </array>
                                    </dict>
                                </array>
                            </dict>
                        </array>
                    </dict>
                    <key>version</key><string></string>
                </dict>
            </plist>
        """.trimIndent()

        val semester = PlusPointsParser.parse(
            ByteArrayInputStream(xml.toByteArray())
        )

        assertEquals("Herbst", semester.name)
        assertEquals(1, semester.subjects.size)
        assertEquals("Mathematik", semester.subjects.single().name)
        assertEquals(1, semester.subjects.single().exams.size)
        val exam = semester.subjects.single().exams.single()
        assertEquals(9.0, exam.mark, 0.0)
        assertEquals(0.0, exam.weight, 0.0)
        assertEquals(1784352453965L, exam.date)
    }
}
