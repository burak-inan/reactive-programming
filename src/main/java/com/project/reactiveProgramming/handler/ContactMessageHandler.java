package com.project.reactiveProgramming.handler;

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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class ContactMessageHandler {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<ContactMessage> contactMessageMono = request.bodyToMono(ContactMessageRequest.class)
                .map(ContactMessageMapper::mapContactMessageRequestToContactMessage);

        return contactMessageMono.flatMap(this::isExistsByEmailAndDate)
                .flatMap(contactMessageRepository::save)
                .map(ContactMessageMapper::mapContactMessageToContactMessageResponse)
                .flatMap(t -> {
                    return ServerResponse.ok().body(Mono.just(t), ContactMessageResponse.class);
                });
    }

    private Mono<ContactMessage> isExistsByEmailAndDate(ContactMessage contactMessage) {
        return contactMessageRepository.existsByEmailAndDate(contactMessage.getEmail(), LocalDate.now())
                .flatMap(t -> {
                    if (t) {
                        throw new ConflictException(String.format(Messages.ALREADY_SEND_A_MESSAGE_TODAY, contactMessage.getEmail()));
                    }
                    return Mono.just(contactMessage);
                });
    }


    public Mono<ServerResponse> getById(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        Mono<ContactMessage> contactMessageMono = contactMessageRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format(Messages.RESOURCE_NOT_FOUND, ContactMessage.class.getSimpleName(), id))));
        return ServerResponse.ok()
                .body(contactMessageMono, ContactMessage.class);
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(contactMessageRepository.findAll(), ContactMessage.class);
    }


    public Mono<ServerResponse> getAllByPage(ServerRequest request){

        Pageable pageable= PageRequest.of(Integer.parseInt(request.queryParam("page").orElse("0")),
                                          Integer.parseInt(request.queryParam("size").orElse("10")),
                                          Sort.by(Sort.Direction.valueOf(request.queryParam("type").orElse("DESC")),request.queryParam("sort").orElse("name")));

        return contactMessageRepository.findAllBy(pageable)
                .map(ContactMessageMapper::mapContactMessageToContactMessageResponse)
                .collectList()
                .zipWith(this.contactMessageRepository.count())
                .map(t-> new PageImpl<>(t.getT1(),pageable,t.getT2()))
                .flatMap(t -> ServerResponse.ok().body(Mono.just(t), ContactMessageResponse.class));
    }


    public Mono<ServerResponse> deleteById(ServerRequest request){
        Integer id= Integer.valueOf(request.pathVariable("id"));
        return isExistById(id).switchIfEmpty(contactMessageRepository.deleteById(id))
                .thenReturn(ServerResponse.ok().bodyValue(ResponseMessage.builder()
                                                                            .message(String.format(Messages.DELETED_SUCCESSFULLY,"Contact Message",id))
                                                                            .httpStatus(HttpStatus.OK)
                                                                            .build()))
                .flatMap(t-> t);
    }

    private Mono<Void> isExistById(Integer id){
        return contactMessageRepository.existsById(id).flatMap(t-> {
            if(!t){
                throw new ResourceNotFoundException(String.format(Messages.RESOURCE_NOT_FOUND,"Contact Message",id));
            }
            return Mono.empty();
        });
    }


    public Mono<ServerResponse> updateById(ServerRequest request){
        Integer id= Integer.valueOf(request.pathVariable("id"));

        Mono<ContactMessage> contactMessageMono = request.bodyToMono(ContactMessageRequest.class)
                .map(ContactMessageMapper::mapContactMessageRequestToContactMessage);

        return isExistById(id)
                .cast(ContactMessage.class)
                .switchIfEmpty(contactMessageMono)
                .flatMap(t-> { t.setId(id);
                                return contactMessageRepository.save(t);})
                .map(ContactMessageMapper::mapContactMessageToContactMessageResponse)
                .flatMap(t-> ServerResponse.ok().body(Mono.just(t),ContactMessageResponse.class));
    }


    public Mono<ServerResponse> searchBySubject(ServerRequest request){

        String subject= request.queryParam("subject").orElse("message");

        Pageable pageable= PageRequest.of(Integer.valueOf(request.queryParam("page").orElse("0")),
                                          Integer.valueOf(request.queryParam("size").orElse("10")),
                                          Sort.by(Sort.Direction.valueOf(request.queryParam("type").orElse("DESC")), request.queryParam("sort").orElse("subject")));

        return contactMessageRepository.findAllBySubjectContaining(subject,pageable)
                .map(ContactMessageMapper::mapContactMessageToContactMessageResponse)
                .collectList()
                .zipWith(this.contactMessageRepository.count())
                .map(t-> new PageImpl<>(t.getT1(),pageable,t.getT2()))
                .flatMap(t -> ServerResponse.ok().body(Mono.just(t), ContactMessageResponse.class));
    }
}

