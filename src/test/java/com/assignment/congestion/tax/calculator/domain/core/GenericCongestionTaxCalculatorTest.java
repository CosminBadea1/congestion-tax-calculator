package com.assignment.congestion.tax.calculator.domain.core;

import com.assignment.congestion.tax.calculator.domain.application_service.mapper.VehicleFactory;
import com.assignment.congestion.tax.calculator.domain.core.model.rules.CongestionTaxInterval;
import com.assignment.congestion.tax.calculator.domain.core.model.rules.CongestionTaxRules;
import com.assignment.congestion.tax.calculator.domain.core.model.rules.SingleChargeRule;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.Vehicle;
import com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType;
import com.assignment.congestion.tax.calculator.fixtures.TollDatesMother;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.assignment.congestion.tax.calculator.adapters.calendar.FakeCalendarClient.SWEDEN_HOLIDAYS_2013;
import static com.assignment.congestion.tax.calculator.domain.core.GothenburgCongestionTaxCalculator.CONGESTION_TAX_BY_TIME_SLOT;
import static com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType.BUS;
import static com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType.DIPLOMAT;
import static com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType.EMERGENCY;
import static com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType.FOREIGN;
import static com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType.MILITARY;
import static com.assignment.congestion.tax.calculator.domain.core.model.vehicles.VehicleType.MOTORCYCLE;
import static org.assertj.core.api.Assertions.assertThat;

class GenericCongestionTaxCalculatorTest {

    public static final int GOTHENBURG_MAX_CONGESTION_TAX_IN_ONE_DAY = 60;
    public static final int GOTHENBURG_SINGLE_CHARGE_WINDOW = 60;

    private final GenericCongestionTaxCalculator underTest = new GenericCongestionTaxCalculator();

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"EMERGENCY", "BUS", "DIPLOMAT", "MOTORCYCLE", "MILITARY", "FOREIGN"}
    )
    void returnZeroTax_whenVehicleIsTaxExempt(VehicleType vehicleType) {
        int actualTax = underTest.calculateTax(
                vehicleOfType(vehicleType),
                List.of(TollDatesMother.aTaxableMonday()),
                gothenburgTaxRules());

        assertThat(actualTax).isEqualTo(0);
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"CAR", "TRACTOR"}
    )
    void returnMaximumDailyTax_whenCongestionChargesExceedDailyLimit(VehicleType vehicleType) {
        List<LocalDateTime> tollDates = TollDatesMother.listOfMaxedChargesInOneDay(GOTHENBURG_SINGLE_CHARGE_WINDOW);

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates, gothenburgTaxRules());

        assertThat(actualTax).isEqualTo(GOTHENBURG_MAX_CONGESTION_TAX_IN_ONE_DAY);
    }

    @ParameterizedTest
    @EnumSource(
            value = VehicleType.class,
            mode = EnumSource.Mode.INCLUDE,
            names = {"CAR", "TRACTOR"}
    )
    void returnZeroTax_whenTollingDatesListIsEmpty(VehicleType vehicleType) {
        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), List.of(), gothenburgTaxRules());

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

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates, gothenburgTaxRules());

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

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates, gothenburgTaxRules());

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

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates, gothenburgTaxRules());

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

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates, gothenburgTaxRules());

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

        int actualTax = underTest.calculateTax(vehicleOfType(VehicleType.CAR), tollDates, gothenburgTaxRules());

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

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates, gothenburgTaxRules());

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

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates, gothenburgTaxRules());

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
                TollDatesMother.aTaxableMonday().plusMinutes(GOTHENBURG_MAX_CONGESTION_TAX_IN_ONE_DAY + 1),
                TollDatesMother.aTaxableMonday().plusMinutes(2 * (GOTHENBURG_MAX_CONGESTION_TAX_IN_ONE_DAY + 1))
        );

        int actualTax = underTest.calculateTax(vehicleOfType(vehicleType), tollDates, gothenburgTaxRules());

        assertThat(actualTax).isEqualTo(13 + 18 + 8);
    }

    private static Vehicle vehicleOfType(VehicleType vehicleType) {
        return new VehicleFactory().create(vehicleType.name());
    }

    private static CongestionTaxRules gothenburgTaxRules() {
        return CongestionTaxRules.builder()
                .maxChargePerDay(GOTHENBURG_MAX_CONGESTION_TAX_IN_ONE_DAY)
                .singleChargeRule(new SingleChargeRule(true, GOTHENBURG_SINGLE_CHARGE_WINDOW))
                .taxExemptVehicles(List.of(EMERGENCY, BUS, DIPLOMAT, MOTORCYCLE, MILITARY, FOREIGN))
                .congestionTaxIntervals(gothenburgCongestionTaxIntervals())
                .publicHolidays(SWEDEN_HOLIDAYS_2013)
                .build();
    }

    private static List<CongestionTaxInterval> gothenburgCongestionTaxIntervals() {
        return CONGESTION_TAX_BY_TIME_SLOT.entrySet().stream()
                .map(entry -> new CongestionTaxInterval(
                        entry.getKey().startTime(), entry.getKey().endTime(), entry.getValue()))
                .toList();
    }
}
