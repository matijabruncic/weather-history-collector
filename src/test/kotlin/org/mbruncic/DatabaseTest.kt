package org.mbruncic

import org.apache.http.HttpHost
import org.assertj.core.api.Assertions.assertThat
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.junit.Before
import org.junit.Test
import org.mbruncic.model.WeatherInformation

class DatabaseTest {

    private val database = Database()
    private var client = RestHighLevelClient(RestClient.builder(HttpHost("localhost", 9200, "http")))

    @Before
    fun setup(){
        client.delete(DeleteRequest("weather", "_doc", "1"))
        client.delete(DeleteRequest("weather", "_doc", "2"))
        client.index(IndexRequest("weather", "_doc", "2").source("""{"test":"test"}""", XContentType.JSON))
    }

    @Test
    fun exists() {
        assertThat(database.exists("1")).isFalse()
        assertThat(database.exists("2")).isTrue()
    }

    @Test
    fun save() {
        database.save("3", WeatherInformation("IUNDEFIN41-test", "Sweden", "20190203", -0.6, 0.7))
        assertThat(client.exists(GetRequest("weather", "_doc", "3"))).isTrue()
    }
}