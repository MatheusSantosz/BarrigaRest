package br.ce.matheus.rest.tests.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.ce.matheus.rest.core.BaseTest;
import br.ce.matheus.rest.tests.Movimentacao;
import br.ce.matheus.rest.utils.BarrigaUtils;
import br.ce.matheus.rest.utils.DataUtils;

public class ContasTest extends BaseTest	{
	
	@Test
	public void deveIncluirUmaContaComSucesso() {
		System.out.println("Incluir");
		 given()
			.body("{ \"nome\": \"Conta Inserida\" }")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
		
	}
	@Test
	public void deveAlterarAContaComSucesso() {
		System.out.println("Alterar");
		Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para alterar");
		given()
			.body("{ \"nome\": \"Conta Alterada\" }")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is("Conta Alterada"))
		;
		
	}
	
	
	@Test
	public void deveValidarCamposObrigatoriosNaMovimentacao() {

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
	
	
	
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"));
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
