package com.example.realEstate.service;

import com.example.realEstate.entity.Offer;
import com.example.realEstate.entity.Property;

import java.util.List;

public interface CustomerService {

    void addToFavorites(long customer_id, long property_id);

    List<Property> getFavoriteProperties(long customer_id);

    void removeFromFavorites(long customer_id, long property_id);

    void makeOffer(Offer offer);

    List<Offer> getOffers(long customer_id);

    Offer getOfferById(long customer_id, long offer_id);

    void withdrawOffer(long customerId, long id);

    void updateOfferPrice(long customerId, long id, double price);
}