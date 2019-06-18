import java.io.File
import java.lang.StringBuilder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths


fun main(vararg param: String) {


    if (param[0].substring(param[0].lastIndexOf('.')+1, param[0].length)!="acss") return

    val fileName = param[0].let { it.substring(it.lastIndexOf('/') + 1, it.length) }
    val fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'))




    val fileString = String(Files.readAllBytes(Paths.get(param[0])), StandardCharsets.UTF_8)


    val cssObj = fileString.readCSS()



    var xmlString =
        fileString.substring(fileString.indexOf("<layout>", 0, true) + 8, fileString.indexOf("</layout>", 0, true))

    cssObj.forEach { key, value ->

        if (key.first() == '#') {
            val builder = StringBuilder()
            value.forEach {
                builder.append("${it.first}=${it.second}")
            }

            xmlString = xmlString.replace("id=\"+@${key.drop(1)}\"", "\"id=\"+@${key.drop(1)}\" $builder")

        } else if (key.first() == '.') {

            val builder = StringBuilder().apply {
                value.forEach {
                    append("${it.first}=${it.second}")
                }
            }.toString()

            xmlString = xmlString.replace("class=\"${key.drop(1)}\"", builder)


        }

    }






    xmlString.writeToFile("$fileNameWithoutExtn.xml")




}



fun String.writeToFile(name: String){
    File(name).let {
        if(it.exists()) it.delete()
        // create a new file
        if(it.createNewFile()){
            println("$it is created successfully.")
            it.writeText(this)
        }
    }
}










fun String.readCSS(): HashMap<String, ArrayList<Pair<String, String>>> {

    val CSSObj = HashMap<String, ArrayList<Pair<String, String>>>()

    let { block ->

        block.substring(block.indexOf("<style>", 0, true)+7, block.indexOf("</style>", 0, true)).replace("\n", "")
            .replace("[\n\r\t]".toRegex(), "")
            .replace("\\s+".toRegex(), "")
            .replace(";}", "}").let { CSSBlock ->

                var pointer = 0
                while (pointer < CSSBlock.length) {

                    CSSBlock.substring(pointer, CSSBlock.indexOf("}", pointer, true) + 1).let { block ->

                        val indexOfK1 = block.indexOf("{", 0, true)
                        val indexOfK2 = block.indexOf("}", 0, true)

                        val superType = block.substring(0, indexOfK1)

                        val subType = block.substring(indexOfK1 + 1, indexOfK2)


                        CSSObj[superType] = arrayListOf<Pair<String, String>>().apply {
                            subType.split(";").forEach {
                                it.split(":").let { pair ->
                                    add(pair[0] to pair[1])
                                }
                            }
                        }


                        if (block.isNotEmpty()) pointer += block.length
                        else pointer++
                    }

                }


            }

    }

    println(CSSObj)

    return CSSObj
}

