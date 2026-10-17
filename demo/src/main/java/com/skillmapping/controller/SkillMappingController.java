package com.skillmapping.controller;

import com.skillmapping.service.SkillMappingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/assessment-collection")
@CrossOrigin
public class SkillMappingController {

    private final SkillMappingService skillMappingService;

    public SkillMappingController(
            SkillMappingService skillMappingService
    ) {
        this.skillMappingService = skillMappingService;
    }

    @GetMapping("/students1/{registerNumber}")
    public ResponseEntity<Map<String, Object>> generateSkillGapAnalysis(
            @PathVariable String registerNumber
    ) {

        Map<String, Object> result =
                skillMappingService.generateSkillGapAnalysis(
                        registerNumber
                );

        return ResponseEntity.ok(result);
    }
}