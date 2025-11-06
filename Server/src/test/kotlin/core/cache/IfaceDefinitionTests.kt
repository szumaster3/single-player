package core.cache

import core.cache.def.impl.ComponentType
import core.cache.def.impl.IfaceDefinition
import core.cache.def.impl.IfaceDefinition.encodeIf3
import core.cache.def.impl.LinkedScripts
import core.cache.def.impl.ScriptTriggers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class IfaceDefinitionTests {

    init {
        TestUtils.preTestSetup()
    }

    @Test
    fun testInterface() {
        val def = IfaceDefinition().apply {
            id = 834
            type = ComponentType.SPRITE
            baseX = 255
            baseY = 450
            baseWidth = 100
            baseHeight = 50
            optionBase = "Click"
            spriteId = 999
            scripts = LinkedScripts()
            triggers = ScriptTriggers()
        }

        val encoded = encodeIf3(def)
        val cacheFile = Cache.getIndexes()[3]?.cacheFile
        Assertions.assertNotNull(cacheFile, "Given index 3, cache file should exist.")
        val success = cacheFile!!.write(def.id, encoded, null)
        Assertions.assertTrue(success, "Failed to write the interface to cache.")
        val readData = cacheFile.read(def.id, null)
        Assertions.assertNotNull(readData, "Read interface is null")
        Assertions.assertArrayEquals(encoded, readData, "Written and read bytes are not the same.")
    }
}
