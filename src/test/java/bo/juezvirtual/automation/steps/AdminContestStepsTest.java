package bo.juezvirtual.automation.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

final class AdminContestStepsTest {
    @Test
    void movesContestEndDateToNextDayWhenScheduleCrossesMidnight() {
        AdminContestSteps.ContestSchedule schedule = AdminContestSteps.calculateTime(
                "actual", "actual", LocalDateTime.of(2026, 7, 18, 23, 14));

        assertEquals("2026-07-18", schedule.startDate());
        assertEquals("23:13", schedule.startTime());
        assertEquals("2026-07-19", schedule.endDate());
        assertEquals("01:14", schedule.endTime());
    }

    @Test
    void movesContestStartDateToPreviousDayJustAfterMidnight() {
        AdminContestSteps.ContestSchedule schedule = AdminContestSteps.calculateTime(
                "actual", "actual", LocalDateTime.of(2026, 7, 19, 0, 0));

        assertEquals("2026-07-18", schedule.startDate());
        assertEquals("23:59", schedule.startTime());
        assertEquals("2026-07-19", schedule.endDate());
        assertEquals("02:00", schedule.endTime());
    }
}
