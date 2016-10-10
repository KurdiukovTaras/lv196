package com.softserve.edu.delivery.controller;

import com.softserve.edu.delivery.dto.FeedbackDTO;
import com.softserve.edu.delivery.service.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    Logger logger = LoggerFactory.getLogger(FeedbackController.class.getName());

    @RequestMapping(path = "all", method = RequestMethod.GET)
    List<FeedbackDTO> getAllFeedbacks(@RequestParam("sortDesc") boolean sortDesc) {
        logger.info("Method FeedbackController.findAll()");
        if (sortDesc) {
            return feedbackService.findAll();
        }
        return feedbackService.findByOrderByFeedbackIdDesc();


    }

    @RequestMapping(path = "id", method = RequestMethod.GET)
    FeedbackDTO getFeedbackById(@RequestParam("id") String feedbackIdString) {
        logger.info("Method FeedbackController.findByFeedbackId()");
        try {
            Long feedbackId = Long.parseLong(feedbackIdString.replaceAll("\\D+", ""));
            return feedbackService.findByFeedbackId(feedbackId);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return new FeedbackDTO();
    }

    @RequestMapping(path = "id/greater-than", method = RequestMethod.GET)
    List<FeedbackDTO> findByFeedbackIdGreaterThan(@RequestParam("id") String feedbackIdString,
                                                  @RequestParam("sortDesc") boolean sortDesc) {
        try {
            Long feedbackId = Long.parseLong(feedbackIdString.replaceAll("\\D+", ""));
            if (sortDesc) {
                return feedbackService.findByFeedbackIdGreaterThan(feedbackId);
            }
            return feedbackService.findByFeedbackIdGreaterThanOrderByFeedbackIdDesc(feedbackId);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @RequestMapping(path = "id/less-than", method = RequestMethod.GET)
    List<FeedbackDTO> findByFeedbackIdLessThan(@RequestParam("id") String feedbackIdString,
                                               @RequestParam("sortDesc") boolean sortDesc) {
        Long feedbackId = 0L;
        try {
            feedbackId = Long.parseLong(feedbackIdString.replaceAll("\\D+", ""));
            if (sortDesc) {
                return feedbackService.findByFeedbackIdLessThan(feedbackId);
            }
            return feedbackService.findByFeedbackIdLessThanOrderByFeedbackIdDesc(feedbackId);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @RequestMapping(path = "text", method = RequestMethod.GET)
    List<FeedbackDTO> getFeedbacksByText(@RequestParam("text") String feedbackText) {
        return feedbackService.findByTextContaining(feedbackText);
    }

    @RequestMapping(path = "rate", method = RequestMethod.GET)
    List<FeedbackDTO> findByRate(@RequestParam("rate") String rateString) {
        try {
            Integer rate = Integer.parseInt(rateString.replaceAll("\\D+", ""));
            return feedbackService.findByRate(rate);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @RequestMapping(path = "rate/greater-than", method = RequestMethod.GET)
    List<FeedbackDTO> findByRateGreaterThan(@RequestParam("rate") String rateString) {
        try {
            Integer rate = Integer.parseInt(rateString.replaceAll("\\D+", ""));
            return feedbackService.findByRateGreaterThan(rate);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @RequestMapping(path = "rate/less-than", method = RequestMethod.GET)
    List<FeedbackDTO> findByRateLessThan(@RequestParam("rate") String rateString) {
        try {
            Integer rate = Integer.parseInt(rateString.replaceAll("\\D+", ""));
            return feedbackService.findByRateLessThan(rate);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @RequestMapping(path = "userName", method = RequestMethod.GET)
    List<FeedbackDTO> getFeedbacksByUserName(@RequestParam("userName") String userName) {
        return feedbackService.findByUserFirstNameOrLastName(userName);
    }

    @RequestMapping(path = "transporterName", method = RequestMethod.GET)
    List<FeedbackDTO> getFeedbacksByTransporterName(@RequestParam("transporterName") String transporterName) {
        return feedbackService.findByTransporterFirstNameOrLastName(transporterName);
    }

    @RequestMapping(path = "feedbackDate", method = RequestMethod.GET)
    List<FeedbackDTO> getFeedbacksByDate(@RequestParam("feedbackDate") String feedbackDateString) {
        try {
            long feedbackDateInMillis = Long.parseLong(feedbackDateString);
            Timestamp createdOn = new Timestamp(feedbackDateInMillis);
            return feedbackService.findByCreatedOn(createdOn);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @RequestMapping(path = "approved", method = RequestMethod.GET)
    List<FeedbackDTO> getFeedbacksByApproved(@RequestParam("approved") String approvedString) {
        Boolean approved = true;
        try {
            approved = Boolean.parseBoolean(approvedString);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return feedbackService.findByApproved(approved);
    }

    @RequestMapping(path = {"changeFeedbackStatus"}, method = RequestMethod.PUT)
    void changeFeedbackStatus(@RequestBody FeedbackDTO feedbackDTO) {
        logger.info("In method FeedbackController.changeFeedbackStatus()");
        feedbackService.update(feedbackDTO);
    }
}