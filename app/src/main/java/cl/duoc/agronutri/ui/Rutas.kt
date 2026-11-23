
package cl.duoc.agronutri.ui

object Rutas {
    const val LOGIN = "login"
    const val ACCESO = "acceso/{rol}"

    const val PREDIOS = "predios"
    const val HISTORIAL = "historial"
    const val REGISTRO = "registro/{predioId}"
    const val RECOMENDACION = "recomendacion/{medicionId}"

    fun acceso(rol: String) = "acceso/$rol"
    fun registro(predioId: Int) = "registro/$predioId"
    fun recomendacion(medicionId: Int) = "recomendacion/$medicionId"
}
