package co.com.sofka.bankingaccount.bankingaccount.factory;

import co.com.sofka.bankingaccount.bankingaccount.domain.factory.TransactionFactory;
import co.com.sofka.bankingaccount.bankingaccount.infrastructure.drivenAdapters.mongoRepository.entities.transaction.TransactionEntity;
import co.com.sofka.bankingaccount.bankingaccount.infrastructure.drivenAdapters.mongoRepository.entities.transaction.impl.AtmDepositEntity;
import co.com.sofka.bankingaccount.bankingaccount.infrastructure.drivenAdapters.mongoRepository.entities.transaction.impl.AtmWithdrawalEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFactoryTest {

    private final TransactionFactory transactionFactory = new TransactionFactory();

    @Test
    void testCreateAtmDepositTransaction() {
        TransactionEntity transaction = transactionFactory.createTransaction("ATM_DEPOSIT", BigDecimal.valueOf(100), "12345");
        assertNotNull(transaction);
        assertTrue(transaction instanceof AtmDepositEntity);
        assertEquals("ATM_DEPOSIT", transaction.getType());
        assertEquals(BigDecimal.valueOf(100), transaction.getAmount());
    }

    @Test
    void testCreateAtmWithdrawalTransaction() {
        TransactionEntity transaction = transactionFactory.createTransaction("ATM_WITHDRAWAL", BigDecimal.valueOf(50), "12345");
        assertNotNull(transaction);
        assertTrue(transaction instanceof AtmWithdrawalEntity);
        assertEquals("ATM_WITHDRAWAL", transaction.getType());
        assertEquals(BigDecimal.valueOf(50), transaction.getAmount());
    }

    @Test
    void testInvalidTransactionType() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                transactionFactory.createTransaction("INVALID_TYPE", BigDecimal.valueOf(100), "12345")
        );
        assertEquals("Transaction type not supported: INVALID_TYPE", exception.getMessage());
    }
}
