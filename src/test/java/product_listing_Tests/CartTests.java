package product_listing_Tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CartTests {

	@BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://fakestoreapi.com";
    }

    @Test
    public void validateOutOfStockProduct() {
        Response response = RestAssured
            .given()
            .body("{ \"productId\": 0, \"quantity\": 1 }") 
            .when()
            .post("/carts")
            .then()
            .extract()
            .response();
        Assert.assertEquals(response.statusCode(), 409, "Expected conflict for out-of-stock product");
        Assert.assertEquals(response.jsonPath().getString("message"), "Product out of stock");
    }

    @Test
    public void validateDuplicateProductAddition() {
        RestAssured.given().body("{ \"productId\": 1, \"quantity\": 1 }").post("/carts"); 
        Response response = RestAssured.given().body("{ \"productId\": 1, \"quantity\": 1 }").post("/carts");
        Assert.assertEquals(response.statusCode(), 200, "Unexpected response code for duplicate product addition");
        Assert.assertEquals(response.jsonPath().getInt("cart[0].quantity"), 2, "Quantity mismatch for duplicate addition");
    }

    @Test
    public void validateInvalidProduct() {
        Response response = RestAssured
            .given()
            .body("{ \"productId\": \"invalid\", \"quantity\": 1 }")
            .when()
            .post("/carts")
            .then()
            .extract()
            .response();
        Assert.assertEquals(response.statusCode(), 400, "Expected bad request for invalid product ID");
        Assert.assertEquals(response.jsonPath().getString("message"), "Invalid product ID");
    }
	
	
}
