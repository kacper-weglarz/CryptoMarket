package io.github.kacperweglarz.cryptomarket.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kacperweglarz.cryptomarket.service.MarketDataService;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.net.URI;

@Component
@ClientEndpoint
@Slf4j
public class WebSocketClient {

    private final MarketDataService marketDataService;
    private final ObjectMapper mapper = new ObjectMapper();
    private Session session;

    @Autowired
    public WebSocketClient(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    //@PostConstruct
    public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            String uri = "wss://stream.binance.com:9443/ws/btcusdt@aggTrade";

            container.connectToServer(this, URI.create(uri));

            log.info("Connected to Binance WebSocket");
        } catch (Exception e) {
            log.error("Failed to connect to Binance WebSocket", e);
        }
    }


    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        log.info("Session opened -> id + {}", session.getId());
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            JsonNode node = mapper.readTree(message);

            log.trace("Raw message received: {}", message);

            String rawSymbol = node.get("s").asText();

            BigDecimal price = new BigDecimal(node.get("p").asText());
            BigDecimal volume = new BigDecimal(node.get("q").asText());

            String fixedSymbol = rawSymbol;

            if (rawSymbol.endsWith("USDT")) {

                String base = rawSymbol.substring(0, rawSymbol.length() - 4);
                String quote = "USDT";

                fixedSymbol = base + "/" + quote;
            }

            marketDataService.updatePrices(fixedSymbol, price, volume);

            log.debug("Price updated for {}: {}", fixedSymbol, price);

        } catch (Exception e) {
            log.error("Error processing WebSocket message", e);
        }
    }

    @OnClose
    public void onClose(Session session) {
        log.warn("Session closed, ID: {}", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket error", error);
    }
}
