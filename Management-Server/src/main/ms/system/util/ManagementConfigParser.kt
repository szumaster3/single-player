package ms.system.util

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.io.FileReader
import kotlin.system.exitProcess

/**
 * Class for parsing the server config, I.E default.conf
 * @param path the path to the JSON file to parse.
 * @author Ceikry
 */
class ManagementConfigParser(path: String) {
    val pathTo = parsePath(path)
    val confFile = File(pathTo)
    val parser = JsonParser()
    var reader: FileReader? = null
    var data: JsonObject? = null

    init {
        if (!confFile.canonicalFile.exists()) {
            println("Could not find ${confFile.canonicalFile}.")
            exitProcess(0)
        } else if (!pathTo.contains(".json")) {
            println("Config file MUST be a JSON file!!")
            println("(Got $pathTo)")
        } else {
            reader = FileReader(pathTo)
            data = parser.parse(reader) as JsonObject
            parseDatabaseInformation()
            parseWorldTechnicalSettings()
            ManagementConstants.SECRET_KEY = data!!["secret_key"].toString()
        }
    }

    private fun parseDatabaseInformation() {
        data ?: return
        val dbData = data!!["DatabaseInformation"] as JsonObject
        ManagementConstants.parseDBProp(dbData)
    }

    private fun parseWorldTechnicalSettings() {
        data ?: return
        val wtiData = data!!["WorldTechnicalInformation"] as JsonObject
        ManagementConstants.parseWTIProp(wtiData)
    }

    /**
     * Parses a path string
     * @param pathString The string to parse
     * @return a String with the proper file separators for the current OS.
     * @author Ceikry
     */
    fun parsePath(pathString: String): String {
        var pathTokens: List<String>? = null
        if (pathString.contains("/"))
            pathTokens = pathString.split("/")
        else if (pathString.contains("\\"))
            pathTokens = pathString.split("\\")

        pathTokens ?: return pathString //return the initial pathString if path does not contain file separators.
        var pathProduct = ""
        for (token in pathTokens) {
            if (token != "")
                pathProduct += "$token${File.separator}"
        }

        return pathProduct
    }
}
