package com.d1ff.analyticservice.service;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.InetAddress;

@Slf4j
@Service
public class GeoIpService {
    private final DatabaseReader reader;

    public GeoIpService(@Value("${geoip.database-path}") String dbPath) throws Exception {
        this.reader = new DatabaseReader.Builder(new File(dbPath))
                .withCache(new CHMCache())
                .build();
        log.info("GeoIP database loaded from: {}", dbPath);
    }

    public String resolveCountry(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) return "";
        try {
            InetAddress address = InetAddress.getByName(ipAddress.trim());
            return reader.country(address).getCountry().getIsoCode();
        } catch (AddressNotFoundException e) {
            return ""; // Может быть приватный айпишник
        } catch (Exception e) {
            log.warn("Failed to resolve country for ip={}", ipAddress);
            return "";
        }
    }

    @PreDestroy
    public void close() throws Exception {
        reader.close();
    }

}
