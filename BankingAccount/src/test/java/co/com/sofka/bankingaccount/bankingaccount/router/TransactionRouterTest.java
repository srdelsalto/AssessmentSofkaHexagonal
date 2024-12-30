package co.com.sofka.bankingaccount.bankingaccount.router;

import co.com.sofka.bankingaccount.bankingaccount.infrastructure.entryPoints.web.handlers.TransactionHandler;
import co.com.sofka.bankingaccount.bankingaccount.infrastructure.entryPoints.web.routers.TransactionRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TransactionRouterTest {

    @Mock
    private TransactionHandler transactionHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Crear instancia del router con el handler simulado
        TransactionRouter router = new TransactionRouter();
        webTestClient = WebTestClient.bindToRouterFunction(router.transactionRoutes(transactionHandler)).build();
    }

    @Test
    void testExecuteTransactionEndpoint() {
        // Simular el comportamiento del handler para devolver ServerResponse
        when(transactionHandler.executeTransaction(any()))
                .thenReturn(ServerResponse.ok().bodyValue("Transaction processed successfully"));

        // Ejecutar la solicitud HTTP
        webTestClient.post()
                .uri("/transaction")
                .bodyValue("{ \"amount\": 100, \"type\": \"ATM_DEPOSIT\", \"accountId\": \"12345\" }")
                .exchange()
                .expectStatus().isOk()  // Verifica que el estado sea 200
                .expectBody(String.class)
                .value(response -> {
                    // Validar el mensaje de respuesta
                    assert response.equals("Transaction processed successfully");
                });
    }

    @Test
    void testExecuteTransactionEndpointWithError() {
        // Simular el comportamiento del handler para devolver un error
        when(transactionHandler.executeTransaction(any()))
                .thenReturn(ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Transaction failed"));

        // Ejecutar la solicitud HTTP
        webTestClient.post()
                .uri("/transaction")
                .bodyValue("{ \"amount\": 100, \"type\": \"ATM_DEPOSIT\", \"accountId\": \"12345\" }")
                .exchange()
                .expectStatus().is5xxServerError()  // Verifica que el estado sea 500
                .expectBody(String.class)
                .value(errorMessage -> {
                    // Validar el mensaje de error
                    assert errorMessage.equals("Transaction failed");
                });
    }
}
