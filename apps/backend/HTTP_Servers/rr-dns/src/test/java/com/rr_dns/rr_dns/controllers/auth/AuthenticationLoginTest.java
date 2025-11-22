package com.rr_dns.rr_dns.controllers.auth;

import com.rr_dns.rr_dns.configs.TestConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;


import static io.restassured.RestAssured.given;

class AuthenticationLoginTest extends TestConfig {

    @Test
    @DisplayName("Deve fazer login com sucesso e retornar token")
    void deveLogarComSucesso() {

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(testEmail, testPassword))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    @DisplayName("Deve falhar ao tentar login com email inexistente")
    void deveFalharEmailNaoEncontrado() {

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "email_inexistente_123@teste.com",
                            "password": "%s"
                        }
                        """.formatted(testPassword))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(404)
                .body("code", equalTo(404))
                .body("status", equalTo("NOT_FOUND"));
    }

    @Test
    @DisplayName("Deve falhar ao tentar logar com senha incorreta")
    void deveFalharSenhaIncorreta() {

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "%s",
                            "password": "senha_errada"
                        }
                        """.formatted(testEmail))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Deve falhar ao enviar email com formato inválido")
    void deveFalharEmailInvalido() {

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "email_invalido",
                            "password": "%s"
                        }
                        """.formatted(testPassword))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("status", equalTo("UNAUTHORIZED"))
                .body("errors[0]", equalTo("Email está em um formato inválido!"));
    }

    @Test
    @DisplayName("Deve falhar ao enviar campo 'email' vazios")
    void deveFalharEmailVazio() {

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "",
                            "password": "%s"
                        }
                        """.formatted(testPassword))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(400)
                .body("code", equalTo(400))
                .body("status", equalTo("BAD_REQUEST"))
                .body("errors[0]", equalTo("Email vazio."));
    }

    @Test
    @DisplayName("Deve falhar ao enviar campo 'password' vazios")
    void deveFalharPasswordVazio() {

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "email": "%s",
                            "password": ""
                        }
                        """.formatted(testEmail))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(400)
                .body("code", equalTo(400))
                .body("status", equalTo("BAD_REQUEST"))
                .body("errors[0]", equalTo("A senha não pode ser vazia."));
    }
}