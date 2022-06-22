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

package com.amazon.customerService.controller;

import com.amazon.customerService.exception.CustomerNotFoundException;
import com.amazon.customerService.model.Customer;
import com.amazon.customerService.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @GetMapping("/customers")
    List<Customer> getCustomers() {
        return customerService.findAll();
    }

    @GetMapping("/customers/load")
    List<Customer> loadCustomers() {
        customerService.loadCustomerData(100);
        return customerService.findAll();
    }

    @PostMapping("/customers")
    Customer createCustomer(@RequestBody Customer customer) {
        return customerService.create(customer);
    }

    @GetMapping("/customers/{id}")
    Customer getCustomerbyId(@PathVariable String id) {
        return customerService.findById(id);
    }

    @PutMapping("/customers/{id}")
    Customer replaceCustomer(@RequestBody Customer newCustomer, @PathVariable String id) {
        Customer customer = customerService.findById(id);

        if (null == customer) {
            throw new CustomerNotFoundException(newCustomer.getId());
        }

        customer.setId(newCustomer.getId());
        customer.setRegDate(newCustomer.getRegDate());
        customer.setEmail(newCustomer.getEmail());
        customer.setAccountNumber(newCustomer.getAccountNumber());
        customer.setName(newCustomer.getName());

        customerService.update(customer);

        return customer;
    }

    @DeleteMapping("/customers/{id}")
    void deleteCustomerById(@PathVariable String id) {
        customerService.deleteById(id);
    }
}
