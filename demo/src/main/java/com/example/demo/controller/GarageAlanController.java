package com.example.demo.controller;

import com.example.demo.exception.GarajboyutAlreadyExistsException;
import com.example.demo.exception.GarajboyutNotFoundException;
import com.example.demo.exception.GirisNotAcceptableException;
import com.example.demo.model.GarageAlan;
import com.example.demo.repository.GarageAlanRepo;
import com.example.demo.repository.GarageRepo;
import com.example.demo.service.Garaj;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static org.springframework.http.HttpStatus.*;

@RestController
@Api(value = "Garage Alan API documentation")
public class GarageAlanController {

    @Autowired
    private GarageAlanRepo garagealanrepo;
    @Autowired
    private GarageRepo garagerepo;

    Garaj garaj = new Garaj();
    GarageAlan newgaragealan = new GarageAlan();

    public int getAlan(){
        List<GarageAlan> liste = garagealanrepo.findAll();
        return liste.get(0).getAlan();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startFirst(){
        if (garagealanrepo.count() != 0) {

            if (garagerepo.count() != 0)
                garaj.setGarajBoyut(getAlan() - garagerepo.sumAlan());
            else
                garaj.setGarajBoyut(getAlan());
        }
    }

    @PostMapping(path="/alan/{alan}")
    @ApiOperation(value = "Garaj boyutu belirlenir", authorizations = { @Authorization(value="apiKey") })
    public ResponseEntity<String> setAlan(@PathVariable int alan){
        if (alan >= 5 && alan <= 50) {

            if (garagealanrepo.count() == 0) {
                garaj.setGarajBoyut(alan);
                newgaragealan.setAlan(alan);
                garagealanrepo.save(newgaragealan);

                return new ResponseEntity<>(OK);
            } else
                throw new GarajboyutAlreadyExistsException("Garaj boyutu zaten belirlenmi??. De??i??tirmek i??in update fonksiyonunu kullan??n...");
        }
        else
            throw new GirisNotAcceptableException("Garaj Boyutu 5-50 aras??nda olmal??d??r...");
    }

    @PutMapping("/alanupdate/{alan}")
    @ApiOperation(value = "Garaj boyutu g??ncellenir", authorizations = { @Authorization(value="apiKey") })
    public ResponseEntity<String> updateAlan(@PathVariable int alan){
        if (alan >= 5 && alan <= 50) {
            newgaragealan.setAlan(alan);

            if (garagerepo.count() != 0) {

                if (alan < garagerepo.sumAlan()) {
                    return new ResponseEntity<>("Garaj ara??lar??n toplam boyutundan daha k??????k olamaz", NOT_ACCEPTABLE);
                } else {
                    garaj.setGarajBoyut(alan - garagerepo.sumAlan());
                    updateAlanByAlan(newgaragealan);
                    return new ResponseEntity<>(OK);
                }
            }
            else {
                updateAlanByAlan(newgaragealan);
                return new ResponseEntity<>(OK);
            }
        }
        else
            throw new GirisNotAcceptableException("Garaj Boyutu 5-50 aras??nda olmal??d??r...");
    }

    public void updateAlanByAlan(GarageAlan newgaragealan){
        GarageAlan oldAlan = getAlanByAlan(getAlan());
        oldAlan.setAlan(newgaragealan.getAlan());

        garagealanrepo.save(oldAlan);
    }

    public GarageAlan getAlanByAlan(int alan) {
        return garagealanrepo.findByAlan(alan)
                .orElseThrow(() -> new GarajboyutNotFoundException("Belirlenmi?? bir garaj boyutu bulunamad??: " + alan));
    }

    @DeleteMapping("/alansil")
    @ApiOperation(value = "Garaj boyutu ve giri?? yap??lan ara??lar silinir", authorizations = { @Authorization(value="apiKey") })
    public ResponseEntity<Void> deleteAlan(){
        garagealanrepo.deleteAll();
        garagerepo.deleteAll();
        return new ResponseEntity<>(OK);
    }

    @ExceptionHandler(GarajboyutAlreadyExistsException.class)
    public ResponseEntity<String> handleGarajboyutAlreadyExistException(GarajboyutAlreadyExistsException ex){
        return new ResponseEntity<>(ex.getMessage(), CONFLICT);
    }

    @ExceptionHandler(GarajboyutNotFoundException.class)
    public ResponseEntity<String> handleGarajboyutNotFoundException(GarajboyutNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler(GirisNotAcceptableException.class)
    public ResponseEntity<String> handleGirisNotAcceptableException(GirisNotAcceptableException ex){
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }
}
