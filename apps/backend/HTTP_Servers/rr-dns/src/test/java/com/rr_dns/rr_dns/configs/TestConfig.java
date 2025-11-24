package com.rr_dns.rr_dns.configs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestConfig {

    public String token;
    public String testEmail;
    public String testPassword = "GabrielTeste@18111995";

    @BeforeAll
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Gera e-mail dinâmico para evitar conflito 409
        testEmail = "user_test_" + UUID.randomUUID() + "@exoo.com";

        // ----- 1. REGISTRO -----
        Map<String, Object> bodyRegister = new HashMap<>();
        bodyRegister.put("nome", "User Test Auto");
        bodyRegister.put("email", testEmail);
        bodyRegister.put("password", testPassword);
        bodyRegister.put("dataNascimento", "18-11-1919");
        bodyRegister.put("descricao", "Registro automático para testes");

        given()
                .contentType(ContentType.JSON)
                .body(bodyRegister)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(201);


        // ----- 2. LOGIN -----
        Map<String, String> bodyLogin = new HashMap<>();
        bodyLogin.put("email", testEmail);
        bodyLogin.put("password", testPassword);

        token =
                given()
                        .contentType(ContentType.JSON)
                        .body(bodyLogin)
                        .when()
                        .post("auth/login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("token");
    }
}
