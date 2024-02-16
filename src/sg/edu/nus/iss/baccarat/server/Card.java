package sg.edu.nus.iss.baccarat.server;

public class Card {
    private final int suits;
    private final int value;

    public Card(int value, int suits) {
        if (value > 0 && value < 14 && suits > 0 && suits < 5) {
            this.value = value;
            this.suits = suits;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public int getSuits() {
        return suits;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        String suitsString;
        String valueString;
        switch (this.suits) {
            case 1: suitsString = "HEARTS"; break;
            case 2: suitsString = "DIAMONDS"; break;
            case 3: suitsString = "SPADES"; break;
            case 4: suitsString = "CLUBS"; break;
            default: suitsString = "N/A"; break;
        }
        switch (this.value) {
            case 1: valueString = "ACE"; break;
            case 2: valueString = "2"; break;
            case 3: valueString = "3"; break;
            case 4: valueString = "4"; break;
            case 5: valueString = "5"; break;
            case 6: valueString = "6"; break;
            case 7: valueString = "7"; break;
            case 8: valueString = "8"; break;
            case 9: valueString = "9"; break;
            case 10: valueString = "10"; break;
            case 11: valueString = "JACK"; break;
            case 12: valueString = "QUEEN"; break;
            case 13: valueString = "KING"; break;
            default: valueString = "N/A"; break;
        }
        return "Card [value=" + valueString + ", suits=" + suitsString + "]";
    }
}
