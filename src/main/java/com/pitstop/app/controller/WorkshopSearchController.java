package com.pitstop.app.controller;

import com.pitstop.app.dto.WorkshopUserFilterRequest;
import com.pitstop.app.dto.WorkshopUserFilterResponse;
import com.pitstop.app.service.impl.WorkshopSearchServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workshops")
@RequiredArgsConstructor
public class WorkshopSearchController {
    private final WorkshopSearchServiceImpl workshopSearchService;

    @PostMapping("/filterWorkshops")
    public ResponseEntity<?> filterWorkshops(@RequestBody WorkshopUserFilterRequest workshopUserRequest){
        try{
            List<WorkshopUserFilterResponse> results = workshopSearchService.filterWorkshopUsers(workshopUserRequest);
            return ResponseEntity.ok().body(results);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
