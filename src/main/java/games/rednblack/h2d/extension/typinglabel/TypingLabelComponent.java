package games.rednblack.h2d.extension.typinglabel;

import com.artemis.PooledComponent;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class TypingLabelComponent extends PooledComponent {

    public transient TypingLabel typingLabel;

    @Override
    public void reset() {
        if (typingLabel != null)
            typingLabel.remove();
        typingLabel = null;
    }
}
