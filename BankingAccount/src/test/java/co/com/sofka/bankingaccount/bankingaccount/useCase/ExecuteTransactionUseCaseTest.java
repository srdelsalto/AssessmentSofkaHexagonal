package co.com.sofka.bankingaccount.bankingaccount.useCase;

import co.com.sofka.bankingaccount.bankingaccount.application.dto.TransactionDTO;
import co.com.sofka.bankingaccount.bankingaccount.domain.model.AtmDeposit;
import co.com.sofka.bankingaccount.bankingaccount.domain.model.Transaction;
import co.com.sofka.bankingaccount.bankingaccount.domain.useCases.ExecuteTransactionUseCase;
import co.com.sofka.bankingaccount.bankingaccount.domain.useCases.gateway.TransactionRepository;
import co.com.sofka.bankingaccount.bankingaccount.infrastructure.drivenAdapters.mongoRepository.entities.transaction.TransactionEntity;
import co.com.sofka.bankingaccount.bankingaccount.infrastructure.drivenAdapters.mongoRepository.entities.transaction.impl.AtmDepositEntity;
import co.com.sofka.bankingaccount.bankingaccount.infrastructure.persistence.mapper.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

public class ExecuteTransactionUseCaseTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private ExecuteTransactionUseCase executeTransactionUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteTransactionSuccess() {
        // Datos de prueba
        AtmDepositEntity transactionEntity = new AtmDepositEntity(BigDecimal.valueOf(100), "12345" );
        transactionEntity.setCost(BigDecimal.valueOf(2));

        AtmDeposit transactionModel = new AtmDeposit(BigDecimal.valueOf(100), "12345");
        transactionModel.setCost(BigDecimal.valueOf(2));

        TransactionDTO expectedDTO = new TransactionDTO("12345", "ATM_DEPOSIT", BigDecimal.valueOf(100), BigDecimal.valueOf(2));

        // Mock del mapper y repositorio
        when(transactionMapper.toModel(transactionEntity)).thenReturn(transactionModel);
        when(transactionRepository.createTransaction(transactionModel)).thenReturn(Mono.just(transactionModel));

        // Ejecución
        Mono<TransactionDTO> result = executeTransactionUseCase.execute(transactionEntity);

        // Verificación
        StepVerifier.create(result)
                .expectNextMatches(dto ->
                                dto.getType().equals(expectedDTO.getType()) &&
                                dto.getAmount().compareTo(expectedDTO.getAmount()) == 0 &&
                                dto.getCost().compareTo(expectedDTO.getCost()) == 0
                )
                .verifyComplete();
    }

    @Test
    void testExecuteTransactionRepositoryError() {
        // Datos de prueba
        AtmDepositEntity transactionEntity = new AtmDepositEntity(BigDecimal.valueOf(100), "12345");
        transactionEntity.setCost(BigDecimal.valueOf(2));

        AtmDeposit transactionModel = new AtmDeposit(BigDecimal.valueOf(100) ,"12345");
        transactionModel.setCost(BigDecimal.valueOf(2));

        // Mock del mapper y repositorio con error
        when(transactionMapper.toModel(transactionEntity)).thenReturn(transactionModel);
        when(transactionRepository.createTransaction(transactionModel)).thenReturn(Mono.error(new RuntimeException("Repository error")));

        // Ejecución
        Mono<TransactionDTO> result = executeTransactionUseCase.execute(transactionEntity);

        // Verificación
        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof RuntimeException &&
                        error.getMessage().equals("Repository error"))
                .verify();
    }
}
