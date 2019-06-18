
val ByteArray.text get() = toString(Charsets.UTF_8)
val Byte.text get() = toChar()
val List<Byte>.text get() = toByteArray().text