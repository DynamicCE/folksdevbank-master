package com.folksdevbank.service;

import com.folksdevbank.dto.AccountDto;
import com.folksdevbank.dto.AccountDtoConverter;
import com.folksdevbank.dto.CreateAccountRequest;
import com.folksdevbank.model.Account;
import com.folksdevbank.model.City;
import com.folksdevbank.model.Currency;
import com.folksdevbank.model.Customer;
import com.folksdevbank.repository.AccountRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.Assert.*;

public
class AccountServiceTest {

    private AccountService accountService;
    private  AccountRepository accountRepository;
    private  CustomerService customerService;
    private  AccountDtoConverter accountDtoConverter;

    private  DirectExchange exchange;

    private  AmqpTemplate rabbitTemplate;

    private  KafkaTemplate<String, String> kafkaTemplate;
    @Before
    public
    void setUp () throws Exception {
        accountRepository = Mockito.mock ( AccountRepository.class );
        customerService = Mockito.mock ( CustomerService.class );
        accountDtoConverter = Mockito.mock ( AccountDtoConverter.class );
        exchange = Mockito.mock ( DirectExchange.class );
        rabbitTemplate = Mockito.mock ( AmqpTemplate.class );
        kafkaTemplate = Mockito.mock ( KafkaTemplate.class );
        accountService = new AccountService ( accountRepository ,customerService,accountDtoConverter,exchange,rabbitTemplate,kafkaTemplate);

    }
    @Test
    public void whenCreateAccountCalledWithValidRequest_itShouldReturnValidAccountDto(){
        CreateAccountRequest createAccountRequest = new CreateAccountRequest ( "3215" );
        createAccountRequest.setCustomerId ( "3215" );
                createAccountRequest.setBalance ( 100.0 );
        createAccountRequest.setCity ( City.ANKARA );
                createAccountRequest.setCurrency ( Currency.EUR );


                Customer customer = Customer.builder ()
                        .id ( "3215" )
                        .address ( null )
                        .city ( City.ANKARA )
                        .dateOfBirth ( 2000 )
                        .name ( "Erkan" )
                        .build ();

        Account account = Account.builder()
                .id(createAccountRequest.getId())
                .balance(createAccountRequest.getBalance())
                .currency(createAccountRequest.getCurrency())
                .customerId(createAccountRequest.getCustomerId())
                .city(createAccountRequest.getCity())
                .build();

        AccountDto accountDto = AccountDto.builder ()
                .id ( "3215" )
                .customerId ( "3215" )
                        .balance ( 100.0 )
                                .currency ( Currency.EUR ).build ();

    Mockito.when ( customerService.getCustomerById ( "3215" ) ).thenReturn ( customer );
    Mockito.when ( accountRepository.save ( account ) ).thenReturn ( account );
    Mockito.when ( accountDtoConverter.convert ( account ) ).thenReturn ( new AccountDto () );

    AccountDto result = accountService.createAccount ( createAccountRequest );
        Assert.assertEquals ( result ,accountDto );
        Mockito.verify ( customerService.getCustomerById ( "12345" ) );
        Mockito.verify ( accountRepository.save ( account ) );
        Mockito.verify ( accountDtoConverter ).convert ( account );


    }
}