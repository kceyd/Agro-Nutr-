package cl.duoc.agronutri.ui

object Rutas {
    const val LOGIN = "login"
    const val PREDIOS = "predios"
    const val HISTORIAL = "historial"
    const val REGISTRO = "registro/{predioId}"
    const val RECOMENDACION = "recomendacion/{medicionId}"

    fun registro(predioId: Int) = "registro/$predioId"
    fun recomendacion(medicionId: Int) = "recomendacion/$medicionId"
}
