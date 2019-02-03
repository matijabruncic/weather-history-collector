package org.mbruncic

import org.mbruncic.model.WeatherInformation
import org.springframework.stereotype.Service
import org.apache.http.HttpHost
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Service
class Database {
    var client = RestHighLevelClient(RestClient.builder(HttpHost("localhost", 9200, "http")))

    fun exists(id: String): Boolean {
        return client.exists(GetRequest("weather", "_doc", id))
    }

    fun save(id: String, weatherInformation: WeatherInformation) {
        val indexRequest = IndexRequest("weather", "_doc", id)
        val source = """
            {
            "weatherStationId":"${weatherInformation.weatherStationId}",
            "countryName":"${weatherInformation.countryName}",
            "date":"${LocalDate.parse(weatherInformation.date, DateTimeFormatter.BASIC_ISO_DATE)}",
            "minTemperature":${weatherInformation.minTemperature},
            "maxTemperature":${weatherInformation.maxTemperature}
            }
        """.trimIndent()
        log.debug { source }
        indexRequest.source(source, XContentType.JSON)
        client.index(indexRequest)
    }

    fun failed(id: String, date: String, message: String?, countryName: String?) {
        val indexRequest = IndexRequest("weather", "_doc", id)
        val source = """
            {
            "date":"${LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE)}",
            "error":true,
            "errorMessage":"${message?.replace("\"", "\\\"")}",
            "countryName":"$countryName"
            }
        """.trimIndent()
        log.debug { source }
        indexRequest.source(source, XContentType.JSON)
        client.index(indexRequest)
    }

}
