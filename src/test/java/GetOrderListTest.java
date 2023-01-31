import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetOrderListTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseURI.BASE_URI;
    }

    @Test
    @DisplayName("Send GET request and get list of orders")
    @Description("We send a GET request and expect to receive a non-empty structured list of orders in the response body (positive test)")
    public void sendGetRequestAndGetListOfOrders(){
        OrdersList ordersList=given().get("/api/v1/orders").body().as(OrdersList.class);
        MatcherAssert.assertThat(ordersList,notNullValue());
    }

    @Test
    @DisplayName("Send GET request and get status code 200")
    @Description("We send a GET request and expect to receive status code 200 (positive test)")
    public void sendGetRequestAndGetStatusCode200(){
        Response response=given().get("/api/v1/orders");
        response.then().statusCode(200);
    }
}
