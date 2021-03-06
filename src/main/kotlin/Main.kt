
package un5.eje5_4


import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.time.LocalDate
import java.util.logging.LogManager
import javax.xml.parsers.DocumentBuilderFactory
import java.util.logging.Level

/**
 * Clase que almacena un documento XML, @param :String ,
 */
class CatalogoLibrosXML(private val cargador: String) {

    companion object {
        val l = LogManager.getLogManager().getLogger("").apply { level= Level.ALL }
    }

     var xmlDoc: Document? = null
    init {
        try {
            xmlDoc = readXml(cargador)
            xmlDoc?.let { it.documentElement.normalize() }
        } catch (e: Exception) {
            requireNotNull(xmlDoc , { e.message.toString() })
        }
    }

    /**
     * metodo que lee el xml
     * @param String nombre de direccion del xml
     *  @return un Document
     */
     private fun readXml(pathName: String): Document {
        val xmlFile = File(pathName)
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
    }

    /**
     * metodo que recibe unos nombres de unos nodos
     * @param Document , tagName (String , etiqueta)
     * @return una lista de nodos
     */
     private fun obtenerListaNodosPorNombre(doc: Document, tagName: String): MutableList<Node> {
        val bookList: NodeList = doc.getElementsByTagName(tagName)
        val lista = mutableListOf<Node>()
        for (i in 0..bookList.length - 1)
            lista.add(bookList.item(i))
        return lista
    }

    fun obtenerAtributosEnMapKV(e: Element): MutableMap<String, String> {
        val mMap = mutableMapOf<String, String>()
        for (j in 0..e.attributes.length - 1)
            mMap.putIfAbsent(e.attributes.item(j).nodeName, e.attributes.item(j).nodeValue)
        return mMap
    }

    /**
    *@param nombre del libro (String)
    * @return  Devuelve true si existe, `false` en caso contrario.
    * */
    fun existeLibro(idLibro: String): Boolean {
        var existe: Boolean
        if (idLibro.isNullOrBlank())
            existe = false
        else {
            var encontrado = xmlDoc?.let {
                var nodosLibro = obtenerListaNodosPorNombre(it, "book")
                ((nodosLibro.indexOfFirst {
                    if (it.getNodeType() === Node.ELEMENT_NODE) {
                        val elem = it as Element
                        obtenerAtributosEnMapKV(elem)["id"] == idLibro
                    } else
                        false
                }) >= 0)
            }
            existe = (encontrado != null && encontrado)
        }
        return existe
    }

    /**
      *@param nombre del libro (idLibro:String)
      * @return  Devuelve un Map con la informacion del libro
      * */
    fun infoLibro(idLibro: String): Map<String, Any> {
        var m = mutableMapOf<String, Any>()
        if (!idLibro.isNullOrBlank())
            xmlDoc?.let {
                var nodosLibro = obtenerListaNodosPorNombre(it, "book")

                var posicionDelLibro = nodosLibro.indexOfFirst {
                    if (it.getNodeType() === Node.ELEMENT_NODE) {
                        val elem = it as Element
                        obtenerAtributosEnMapKV(elem)["id"] == idLibro
                    } else false
                }
                if (posicionDelLibro >= 0) {
                    if (nodosLibro[posicionDelLibro].getNodeType() === Node.ELEMENT_NODE) {
                        val elem = nodosLibro[posicionDelLibro] as Element
                        m.put("id", idLibro)
                        m.put("author", elem.getElementsByTagName("author").item(0).textContent)
                        m.put("genre", elem.getElementsByTagName("genre").item(0).textContent)
                        m.put("price", elem.getElementsByTagName("price").item(0).textContent.toDouble())
                        m.put(
                            "publish_date",
                            LocalDate.parse(elem.getElementsByTagName("publish_date").item(0).textContent)
                        )
                        m.put("description", elem.getElementsByTagName("description").item(0).textContent)
                    }
                }
            }
        return m
    }
    fun i(msg:String)
    {
        CatalogoLibrosXML.l.info { msg }
    }
}

fun main() {
    var portatil = "/home/edu/IdeaProjects/IESRA-DAM-Prog/ejercicios/src/main/kotlin/un5/eje5_4/Catalog.xml"
    var casa = "/home/usuario/Documentos/workspace/IdeaProjects/IESRA-DAM/ejercicios/src/main/kotlin/un5/eje5_4/Catalog.xml"

    var cat = CatalogoLibrosXML(casa)
    var id = "bk105"
    cat.i(cat.existeLibro(id).toString())
    cat.i(cat.infoLibro(id).toString())
}

/*
Implementar una clase `CatalogoLibrosXML` con sus m??todos y propiedades. Usa los modificadores de acceso adecuado seg??n lo creas conveniente e intenta separar la funcionalidad en m??todos que tengan sentido para la clase y que hagan una ??nica cosa.
### Propiedades
- Las que necesites.
### M??todos
- `constructor(cargador:String)`: Debe abortar si el fichero no existe o es incorrecto.
- `existeLibro(idLibro:String): Boolean`: Devuelve true si existe, `false` en caso contrario.
- `infoLibro(idLibro:String): Map<String,Any>`: Devuelve un `Map` con los atributos y valores del libro. Devolver??
  un `Map` vac??o en caso contrario.
 */
