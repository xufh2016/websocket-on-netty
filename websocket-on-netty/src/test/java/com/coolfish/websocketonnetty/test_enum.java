package com.coolfish.websocketonnetty;

/**
 * @className: test_enum
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2023/6/16
 */
public class test_enum {
    public enum Suits {
        DIAMAND,CLUBS,HEARTS,SPADE
    }
    public enum Ranks {
        ACE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,TEN,JACK,QUEEN,KING
    }

    public static void main(String[] args) {
        Suits s;
        Ranks r;

        s=Suits.valueOf("HEARTS");
        r=Ranks.valueOf("TWO");

        Card c2=new Card(s,r);
    }
}

class Card{
    private test_enum.Suits s;
    //因为enum枚举类在test_num里面，所以在其他类中要用test_enum.Suits调用
    private test_enum.Ranks r;
    private boolean file=true;

    public Card(test_enum.Suits t,test_enum.Ranks y) {
        s=t;
        r=y;
    }

    public Card() {
        s=test_enum.Suits.valueOf("DIAMAND");
        r=test_enum.Ranks.valueOf("ACE");
    }
}

