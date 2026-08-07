package ch.example.gradetracker.model

data class ScheduleTimeSlot(
    val startTime: Int,
    val endTime: Int
)

val scheduleTimeSlots = listOf(
    ScheduleTimeSlot(805, 850),
    ScheduleTimeSlot(900, 945),
    ScheduleTimeSlot(1005, 1050),
    ScheduleTimeSlot(1100, 1145),
    ScheduleTimeSlot(1155, 1240),
    ScheduleTimeSlot(1250, 1335),
    ScheduleTimeSlot(1345, 1430),
    ScheduleTimeSlot(1440, 1525),
    ScheduleTimeSlot(1535, 1620),
    ScheduleTimeSlot(1630, 1715),
    ScheduleTimeSlot(1715, 1800)
)