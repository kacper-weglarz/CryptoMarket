package io.github.kacperweglarz.cryptomarket.serviceTest;

import io.github.kacperweglarz.cryptomarket.repository.MarketDataRepository;
import io.github.kacperweglarz.cryptomarket.service.MarketDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MarketDataServiceTest {

    @Mock
    MarketDataRepository marketDataRepository;

    @InjectMocks
    MarketDataService marketDataService;


}
