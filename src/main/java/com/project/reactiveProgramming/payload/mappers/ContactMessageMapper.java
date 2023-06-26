package com.project.reactiveProgramming.payload.mappers;

import com.project.reactiveProgramming.payload.request.ContactMessageRequest;
import com.project.reactiveProgramming.entity.ContactMessage;
import com.project.reactiveProgramming.payload.response.ContactMessageResponse;
import com.project.reactiveProgramming.payload.response.ResponseMessage;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;


public class ContactMessageMapper {

    public static ContactMessageResponse mapContactMessageToContactMessageResponse(ContactMessage contactMessage){
        return ContactMessageResponse.builder()
                .name(contactMessage.getName())
                .subject(contactMessage.getSubject())
                .message(contactMessage.getMessage())
                .email(contactMessage.getEmail())
                .date(LocalDate.now())
                .build();
    }


    // TODO please check builder design pattern
    // I would give a name to this method like mapContactMessageRequestToContactMessage
    public static ContactMessage mapContactMessageRequestToContactMessage(ContactMessageRequest contactMessageRequest){
        return ContactMessage.builder()
                .name(contactMessageRequest.getName())
                .subject(contactMessageRequest.getSubject())
                .message(contactMessageRequest.getMessage())
                .email(contactMessageRequest.getEmail())
                .date(LocalDate.now())
                .build();
    }

    public static ResponseMessage<ContactMessageResponse> mapContactMessageToResponseMessage(String message, HttpStatus httpStatus ,ContactMessage contactMessage){
        return ResponseMessage.<ContactMessageResponse>builder()
                // this message should be moved to Messages class and called from there
                .message(message)
                .httpStatus(httpStatus)
                .object(mapContactMessageToContactMessageResponse(contactMessage))
                .build();
    }
}
