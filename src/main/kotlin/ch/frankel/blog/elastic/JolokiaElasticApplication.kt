package ch.frankel.blog.elastic

import io.searchbox.client.JestClient
import io.searchbox.core.Index
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.context.annotation.*
import org.springframework.scheduling.annotation.*
import org.springframework.web.client.*
import java.util.*


@SpringBootApplication
@EnableScheduling
open class JolokiaElasticApplication {

    @Autowired lateinit var client: JestClient

    @Bean open fun template() = RestTemplate()

    @Scheduled(fixedRate = 5000)
    open fun transfer() {
        val result = template().getForObject(
                "http://localhost:8080/manage/jolokia/read/org.springframework.metrics:name=response,type=gauge,value=manage.beans",
                String::class.java)
        val index = Index.Builder(result).index("metrics").type("metric").id(UUID.randomUUID().toString()).build()
        client.execute(index)
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(JolokiaElasticApplication::class.java, *args)
}