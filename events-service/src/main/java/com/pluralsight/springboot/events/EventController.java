package com.pluralsight.springboot.events;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class EventController {

    private final OrganizerRepository organiserRepository;
    private final EventRepository eventRepository;
    private final ProductRepository productRepository;

    public EventController(OrganizerRepository organiserRepository, EventRepository eventRepository, ProductRepository productRepository) {
        this.organiserRepository = organiserRepository;
        this.eventRepository = eventRepository;
        this.productRepository = productRepository;
    }

    @GetMapping(path = "/organisers")
    public List<Organizer> getOrganisers() {
        return organiserRepository.findAll();
    }

    @GetMapping(path = "/events")
    public List<Event> getEventsByOrganiser(@RequestParam("organiserId") int organiserId) {
        return eventRepository.findByOrganizerId(organiserId);
    }

    @GetMapping(path = "/events/{id}")
    public Event getEventById(@PathVariable("id") int eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException("Event with id " + eventId + " not found"));
    }

    @GetMapping(path = "/products")
    public List<Product> getProductByEvent(@RequestParam("eventId") int eventId) {
        return productRepository.findByEventId(eventId);
    }
}