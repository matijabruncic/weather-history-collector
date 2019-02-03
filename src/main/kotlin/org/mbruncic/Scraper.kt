package org.mbruncic

import com.mashape.unirest.http.Unirest
import org.json.JSONArray
import org.json.JSONObject
import org.mbruncic.model.WeatherInformation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
open class Scraper(@Autowired val database: Database) {

    private val weatherStations = setOf("ISEHUDDI2", "ISTOCKHO926", "IHUDDING101", "IENSKEDE2", "IBROMMAC3", "ISTOCKHO94", "INASOLNA2", "IBROMMAC4", "IJOHANNE72"
            , "I21TRGZR3", "IUNDEFIN41", "IRASINJA3", "IZAGREB49", "ISVETANE2", "IGRADZAG3", "IGRADZAG10", "IZAGREB5", "IZAGREB48", "IGRADZAG11"
    ,"IMEDELLN16", "IRETIRO4", "IANTVERE3"
            )

    @Scheduled(fixedDelayString = "PT1M")
    open fun scrape() {
        val startDate = LocalDate.now()
        val endDate = LocalDate.of(2016, 12, 31)
        var currentDate = startDate
        while (currentDate.isAfter(endDate)) {
            val date = currentDate.format(DateTimeFormatter.BASIC_ISO_DATE)
            for (weatherStationId in weatherStations) {
                val id = "$weatherStationId-$date"
                if (!database.exists(id)) {
                    val url = "https://api-ak.wunderground.com/api/606f3f6977348613/history_$date/units:metric/v:2.0/q/pws:$weatherStationId.json"
                    var weatherStationCountryName: String? = null
                    try {
                        val body = Unirest.get(url).asJson().body
                        weatherStationCountryName = ((body.`object`["response"] as JSONObject)["location"] as JSONObject)["country_name"] as String
                        val summary = (((body.`object`["history"] as JSONObject)["days"] as JSONArray)[0] as JSONObject)["summary"] as JSONObject
                        val maxTemperature = summary["max_temperature"] as Double
                        val minTemperature = summary["min_temperature"] as Double
                        log.debug { "weatherStation=$weatherStationCountryName date=$date max_temperature = $maxTemperature" }
                        log.debug { "weatherStation=$weatherStationCountryName date=$date min_temperature = $minTemperature" }
                        database.save(id, WeatherInformation(weatherStationId, weatherStationCountryName, date, minTemperature, maxTemperature))
                        log.info { "Saved data for $currentDate" }
                    } catch (e: Exception) {
                        log.error { "Error for $weatherStationId - $currentDate - ${e.message} - $url" }
                        try {
                            database.failed(id, date, e.message, weatherStationCountryName)
                        } catch (e: Exception) {
                            log.error { e.message }
                        }
                    }
                }
            }
            currentDate = currentDate.minusDays(1)
        }
    }
}