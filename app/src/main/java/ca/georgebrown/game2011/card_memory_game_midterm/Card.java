
package ca.georgebrown.game2011.card_memory_game_midterm;

import android.content.Context;
import android.widget.ImageButton;

/**
 * Memory Match Game Midterm
 * Created by Diego Eduardo Camacho(STU ID# 101090467) and Cory Ronald
 * On March 16 2018
 * @class Card Class
 * @objective Extends ImageButton.
 *
 * @method FlipCard: Changes the current image resource to either the front on the card or the back of the card.
 */
public class Card extends ImageButton {
    int imageFrontID;
    int imageBackID;

    boolean flipped = true;

    public int getImageFrontID() {
        return imageFrontID;
    }


    public Card(Context context) {
        super(context);
        imageBackID = R.drawable.cardback;


    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        imageFrontID = resId;
    }

    public void FlipCard() {
        if (flipped) {
            super.setImageResource(imageBackID);
             flipped = false;
        } else {
            super.setImageResource(imageFrontID);
            flipped = true;

        }
    }
}

