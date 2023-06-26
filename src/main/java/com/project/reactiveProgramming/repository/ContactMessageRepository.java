package com.project.reactiveProgramming.repository;

import com.project.reactiveProgramming.entity.ContactMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface ContactMessageRepository extends R2dbcRepository<ContactMessage,Integer> {

    Mono<Boolean> existsByEmailAndDate(String email,LocalDate date);

    Flux<ContactMessage> findAllByEmail(String email);

    @Query(value= "Select * From contact_message c Where c.email= :email And c.date= :date")
    Mono<ContactMessage> findByEmailAndDate(String email, LocalDate date);


    Flux<ContactMessage> findAllBy(Pageable pageable);

    Flux<ContactMessage> findAllBySubjectContaining(String subject, Pageable pageable);
}
