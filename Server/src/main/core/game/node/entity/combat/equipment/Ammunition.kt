package core.game.node.entity.combat.equipment

import core.api.log
import core.game.node.entity.Entity
import core.game.node.entity.impl.Projectile
import core.game.world.update.flag.context.Graphics
import core.tools.Log
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Represents range ammunition types.
 *
 * @author Emperor
 */
class Ammunition(
    val itemId: Int,
    val startGraphics: Graphics?,
    val darkBowGraphics: Graphics?,
    val projectile: Projectile?,
    val poisonDamage: Int,
    val projectileStartHeight: Int = 0,
    val projectileType: Int = 0,
    val projectileAngle: Int = 0,
    val projectileBaseSpeed: Int = 0,
    val projectileId: Int = 0,
    var effect: BoltEffect? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ammunition

        if (itemId != other.itemId) return false
        if (startGraphics != other.startGraphics) return false
        if (darkBowGraphics != other.darkBowGraphics) return false
        if (projectile != other.projectile) return false
        if (poisonDamage != other.poisonDamage) return false
        if (projectileStartHeight != other.projectileStartHeight) return false
        if (projectileType != other.projectileType) return false
        if (projectileAngle != other.projectileAngle) return false
        if (projectileBaseSpeed != other.projectileBaseSpeed) return false
        if (projectileId != other.projectileId) return false
        if (effect != other.effect) return false

        return true
    }

    override fun hashCode(): Int {
        var result = itemId
        result = 31 * result + (startGraphics?.hashCode() ?: 0)
        result = 31 * result + (darkBowGraphics?.hashCode() ?: 0)
        result = 31 * result + (projectile?.hashCode() ?: 0)
        result = 31 * result + poisonDamage
        result = 31 * result + projectileStartHeight
        result = 31 * result + projectileType
        result = 31 * result + projectileAngle
        result = 31 * result + projectileBaseSpeed
        result = 31 * result + projectileId
        result = 31 * result + (effect?.hashCode() ?: 0)
        return result
    }

    companion object {
        private val AMMUNITION = mutableMapOf<Int, Ammunition>()

        fun putAmmunition(id: Int, ammo: Ammunition) {
            AMMUNITION[id] = ammo
        }

        /**
         * Loads all the {@code Ammunition} info to the mapping.
         *
         * @return {@code true} if successful, {@code false} otherwise.
         */
        @JvmStatic
        fun initialize(): Boolean {
            val doc = try {
                val factory = DocumentBuilderFactory.newInstance()
                val builder = factory.newDocumentBuilder()
                builder.parse(File("data/configs/ammunition.xml"))
            } catch (e: Throwable) {
                e.printStackTrace()
                return false
            }

            val nodeList = doc.documentElement.childNodes
            var i = 1
            while (i < nodeList.length) {
                val n = nodeList.item(i)
                if (n != null && n.nodeName.equals("Ammunition", ignoreCase = true)) {
                    val list = n.childNodes
                    var itemId = 0
                    var graphicsId = 0
                    var startGraphics: Graphics? = null
                    var darkBowGraphics: Graphics? = null
                    var projectile: Projectile? = null
                    var poisonDamage = 0
                    var projectileStartHeight = 0
                    var projectileType = 0
                    var projectileAngle = 0
                    var projectileBaseSpeed = 0
                    var projectileId = 0

                    var a = 1
                    while (a < list.length) {
                        val node = list.item(a)
                        when {
                            node.nodeName.equals("itemId", ignoreCase = true) -> {
                                itemId = node.textContent.toInt()
                            }

                            node.nodeName.equals("startGraphicsId", ignoreCase = true) -> {
                                graphicsId = node.textContent.toInt()
                            }

                            node.nodeName.equals("startGraphicsHeight", ignoreCase = true) -> {
                                startGraphics = Graphics(graphicsId, node.textContent.toInt(), 0)
                            }

                            node.nodeName.equals("darkBowGraphicsId", ignoreCase = true) -> {
                                graphicsId = node.textContent.toInt()
                            }

                            node.nodeName.equals("darkBowGraphicsHeight", ignoreCase = true) -> {
                                darkBowGraphics = Graphics(graphicsId, node.textContent.toInt(), 0)
                            }

                            node.nodeName.equals("projectileId", ignoreCase = true) -> {
                                projectileStartHeight = node.attributes.getNamedItem("start_height").textContent.toInt()
                                projectileType = node.attributes.getNamedItem("type").textContent.toInt()
                                projectileAngle = node.attributes.getNamedItem("angle").textContent.toInt()
                                projectileBaseSpeed = node.attributes.getNamedItem("base_speed").textContent.toInt()
                                projectileId = node.textContent.toInt()
                                projectile = Projectile.create(
                                    null as Entity?,
                                    null as Entity?,
                                    projectileId,
                                    projectileStartHeight,
                                    36,
                                    projectileType,
                                    projectileBaseSpeed,
                                    projectileAngle,
                                    0
                                )
                            }

                            node.nodeName.equals("poisonDamage", ignoreCase = true) -> {
                                poisonDamage = node.textContent.toInt()
                                AMMUNITION[itemId] = Ammunition(
                                    itemId,
                                    startGraphics,
                                    darkBowGraphics,
                                    projectile,
                                    poisonDamage,
                                    projectileStartHeight,
                                    projectileType,
                                    projectileAngle,
                                    projectileBaseSpeed,
                                    projectileId
                                )
                            }
                        }
                        a += 2
                    }
                }
                i += 2
            }
            AMMUNITION.values.forEach { println(it) }
            return true
        }

        private fun parse(ammunitionMap: Map<Int, Ammunition>, file: File) {
            val docFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = docFactory.newDocumentBuilder()

            val doc = docBuilder.newDocument()
            val root = doc.createElement("Ammunitions")
            doc.appendChild(root)

            ammunitionMap.values.forEach { ammo ->
                val ammoElem = doc.createElement("Ammunition")
                root.appendChild(ammoElem)

                fun addElement(name: String, value: String) {
                    val el = doc.createElement(name)
                    el.appendChild(doc.createTextNode(value))
                    ammoElem.appendChild(el)
                }

                addElement("itemId", ammo.itemId.toString())

                ammo.startGraphics?.let {
                    addElement("startGraphicsId", it.id.toString())
                    addElement("startGraphicsHeight", it.height.toString())
                }

                ammo.darkBowGraphics?.let {
                    addElement("darkBowGraphicsId", it.id.toString())
                    addElement("darkBowGraphicsHeight", it.height.toString())
                }

                if (ammo.projectile != null) {
                    val projElem = doc.createElement("projectileId")
                    projElem.setAttribute("start_height", ammo.projectileStartHeight.toString())
                    projElem.setAttribute("type", ammo.projectileType.toString())
                    projElem.setAttribute("angle", ammo.projectileAngle.toString())
                    projElem.setAttribute("base_speed", ammo.projectileBaseSpeed.toString())
                    projElem.appendChild(doc.createTextNode(ammo.projectileId.toString()))
                    ammoElem.appendChild(projElem)
                }

                addElement("poisonDamage", ammo.poisonDamage.toString())
            }

            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(DOMSource(doc), StreamResult(file))

            log(this::class.java, Log.DEBUG, "Parsed ammunition.xml.")
        }

        @JvmStatic
        fun getAmmunition(): Map<Int, Ammunition> = AMMUNITION

        @JvmStatic
        fun get(id: Int): Ammunition? = AMMUNITION[id]

        @JvmStatic
        fun main() {
            if (initialize()) {
                parse(getAmmunition(), File("data/configs/ammunition.xml"))
            }
        }
    }

    override fun toString(): String {
        return "Ammunition(itemId=$itemId, startGraphics=$startGraphics, darkBowGraphics=$darkBowGraphics, projectile=$projectile, poisonDamage=$poisonDamage, projectileId=$projectileId, projectileStartHeight=$projectileStartHeight, projectileType=$projectileType, projectileAngle=$projectileAngle, projectileBaseSpeed=$projectileBaseSpeed, effect=$effect)"
    }
}