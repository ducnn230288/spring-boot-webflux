package com.pluralsight.springboot.registration;

import com.pluralsight.springboot.events.Event;
import com.pluralsight.springboot.events.EventsClient;
import com.pluralsight.springboot.events.Product;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController()
@RequestMapping(path = "/registrations")
public class RegistrationController {

    private final EventsClient eventsClient;
    private final RegistrationRepository registrationRepository;

    public RegistrationController(EventsClient eventsClient, RegistrationRepository registrationRepository) {
        this.eventsClient = eventsClient;
        this.registrationRepository = registrationRepository;
    }

    @PostMapping
    public Registration create(@RequestBody @Valid Registration registration) {
        Product product = eventsClient.getProductById(registration.productId());
        Event event = eventsClient.getEventById(product.eventId());

        String ticketCode = UUID.randomUUID().toString();

        return registrationRepository.save(
                new Registration(
                        null,
                        registration.productId(),
                        event.name(), product.price(),
                        ticketCode,
                        registration.attendeeName()
                )
        );
    }

    @GetMapping(path = "/{ticketCode}")
    public Registration read(@PathVariable("ticketCode") String ticketCode) {
        return registrationRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new NoSuchElementException("Registration with ticket code " + ticketCode + " not found"));
    }

    @PutMapping
    public Registration update(@RequestBody @Valid Registration registration) {
        String ticketCode = registration.ticketCode();
        Product product = eventsClient.getProductById(registration.productId());
        Event event = eventsClient.getEventById(product.eventId());

        var existing = registrationRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new NoSuchElementException("Registration with ticket code " + ticketCode + "not found"));

        return registrationRepository.save(
                new Registration(
                        existing.id(),
                        existing.productId(),
                        event.name(), product.price(),
                        ticketCode,
                        existing.attendeeName()
                )
        );
    }

    @DeleteMapping(path = "/{ticketCode}")
    public void delete(@PathVariable("ticketCode") String ticketCode) {
        registrationRepository.deleteByTicketCode(ticketCode);
    }

}
