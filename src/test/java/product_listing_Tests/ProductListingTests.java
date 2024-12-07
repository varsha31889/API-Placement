package product_listing_Tests;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ProductListingTests {
	
	 @BeforeClass
	    public void setup() {
	        RestAssured.baseURI = "https://fakestoreapi.com";
	    }

	
	 @Test
	    public void validatePagination() {
	        Response response = RestAssured.get("/products?page=1&limit=5");
	        Assert.assertEquals(response.statusCode(), 200, "Status code mismatch");
	        Assert.assertTrue(response.jsonPath().getList("$").size() <= 5, "Number of products exceeds limit");
	    }

	    @Test
	    public void validateSorting() {
	        Response response = RestAssured.get("/products?sort=price&order=asc");
	        List<Double> prices = response.jsonPath().getList("price", Double.class);
	        for (int i = 1; i < prices.size(); i++) {
	            Assert.assertTrue(prices.get(i) >= prices.get(i - 1), "Products are not sorted by price");
	        }
	    }

	    @Test
	    public void validateEmptyCategory() {
	        Response response = RestAssured.get("/products?category=nonexistent");
	        Assert.assertEquals(response.statusCode(), 200, "Status code mismatch");
	        Assert.assertTrue(response.jsonPath().getList("$").isEmpty(), "Expected no products for empty category");
	    }
	
	
	
}

