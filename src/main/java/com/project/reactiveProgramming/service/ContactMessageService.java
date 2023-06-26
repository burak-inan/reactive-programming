package com.project.reactiveProgramming.service;

import com.project.reactiveProgramming.entity.ContactMessage;
import com.project.reactiveProgramming.exception.ConflictException;
import com.project.reactiveProgramming.exception.ResourceNotFoundException;
import com.project.reactiveProgramming.payload.mappers.ContactMessageMapper;
import com.project.reactiveProgramming.payload.request.ContactMessageRequest;
import com.project.reactiveProgramming.payload.response.ContactMessageResponse;
import com.project.reactiveProgramming.payload.response.ResponseMessage;
import com.project.reactiveProgramming.repository.ContactMessageRepository;
import com.project.reactiveProgramming.utils.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class ContactMessageService {

    @Autowired
    private ContactMessageRepository contactMessageRepository;


    public Mono<ContactMessageResponse> saveContactMessage(ContactMessageRequest contactMessageRequest) {

        return contactMessageRepository.existsByEmailAndDate(contactMessageRequest.getEmail(), LocalDate.now())
                .flatMap(t -> {
                    if (t) {
                        throw new ConflictException(String.format(Messages.ALREADY_SEND_A_MESSAGE_TODAY, contactMessageRequest.getEmail()));
                    }
                    return Mono.just(ContactMessageMapper.mapContactMessageRequestToContactMessage(contactMessageRequest));
                })
                .flatMap(contactMessageRepository::save)
                .map(ContactMessageMapper::mapContactMessageToContactMessageResponse);
    }


    public Mono<ContactMessageResponse> getContactMessageById(Integer id) {

        return contactMessageRepository.findById(id)
                .mapNotNull(ContactMessageMapper::mapContactMessageToContactMessageResponse)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format(Messages.RESOURCE_NOT_FOUND, ContactMessage.class.getSimpleName(), id))));

    }

    public Flux<ContactMessageResponse> getAllContactMessageByEmail(String email) {

        return contactMessageRepository.findAllByEmail(email)
                .mapNotNull(ContactMessageMapper::mapContactMessageToContactMessageResponse)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format(Messages.CONTACT_MESSAGE_NOT_FOUNT, ContactMessage.class.getSimpleName(), email))));

    }

    public Mono<ContactMessageResponse> getContactMessageByEmailAndDate(String email, String date) {

        LocalDate date1= LocalDate.parse(date);

        return contactMessageRepository.findByEmailAndDate(email, date1)
                .map(ContactMessageMapper::mapContactMessageToContactMessageResponse)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format(Messages.CONTACT_MESSAGE_NOT_FOUNT, ContactMessage.class.getSimpleName(), email))));

    }

    public Mono<ContactMessageResponse> getContactMessageByEmailAndDateWithBody(ContactMessageRequest contactMessageRequest) {

        return contactMessageRepository.findByEmailAndDate(contactMessageRequest.getEmail(), contactMessageRequest.getDate())
                .mapNotNull(ContactMessageMapper::mapContactMessageToContactMessageResponse)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format(Messages.CONTACT_MESSAGE_NOT_FOUNT, ContactMessage.class.getSimpleName(), contactMessageRequest.getEmail()))));

    }

    public Flux<ContactMessageResponse> getAllContactMessage() {

        return contactMessageRepository.findAll()
                .map(ContactMessageMapper::mapContactMessageToContactMessageResponse);
    }


    public Mono<Page<ContactMessageResponse>> getAllContactMessageByPage(int page, int size, String sort, Sort.Direction type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(type, sort));

        return contactMessageRepository.findAllBy(pageable)
                .map(ContactMessageMapper::mapContactMessageToContactMessageResponse)
                .collectList()
                .zipWith(this.contactMessageRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }

    public Mono<ResponseMessage> deleteById(Integer id) {

        return isExistById(id).switchIfEmpty(contactMessageRepository.deleteById(id))
                .thenReturn(ResponseMessage.builder()
                        .message(String.format(Messages.DELETED_SUCCESSFULLY, "Contact Message", id))
                        .httpStatus(HttpStatus.OK)
                        .build()
                );
    }

    private Mono<Void> isExistById(Integer id) {
        return contactMessageRepository.existsById(id).flatMap(t -> {
            if (!t) {
                throw new ResourceNotFoundException(String.format(Messages.RESOURCE_NOT_FOUND, "Contact Message", id));
            }
            return Mono.empty();
        });
    }

    public Mono<ResponseMessage<ContactMessageResponse>> updateById(Integer id, ContactMessageRequest contactMessageRequest) {

        ContactMessage updatedContactMessage = ContactMessageMapper.mapContactMessageRequestToContactMessage(contactMessageRequest);
        updatedContactMessage.setId(id);

        return isExistById(id)
                .cast(ContactMessage.class)
                .switchIfEmpty(Mono.just(updatedContactMessage))
                .flatMap(contactMessageRepository::save)
                .map(t-> ContactMessageMapper.mapContactMessageToResponseMessage("Contact Message updated successfully", HttpStatus.OK, t));
    }

    public Mono<Page<ContactMessageResponse>> SearchBySubject(String subject, int page, int size, String sort, Sort.Direction type) {
        Pageable pageable= PageRequest.of(page,size,Sort.by(type,sort));

        return contactMessageRepository.findAllBySubjectContaining(subject,pageable)
                .map(ContactMessageMapper::mapContactMessageToContactMessageResponse)
                .collectList()
                .zipWith(this.contactMessageRepository.count())
                .map(t-> new PageImpl<>(t.getT1(),pageable,t.getT2()));
    }
}
