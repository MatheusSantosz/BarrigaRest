package br.ce.matheus.rest.tests.refac;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.ce.matheus.rest.core.BaseTest;

import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AuthTest extends BaseTest	{
	
	
	@Test
	public void naoDeveAcessarApiSemToken() {
		FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
		
	}
	
}
