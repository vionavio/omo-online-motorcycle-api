package com.viona.onlinemotorcycle.location.controller

import com.viona.onlinemotorcycle.base.BaseResponse
import com.viona.onlinemotorcycle.location.entity.model.Location
import com.viona.onlinemotorcycle.location.entity.model.Routes
import com.viona.onlinemotorcycle.location.services.LocationService
import com.viona.onlinemotorcycle.utils.coordinateStringToData
import com.viona.onlinemotorcycle.utils.toResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/location")
class LocationController {

    @Autowired
    private lateinit var locationServices: LocationService

    @GetMapping("/search")
    fun searchLocation(
        @RequestParam(value = "name") name: String,
        @RequestParam(value = "coordinate") coordinate: String
    ): BaseResponse<List<Location>> {
        val coordinates = coordinate.coordinateStringToData()
        return locationServices.searchLocation(name, coordinates).toResponses()
    }

    @GetMapping("/reserve")
    fun reserveLocation(
        @RequestParam(value = "coordinate2", required = true) coordinate2: String
    ): BaseResponse<List<Location>> {
        val coordinates = coordinate2.coordinateStringToData()
        return locationServices.reserveLocation(coordinates).toResponses()
    }

    @GetMapping("/routes")
    fun routesLocation(
        @RequestParam(value = "origin") origin: String,
        @RequestParam(value = "destination") destination: String
    ): BaseResponse<Routes> {
        val coordinatesOrigin = origin.coordinateStringToData()
        val coordinatesDestination = destination.coordinateStringToData()
        return locationServices.getRoutesLocation(coordinatesOrigin, coordinatesDestination).toResponses()
    }
}