package com.codebank.accounts.service;

import com.codebank.accounts.dto.CustomerDto;

public interface IAccountsService {

    /**
     *
     * @param customerDto
     */
    void createAccount(CustomerDto customerDto);
}