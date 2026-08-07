package ch.example.gradetracker.data.remote.model

data class MaxHalfdayResponse(
    val data: MaxHalfdayData,
    val status: String?,
    val error: String
)


data class MaxHalfdayData(
    val amount: Int
)