import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CreateNewCourierTest {
    CourierNew json;
    CourierNew jsonFirstCourier;
    CourierNew jsonSecondCourier;
    Response response;
    Response responseFirstCourier;
    Response responseSecondCourier;

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseURI.BASE_URI;
    }

    @Test
    @DisplayName("Create courier with valid data and get status code 201")
    @Description("We register a courier with valid data and expect to receive a status code 201 and {ok: true} in the response body(positive test)")
    public void createCourierWithValidDataAndGetStatusCode201() {
        json = Steps.getValidDataForSuccessfullRegistration();
        response = Steps.sendPostRequestSetUp(json);
        Steps.checkStatusCodeAndBody(response, 201);
    }

    @Test
    @DisplayName("Create courier's twin and get unsuccessed status code 409")
    @Description("We register 2x couriers with completely identical data and expect to receive the status code 409 and correct message(Этот логин уже используется) when registering a second courier with the same data (negative test)")
    public void createCouriersTwinAndGetUnsuccessedStatusCode409() {
        jsonFirstCourier = Steps.getValidDataForSuccessfullRegistration();
        jsonSecondCourier = jsonFirstCourier;
        responseFirstCourier = Steps.sendPostRequestSetUp(jsonFirstCourier);
        responseSecondCourier = Steps.sendPostRequestSetUp(jsonSecondCourier);
        Steps.checkStatusCodeAndMessage(responseSecondCourier, 409, "Этот логин уже используется");
    }

    @Test
    @DisplayName("Create courier with existing login and get unsuccessed status code 409")
    @Description("We register 2x couriers with completely identical logins and expect to receive the status code 409 and correct message(Этот логин уже используется) when registering a second courier with the same data (negative test) ")
    public void createCourierWithExistingLoginAndGetUnsuccessedStatusCode409() {
        jsonFirstCourier = Steps.getValidDataForSuccessfullRegistration();
        jsonSecondCourier = Steps.getValidDataForSuccessfullRegistration();
        jsonSecondCourier.setLogin(jsonFirstCourier.getLogin());
        responseFirstCourier = Steps.sendPostRequestSetUp(jsonFirstCourier);
        responseSecondCourier = Steps.sendPostRequestSetUp(jsonSecondCourier);
        Steps.checkStatusCodeAndMessage(responseSecondCourier, 409, "Этот логин уже используется");
    }

    @Test
    @DisplayName("Create courier without login and get unsuccessed status code 400")
    @Description("We register the courier without a login and expect that registration will not pass and we will receive the status code 400 and correct message(Недостаточно данных для создания учетной записи) (negative test)")
    public void createCourierWithoutLoginAndGetUnsuccessedStatusCode400() {
        json = Steps.getValidDataForSuccessfullRegistration();
        json.setLogin(null);
        response = Steps.sendPostRequestSetUp(json);
        Steps.checkStatusCodeAndMessage(response, 400, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @DisplayName("Create courier without password and get unsuccessed status code 400")
    @Description("We register the courier without a password and expect that registration will not pass and we will receive the status code 400 and correct message(Недостаточно данных для создания учетной записи)(negative test)")
    public void createCourierWithoutPasswordAndGetUnsuccessedStatusCode400() {
        json = Steps.getValidDataForSuccessfullRegistration();
        json.setPassword("");
        response = Steps.sendPostRequestSetUp(json);
        Steps.checkStatusCodeAndMessage(response, 400, "Недостаточно данных для создания учетной записи");
    }

    @After
    public void tearDown() {

        try {
            Steps.deleteNewCourier(json);
        } catch (NullPointerException exeption) {
        }

        try {
            Steps.deleteNewCourier(jsonFirstCourier);
        } catch (NullPointerException exeption) {
        }

        try {
            Steps.deleteNewCourier(jsonSecondCourier);
        } catch (NullPointerException exeption) {
        }
    }
}
