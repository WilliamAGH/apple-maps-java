package com.williamcallahan.applemaps.domain.model;

/**
 * Defines Apple Maps point-of-interest categories.
 */
public enum PoiCategory {
    AIRPORT("Airport"),
    AIRPORT_GATE("AirportGate"),
    AIRPORT_TERMINAL("AirportTerminal"),
    AMUSEMENT_PARK("AmusementPark"),
    ATM("ATM"),
    AQUARIUM("Aquarium"),
    BAKERY("Bakery"),
    BANK("Bank"),
    BEACH("Beach"),
    BREWERY("Brewery"),
    BOWLING("Bowling"),
    CAFE("Cafe"),
    CAMPGROUND("Campground"),
    CAR_RENTAL("CarRental"),
    EV_CHARGER("EVCharger"),
    FIRE_STATION("FireStation"),
    FITNESS_CENTER("FitnessCenter"),
    FOOD_MARKET("FoodMarket"),
    GAS_STATION("GasStation"),
    HOSPITAL("Hospital"),
    HOTEL("Hotel"),
    LAUNDRY("Laundry"),
    LIBRARY("Library"),
    MARINA("Marina"),
    MOVIE_THEATER("MovieTheater"),
    MUSEUM("Museum"),
    NATIONAL_PARK("NationalPark"),
    NIGHTLIFE("Nightlife"),
    PARK("Park"),
    PARKING("Parking"),
    PHARMACY("Pharmacy"),
    PLAYGROUND("Playground"),
    POLICE("Police"),
    POST_OFFICE("PostOffice"),
    PUBLIC_TRANSPORT("PublicTransport"),
    RELIGIOUS_SITE("ReligiousSite"),
    RESTAURANT("Restaurant"),
    RESTROOM("Restroom"),
    SCHOOL("School"),
    STADIUM("Stadium"),
    STORE("Store"),
    THEATER("Theater"),
    UNIVERSITY("University"),
    WINERY("Winery"),
    ZOO("Zoo"),
    LANDMARK("Landmark");

    private final String apiValue;

    PoiCategory(String apiValue) {
        this.apiValue = apiValue;
    }

    public String apiValue() {
        return apiValue;
    }

    @Override
    public String toString() {
        return apiValue;
    }
}
