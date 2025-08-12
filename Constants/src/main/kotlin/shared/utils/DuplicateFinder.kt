package shared.utils

import java.io.File

object DuplicateFinder {
    @JvmStatic
    fun duplicateDetection(vararg classes: Class<*>, report: File) {
        val rows = mutableListOf(listOf("Class", "Value", "Fields"))

        for (clazz in classes) {
            val valueMap = mutableMapOf<Int, MutableList<String>>()

            clazz.declaredFields
                .filter {
                    java.lang.reflect.Modifier.isStatic(it.modifiers) &&
                            java.lang.reflect.Modifier.isFinal(it.modifiers)
                }
                .filter {
                    it.type == Int::class.javaPrimitiveType || it.type == Integer::class.java
                }
                .forEach {
                    it.isAccessible = true
                    val value = it.getInt(null)
                    valueMap.getOrPut(value) { mutableListOf() }.add(it.name)
                }

            val duplicates = valueMap.filter { it.value.size > 1 }
            duplicates.forEach { (value, fields) ->
                rows.add(listOf(clazz.simpleName, value.toString(), fields.joinToString(", ")))
            }
        }

        if (rows.size == 1) {
            println("Report complete: no conflicts detected.")
            return
        }
        report.parentFile?.mkdirs()
        report.printWriter().use { out ->
            rows.forEach { row -> out.println(row.joinToString(";")) }
        }

        println("Report completed. Path: [${report.absolutePath}]")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val reportFile = if (args.isNotEmpty()) File(args[0]) else File("duplicates_report.csv")
        duplicateDetection(
            shared.consts.Graphics::class.java,
            shared.consts.Items::class.java,
            shared.consts.Music::class.java,
            shared.consts.Network::class.java,
            shared.consts.NPCs::class.java,
            shared.consts.Quests::class.java,
            shared.consts.Regions::class.java,
            shared.consts.Scenery::class.java,
            shared.consts.Sounds::class.java,
            shared.consts.Vars::class.java,
            shared.consts.Animations::class.java,
            shared.consts.Components::class.java,
            report = reportFile
        )
    }
}