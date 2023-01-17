/*
 * Copyright 2010-2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazon.customerService.service;

import com.amazon.customerService.model.Customer;
import com.amazon.customerService.repository.CustomerRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findById(String id) {
        return customerRepository.findById(id);
    }

    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer update(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteById(String id) {
        customerRepository.deleteById(id);
    }

    public void loadCustomerData(int count) {

        for (int i = 0; i < count; i++) {
            String generatedString = RandomStringUtils.randomAlphabetic(10);

            Customer cust = new Customer();
            cust.setName(generatedString);
            cust.setId(UUID.randomUUID().toString());
            cust.setRegDate(Date.from(Instant.now()));
            cust.setEmail(generatedString + "@test.com");

            Random rnd = new Random();
            int number = rnd.nextInt(999999);

            cust.setAccountNumber(String.format("%06d", number));

            customerRepository.save(cust);
        }
    }
}
