package com.project.reactiveProgramming.router;

import com.project.reactiveProgramming.handler.ContactMessageHandler;
import com.project.reactiveProgramming.payload.request.ContactMessageRequest;
import com.project.reactiveProgramming.payload.response.ContactMessageResponse;
import com.project.reactiveProgramming.payload.response.ResponseMessage;
import com.project.reactiveProgramming.service.ContactMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("stream")
public class ContactMessageRouter {

    @Autowired
    private ContactMessageHandler handler;

    @Autowired
    private ContactMessageService service;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(){
        return RouterFunctions.route()
                .GET("router/contactMessage/getById/{id}",handler::getById)
                .GET("router/contactMessage/getAll",handler::getAll)
                .POST("router/contactMessage/save",handler::save)
                .GET("router/contactMessage/getAllByPage",handler::getAllByPage)
                .DELETE("router/contactMessage/deleteById/{id}",handler::deleteById)
                .PUT("router/contactMessage/updateById/{id}",handler::updateById)
                .GET("router/contactMessage/searchBySubject",handler::searchBySubject)
                .build();
    }

    @PostMapping ("/save")
    public Mono<ContactMessageResponse> save(@RequestBody ContactMessageRequest contactMessageRequest){
        return service.saveContactMessage(contactMessageRequest);
    }

    @GetMapping("/getById/{id}")
    public Mono<ContactMessageResponse> getById(@PathVariable Integer id){
        return service.getContactMessageById(id);
    }

    @GetMapping("/getAllByEmail")
    public Flux<ContactMessageResponse> getAllByEmail(@RequestParam String email){
        return service.getAllContactMessageByEmail(email);
    }

    @GetMapping("/getByEmailAndDate")
    public Mono<ContactMessageResponse> getByEmailAndDate(@RequestParam String email,@RequestParam String date){
        return service.getContactMessageByEmailAndDate(email,date);
    }


    @GetMapping("/getByEmailAndDateWithBody")
    public Mono<ContactMessageResponse> getByEmailAndDateWithBody(@RequestBody ContactMessageRequest contactMessageRequest){
        return service.getContactMessageByEmailAndDateWithBody(contactMessageRequest);
    }


    @GetMapping(value = "/getAll",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ContactMessageResponse> getAll(){
        return service.getAllContactMessage();
    }


    @GetMapping("/getAllByPage")
    public Mono<Page<ContactMessageResponse>> getAllByPage(@RequestParam int page,
                                                           @RequestParam int size,
                                                           @RequestParam String sort,
                                                           @RequestParam Sort.Direction type){
        return service.getAllContactMessageByPage(page,size,sort,type);
    }


    @DeleteMapping("/deleteById/{id}")
    public Mono<ResponseMessage> deleteById(@PathVariable Integer id){
        return service.deleteById(id);
    }


    @PutMapping("/updateById/{id}")
    public Mono<ResponseMessage<ContactMessageResponse>> updateById(@RequestBody ContactMessageRequest contactMessageRequest,@PathVariable Integer id){
        return service.updateById(id,contactMessageRequest);
    }


    @GetMapping("/searchBySubject")
    public Mono<Page<ContactMessageResponse>> searchBySubject(@RequestParam(value= "subject") String subject,
                                                        @RequestParam(value= "page", defaultValue= "0") int page,
                                                        @RequestParam(value= "size", defaultValue= "10") int size,
                                                        @RequestParam(value= "sort", defaultValue= "date") String sort,
                                                        @RequestParam(value= "type", defaultValue= "desc") Sort.Direction type){
        return service.SearchBySubject(subject,page,size,sort,type);
    }

}
