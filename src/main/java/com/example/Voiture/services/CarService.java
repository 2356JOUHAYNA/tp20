package com.example.Voiture.services;

import com.example.Voiture.entities.Car;
import com.example.Voiture.models.CarResponse;
import com.example.Voiture.models.Client;
import com.example.Voiture.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RestTemplate restTemplate;

    // ✅ Appel via la Gateway
    private static final String CLIENT_SERVICE_URL =
            "http://localhost:8888/SERVICE-CLIENT/api/client/";

    // ✅ Récupérer toutes les voitures
    public List<CarResponse> findAll() {
        return carRepository.findAll()
                .stream()
                .map(this::mapToCarResponse)
                .collect(Collectors.toList());
    }

    // ✅ Récupérer une voiture par ID
    public CarResponse findById(Long id) throws Exception {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new Exception("Voiture non trouvée avec l'ID: " + id));

        return mapToCarResponse(car);
    }

    // ✅ Mapping PRO avec appel REST
    private CarResponse mapToCarResponse(Car car) {

        Client client = null;
        try {
            client = restTemplate.getForObject(
                    CLIENT_SERVICE_URL + car.getClient_id(),
                    Client.class
            );
        } catch (Exception e) {
            System.err.println("Erreur Client: " + e.getMessage());
        }

        return new CarResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getMatricule(),
                client
        );
    }
}
