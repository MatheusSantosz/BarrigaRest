package br.ce.matheus.rest.tests;

import static org.hamcrest.Matchers.*;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.ce.matheus.rest.core.BaseTest;
import br.ce.matheus.rest.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {
		
	
	private static String CONTA_NAME = "Conta " + System.nanoTime();
	
	private static Integer CONTA_ID;
	private static Integer MOV_ID;
	
	@BeforeClass
	public static void login() {
		
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
	}
	
	
	
	@Test
	public void tus02_deveIncluirUmaContaComSucesso() {

		CONTA_ID = given()
			.body("{ \"nome\": \""+CONTA_NAME+"\" }")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
		
	}
	@Test
	public void tus03_deveAlterarAContaComSucesso() {
		
		given()
			.body("{ \"nome\": \""+CONTA_NAME+" alterada\" }")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is(CONTA_NAME+" alterada"))
		;
		
	}
	@Test
	public void tus04_naoDeveInserirContaComOMesmoNome() {
		
		given()
			.body("{ \"nome\": \""+CONTA_NAME+" alterada\" }")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
		
	}
	
	@Test
	public void tus05_deveInserirUmMovimentacaoComSucesso() {
		Movimentacao mov = getMovimentacaoValida();
		
		MOV_ID = given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
		
	}
	@Test
	public void tus06_deveValidarCamposObrigatoriosNaMovimentacao() {

		given()
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems(
					"Data da Movimentação é obrigatório", 
					"Data do pagamento é obrigatório", 
					"Descrição é obrigatório", 
					"Interessado é obrigatório",
					"Valor é obrigatório", 
					"Valor deve ser um número", 
					"Conta é obrigatório", 
					"Situação é obrigatório"
					))
				;
		
	}
	@Test
	public void tus07_naoDeveInserirDocumentacaoComDataFutura() {
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao(DataUtils.getDataDiferencaDias(2));

		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
			
				;
	}
	@Test
	public void tus08_naoDeveRemoverContaComMovimentacao() {

		given()
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
				;
	}
	
	@Test
	public void tus09_deveCalcularSaldoContas() {

		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00"))
				;
	}
	@Test
	public void tus10_deveRemoverUmaMovimentacao() {

		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204);
	}
	@Test
	public void tus11_naoDeveAcessarApiSemToken() {
		FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
		
	}
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(CONTA_ID);
		//mov.setUsuario_id(APP_PORT);
		mov.setDescricao("Descricao da Movimentacao");
		mov.setEnvolvido("Envolvido na mov");
		mov.setTipo("REC");
		mov.setData_transacao(DataUtils.getDataDiferencaDias(-1));
		mov.setData_pagamento(DataUtils.getDataDiferencaDias(5));
		mov.setValor(100f);
		mov.setStatus(true);
		return mov;
		
	}
}

