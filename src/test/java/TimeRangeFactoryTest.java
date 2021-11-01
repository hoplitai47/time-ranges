
import org.junit.Assert;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class TimeRangeFactoryTest {

    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final ZoneId ZONE_ID_WARSAW = ZoneId.of("Europe/Warsaw");

    /**
     * 28 Mar 2021 - Daylight Saving Time Started
     * When local standard time was about to reach
     * Sunday, 28 March 2021, 02:00:00 clocks were turned forward 1 hour to
     * Sunday, 28 March 2021, 03:00:00 local daylight time instead.
     * <p> CET [UTC+1], CEST [UTC+2]
     */
    private static final String MARCH_27_T_00_00 = "2021-03-27T00:00:00Z";
    private static final String MARCH_27_T_22_30 = "2021-03-27T22:30:00Z";
    private static final String MARCH_27_T_23_30 = "2021-03-27T23:30:00Z";
    private static final String MARCH_28_T_00_00 = "2021-03-28T00:00:00Z";
    private static final String MARCH_28_T_01_00 = "2021-03-28T01:00:00Z";
    private static final String MARCH_28_T_02_00 = "2021-03-28T02:00:00Z";
    private static final String MARCH_29_T_00_00 = "2021-03-29T00:00:00Z";

    private static final ZonedDateTime ZDT_UTC_MARCH_27_T_00_00 = ZonedDateTime.parse(MARCH_27_T_00_00,
            DateTimeFormatter.ISO_DATE_TIME);
    private static final ZonedDateTime ZDT_UTC_MARCH_27_T_22_30 = ZonedDateTime.parse(MARCH_27_T_22_30,
            DateTimeFormatter.ISO_DATE_TIME);
    private static final ZonedDateTime ZDT_UTC_MARCH_27_T_23_30 = ZonedDateTime.parse(MARCH_27_T_23_30,
            DateTimeFormatter.ISO_DATE_TIME);
    private static final ZonedDateTime ZDT_UTC_MARCH_28_T_00_00 = ZonedDateTime.parse(MARCH_28_T_00_00,
            DateTimeFormatter.ISO_DATE_TIME);
    private static final ZonedDateTime ZDT_UTC_MARCH_28_T_01_00 = ZonedDateTime.parse(MARCH_28_T_01_00,
            DateTimeFormatter.ISO_DATE_TIME);
    private static final ZonedDateTime ZDT_UTC_MARCH_28_T_02_00 = ZonedDateTime.parse(MARCH_28_T_02_00,
            DateTimeFormatter.ISO_DATE_TIME);
    private static final ZonedDateTime ZDT_UTC_MARCH_29_T_00_00 = ZonedDateTime.parse(MARCH_29_T_00_00,
            DateTimeFormatter.ISO_DATE_TIME);

    @Test
    public void createTodayRange_testUtc() {
        Instant sameInstant = ZDT_UTC_MARCH_27_T_23_30.toInstant();
        Clock clockUTC = Clock.fixed(sameInstant, ZONE_ID_UTC); // is still 27th of March, because it's UTC=>UTC

        TimeRangeFactory timeRangeFactory = new TimeRangeFactory(clockUTC);
        TimeRangeFactory.TimeRange testedRange = timeRangeFactory.createTodayRange();

        TimeRangeFactory.TimeRange expectedRange = new TimeRangeFactory.TimeRange(
                ZonedDateTime.parse("2021-03-27T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME),
                ZonedDateTime.parse("2021-03-28T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME));

        assertStrictEqualTimeRange(expectedRange, testedRange);
    }

    @Test
    public void createTodayRange_testWarsaw() {
        Instant sameInstant = ZDT_UTC_MARCH_27_T_23_30.toInstant();
        Clock clockWarsaw = Clock.fixed(sameInstant, ZONE_ID_WARSAW); // should already be 28th on March, because it's UTC=>CET

        TimeRangeFactory timeRangeFactory = new TimeRangeFactory(clockWarsaw);
        TimeRangeFactory.TimeRange testedRange = timeRangeFactory.createTodayRange();

        TimeRangeFactory.TimeRange expectedRange = new TimeRangeFactory.TimeRange(
                ZonedDateTime.parse("2021-03-28T00:00:00+01:00", DateTimeFormatter.ISO_DATE_TIME),
                ZonedDateTime.parse("2021-03-29T00:00:00+02:00", DateTimeFormatter.ISO_DATE_TIME));

        assertStrictEqualTimeRange(expectedRange, testedRange);
    }

    @Test
    public void parseFromLegacyString_8Numeric_testUTC() {
        Clock clockWarsaw = Clock.system(ZONE_ID_UTC);
        TimeRangeFactory timeRangeFactory = new TimeRangeFactory(clockWarsaw);

        String startDateRaw = "20770314";
        String endDateRaw = "20770317";

        TimeRangeFactory.TimeRange testedRange = timeRangeFactory.parseFromLegacyString(startDateRaw, endDateRaw);

        TimeRangeFactory.TimeRange expectedRange = new TimeRangeFactory.TimeRange(
                ZonedDateTime.parse("2077-03-14T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME),
                ZonedDateTime.parse("2077-03-17T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME));

        assertStrictEqualTimeRange(expectedRange, testedRange);
    }

    @Test
    public void parseFromLegacyString_8Numeric_testWarsaw() {
        Clock clockWarsaw = Clock.system(ZONE_ID_WARSAW);
        TimeRangeFactory timeRangeFactory = new TimeRangeFactory(clockWarsaw);

        String startDateRaw = "20770314";
        String endDateRaw = "20770317";

        TimeRangeFactory.TimeRange testedRange = timeRangeFactory.parseFromLegacyString(startDateRaw, endDateRaw);

        TimeRangeFactory.TimeRange expectedRange = new TimeRangeFactory.TimeRange(
                ZonedDateTime.parse("2077-03-14T00:00:00+01:00", DateTimeFormatter.ISO_DATE_TIME),
                ZonedDateTime.parse("2077-03-17T00:00:00+01:00", DateTimeFormatter.ISO_DATE_TIME));

        assertStrictEqualTimeRange(expectedRange, testedRange);
    }

    @Test
    public void parseFromLegacyString_EpochMillis_testUTC() {
        Clock clockWarsaw = Clock.system(ZONE_ID_UTC);
        TimeRangeFactory timeRangeFactory = new TimeRangeFactory(clockWarsaw);

        String startDateRaw = "3382905600000";
        String endDateRaw = "3383164800000";

        TimeRangeFactory.TimeRange testedRange = timeRangeFactory.parseFromLegacyString(startDateRaw, endDateRaw);

        TimeRangeFactory.TimeRange expectedRange = new TimeRangeFactory.TimeRange(
                ZonedDateTime.parse("2077-03-14T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME),
                ZonedDateTime.parse("2077-03-17T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME));

        assertStrictEqualTimeRange(expectedRange, testedRange);
    }

    @Test
    public void parseFromLegacyString_EpochMillis_testWarsaw() {
        Clock clockWarsaw = Clock.system(ZONE_ID_WARSAW);
        TimeRangeFactory timeRangeFactory = new TimeRangeFactory(clockWarsaw);

        String startDateRaw = "3382905600000";
        String endDateRaw = "3383164800000";

        TimeRangeFactory.TimeRange testedRange = timeRangeFactory.parseFromLegacyString(startDateRaw, endDateRaw);

        TimeRangeFactory.TimeRange expectedRange = new TimeRangeFactory.TimeRange(
                ZonedDateTime.parse("2077-03-14T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME),
                ZonedDateTime.parse("2077-03-17T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME));

        assertStrictEqualTimeRange(expectedRange, testedRange);
    }

    private void assertStrictEqualTimeRange(final TimeRangeFactory.TimeRange expected,
                                            final TimeRangeFactory.TimeRange tested) {
        Assert.assertEquals(expected.getStartDateTime().toInstant(), tested.getStartDateTime().toInstant());
        Assert.assertEquals(expected.getEndDateTime().toInstant(), tested.getEndDateTime().toInstant());
    }
}