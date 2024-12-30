package co.com.sofka.bankingaccount.bankingaccount.infrastructure.persistence.mapper;

import co.com.sofka.bankingaccount.bankingaccount.domain.model.Account;
import co.com.sofka.bankingaccount.bankingaccount.infrastructure.drivenAdapters.mongoRepository.entities.AccountEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-27T11:05:45-0500",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.11.1.jar, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountEntity toAccountEntity(Account account) {
        if ( account == null ) {
            return null;
        }

        AccountEntity.AccountEntityBuilder accountEntity = AccountEntity.builder();

        accountEntity.id( account.getId() );
        accountEntity.entitledUser( account.getEntitledUser() );
        accountEntity.balance( account.getBalance() );

        return accountEntity.build();
    }

    @Override
    public List<AccountEntity> toAccountEntityList(List<Account> accounts) {
        if ( accounts == null ) {
            return null;
        }

        List<AccountEntity> list = new ArrayList<AccountEntity>( accounts.size() );
        for ( Account account : accounts ) {
            list.add( toAccountEntity( account ) );
        }

        return list;
    }

    @Override
    public Account toAccount(AccountEntity accountEntity) {
        if ( accountEntity == null ) {
            return null;
        }

        Account.AccountBuilder account = Account.builder();

        account.id( accountEntity.getId() );
        account.entitledUser( accountEntity.getEntitledUser() );
        account.balance( accountEntity.getBalance() );

        return account.build();
    }
}
