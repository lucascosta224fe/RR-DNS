package com.rr_dns.rr_dns.controllers.auth;

import com.rr_dns.rr_dns.configs.TestConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

class AuthenticationProfileTest extends TestConfig {
    @Test
    @DisplayName("Deve retornar um json com informações do usuário autenticado")
    void deveRetornarProfileComSucesso() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get("/auth/profile")
                .then()
                .statusCode(200)
                .body("sessionId", notNullValue())
                .body("loginAt", notNullValue())
                .body("user.name", equalTo("User Test Auto"))
                .body("user.dataNascimento", equalTo("1919-11-18"))
                .body("user.descricao", equalTo("Registro automático para testes"));
    }

    @Test
    @DisplayName("Deve falhar ao não receber um token")
    void deveFalharSemToken() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/auth/profile")
                .then()
                .statusCode(500)
                .body("error", equalTo("Erro no processo de autenticação"));
    }

    @Test
    @DisplayName("Deve falhar ao receber um token inválido")
    void deveFalharComTokenInvalido() {
        given()
                .header("Authorization", "Bearer TOKEN_INVALIDO")
                .contentType(ContentType.JSON)
                .when()
                .get("/auth/profile")
                .then()
                .statusCode(500)
                .body("error", equalTo("Erro no processo de autenticação"));
    }
}