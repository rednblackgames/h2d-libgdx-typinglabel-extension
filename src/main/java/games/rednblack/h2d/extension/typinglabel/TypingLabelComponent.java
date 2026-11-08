package games.rednblack.h2d.extension.typinglabel;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import games.rednblack.editor.renderer.ecs.PooledComponent;
import com.badlogic.gdx.utils.CharArray;
import com.github.tommyettinger.textra.Styles;
import com.github.tommyettinger.textra.TypingLabel;

public class TypingLabelComponent extends PooledComponent {

    public transient TypingLabel typingLabel;
    public transient Styles.LabelStyle labelStyle;
    /** The label's font the typing label was made from: when the label's changes, it is made again. */
    public transient BitmapFont sourceFont;
    /** The outline layer, typed in step with the text and drawn under it; null without an outline. */
    public transient TypingLabel outlineLabel;
    public transient Styles.LabelStyle outlineStyle;
    public transient BitmapFont sourceOutline;

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
        sourceFont = null;
        if (outlineLabel != null)
            outlineLabel.remove();
        outlineLabel = null;
        if (outlineStyle != null)
            outlineStyle.font.dispose();
        outlineStyle = null;
        sourceOutline = null;
        originalText.setLength(0);
    }
}
