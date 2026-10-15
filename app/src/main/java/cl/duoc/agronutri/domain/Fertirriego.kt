package cl.duoc.agronutri.domain

import cl.duoc.agronutri.data.local.Medicion

enum class Nutriente(val simbolo: String, val fertilizante: String, val kgPorPpm: Double) {
    N("N", "Nitrato de amonio", 0.35),
    P("P", "MAP (fosfato monoamónico)", 0.45),
    K("K", "Nitrato de potasio", 0.40),
    CA("Ca", "Nitrato de calcio", 0.50),
    MG("Mg", "Sulfato de magnesio", 0.60)
}

data class DosisItem(
    val nutriente: Nutriente,
    val deficitPpm: Double,
    val kgPorHa: Double
)

/**
 * Cálculo básico de fertirriego. Los requerimientos son datos ficticios;
 * reemplázalos por el catálogo oficial que entregue el caso.
 */
object Fertirriego {
    val cultivos = listOf("Palto", "Cítricos", "Tomate")
    val etapas = listOf("Floración", "Engorde", "Maduración")

    private fun req(n: Double, p: Double, k: Double, ca: Double, mg: Double) = mapOf(
        Nutriente.N to n, Nutriente.P to p, Nutriente.K to k,
        Nutriente.CA to ca, Nutriente.MG to mg
    )

    private val requerimientos = mapOf(
        "Palto" to mapOf(
            "Floración" to req(40.0, 25.0, 180.0, 150.0, 50.0),
            "Engorde" to req(55.0, 30.0, 240.0, 170.0, 60.0),
            "Maduración" to req(30.0, 20.0, 200.0, 140.0, 45.0)
        ),
        "Cítricos" to mapOf(
            "Floración" to req(45.0, 25.0, 160.0, 140.0, 45.0),
            "Engorde" to req(60.0, 30.0, 220.0, 160.0, 55.0),
            "Maduración" to req(35.0, 20.0, 190.0, 130.0, 40.0)
        ),
        "Tomate" to mapOf(
            "Floración" to req(70.0, 35.0, 220.0, 180.0, 55.0),
            "Engorde" to req(90.0, 40.0, 300.0, 200.0, 65.0),
            "Maduración" to req(60.0, 30.0, 260.0, 170.0, 50.0)
        )
    )

    fun requerimiento(cultivo: String, etapa: String): Map<Nutriente, Double> =
        requerimientos[cultivo]?.get(etapa) ?: emptyMap()

    fun calcular(m: Medicion): List<DosisItem> {
        val objetivo = requerimiento(m.cultivo, m.etapa)
        val medidos = mapOf(
            Nutriente.N to m.n, Nutriente.P to m.p, Nutriente.K to m.k,
            Nutriente.CA to m.ca, Nutriente.MG to m.mg
        )
        return Nutriente.entries.map { nutriente ->
            val deficit = ((objetivo[nutriente] ?: 0.0) - (medidos[nutriente] ?: 0.0))
                .coerceAtLeast(0.0)
            DosisItem(nutriente, deficit, deficit * nutriente.kgPorPpm)
        }
    }
}
