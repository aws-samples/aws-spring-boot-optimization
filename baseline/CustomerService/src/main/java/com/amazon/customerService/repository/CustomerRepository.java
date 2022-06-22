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

package com.amazon.customerService.repository;

import com.amazon.customerService.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class CustomerRepository {

    private DynamoDbTable<Customer> table;

    @Autowired
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public Customer save(final Customer customer) {
        log.debug("Storing customer -> " + customer);
        DynamoDbTable<Customer> customerTable = getTable();
        customerTable.putItem(customer);

        return customer;
    }

    public Customer findById(final String id) {

        log.debug("Find customer with id: " + id);
        Key key = Key.builder().partitionValue(id).build();

        DynamoDbTable<Customer> customerTable = getTable();
        return customerTable.getItem(key);
    }

    public List<Customer> findAll() {

        log.debug("Find all customers");
        List<Customer> customerList = new ArrayList<>();

        DynamoDbTable<Customer> customerTable = getTable();
        customerTable.scan().items().forEach(customerList::add);

        log.debug("Found customers: ");
        for (Customer customer: customerList
             ) {
            log.debug("  -> " + customer);
        }

        return customerList;
    }

    public void deleteById(String id) {

        log.debug("Delete customer with id: " + id);
        DynamoDbTable<Customer> customerTable = getTable();

        Key key = Key.builder().partitionValue(id).build();

        customerTable.deleteItem(key);
    }

    private DynamoDbTable<Customer> getTable() {

        if (table == null) {
            table = dynamoDbEnhancedClient.table("Customer",
                    TableSchema.fromBean(Customer.class));
        }

        return table;
    }
}
