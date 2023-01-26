import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class MakeOrderTest {
    private final String firstName;
    private final String lastName;
    private final String address;
    private final String metroStation;
    private final String phone;
    private final int rentTime;
    private final String deliveryDate;
    private final String comment;
    private final List<String> color;

    public MakeOrderTest(String firstName, String lastName, String address, String metroStation, String phone, int rentTime, String deliveryDate, String comment, List<String> color){
        this.firstName=firstName;
        this.lastName=lastName;
        this.address=address;
        this.metroStation=metroStation;
        this.phone=phone;
        this.rentTime=rentTime;
        this.deliveryDate=deliveryDate;
        this.comment=comment;
        this.color=color;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData(){
        return new Object[][] {
                {"Naruto","Uchiha","Konoha, 142 apt.","4","+7 800 355 35 35",5,"2023-02-26","Saske, come back to Konoha", List.of("")},
                {"Иван","Иванов","г.Уфа, Менделеева 199, подъезд 2","Откуда ж тут метро","+7 987 111 11 11",3,"2023-01-25","Без комментариев", List.of("BLACK")},
                {"Владимир","Путин","г.Москва, улица Академика Зелинского, дом №6", "А что такое метро?", "sensored",1,"2023-06-06","Будьте любезны, не опаздывайте, будьте сознательными гражданами", List.of("GREY")},
                {"Jack","Daniel's","51 Mechanic St S, Lynchburg, TN 37352, USA","Not Avaliable","+1 234 267 89 00",7,"2020-06-06","Have a nice day!", List.of("BLACK","GREY")},
        };
    }

    @Before
    public void setUp(){
        RestAssured.baseURI="https://qa-scooter.praktikum-services.ru/";
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
