package com.paymentprocessor.startup;

import com.paymentprocessor.config.ClientProperties;
import com.paymentprocessor.entity.Client;
import com.paymentprocessor.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StartupService {

    private final ClientProperties clientProperties;
    private final ClientRepository clientRepository;

    @EventListener
    public void applicationStarted(ApplicationStartedEvent e) {
        clientRepository.saveAll(clientProperties.getUsernames().stream()
                .map(username -> new Client(username))
                .collect(Collectors.toList()));
    }

}
