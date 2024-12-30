package co.com.sofka.bankingaccount.bankingaccount.handler;

import co.com.sofka.bankingaccount.bankingaccount.application.dto.TransactionDTO;
import co.com.sofka.bankingaccount.bankingaccount.application.dto.request.TransactionRequestDTO;
import co.com.sofka.bankingaccount.bankingaccount.domain.factory.TransactionFactory;
import co.com.sofka.bankingaccount.bankingaccount.domain.useCases.ExecuteTransactionUseCase;
import co.com.sofka.bankingaccount.bankingaccount.infrastructure.drivenAdapters.mongoRepository.entities.transaction.impl.AtmDepositEntity;
import co.com.sofka.bankingaccount.bankingaccount.infrastructure.entryPoints.web.handlers.TransactionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionHandlerTest {
    @Mock
    private TransactionFactory transactionFactory;

    @Mock
    private ExecuteTransactionUseCase executeTransactionUseCase;

    private TransactionHandler transactionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionHandler = new TransactionHandler(transactionFactory, executeTransactionUseCase);
    }

    @Test
    void testExecuteTransactionSuccess() {
        // Datos de prueba
        TransactionRequestDTO requestDTO = new TransactionRequestDTO("12345","ATM_DEPOSIT", BigDecimal.valueOf(100));
        AtmDepositEntity transactionEntity = new AtmDepositEntity( BigDecimal.valueOf(100),"12345");
        transactionEntity.setCost(BigDecimal.valueOf(2));

        TransactionDTO responseDTO = new TransactionDTO("12345", "ATM_DEPOSIT", BigDecimal.valueOf(100), BigDecimal.valueOf(2));

        // Mock del ServerRequest
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(TransactionRequestDTO.class)).thenReturn(Mono.just(requestDTO));

        // Mock de la fábrica y el caso de uso
        when(transactionFactory.createTransaction("ATM_DEPOSIT", BigDecimal.valueOf(100), "12345"))
                .thenReturn(transactionEntity);
        when(executeTransactionUseCase.execute(transactionEntity)).thenReturn(Mono.just(responseDTO));

        // Ejecución
        Mono<ServerResponse> result = transactionHandler.executeTransaction(serverRequest);

        // Verificación
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    assert response.statusCode().equals(HttpStatus.CREATED);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testExecuteTransactionError() {
        // Datos de prueba
        TransactionRequestDTO requestDTO = new TransactionRequestDTO("12345", "ATM_DEPOSIT", BigDecimal.valueOf(100) );

        // Mock del ServerRequest
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(TransactionRequestDTO.class)).thenReturn(Mono.just(requestDTO));

        // Mock de la fábrica y el caso de uso con error
        when(transactionFactory.createTransaction("ATM_DEPOSIT", BigDecimal.valueOf(100), "12345"))
                .thenThrow(new IllegalArgumentException("Invalid transaction type"));

        // Ejecución
        Mono<ServerResponse> result = transactionHandler.executeTransaction(serverRequest);

        // Verificación
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    assert response.statusCode().equals(HttpStatus.BAD_REQUEST);
                    return true;
                })
                .verifyComplete();
    }
}
