package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.model.User;
import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        ParkingLot parkingLot;
        try {
             parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        }
        catch (Exception e){
            throw new Exception("Reservation cannot be made");
        }

        User user;
        try {
             user = userRepository3.findById(userId).get();
        }
        catch (Exception e){
            throw new Exception("Reservation cannot be made");
        }

        List<Spot> spots = parkingLot.getSpotList();
        List<Spot> spotList = new ArrayList<>();

        for (Spot s : spots){
            if(s.isOccupied() == true){
                int capacity;
                if(s.getSpotType() == SpotType.TWO_WHEELER){
                    capacity = 2;
                }

                else if(s.getSpotType() == SpotType.FOUR_WHEELER){
                    capacity = 4;
                }
                else {
                    capacity = Integer.MAX_VALUE;
                }

                if(capacity >= numberOfWheels){
                    spotList.add(s);
                }
            }

        }

        if(spotList.isEmpty() == true){
            throw new Exception("Reservation cannot be made");
        }

        Spot spotReserve = null;
        int minimumPrice = Integer.MAX_VALUE;
        for (Spot spot : spotList){
            int price = spot.getPricePerHour() * timeInHours;
            if(price < minimumPrice){
                minimumPrice = price;
                spotReserve = spot;
            }
        }

        spotReserve.setOccupied(true);

        Reservation reservation = new Reservation();
        reservation.setSpot(spotReserve);
        reservation.setNumberOfHour(timeInHours);
        reservation.setUser(user);
        reservation.setPayment(null);

        user.getReservationList().add(reservation);

        userRepository3.save(user);
        spotRepository3.save(spotReserve);

        return reservation;
    }
}
