package com.softserve.edu.delivery.controller;

import com.softserve.edu.delivery.dto.FeedbackDto;
import com.softserve.edu.delivery.service.FeedbackService;
import com.softserve.edu.delivery.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static com.softserve.edu.delivery.config.SecurityConstraints.MODERATOR;

@RestController
@RequestMapping(path = "feedbacks")
public class FeedbackController {

    private final Logger logger = LoggerFactory.getLogger(FeedbackController.class.getName());
    private final HttpHeaders responseHeaders = new HttpHeaders();
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private NotificationService notificationService;
    private HttpStatus status;

    @PreAuthorize(MODERATOR)
    @RequestMapping(path = "all", method = RequestMethod.POST)
    List<FeedbackDto> getAllFeedbacks(@RequestParam("text") String text,
                                      @RequestParam("rate") String rateString,
                                      @RequestParam("userName") String userName,
                                      @RequestParam("transporterName") String transporterName,
                                      @RequestParam("createdOn") String createdOnString,
                                      @RequestParam("approved") String approvedString,
                                      @RequestParam("sortBy") String sortBy,
                                      @RequestParam("sortDesc") String sortDesc,
                                      @RequestParam("currentPage") int currentPage,
                                      @RequestParam("itemsPerPage") int itemsPerPage) {

        logger.info("Before feedbackService.findFiltered()");
        return feedbackService.findFiltered(text, rateString, userName, transporterName, createdOnString,
                approvedString, sortBy, sortDesc, currentPage, itemsPerPage);
    }

    @PreAuthorize(MODERATOR)
    @RequestMapping(path = "totalItems", method = RequestMethod.GET)
    long getTotalItemsNumber() {
        logger.info("Before feedbackService.getTotalItemsNumber()");
        return feedbackService.getTotalItemsNumber();
    }

    @PreAuthorize(MODERATOR)
    @RequestMapping(path = {"updateFeedback"}, method = RequestMethod.PUT)
    ResponseEntity updateFeedback(@RequestBody FeedbackDto feedbackDTO) {
        logger.info("Before feedbackService.update(feedbackDTO)");
        status = HttpStatus.OK;
        responseHeaders.set("message", "OK");

        try {
            feedbackService.update(feedbackDTO);
        } catch (NoSuchElementException e) {
            logger.info("Exception while trying to update feedback with id " + feedbackDTO.getFeedbackId() +
                    " in feedbackService.update(feedbackDTO) " + e.getMessage());
            status = HttpStatus.NOT_FOUND;
            responseHeaders.set("message", e.getMessage());
        }
        logger.info("After feedbackService.update(feedbackDTO)");
        notificationService.updateFeedback(feedbackDTO);
        return new ResponseEntity(responseHeaders, status);
    }

    @PreAuthorize(MODERATOR)
    @RequestMapping(path = {"deleteFeedback/{feedbackId}"}, method = RequestMethod.DELETE)
    ResponseEntity deleteFeedback(@PathVariable Long feedbackId) {
        logger.info("Before feedbackService.delete(feedbackId)");
        status = HttpStatus.OK;
        responseHeaders.set("message", "OK");

        try {
            feedbackService.delete(feedbackId);
        } catch (NoSuchElementException e) {
            logger.info("Exception while trying to delete feedback with id " + feedbackId +
                    " in feedbackService.delete(feedbackDTO) " + e.getMessage());
            status = HttpStatus.NOT_FOUND;
            responseHeaders.set("message", e.getMessage());
        }
        logger.info("After feedbackService.delete(feedbackDTO)");

        return new ResponseEntity(responseHeaders, status);
    }
}