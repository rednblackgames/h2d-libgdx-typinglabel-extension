package games.rednblack.h2d.extension.typinglabel;

import games.rednblack.editor.renderer.ecs.PooledComponent;
import com.badlogic.gdx.utils.CharArray;
import com.github.tommyettinger.textra.Styles;
import com.github.tommyettinger.textra.TypingLabel;

public class TypingLabelComponent extends PooledComponent {

    public transient TypingLabel typingLabel;
    public transient Styles.LabelStyle labelStyle;

    private final CharArray originalText = new CharArray();

    public CharArray getOriginalText() {
        return originalText;
    }

    public void setOriginalText(CharArray originalText) {
        this.originalText.setLength(0);
        this.originalText.append(originalText);
    }

    @Override
    public void reset() {
        if (typingLabel != null)
            typingLabel.remove();
        typingLabel = null;
        if (labelStyle != null)
            labelStyle.font.dispose();
        labelStyle = null;
        originalText.setLength(0);
    }
}
