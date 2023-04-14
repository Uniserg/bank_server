package com.serguni.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serguni.clients.GraphDriver;
import com.serguni.clients.KeycloakAdminClient;
import com.serguni.models.*;
import com.serguni.repositories.MyBankRepository;
import com.serguni.vars.MyBankVars;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;


@QuarkusTest
class IntegrationTest {

    @Inject
    KeycloakAdminClient keycloakAdminClient;

    @Inject
    ObjectMapper om;

    @Inject
    GraphDriver gd;

    @Inject
    MyBankRepository myBankRepository;

    String adminAccessToken;

    String testUserAccessToken;

    Product createProduct() throws JsonProcessingException {
        Product product = new Product();
        product.setName("Test Debit Card");
        product.setDescription("Test Debit Card");
        product.setRate(60);
        product.setPeriod((byte) 4);

        System.out.println(given()
                .when().contentType(ContentType.JSON)
                .auth().oauth2(adminAccessToken)
                .body(om.writeValueAsString(product))
                .post("/products")
                .then()
                .extract().body().asString());

        return om.readValue(
                given()
                        .when().contentType(ContentType.JSON)
                        .auth().oauth2(adminAccessToken)
                        .body(om.writeValueAsString(product))
                        .post("/products")
                        .then()
                        .extract().body().asString(),
                Product.class);
    }

    void getAllProducts() {
        given()
                .when()
                .get("/products")
                .then()
                .statusCode(200);
    }

    Individual register() throws JsonProcessingException {
        RegistrationForm registrationForm = RegistrationForm.builder()
                .email("test@mail.ru")
                .lastName("TEST")
                .firstName("TEST")
                .middleName("TEST")
                .inn("9829219292")
                .passport("4929393930")
                .phoneNumber("+7 (977) 272-91-75")
                .login("test")
                .password("test")
                .build();

        return om
                .readValue(
                        given()
                                .when()
                                .contentType(ContentType.JSON)
                                .body(om.writeValueAsString(registrationForm))
                                .post("/individuals/register")
                                .then()
                                .extract().body().asString(),
                        Individual.class);
    }

    ProductOrder createProductOrder(String productName) throws JsonProcessingException {
        ProductOrder productOrder = new ProductOrder();
        productOrder.setProductName(productName);
        productOrder.setAddress("TEST ADDRESS");
        productOrder.setScheduledDate(new Date());

        return om.readValue(
                given()
                .when()
                .auth().oauth2(testUserAccessToken)
                        .contentType(ContentType.JSON)
                .body(om.writeValueAsString(productOrder))
                .post("/product_orders")
                .then()
                .extract().body().asString(), ProductOrder.class);
    }

    void getProductOrders() {
        given()
                .when()
                .auth().oauth2(testUserAccessToken)
                .get("/individuals/me/product_orders")
                .then()
                .statusCode(200);
    }

    void confirm(long productOrderId) {
        given()
                .when()
                .auth().oauth2(adminAccessToken)
                .post("/product_orders/" + productOrderId + "/confirm")
                .then()
                .statusCode(200);
    }

    void complete(long productOrderId) {
        given()
                .when()
                .auth().oauth2(adminAccessToken)
                .post("/product_orders/" + productOrderId + "/complete")
                .then()
                .statusCode(200);
    }

    void refuse(long productOrderId) {
        given()
                .when()
                .auth().oauth2(adminAccessToken)
                .post("/product_orders/" + productOrderId + "/refuse")
                .then()
                .statusCode(200);
    }

    List<DebitCard> getDebitCards() throws JsonProcessingException {
        return om.readValue(
                given()
                        .when()
                        .auth().oauth2(testUserAccessToken)
                        .get("/individuals/me/debit_cards")
                        .then()
                        .extract().body().asString(),
                new TypeReference<>() {}
                );
    }

    void getAccountRequisites(String cardNumber) {
        given()
                .when()
                .auth().oauth2(testUserAccessToken)
                .get("/individuals/me/debit_cards/" + cardNumber + "/account_requisites")
                .then()
                .statusCode(200);
    }

    void getProfileByPhoneNumber(String phoneNumber) {
        given()
                .when()
                .auth().oauth2(testUserAccessToken)
                .get("/individuals/search/phone/" + phoneNumber)
                .then()
                .statusCode(200);
    }

    void getProfileByCardNumber(String cardNumber) {
        given()
                .when()
                .auth().oauth2(testUserAccessToken)
                .get("/individuals/search/card_number/" + cardNumber)
                .then()
                .statusCode(200);
    }

    List<String> getAllCardNumbersByUserSub(String userSub) throws JsonProcessingException {
        return om.readValue(
                given()
                .when()
                .auth().oauth2(testUserAccessToken)
                .get("/debit_cards/search/" + userSub)
                .then()
                        .extract().body().asString(),
                new TypeReference<>() {});
    }

    void createTransfer(String cardNumberFrom, String  cardNumberTo) throws JsonProcessingException {

        Transfer transfer = new Transfer();
        transfer.setCardNumberFrom(cardNumberFrom);
        transfer.setCardNumberTo(cardNumberTo);
        transfer.setAmount(5);
        transfer.setMessage("Test");

        given()
                .when()
                .auth().oauth2(testUserAccessToken)
                .contentType(ContentType.JSON)
                .body(om.writeValueAsString(transfer))
                .post("/debit_cards/transfer")
                .then()
                .statusCode(200);
    }

    void getAllTransfers(String cardNumber) throws JsonProcessingException {
        given()
                .when()
                .auth().oauth2(testUserAccessToken)
                .contentType(ContentType.JSON)
                .body(om.writeValueAsString(cardNumber))
                .post("/debit_cards/" + cardNumber + "/account_operations")
                .then()
                .statusCode(200);
    }

    @Test
    void test() throws JsonProcessingException {

        MyBank myBank = new MyBank();

        myBank.setBik(MyBankVars.MY_BANK_BIK.toString());
        myBank.setName("My bank");
        myBank.setKpp("302102");
        myBank.setCorrespondAccount("091831903813902379");
        myBank.setAccountsCount(0);

        myBankRepository.create(myBank);

        adminAccessToken = keycloakAdminClient.getAccessToken("admin", "admin");
        Product product = createProduct();
        getAllProducts();
        Individual individual = register();
        testUserAccessToken = keycloakAdminClient.getAccessToken("test", "test");
        ProductOrder productOrderIdForComplete1 = createProductOrder(product.getName());
        ProductOrder productOrderIdForComplete2 = createProductOrder(product.getName());
        ProductOrder productOrderIdForRefuse = createProductOrder(product.getName());
        getProductOrders();

        confirm(productOrderIdForComplete1.getId());
        complete(productOrderIdForComplete1.getId());

        confirm(productOrderIdForComplete2.getId());
        complete(productOrderIdForComplete2.getId());

        confirm(productOrderIdForRefuse.getId());
        refuse(productOrderIdForRefuse.getId());

        List<DebitCard> cards = getDebitCards();
        getAccountRequisites(cards.get(0).getNumber());
        getProfileByPhoneNumber(individual.getPhoneNumber());
        getProfileByCardNumber(cards.get(0).getNumber());
        List<String> cardNumbers = getAllCardNumbersByUserSub(individual.getSub());


        gd
                .g().V()
                .has(DebitCard.class.getSimpleName(), "number", cards.get(0).getNumber())
                .out("MANAGES")
                .property("balance", 100)
                .elementMap().next();

        createTransfer(cardNumbers.get(0), cardNumbers.get(1));
        getAllTransfers(cardNumbers.get(0));
    }
}