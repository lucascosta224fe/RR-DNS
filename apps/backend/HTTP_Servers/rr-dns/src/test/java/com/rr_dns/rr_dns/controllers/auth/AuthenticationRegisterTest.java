package com.rr_dns.rr_dns.controllers.auth;

import com.rr_dns.rr_dns.configs.TestConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class AuthenticationRegisterTest extends TestConfig {

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void deveRegistrarNovoUsuarioComSucesso() {
        String email = "teste_registro_" + UUID.randomUUID() + "@exoo.com";

        Map<String, Object> body = new HashMap<>();
        body.put("nome", "Cadastro Novo");
        body.put("email", email);
        body.put("password", "SenhaForte123!");
        body.put("dataNascimento", "18-11-1919");
        body.put("descricao", "Teste de registro isolado");

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar um registro com email já existente")
    void deveFalharAoRegistrarEmailDuplicado() {
        Map<String, Object> body = new HashMap<>();
        body.put("nome", "User Test Auto");
        body.put("email", testEmail);
        body.put("password", testPassword);
        body.put("dataNascimento", "18-11-1919");
        body.put("descricao", "Registro automático para testes");

        given()

                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/auth/register")
                .then()

                .statusCode(409)
                .body("code", equalTo(409))
                .body("status", equalTo("CONFLICT"))
                .body("errors[0]", equalTo("Este e-mail já está cadastrado."));
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar um registro com email vazio")
    void deveFalharAoRegistrarEmailFazio() {
        Map<String, Object> body = new HashMap<>();
        body.put("nome", "User Test Auto");
        body.put("email", ""); // EMAIL VAZIO
        body.put("password", testPassword);
        body.put("dataNascimento", "18-11-1919");
        body.put("descricao", "Registro automático para testes");

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo(400))
                .body("status", equalTo("BAD_REQUEST"))
                .body("errors[0]", equalTo("email: não deve estar em branco") );
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar um registro com um email inválido")
    void deveFalharAoRegistrarEmailInvalido() {
        Map<String, Object> body = new HashMap<>();
        body.put("nome", "User Test Auto");
        body.put("email", "user_test@com"); // EMAIL INVÁLIDO
        body.put("password", testPassword);
        body.put("dataNascimento", "18-11-1919");
        body.put("descricao", "Registro automático para testes");

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo(400))
                .body("status", equalTo("BAD_REQUEST"))
                .body("errors[0]", equalTo("email: Email inválido!"));
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar um registro com o campo nome vazio")
    void deveFalharAoRegistrarNomeVazio() {
        String email = "teste_registro_" + UUID.randomUUID() + "@exoo.com";

        Map<String, Object> body = new HashMap<>();
        body.put("nome", "");
        body.put("email", email);
        body.put("password", "SenhaForte123!");
        body.put("dataNascimento", "18-11-1919");
        body.put("descricao", "Teste de registro isolado");

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo(400))
                .body("status", equalTo("BAD_REQUEST"))
                .body("errors[0]", equalTo("nome: não deve estar em branco"));
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar um registro com data posterior a 2025")
    void deveFalharAoRegistrarComDataFutura() {
        String email = "teste_registro_" + UUID.randomUUID() + "@exoo.com";

        Map<String, Object> body = new HashMap<>();
        body.put("nome", "Cadastro Novo");
        body.put("email", email);
        body.put("password", "SenhaForte123!");
        body.put("dataNascimento", "18-11-2026");
        body.put("descricao", "Teste de registro isolado");

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo(400))
                .body("status", equalTo("BAD_REQUEST"))
                .body("errors[0]", equalTo("dataNascimento: A data de nascimento deve ser no passado"));
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar um registro com idade superior a 114 anos")
    void deveFalharAoRegistrarComDataAcimaDe114Anos() {
        String email = "teste_registro_" + UUID.randomUUID() + "@exoo.com";

        Map<String, Object> body = new HashMap<>();
        body.put("nome", "Cadastro Novo");
        body.put("email", email);
        body.put("password", "SenhaForte123!");
        body.put("dataNascimento", "18-11-1909");
        body.put("descricao", "Teste de registro isolado");

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(409)
                .body("code", equalTo(409))
                .body("status", equalTo("CONFLICT"))
                .body("errors[0]", equalTo("Data de nascimento inválida!"));
    }

}