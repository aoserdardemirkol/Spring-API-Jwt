package com.example.demo.controller;

import com.example.demo.exception.AracAlreadyExistsException;
import com.example.demo.exception.AracNotFoundException;
import com.example.demo.exception.GirisNotAcceptableException;
import com.example.demo.exception.TanımlıGarajNotExistsException;
import com.example.demo.model.Garage;
import com.example.demo.model.GarageAlan;
import com.example.demo.repository.GarageAlanRepo;
import com.example.demo.repository.GarageRepo;
import com.example.demo.security.jwt.JwtUtil;
import com.example.demo.service.Arac;
import com.example.demo.service.AracFabrikasi;
import com.example.demo.service.Garaj;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import static org.springframework.http.HttpStatus.*;

@RestController
@Api(value = "Garage API documentation")
public class GarageController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private GarageRepo garagerepo;
    @Autowired
    private GarageAlanRepo garagealanrepo;

    Garaj garaj = new Garaj();
    Garage newGarage = new Garage();

    @EventListener(ApplicationReadyEvent.class)
    public void startFirst(){
        if (garagealanrepo.count() != 0) {
            if (garagerepo.count() != 0)
                garaj.setGarajBoyut(getAlan() - garagerepo.sumAlan());
            else
                garaj.setGarajBoyut(getAlan());
        }
    }

    public int getAlan(){
        List<GarageAlan> liste = garagealanrepo.findAll();
        return liste.get(0).getAlan();
    }

    @GetMapping("/all")
    @ApiOperation(value = "Bütün kullanıcıları listeler", authorizations = { @Authorization(value="apiKey") })
    public ResponseEntity<List<Garage>> getArac() {
        return new ResponseEntity<>(garagerepo.findAll(), OK);
    }

    @GetMapping(path="/{id}")
    @ApiOperation(value = "Girilen id değerine göre aracın bilgilerini getirir", authorizations = { @Authorization(value="apiKey") })
    public ResponseEntity<Garage> getArac(@PathVariable int id){
        return new ResponseEntity<>(garagerepo.findById(id)
                .orElseThrow(() -> new AracNotFoundException("Girilen id ye ait araç bulunamadı: " + id)), OK);
    }

    @ApiOperation(value = "Araç girişi yapılır", authorizations = { @Authorization(value="apiKey") })
    @PostMapping("/giris")
    public ResponseEntity<Garage> createIslem(@RequestBody Garage garage){
        // Veri girişi yapılmadan önce veritabanında bulunan garagealan tablosunda değer olup olmadığı kontrol edildi.
        startFirst();
        // Plaka ya göre daha önceden aracın giriş yapıp yapmadığı kontrol edildimesi için garagerepo.findByPlaka() tanımlandı
        Optional<Garage> garageByPlaka = garagerepo.findByPlaka(garage.getPlaka());

        // throw new GirisNotAcceptableException
        // throw new AracAlreadyExistsException
        // throw new TanımlıGarajNotExistsException gibi exceptionlar özelleştirldi.
        if (getAracTip(garage.getTip(), garage.getPlaka()).getAlan() > garaj.getGarajBoyut())
            throw new GirisNotAcceptableException("Garajda yer yok önce çıkış yapılmalı");
        else if (garageByPlaka.isPresent())
            throw new AracAlreadyExistsException(garage.getPlaka() + " plakalı araç zaten garajda...");
        else if(garagealanrepo.count()==0)
            throw new TanımlıGarajNotExistsException("Tanımlı garaj boyutu yok");
        else {
            // Veritabanında bulunan garage tablosuna veri girişi yapılacağından dolayı tanımlandı.
            Garage newGarage = new Garage();

            // garage tablosunda bulunan değerler atandı.
            // garage.getTip() metodu ile veriler soyut AracFabrikasina veriler gönderilerek
            // AracFabrikasının alt metotlarından girilen arac tipine göre kapladığı alan belirlendi.
            newGarage.setAlan(getAracTip(garage.getTip(), garage.getPlaka()).getAlan());
            newGarage.setPlaka(garage.getPlaka());
            newGarage.setTip(garage.getTip());

            // Giriş yapıldıktan sonra garaj sınıfından boyut girilen boyut düşürüldü.
            garaj.setGarajBoyut(garaj.getGarajBoyut() - newGarage.getAlan());

            return new ResponseEntity<>(garagerepo.save(newGarage), OK);
        }
    }

    public Arac getAracTip(int tip, String plaka){
        return AracFabrikasi.getArac(tip, plaka);
    }

    @ApiOperation(value = "Girilen id değerinin verileri güncellenebilir", authorizations = { @Authorization(value="apiKey") })
    @PutMapping("/update/{id}/{tip}/{plaka}")
    public ResponseEntity<Garage> updateIslem(@PathVariable int id, @PathVariable int tip, @PathVariable String plaka) {
        if (getAracTip(tip, plaka).getAlan() > garaj.getGarajBoyut())
            throw new GirisNotAcceptableException("Garajda yer yok önce çıkış yapılmalı");
        else {
            updateIslemById(id, tip, plaka);
            startFirst();
            return new ResponseEntity<>(OK);
        }
    }

    public void updateIslemById(int id, int tip, String plaka) {
        Garage oldArac = getAracById(id);

        oldArac.setTip(tip);
        oldArac.setPlaka(plaka);
        oldArac.setAlan(getAracTip(tip, plaka).getAlan());

        garagerepo.save(oldArac);
    }

    public Garage getAracById(int id) {
        return garagerepo.findById(id)
                .orElseThrow(() -> new AracNotFoundException("Girilen id ye ait araç bulunamadı: " + id));
    }

    @ApiOperation(value = "Girilen plaka değerini siler Araç çıkışı yapılır.", authorizations = { @Authorization(value="apiKey") })
    @DeleteMapping("/delete/{plaka}")
    @Transactional
    public ResponseEntity<Void> deleteAracByPlaka(@PathVariable String plaka) {
        Optional<Garage> garageByPlaka = garagerepo.findByPlaka(plaka);
        if (garageByPlaka.isPresent()){
            garagerepo.deleteByPlaka(plaka);
            return new ResponseEntity<>(OK);
        }
        else
            throw new AracNotFoundException("Silmek istediğiniz araç garajda değil");
    }

    @ExceptionHandler(AracNotFoundException.class)
    public ResponseEntity<String> handleAracNotFoundException(AracNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler(AracAlreadyExistsException.class)
    public ResponseEntity<String> handleAracAlreadyExistException(AracAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), CONFLICT);
    }

    @ExceptionHandler(TanımlıGarajNotExistsException.class)
    public ResponseEntity<String> handleTanımlıGarajNotExistsException(TanımlıGarajNotExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler(GirisNotAcceptableException.class)
    public ResponseEntity<String> handleGirisNotAcceptableException(GirisNotAcceptableException ex){
        return new ResponseEntity<>(ex.getMessage(), NOT_ACCEPTABLE);
    }
}
