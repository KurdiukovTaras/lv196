package com.softserve.edu.delivery.service;

import com.softserve.edu.delivery.domain.Offer;
import com.softserve.edu.delivery.domain.Order;
import com.softserve.edu.delivery.dto.FeedbackDTO;
import com.softserve.edu.delivery.dto.OrderForAddDto;
import com.softserve.edu.delivery.dto.OrderForListDto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface OrderService {

    /**
     * Finds the list of active orders by given parameters
     *
     * @param email - email of assigned customer
     * @param page - number of page
     * @param size - orders amount on the page
     * @return list of active orders for the relevant parameters
     */
    List<OrderForListDto> findAllActiveOrders(String email, int page, int size);

    /**
     * Creates new order based of given dto and assigns it to user with given email
     *
     * @param dto - order dto
     * @param email - of user to assign
     * @throws IllegalArgumentException if dto is null or no such user with given email
     * or city from/to could not be found
     */
    void addOrder(OrderForAddDto dto, String email);

/**
* Author - Taras Kurdiukov
*/
    //Method for user story - "As customer I want to write transporter feedback."
    void addFeedback (FeedbackDTO dto, String email);
    //Method for user story - "As customer I want to change offer status."
    void changeStatus(String order_id, Boolean offerStatus);

/**
* Author - Ivan Synyshyn
*/
    //As transporter I want to choose orders by filter
    List<OrderForListDto> getOrdersByCityFrom(String name);
    List<OrderForListDto> getOrdersByCityTo(String name);
    List<OrderForListDto> getOrdersByWeight(BigDecimal weight);
    List<OrderForListDto> getOrdersByArriwalDate(Timestamp arrivalDate);

    //As transporter I want to add Offer on order.
    List<Offer> addOffer(Long orderId, Offer offer);

}
