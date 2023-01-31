import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class MakeOrderTest {
    private final String deliveryDate;
    private final List<String> color;
    String firstName;
    String lastName;
    String address;
    String metroStation;
    String phone;
    int rentTime;
    String comment;

    public MakeOrderTest(String deliveryDate, List<String> color){
        this.deliveryDate=deliveryDate;
        this.color=color;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData(){
        return new Object[][] {
                {"2023-02-26",List.of("")},
                {"2023-01-25",List.of("BLACK")},
                {"2023-06-06",List.of("GREY")},
                {"2020-06-06",List.of("BLACK","GREY")},
        };
    }

    @Before
    public void setUp(){
        RestAssured.baseURI=BaseURI.BASE_URI;
        Faker fakerLocale = new Faker(new Locale("ru"));
        firstName=fakerLocale.name().firstName();
        lastName=fakerLocale.name().lastName();
        address=fakerLocale.address().fullAddress();
        metroStation=fakerLocale.address().streetName();
        phone=fakerLocale.phoneNumber().phoneNumber();
        rentTime=new Random().nextInt(8);
        comment=fakerLocale.commerce().department();
    }

    @Test
    @DisplayName("Make order with different colors and get status code 201")
    @Description("We create an order with different colors and expect to receive the status code 201 and \"track\" in the response body (positive test)")
    public void makeOrderWithDifferentColorsAndGetStatusCode201(){

        OrderParameters jsonOrder=new OrderParameters(firstName,lastName,address,metroStation,phone,rentTime,deliveryDate,comment,color);
        Response response=given().header("Content-type","application/json").and().body(jsonOrder).post("/api/v1/orders");
        response.then().statusCode(201).and().assertThat().body("track", notNullValue());
    }
}
