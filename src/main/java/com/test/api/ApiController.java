package com.test.api;

import com.test.api.models.PriceRequest;
import com.test.api.models.PriceRequestResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

@RestController
public class ApiController {

    // create a DynamoDB client
    private static final Region region = Region.EU_CENTRAL_1;
    private static final DynamoDbClient db = DynamoDbClient.builder().region(region).build();

    // initialize a MessageDigest with the SHA-256 algorithm
    private static final MessageDigest digest;
    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/api/echo", produces = "text/plain")
    public String echo(@RequestParam String string) {
        return string;
    }

    @PostMapping(value = "/api/getPrice", produces = "application/json")
    public PriceRequestResponse getPrice(@RequestBody PriceRequest priceRequest) {

        // first we retrieve the price of the item from the database
        GetItemRequest request = GetItemRequest.builder()
                .key(Collections.singletonMap("item_id", AttributeValue.builder().s(priceRequest.getItemId()).build()))
                .tableName("online-shop-items")
                .build();
        Map<String, AttributeValue> item = db.getItem(request).item();

        PriceRequestResponse priceRequestResponse = new PriceRequestResponse();
        priceRequestResponse.setItemId(priceRequest.getItemId());
        priceRequestResponse.setQuantity(priceRequest.getQuantity());
        priceRequestResponse.setPerItemPrice(Integer.parseInt(item.get("per_item_price").n()));
        priceRequestResponse.setTaxRate(Float.parseFloat(item.get("tax_rate").n()));
        priceRequestResponse.calculatePrices();

        return priceRequestResponse;
    }

    @PostMapping(value = "/api/compute", produces = "text/plain")
    public String compute(@RequestBody int[] numbers) {

        // first we sort the array
        // Arrays.sort() uses a dual-pivot quicksort algorithm for integers (in OpenJDK 22)
        Arrays.sort(numbers);

        // now we calculate the SHA-256 hash using the algorithm described in the thesis
        byte[] hash = {};
        for (int number : numbers) {
            byte[] numberHash = digest.digest(new byte[]{(byte) number});
            byte[] newHash = new byte[hash.length + numberHash.length];
            System.arraycopy(hash, 0, newHash, 0, hash.length);
            System.arraycopy(numberHash, 0, newHash, hash.length, numberHash.length);
            hash = digest.digest(newHash);
        }
        return new BigInteger(1, hash).toString(16);
    }

    @PostMapping(value = "/api/parse", produces = "text/plain")
    public String parse(@RequestBody String[] strings, @RequestParam String searchString) {
        for(int i = 0; i < strings.length; i++) {
            if(strings[i].equals(searchString)) {
                return Integer.toString(i);
            }
        }
        return "-1";
    }

    @GetMapping(value = "/api/query", produces = "text/plain")
    public String query(@RequestParam String initialPrimaryKey) {
        // we do the table round-trip
        String primaryKey = initialPrimaryKey;
        int counter = 0;
        do {
            primaryKey = getNextPrimaryKey(primaryKey);
            counter++;
        } while (!primaryKey.equals(initialPrimaryKey));
        return Integer.toString(counter);
    }

    private static String getNextPrimaryKey(String primaryKey) {
        GetItemRequest request = GetItemRequest.builder()
                .key(Collections.singletonMap("primary_key", AttributeValue.builder().s(primaryKey).build()))
                .tableName("round-trip-table")
                .build();
        return db.getItem(request).item().get("next_primary_key").s();
    }
}