package com.codebank.accounts.service.impl;

import com.codebank.accounts.constants.AccountsConstants;
import com.codebank.accounts.dto.AccountsDto;
import com.codebank.accounts.dto.CustomerDto;
import com.codebank.accounts.entity.Accounts;
import com.codebank.accounts.entity.Customer;
import com.codebank.accounts.exceptions.CustomerAlreadyExistsException;
import com.codebank.accounts.exceptions.ResourceNotFoundException;
import com.codebank.accounts.mapper.AccountsMapper;
import com.codebank.accounts.mapper.CustomerMapper;
import com.codebank.accounts.repository.AccountsRepository;
import com.codebank.accounts.repository.CustomerRepository;
import com.codebank.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountsRepository accountsRepository;
    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer newCustomer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> customerMobileNumberCheck = customerRepository.findByMobileNumber(newCustomer.getMobileNumber());
        if (customerMobileNumberCheck.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobileNumber "
                    +customerDto.getMobileNumber());
        }
        newCustomer.setCreatedAt(LocalDateTime.now());
        newCustomer.setCreatedBy("Pruthvi Sambu");
        customerRepository.save(newCustomer);
        accountsRepository.save(createNewAccount(newCustomer));
    }


    /**
     * @param customer - Customer Object
     * @return the new account details
     */
    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        newAccount.setCreatedAt(LocalDateTime.now());
        newAccount.setCreatedBy("Pruthvi Sambu");
        return newAccount;
    }


    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("accounts", "customerId", customer.getCustomerId().toString())
        );
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
        return customerDto;
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdate = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();
        if (accountsDto != null) {
            Accounts account = accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "Account Number", accountsDto.getAccountNumber().toString())
            );
            AccountsMapper.mapToAccounts(accountsDto, account);
            accountsRepository.save(account);
            Long customerId = account.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "Customer Id", customerId.toString())
            );

            CustomerMapper.mapToCustomer(customerDto, customer);
            customerRepository.save(customer);
            isUpdate = true;
        }
        return isUpdate;
    }
}
