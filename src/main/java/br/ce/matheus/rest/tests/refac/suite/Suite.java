package br.ce.matheus.rest.tests.refac.suite;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.matheus.rest.core.BaseTest;
import br.ce.matheus.rest.tests.refac.AuthTest;
import br.ce.matheus.rest.tests.refac.ContasTest;
import br.ce.matheus.rest.tests.refac.MovimentacaoTest;
import br.ce.matheus.rest.tests.refac.SaldoTest;
import io.restassured.RestAssured;

@RunWith(org.junit.runners.Suite.class)
@SuiteClasses({
	ContasTest.class,
	MovimentacaoTest.class,
	SaldoTest.class,
	AuthTest.class
})
public class Suite extends BaseTest {
	@BeforeClass
	public static void login() {
		System.out.println("Before Conta ");

		Map<String, String> login = new HashMap<>();
		login.put("email", "matheus13@gmail.com");
		login.put("senha", "123456");

		String TOKEN =	given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.body("nome", is("matheus"))
			.extract().path("token");
		
		RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
		RestAssured.get("/reset").then().statusCode(200);
	}
}
