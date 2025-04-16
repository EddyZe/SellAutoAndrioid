package com.example.sellauto.clients.sellauto.payloads.ads

import com.example.sellauto.enums.BodyType
import com.example.sellauto.enums.DriveMode
import com.example.sellauto.enums.EngineType
import com.example.sellauto.enums.TransmissionType

data class CarDetailsDto(
    val carId: Long,
    val year: Int,
    val vin: String,
    val mileage: Int,
    val engineType: EngineType,
    val transmissionType: TransmissionType,
    val bodyType: BodyType,
    val driveMode: DriveMode,
    val brand: BrandBasePayload,
    val model: ModelBasePayload,
    val photos: List<PhotoBasePayload>,
    val color: ColorBasePayload,
)
