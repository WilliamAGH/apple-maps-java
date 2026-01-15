package com.williamcallahan.applemaps.domain.model;

/**
 * Defines Apple Maps point-of-interest categories.
 */
public enum PoiCategory {
    /** Airport. */
    AIRPORT("Airport"),
    /** Airport gate. */
    AIRPORT_GATE("AirportGate"),
    /** Airport terminal. */
    AIRPORT_TERMINAL("AirportTerminal"),
    /** Amusement park. */
    AMUSEMENT_PARK("AmusementPark"),
    /** ATM. */
    ATM("ATM"),
    /** Aquarium. */
    AQUARIUM("Aquarium"),
    /** Bakery. */
    BAKERY("Bakery"),
    /** Bank. */
    BANK("Bank"),
    /** Beach. */
    BEACH("Beach"),
    /** Brewery. */
    BREWERY("Brewery"),
    /** Bowling alley. */
    BOWLING("Bowling"),
    /** Cafe. */
    CAFE("Cafe"),
    /** Campground. */
    CAMPGROUND("Campground"),
    /** Car rental. */
    CAR_RENTAL("CarRental"),
    /** EV charger. */
    EV_CHARGER("EVCharger"),
    /** Fire station. */
    FIRE_STATION("FireStation"),
    /** Fitness center. */
    FITNESS_CENTER("FitnessCenter"),
    /** Food market. */
    FOOD_MARKET("FoodMarket"),
    /** Gas station. */
    GAS_STATION("GasStation"),
    /** Hospital. */
    HOSPITAL("Hospital"),
    /** Hotel. */
    HOTEL("Hotel"),
    /** Laundry. */
    LAUNDRY("Laundry"),
    /** Library. */
    LIBRARY("Library"),
    /** Marina. */
    MARINA("Marina"),
    /** Movie theater. */
    MOVIE_THEATER("MovieTheater"),
    /** Museum. */
    MUSEUM("Museum"),
    /** National park. */
    NATIONAL_PARK("NationalPark"),
    /** Nightlife. */
    NIGHTLIFE("Nightlife"),
    /** Park. */
    PARK("Park"),
    /** Parking. */
    PARKING("Parking"),
    /** Pharmacy. */
    PHARMACY("Pharmacy"),
    /** Playground. */
    PLAYGROUND("Playground"),
    /** Police. */
    POLICE("Police"),
    /** Post office. */
    POST_OFFICE("PostOffice"),
    /** Public transport. */
    PUBLIC_TRANSPORT("PublicTransport"),
    /** Religious site. */
    RELIGIOUS_SITE("ReligiousSite"),
    /** Restaurant. */
    RESTAURANT("Restaurant"),
    /** Restroom. */
    RESTROOM("Restroom"),
    /** School. */
    SCHOOL("School"),
    /** Stadium. */
    STADIUM("Stadium"),
    /** Store. */
    STORE("Store"),
    /** Theater. */
    THEATER("Theater"),
    /** University. */
    UNIVERSITY("University"),
    /** Winery. */
    WINERY("Winery"),
    /** Zoo. */
    ZOO("Zoo"),
    /** Landmark. */
    LANDMARK("Landmark");

    private final String apiValue;

    PoiCategory(String apiValue) {
        this.apiValue = apiValue;
    }

    /**
     * Returns the Apple Maps Server API value for this category.
     *
     * @return the value used by the API
     */
    public String apiValue() {
        return apiValue;
    }

    @Override
    public String toString() {
        return apiValue;
    }
}
