package com.assignment.congestion.tax.calculator.domain.core;

import com.assignment.congestion.tax.calculator.domain.application_service.mapper.VehicleFactory;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.Vehicle;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType;
import com.assignment.congestion.tax.calculator.fixtures.PublicHolidaysStub;
import com.assignment.congestion.tax.calculator.fixtures.TollDatesMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GothenburgCongestionTaxCalculatorTest {

    public static final int MAXIMUM_CONGESTION_TAX_IN_ONE_DAY = 60;
    public static final int SINGLE_CHARGE_WINDOW_IN_MINUTES = 60;

    private GothenburgCongestionTaxCalculator underTest;

    @BeforeEach
    void setUp() {
        underTest = new GothenburgCongestionTaxCalculator(new PublicHolidaysStub());
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"EMERGENCY", "BUS", "DIPLOMAT", "MOTORCYCLE", "MILITARY", "FOREIGN"}
    )
    void returnZeroTax_whenVehicleIsTaxExempt(VehicleType vehicleType) {
        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), List.of(TollDatesMother.aTaxableMonday()));

        assertThat(actualTax).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"CAR", "TRACTOR"}
    )
    void returnMaximumDailyTax_whenCongestionChargesExceedDailyLimit(VehicleType vehicleType) {
        List<LocalDateTime> tollDates = TollDatesMother.listOfMaxedChargesInOneDay(SINGLE_CHARGE_WINDOW_IN_MINUTES);

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates);

        assertThat(actualTax).isEqualTo(MAXIMUM_CONGESTION_TAX_IN_ONE_DAY);
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"CAR", "TRACTOR"}
    )
    void returnZeroTax_whenTollingDatesListIsEmpty(VehicleType vehicleType) {
        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), List.of());

        assertThat(actualTax).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"CAR", "TRACTOR"}
    )
    void returnZeroTax_whenTollingDayIsDuringWeekends(VehicleType vehicleType) {
        List<LocalDateTime> tollDates = TollDatesMother.listOfWeekends();

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates);

        assertThat(actualTax).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"CAR", "TRACTOR"}
    )
    void returnZeroTax_whenTollingDayIsDuringPublicHolidays(VehicleType vehicleType) {
        List<LocalDateTime> tollDates = TollDatesMother.listOfHolidays();

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates);

        assertThat(actualTax).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"CAR", "TRACTOR"}
    )
    void returnZeroTax_whenTollingDayIsTheDayBeforeAPublicHoliday(VehicleType vehicleType) {
        List<LocalDateTime> tollDates = TollDatesMother.listOfDaysBeforePublicHolidays();

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates);

        assertThat(actualTax).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"CAR", "TRACTOR"}
    )
    void returnZeroTax_whenTollingDayIsInJuly(VehicleType vehicleType) {
        List<LocalDateTime> tollDates = TollDatesMother.listOfDaysInJuly();

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates);

        assertThat(actualTax).isEqualTo(0);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
        06:00,    8
        06:29,    8
        06:30,    13
        06:59,    13
        07:00,    18
        07:59,    18
        08:00,    13
        08:29,    13
        08:30,    8
        14:59,    8
        15:00,    13
        15:29,    13
        15:30,    18
        16:59,    18
        17:00,    13
        17:59,    13
        18:00,    8
        18:29,    8
    """)
    void returnProperCongestionTax_whenTollTimeIsTheIntervalBoundary(LocalTime tollTime, int expectedTax) {
        List<LocalDateTime> tollDates = List.of(
                TollDatesMother.aTaxableMonday().toLocalDate().atTime(tollTime)
        );

        int actualTax = underTest.calculateTax(vehicleOfType(VehicleType.CAR), tollDates);

        assertThat(actualTax).isEqualTo(expectedTax);
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"CAR", "TRACTOR"}
    )
    void returnZeroTax_whenCongestionTaxAtTollingTimeIsZero(VehicleType vehicleType) {
        List<LocalDateTime> tollDates = TollDatesMother.listOfZeroChargeTollTimes();

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates);

        assertThat(actualTax).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"CAR", "TRACTOR"}
    )
    void returnSingleCharge_whenTollingTimesInSingleChargeWindow(VehicleType vehicleType) {
        List<LocalDateTime> tollDates = List.of(
                TollDatesMother.aTaxableMonday(),
                TollDatesMother.aTaxableMonday().plusMinutes(1),
                TollDatesMother.aTaxableMonday().plusMinutes(10),
                TollDatesMother.aTaxableMonday().plusMinutes(20),
                TollDatesMother.aTaxableMonday().plusMinutes(30),
                TollDatesMother.aTaxableMonday().plusMinutes(59),
                TollDatesMother.aTaxableMonday().plusHours(1)
        );

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates);

        assertThat(actualTax).isEqualTo(18);
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"CAR", "TRACTOR"}
    )
    void returnSumOfSingleCharges_whenTollingTimesExceedSingleChargeWindow(VehicleType vehicleType) {
        List<LocalDateTime> tollDates = List.of(
                TollDatesMother.aTaxableMonday(),
                TollDatesMother.aTaxableMonday().plusMinutes(MAXIMUM_CONGESTION_TAX_IN_ONE_DAY + 1),
                TollDatesMother.aTaxableMonday().plusMinutes(2 * (MAXIMUM_CONGESTION_TAX_IN_ONE_DAY + 1))
        );

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates);

        assertThat(actualTax).isEqualTo(13 + 18 + 8);
    }

    private static Vehicle vehicleOfType(VehicleType vehicleType) {
        return new VehicleFactory().create(vehicleType.name());
    }
}
