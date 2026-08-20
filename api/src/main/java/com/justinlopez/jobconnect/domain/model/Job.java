package com.justinlopez.jobconnect.domain.model;

import com.justinlopez.jobconnect.domain.model.enums.JobStatus;
import com.justinlopez.jobconnect.domain.model.enums.OfferStatus;
import com.justinlopez.jobconnect.domain.model.vo.Address;
import com.justinlopez.jobconnect.domain.model.vo.Money;
import com.justinlopez.jobconnect.domain.model.vo.UserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a job posted by a client in the JobConnect platform.
 * A job can have multiple offers from professionals, and its status can change based on the actions taken by the client and professionals.
 */
public class Job {

    private final UUID id;
    private final String title;
    private final String description;
    private final Category category;
    private final Money budget;
    private final Address location;
    private final UserId clientId;
    private UserId selectedProfessionalId;
    private JobStatus status;
    private final List<Offer> offers;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Job(UUID id, String title, String description, Category category, Money budget, Address location, UserId clientId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.budget = budget;
        this.location = location;
        this.clientId = clientId;
        this.status = JobStatus.PUBLISHED;
        this.offers = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Adds an offer to the job.
     * This method checks if the job is in a state that allows offers to be added and
     * ensures that the professional making the offer is not the client who posted the job.
     * It also checks if the professional has already made an offer on this job to prevent duplicate
     * @param offer The offer to be added to the job.
     */
    public void addOffer(Offer offer) {
        if (this.status != JobStatus.PUBLISHED) {
            throw new IllegalStateException("Cannot add offers to a job that is not published.");
        }
        if (this.clientId.equals(offer.getProfessionalId())) {
            throw new IllegalArgumentException("Client cannot make an offer on their own job.");
        }
        // Check if the professional has already made an offer on this job
        boolean alreadyOffered = this.offers.stream()
                .anyMatch(existingOffer -> existingOffer.getProfessionalId().equals(offer.getProfessionalId()));
        if (alreadyOffered) {
            throw new IllegalArgumentException("Professional has already made an offer on this job.");
        }
        this.offers.add(offer);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Accepts an offer for the job.
     * This method will change the status of the job to IN_PROGRESS and set the selected professional.
     * It will also reject all other pending offers for the job.
     * @param offerId The ID of the offer to be accepted.
     */
    public void acceptOffer(UUID offerId) {

        // Ensure the job is in a state that allows accepting offers
        if (this.status != JobStatus.PUBLISHED) {
            throw new IllegalStateException("Cannot accept offers for a job that is not published.");
        }

        // Find the offer to accept by its ID
        Offer offerToAccept = this.offers.stream()
                .filter(offer -> offer.getId().equals(offerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Offer not found for this job."));

        // Ensure the offer is still pending before accepting
        if (offerToAccept.getStatus() != OfferStatus.PENDING) {
            throw new IllegalStateException("Only pending offers can be accepted.");
        }

        // Accept the selected offer and reject all other pending offers
        this.offers.forEach(o -> {
            if (o.getId().equals(offerId)) {
                o.accept();
            } else {
                o.reject();
            }
        });

        this.selectedProfessionalId = offerToAccept.getProfessionalId();
        this.status = JobStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks the job as pending confirmation.
     * This method can only be called if the job is currently in progress.
     * It updates the job's status to PENDING_CONFIRMATION and records the time of the update.
     */
    public void markAsPendingConfirmation() {
        if (this.status != JobStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only jobs IN_PROGRESS can be marked as pending");
        }
        this.status = JobStatus.PENDING_CONFIRMATION;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Confirms the completion of the job.
     * This method can only be called if the job is currently pending confirmation.
     * It updates the job's status to COMPLETED and records the time of the update.
     */
    public void confirmCompletion() {
        if (this.status != JobStatus.PENDING_CONFIRMATION) {
            throw new IllegalStateException("Only jobs PENDING_CONFIRMATION can be completed");
        }
        this.status = JobStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancels the job.
     * This method can only be called if the job is not already completed or canceled.
     * It updates the job's status to CANCELED and records the time of the update.
     */
    public void cancel() {
        if (this.status == JobStatus.COMPLETED || this.status == JobStatus.CANCELED) {
            throw new IllegalStateException("Cannot cancel a job that is already completed or canceled.");
        }
        this.status = JobStatus.CANCELED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Initializes the list of offers for the job.
     * This method clears the existing offers and adds the new ones.
     *
     * @param offers The list of offers to initialize.
     */
    public void initializeOffers(List<Offer> offers) {
        this.offers.clear();
        this.offers.addAll(offers);
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public Money getBudget() {
        return budget;
    }

    public Address getLocation() {
        return location;
    }

    public UserId getClientId() {
        return clientId;
    }

    public UserId getSelectedProfessionalId() {
        return selectedProfessionalId;
    }

    public JobStatus getStatus() {
        return status;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setSelectedProfessionalId(UserId selectedProfessionalId) {
        this.selectedProfessionalId = selectedProfessionalId;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
