package org.mbruncic

import org.springframework.stereotype.Service

@Service
class WeatherStationsProvider {

    private val stockholm = setOf("ISEHUDDI2", "ISTOCKHO926", "IHUDDING101", "IENSKEDE2", "IBROMMAC3", "ISTOCKHO94", "INASOLNA2", "IBROMMAC4", "IJOHANNE72")
    private val croatia = setOf("I21TRGZR3", "IUNDEFIN41", "IRASINJA3", "IZAGREB49", "ISVETANE2", "IGRADZAG3", "IGRADZAG10", "IZAGREB5", "IZAGREB48", "IGRADZAG11")
    private val medellin = setOf("IMEDELLN16", "IRETIRO4", "IANTVERE3", "IRETIRO8", "IRETIRO3")
    private val barcelona = setOf("IBARCELO777", "IBARCELO169", "ICATALUN165", "IBLANOVA19", "IBARCELO791")
    private val christchurch = setOf("ICHRISTC224", "ICHRISTC195", "ICHRISTC159", "ICHRISTC171", "ICANTERB357", "ICANAVON2", "ICANTERB312")

    private val weatherStations by lazy { stockholm.union(croatia).union(medellin).union(barcelona).union(christchurch) }

    fun resolveWeatherStations(): Set<String> {
        return weatherStations
    }
}