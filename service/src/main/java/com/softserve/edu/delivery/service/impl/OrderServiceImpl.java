package com.softserve.edu.delivery.service.impl;

import com.softserve.edu.delivery.dao.*;
import com.softserve.edu.delivery.domain.*;
import com.softserve.edu.delivery.dto.FeedbackDTO;
import com.softserve.edu.delivery.dto.OfferDto;
import com.softserve.edu.delivery.dto.OrderForAddDto;
import com.softserve.edu.delivery.dto.OrderForListDto;
import com.softserve.edu.delivery.repository.CityRepository;
import com.softserve.edu.delivery.repository.OrderRepository;
import com.softserve.edu.delivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final CityDao cityDao;
    private final FeedbackDao feedbackDao;
    private final OfferDao offerDao;
    private final CityRepository cityRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderDao orderDao, UserDao userDao, CityDao cityDao, FeedbackDao feedbackDao, OfferDao offerDao, CityRepository cityRepository) {
        this.orderRepository = orderRepository;
        this.orderDao = orderDao;
        this.userDao = userDao;
        this.cityDao = cityDao;
        this.feedbackDao = feedbackDao;
        this.offerDao = offerDao;
        this.cityRepository = cityRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderForListDto> findAllActiveOrders(String email) {
        return orderDao
                .findActiveOrders(email)
                .stream()
                .map(order -> {
                    OrderForListDto dto = OrderForListDto.of(order);
                    if (order.getOrderStatus() == OrderStatus.IN_PROGRESS) {
                        String name = orderDao
                                .findDriverNameByOrderId(dto.getId())
                                .orElse(null);
                        dto.setDriverName(name);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void addOrder(OrderForAddDto dto, String email) {
        if (dto == null) {
            throw new IllegalArgumentException("Order dto must not be null");
        }

        User user = userDao.findOne(email)
                .orElseThrow(() -> new IllegalArgumentException("No such user with email: " + email));

        City from = cityRepository.findOneOpt(dto.getCityIdFrom())
                .orElseThrow(() -> new IllegalArgumentException("No such city with id: " + dto.getCityIdTo()));

        City to = cityRepository.findOneOpt(dto.getCityIdTo())
                .orElseThrow(() -> new IllegalArgumentException("No such city with id: " + dto.getCityIdTo()));

        orderRepository.save(new Order()
                .setOrderStatus(OrderStatus.OPEN)
                .setCustomer(user)
                .setCityFrom(from)
                .setCityTo(to)
                .setRegistrationDate(new Timestamp(new Date().getTime()))
                .setArrivalDate(dto.getArrivalDate())
                .setHeight(dto.getHeight())
                .setWidth(dto.getWidth())
                .setLength(dto.getLength())
                .setWeight(dto.getWeight())
                .setDescription(dto.getDescription())
        );
    }

    @Override
    public void removeOrder(Long id) {
        orderRepository.removeById(id);
    }

    @Override
    public void addFeedback(FeedbackDTO dto, String email) {
        if (dto == null) {
            throw new IllegalArgumentException("Feedback dto must not be null");}

        User user = userDao.findOne(email)
                .orElseThrow(() -> new IllegalArgumentException("No such user with email: " + email));
        /*changed by Ivan Rudnytskyi - the structure of FeedbackDTO was changed - entities Order and User are removed.
        *to get User use feedbackDTO.getUserId(), Order - feedbackDTO.getOrderId()
         */
        Order order = orderDao.findOne(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("No such order with id: " + dto.getOrderId()));

        Feedback feedback = new Feedback();
        feedback.setOrder(order);
        feedback.setUser(user);
        feedback.setRate(dto.getRate());
        feedback.setText(dto.getText());
        feedback.setApproved(false);
        feedbackDao.save(feedback);
    }

    public void changeStatus(Long offerId, Boolean offerStatus) {
        Boolean newOfferStatus=!offerStatus;
        Offer offer = offerDao.findOne(offerId)
                .orElseThrow(() -> new IllegalArgumentException("No such user with email: " + offerId));
        offer.setApproved(newOfferStatus);
        offerDao.save(offer);

    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderForListDto> findAllClosedOrders(String email) {
        return orderDao
                .findClosedOrders(email)
                .stream()
                .map(OrderForListDto::of)
                .collect(Collectors.toList());
    }


    /*--------------------IvanSynyshyn----------------------------*/
    @Override
    public List<OrderForListDto> getOrdersByCityFrom(String name) {
        List<OrderForListDto> result = new ArrayList<>();
        Long cityId = 0L;
        if (name == null) {
            throw new IllegalArgumentException("Write name of city");
        }
        for (City city : cityDao.getCityByName(name)) {
            cityId = city.getCityId();
            if (cityId == 0) {
                throw new IllegalArgumentException("Incorrect name of city");
            }
        }
        for (Order ord : orderDao.getOrderByCityFrom(cityId)) {
            result.add(OrderForListDto.of(ord));
        }
        return result;
    }
    @Override
    public List<OrderForListDto> getOrdersByCityTo(String name) {
        List<OrderForListDto> result = new ArrayList<>();
        Long cityId = 0L;
        if (name == null) {
            throw new IllegalArgumentException("Write name of city");
        }

        for (City city : cityDao.getCityByName(name)) {
            cityId = city.getCityId();
            if (cityId == 0) {
                throw new IllegalArgumentException("Incorrect name of city");
            }
        }
        for (Order ord : orderDao.getOrderByCityTo(cityId)) {
            result.add(OrderForListDto.of(ord));
        }
        return result;
    }

    @Override
    public List<OrderForListDto> getOrdersByWeight(BigDecimal weight) {
        List<OrderForListDto> result = new ArrayList<>();
        if (weight.doubleValue() <= 0.0) {
            throw new IllegalArgumentException("Incorect weight");
        }
        for (Order ord : orderDao.getOrderByWeight(weight)) {
            result.add(OrderForListDto.of(ord));
        }
        return result;
    }

    @Override
    public List<OrderForListDto> getOrdersByArriwalDate(Timestamp arrivalDate) {
        List<OrderForListDto> result = new ArrayList<>();
        Date date = new Date();
        if (arrivalDate.getTime() < date.getTime()) {
            throw new IllegalArgumentException("Wrong date format");
        }
        for (Order ord : orderDao.getOrderByArrivalDate(arrivalDate)) {
            result.add(OrderForListDto.of(ord));
        }
        return result;
    }

    @Override
    public List<OfferDto> addOffer(Long orderId) {
        List<OfferDto> result = new ArrayList<>();
        Order order = orderDao.findOne(orderId).get();
        order.getOffers().add(new Offer());
        Order updatedOrder = orderDao.update(order);
        for (Offer off : updatedOrder.getOffers()) {
            result.add(OfferDto.offerToOfferDto(off));
        }
        return result;
    }

}
