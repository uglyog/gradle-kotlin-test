package io.pact

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.LambdaDsl
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import org.apache.http.client.fluent.Request.Get
import org.apache.http.client.fluent.Request.Post
import org.apache.http.entity.ContentType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(PactConsumerTestExt::class)
internal class BooleanTypeFailingTest {

    @Pact(provider = "serviceA", consumer = "serviceB")
    fun pact(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .given("foo")
            .uponReceiving("bar")
            .path("/v1/test")
            .method("POST")
            .body(
                LambdaDsl.newJsonBody {
                    it.array("array") { array ->
                        array.`object` { obj ->
                            obj.booleanType("boolean1", true)
                        }
                    }
                }.build()
            )
            .willRespondWith()
            .status(200)
            .toPact()
    }

    @Test
    @PactTestFor(providerName = "serviceA", port = "0", pactMethod = "pact")
    fun configDomain(mockServer: MockServer) {
        val httpResponse = Post(mockServer.getUrl() + "/v1/test")
            .bodyString("{\"array\": [{\"boolean1\":false}]}", ContentType.APPLICATION_JSON)
            .execute().returnResponse()
    }
}
