package com.abbeycallow.haunted.business.service;

import com.abbeycallow.haunted.business.domain.RoomReservation;
import com.abbeycallow.haunted.data.entity.Guest;
import com.abbeycallow.haunted.data.entity.Reservation;
import com.abbeycallow.haunted.data.entity.Room;
import com.abbeycallow.haunted.data.repository.GuestRepository;
import com.abbeycallow.haunted.data.repository.ReservationRepository;
import com.abbeycallow.haunted.data.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class ReservationService {
    private RoomRepository roomRepository;
    private GuestRepository guestRepository;
    private ReservationRepository reservationRepository;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public ReservationService(RoomRepository roomRepository, GuestRepository guestRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<RoomReservation> getRoomReservationsForDate(Date date) {
        Iterable<Room> rooms = this.roomRepository.findAll();
        Map<Long, RoomReservation> roomReservationMap = new HashMap<>();
        rooms.forEach(room -> {
            RoomReservation roomReservation = new RoomReservation();
            roomReservation.setRoomId(room.getId());
            roomReservation.setRoomName(room.getName());
            roomReservation.setRoomNumber(room.getNumber());
            roomReservationMap.put(room.getId(), roomReservation);
        });
        Iterable<Reservation> reservations = this.reservationRepository.findByDate(new java.sql.Date(date.getTime()));
        if (null != reservations) {
            reservations.forEach(reservation -> {
                Optional<Guest> guestResp = this.guestRepository.findById(reservation.getGuestId());
                if (guestResp.isPresent()) {
                    Guest guest = guestResp.get();
                    RoomReservation roomReservations = roomReservationMap.get(reservation.getId());
                    roomReservations.setDate(date);
                    roomReservations.setFirstNAme(guest.getFirstName());
                    roomReservations.setLastName(guest.getLastName());
                    roomReservations.setGuestId(guest.getId());
                }
            });
        }
        List<RoomReservation> roomReservations = new ArrayList<>();
        for (Long roomId : roomReservationMap.keySet()) {
            roomReservations.add(roomReservationMap.get(roomId));
        }
        return roomReservations;
    }
//    private Date createDateFromDateString(String dateString){
//        Date date = null;
//        if(null!=dateString) {
//            try {
//                date = DATE_FORMAT.parse(dateString);
//            }catch(ParseException pe){
//                date = new Date();
//            }
//        }else{
//            date = new Date();
//        }
//        return date;
//    }
//}

}


