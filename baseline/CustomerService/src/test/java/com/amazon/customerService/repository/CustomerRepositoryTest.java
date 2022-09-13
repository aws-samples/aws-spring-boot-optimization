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
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("local")
@TestPropertySource(properties = {
        "amazon.dynamodb.endpoint=http://localhost:8000/"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-local.properties")
public class CustomerRepositoryTest {

    @Autowired
    CustomerRepository repository;
    @Autowired
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private DynamoDbTable table;
    private Customer testCustomer;

    @BeforeAll
    public void setup() throws Exception {

        // Create the table locally

        table = dynamoDbEnhancedClient.table("Customer",
                TableSchema.fromBean(Customer.class));

        testCustomer = createCustomer();

        try {
            table.describeTable();
        } catch (software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException exc) {
            // Table doesn't exist
            table.createTable();
        }

        // Find and delete all customer entries
        List<Customer> customerList = repository.findAll();
        System.out.println("Customerlist: " + customerList);
        for (Customer customer : customerList) {
            System.out.println("Customer: " + customer);
            repository.deleteById(customer.getId());
        }
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setAccountNumber("111111");
        customer.setEmail("test@test.com");
        customer.setRegDate(Instant.now());
        customer.setName("John Doe");

        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();

        customer.setId(uuidAsString);

        return customer;
    }

    @Test
    @Order(1)
    public void testCreate() {
        System.out.println("Adding customer: " + testCustomer);

        Customer savedCustomer = repository.save(testCustomer);
        Customer readCustomer = repository.findById(savedCustomer.getId());

        Assert.assertEquals(savedCustomer, readCustomer);
    }

    @Test
    @Order(2)
    public void testRead() {
        String customerId = testCustomer.getId();

        Customer tmpCustomer = repository.findById(customerId);
        Assert.assertEquals(testCustomer, tmpCustomer);
    }

    @Test
    @Order(3)
    public void testUpdate() {

        testCustomer.setName("NewName");
        repository.save(testCustomer);

        Customer tmpCustomer = repository.findById(testCustomer.getId());
        Assert.assertEquals(testCustomer.getName(), tmpCustomer.getName());
    }

    @Test
    @Order(4)
    public void testDelete() {

        repository.deleteById(testCustomer.getId());

        List<Customer> customerList = repository.findAll();
        Assert.assertEquals(0, customerList.size());
    }

    @AfterAll
    public void tearDown() throws Exception {
        table.deleteTable();
    }

}
