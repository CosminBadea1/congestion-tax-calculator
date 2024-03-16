package com.assignment.congestion.tax.calculator.integration;

import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType;
import com.assignment.congestion.tax.calculator.fixtures.TollDatesMother;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType.CAR;
import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CongestionTaxCalculatorAcceptanceTests {

    private static final String CONGESTION_TAX_CALCULATOR_URL = "api/congestion-tax/%s?vehicleType=%s&tollDates=%s";
    private static final String GOTHENBURG_CONGESTION_TAX_URL = "api/congestion-tax/Gothenburg?vehicleType=%s&tollDates=%s";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
    }

    @Test
    void returnBadRequest_whenCalculatingTaxInUnsupportedCity() {
        String anyUnsupportedCity = "Malmo";

        given()
                .when()
                    .get(CONGESTION_TAX_CALCULATOR_URL.formatted(
                            anyUnsupportedCity, CAR, TollDatesMother.aTaxableMonday()))

                .then()
                    .statusCode(BAD_REQUEST.value())
                    .body("message", equalTo(
                            "%s is not a supported city. Currently, Gothenburg is the only supported city!".formatted(anyUnsupportedCity)));
    }

    @Test
    void returnBadRequest_whenCalculatingTaxInUnsupportedYear() {
        LocalDateTime anyUnsupportedYear = TollDatesMother.aTaxableMonday().withYear(2014);
        String tollDatesWithUnsupportedYear = Stream.of(TollDatesMother.aTaxableMonday(), anyUnsupportedYear)
                .map(LocalDateTime::toString)
                .collect(joining(","));

        given()
                .when()
                    .get(GOTHENBURG_CONGESTION_TAX_URL.formatted(CAR,  tollDatesWithUnsupportedYear))

                .then()
                    .statusCode(BAD_REQUEST.value())
                    .body("message", equalTo(
                            "Currently we only support calculations for toll dates in the year: 2013"));
    }

    @Test
    void returnBadRequest_whenCalculatingTaxForUnsupportedVehicle() {
        String anyUnsupportedVehicle = "BOAT";

        given()
                .when()
                    .get(GOTHENBURG_CONGESTION_TAX_URL.formatted("BOAT",  TollDatesMother.aTaxableMonday()))

                .then()
                    .statusCode(BAD_REQUEST.value())
                    .body("message", equalTo(
                            "Vehicle of type %s is not supported!".formatted(anyUnsupportedVehicle)));
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"EMERGENCY", "BUS", "DIPLOMAT", "MOTORCYCLE", "MILITARY", "FOREIGN"}
    )
    void returnZeroTax_whenCalculatingTaxForTaxExemptVehicle(VehicleType vehicleType) {
        given()
                .when()
                    .get(GOTHENBURG_CONGESTION_TAX_URL.formatted(vehicleType,  TollDatesMother.aTaxableMonday()))

                .then()
                    .statusCode(OK.value())
                    .body("amount", equalTo(0))
                    .body("currency", equalTo("SEK"));
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"CAR", "TRACTOR"}
    )
    void returnCongestionTax_whenTollDatesMatchMultipleRules(VehicleType vehicleType) {
        String postItDates = TollDatesMother.listOfMixedDates().stream()
                .map(LocalDateTime::toString)
                .collect(joining(","));

        given()
                .when()
                    .get(GOTHENBURG_CONGESTION_TAX_URL.formatted(vehicleType, postItDates))

                .then()
                    .statusCode(OK.value())
                    .body("amount", equalTo(89))
                    .body("currency", equalTo("SEK"));
    }
}
