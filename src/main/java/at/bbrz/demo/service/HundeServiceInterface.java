package at.bbrz.demo.service;

import at.bbrz.demo.model.Hund;

import java.util.List;

public interface HundeServiceInterface {
    Hund getHundById(int hundId);
    List<Hund> getAllHunde();
    Hund deleteHundById(int hundId);
    Hund addHund(String name, int age);
}
