package com.bcp.workshop.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class AuthResourceTest {
    @Test
    void logsInWithWorkshopUserAndIssuesExpectedJwt() throws Exception {
        String token = given()
                .contentType("application/json")
                .body("{\"username\":\"student\",\"password\":\"Workshop123!\",\"client\":\"angular-workshop\"}")
                .when().post("/auth/login")
                .then()
                .statusCode(200)
                .body("user.username", is("student"))
                .body("expiresIn", is(3600))
                .extract().path("token");

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode header = mapper.readTree(Base64.getUrlDecoder().decode(parts[0]));
        JsonNode claims = mapper.readTree(Base64.getUrlDecoder().decode(parts[1]));
        assertEquals("RS256", header.path("alg").asText());
        assertEquals("student", claims.path("sub").asText());
        assertEquals("STUDENT", claims.path("groups").get(0).asText());
        assertEquals("workshop-auth", claims.path("iss").asText());
        assertEquals("workshop-requirements-api", claims.path("aud").asText());
        assertTrue(claims.path("exp").asLong() > Instant.now().getEpochSecond());
    }
}
