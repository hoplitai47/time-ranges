import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TimeRangeFactory {
    private static final String LEGACY_DATE_PATTERN = "yyyyMMdd";
    private final DateTimeFormatter legacyDtFormatter;
    private final Clock clock;

    public static class TimeRange {
        private final ZonedDateTime startDateTime;
        private final ZonedDateTime endDateTime;

        public TimeRange(final ZonedDateTime startDateTime,
                         final ZonedDateTime endDateTime) {
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }

        public ZonedDateTime getStartDateTime() {
            return startDateTime;
        }

        public ZonedDateTime getEndDateTime() {
            return endDateTime;
        }
    }

    public TimeRangeFactory(final Clock clock) {
        this.clock = clock;
        this.legacyDtFormatter = DateTimeFormatter.ofPattern(LEGACY_DATE_PATTERN);
    }

    public static TimeRangeFactory withSystemDefaults() {
        return new TimeRangeFactory(Clock.systemDefaultZone());
    }

    public TimeRange createTodayRange() {
        ZoneId zoneId = clock.getZone();
        LocalDateTime startLdt = LocalDate.now(clock).atStartOfDay();
        LocalDateTime endLdt = startLdt.plusDays(1L);

        ZonedDateTime startDateTime = startLdt.atZone(zoneId);
        ZonedDateTime endDateTime = endLdt.atZone(zoneId);
        return new TimeRange(startDateTime, endDateTime);
    }

    public TimeRange parseFromLegacyString(final String startDateRaw,
                                           final String endDateRaw) {
        final boolean isLegacyDatePatternLength = LEGACY_DATE_PATTERN.length() == startDateRaw.length()
                && LEGACY_DATE_PATTERN.length() == endDateRaw.length();
        ZonedDateTime startDateTime;
        ZonedDateTime endDateTime;

        if (isLegacyDatePatternLength) {
            startDateTime = parseAsLegacyDate(startDateRaw);
            endDateTime = parseAsLegacyDate(endDateRaw);
        } else {
            startDateTime = parseAsEpochMillis(startDateRaw);
            endDateTime = parseAsEpochMillis(endDateRaw);
        }

        return new TimeRange(startDateTime, endDateTime);
    }

    private ZonedDateTime parseAsLegacyDate(final String dateRaw) {
        LocalDate localDate = LocalDate.parse(dateRaw, legacyDtFormatter);
        ZonedDateTime zonedDateTime = localDate.atStartOfDay().atZone(clock.getZone());
        return zonedDateTime;
    }

    private ZonedDateTime parseAsEpochMillis(final String dateRaw) {
        long epochMillis = Long.parseLong(dateRaw);
        Instant dateInstant = Instant.ofEpochMilli(epochMillis);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(dateInstant, ZoneOffset.UTC);
        return zonedDateTime;
    }

}
