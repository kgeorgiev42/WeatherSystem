package main.com.isoft.rest.controller;

import java.util.List;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import main.com.isoft.rest.db.model.ConfigData;
import main.com.isoft.rest.db.repository.ConfigRepository;
import main.com.isoft.rest.exception.ResourceNotFoundException;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ConfigController {
    
    @Autowired
    private ConfigRepository config_repos;
    
    @GetMapping("/config")
    public Page<ConfigData> getConfigData(Pageable pageable) {
        return config_repos.findAll(pageable);
    }
    
    @GetMapping("/config/id/{configId}")
    public Optional<ConfigData> getConfigData(@PathVariable Long configId) {
        return config_repos.findById(configId);
    }
    
    @GetMapping("/config/site/{configSite}")
    public List<ConfigData> getConfigDataSite(@PathVariable String configSite) {
        return config_repos.findConfigBySite(configSite);
    }
    
    @PostMapping("/config")
    public ConfigData createConfig(@Valid @RequestBody ConfigData config) {
        return config_repos.save(config);
    }
    
    @PutMapping("/config/{configId}")
    public ConfigData updateConfig(@PathVariable Long configId,
                                   @Valid @RequestBody ConfigData configRequest) {
        return config_repos.findById(configId)
                .map(config -> {
                    config.setCity(configRequest.getCity());
                    config.setCountry(configRequest.getCountry());
                    config.setSite(configRequest.getSite());
                    return config_repos.save(config);
                }).orElseThrow(() -> new ResourceNotFoundException("Config not found with id " + configId));
    }
    
    @DeleteMapping("/config/{configId}")
    public ResponseEntity<?> deleteConfig(@PathVariable Long configId) {
        return config_repos.findById(configId)
                .map(config -> {
                    config_repos.delete(config);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Config not found with id " + configId));
    }
}
