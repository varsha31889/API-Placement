package product_listing_Tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CheckoutTests {

	@BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://fakestoreapi.com";
    }

    @Test
    public void validateInsufficientBalance() {
        Response response = RestAssured
            .given()
            .body("{ \"paymentMethod\": \"card\", \"totalAmount\": 10000 }") // Assuming 10000 exceeds user balance
            .when()
            .post("/checkout")
            .then()
            .extract()
            .response();
        Assert.assertEquals(response.statusCode(), 402, "Expected insufficient balance error");
        Assert.assertEquals(response.jsonPath().getString("message"), "Insufficient balance");
    }

    @Test
    public void validateInvalidCouponCode() {
        Response response = RestAssured
            .given()
            .body("{ \"coupon\": \"INVALIDCODE\" }")
            .when()
            .post("/checkout")
            .then()
            .extract()
            .response();
        Assert.assertEquals(response.statusCode(), 400, "Expected bad request for invalid coupon");
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid coupon code");
    }

    @Test
    public void validateCartInventoryMismatch() {
        Response response = RestAssured
            .given()
            .body("{ \"cart\": [{ \"productId\": 1, \"quantity\": 50 }] }") // Assuming 50 exceeds stock
            .when()
            .post("/checkout")
            .then()
            .extract()
            .response();
        Assert.assertEquals(response.statusCode(), 409, "Expected conflict for cart-inventory mismatch");
        Assert.assertEquals(response.jsonPath().getString("message"), "Product unavailable in inventory");
    }
	
}
