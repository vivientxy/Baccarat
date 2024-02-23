import org.junit.Test;

import server.BaccaratEngine;
import server.DBHandler;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test
 */
public class BaccaratTest {

    @Test
    public void testHandCalculator() {
        // test DBHandler.calcHandValue() always return a value between 0 and 9 (both inclusive)
        List<Integer> hand = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < 3; i++) {
            hand.add(rand.nextInt(11));
        }

        int value = DBHandler.calcHandValue(hand);
        assertTrue(value >= 0 && value <= 9);
    }

    @Test
    public void testWinningMultiplier() {
        String bet = "p";
        String hand = "P|10|2|3,B|2|7|4";

        float result = BaccaratEngine.winningMultiplier(bet, hand);

        assertNotNull(result);
    }

}