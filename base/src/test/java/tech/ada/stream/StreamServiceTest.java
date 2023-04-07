package tech.ada.stream;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import tech.ada.streams.StreamsService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StreamServiceTest {

    StreamsService service = new StreamsService();

    @Test
    public void firstOdd() {
        List<Integer> entry = List.of(1,2,3,4,5);
        Optional<Integer> firstOdd = service.firstOdd(entry);
        assertEquals(1, firstOdd.get());
    }

    @Test
    public void odds() {
        List<Integer> entry = List.of(1,2,3,4,5);
        List<Integer> odds = service.getOdd(entry);
        assertEquals(Arrays.asList(1,3,5), odds);
    }

    @Test
    public void hasOdd() {
        List<Integer> entry = List.of(2,4,6);
        boolean arentOdd = service.hasOdd(entry);
        assertFalse( arentOdd );
    }

}