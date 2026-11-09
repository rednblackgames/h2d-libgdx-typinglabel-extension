package games.rednblack.h2d.extension.typinglabel;

import games.rednblack.editor.renderer.ecs.ComponentMapper;
import games.rednblack.editor.renderer.ecs.annotations.All;
import games.rednblack.editor.renderer.ecs.systems.IteratingSystem;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Styles;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.github.tommyettinger.textra.TypingLabel;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.ParentNodeComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.ViewPortComponent;
import games.rednblack.editor.renderer.components.label.LabelComponent;

@All(LabelComponent.class)
public class TypingLabelSystem extends IteratingSystem {
    protected ComponentMapper<LabelComponent> labelComponentMapper;
    protected ComponentMapper<TypingLabelComponent> typingLabelComponentMapper;
    protected ComponentMapper<DimensionsComponent> dimensionsComponentMapper;
    protected ComponentMapper<TransformComponent> transformMapper;
    protected ComponentMapper<ParentNodeComponent> parentMapper;
    protected ComponentMapper<ViewPortComponent> viewPortMapper;

    /**
     * When false the labels are kept untouchable, so that pointer driven effects still highlight on
     * hover but a click never fires them. The editor drives this from its sandbox settings, to keep
     * {@code {LINK=...}} from opening a browser every time a label is picked on the canvas.
     */
    private boolean clickable = true;

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @Override
    protected void process(int entity) {
        LabelComponent labelComponent = labelComponentMapper.get(entity);
        TypingLabelComponent typingLabelComponent = typingLabelComponentMapper.get(entity);
        labelComponent.typingEffect = typingLabelComponent != null;
        if (!labelComponent.typingEffect) return;

        DimensionsComponent dimensionsComponent = dimensionsComponentMapper.get(entity);

        //A label given other fonts (its outline changed in the editor) gets typing labels of its own
        //again: the ones it has were made from the old fonts.
        if (typingLabelComponent.typingLabel != null && (typingLabelComponent.sourceFont != labelComponent.style.font
                || typingLabelComponent.sourceOutline != labelComponent.outlineFont)) {
            typingLabelComponent.reset();
        }

        if (typingLabelComponent.typingLabel == null) {
            typingLabelComponent.sourceFont = labelComponent.style.font;
            typingLabelComponent.labelStyle = new Styles.LabelStyle(labelComponent.style);
            typingLabelComponent.typingLabel = make(entity, typingLabelComponent.labelStyle, labelComponent, dimensionsComponent);
            //The outline is a layer of its own, typed in step with the text: same text, same tokens,
            //same delta, and fonts with the same metrics.
            typingLabelComponent.sourceOutline = labelComponent.outlineFont;
            if (labelComponent.outlineFont != null) {
                typingLabelComponent.outlineStyle = new Styles.LabelStyle(new Label.LabelStyle(labelComponent.outlineFont, null));
                typingLabelComponent.outlineLabel = make(entity, typingLabelComponent.outlineStyle, labelComponent, dimensionsComponent);
            }
            typingLabelComponent.setOriginalText(labelComponent.getText());
        } else {
            boolean newText = !typingLabelComponent.getOriginalText().equals(labelComponent.getText());
            if (newText) typingLabelComponent.setOriginalText(labelComponent.getText());
            update(typingLabelComponent.typingLabel, newText, labelComponent, dimensionsComponent);
            if (typingLabelComponent.outlineLabel != null) update(typingLabelComponent.outlineLabel, newText, labelComponent, dimensionsComponent);
        }

        if (typingLabelComponent.outlineLabel != null) {
            typingLabelComponent.outlineLabel.setTouchable(Touchable.disabled);
            typingLabelComponent.outlineLabel.act(engine.getDelta());
        }
        typingLabelComponent.typingLabel.setTouchable(clickable ? Touchable.enabled : Touchable.disabled);
        typingLabelComponent.typingLabel.act(engine.getDelta());
    }

    private H2DTypingLabel make(int entity, Styles.LabelStyle style, LabelComponent labelComponent, DimensionsComponent dimensionsComponent) {
        H2DTypingLabel typingLabel = new H2DTypingLabel(labelComponent.getText().toString(), style);
        // Lets effects that track the mouse, like {LINK=...}, turn the pointer into this entity's
        // coordinates; the label is drawn by the pipeline, not by a Stage, so nothing else can.
        typingLabel.setEntity(entity, transformMapper, parentMapper, viewPortMapper);
        Font font = style.font;
        font.scale(labelComponent.fontScaleX, labelComponent.fontScaleY);
        typingLabel.setSize(dimensionsComponent.width, dimensionsComponent.height);
        typingLabel.setWrap(labelComponent.wrap);
        typingLabel.setAlignment(labelComponent.labelAlign);
        return typingLabel;
    }

    private static void update(TypingLabel typingLabel, boolean newText, LabelComponent labelComponent, DimensionsComponent dimensionsComponent) {
        //A new text is typed out from its start. TextraTypist's setText alone shows it whole when the
        //text before had finished typing, which is the usual case: the label was waiting for it.
        //restart(text) parses and lays it out once; setText then restart() did it three times over.
        if (newText) typingLabel.restart(labelComponent.getText().toString());
        if (typingLabel.isWrap() != labelComponent.wrap) typingLabel.setWrap(labelComponent.wrap);
        if (typingLabel.getAlignment() != labelComponent.labelAlign) typingLabel.setAlignment(labelComponent.labelAlign);
        if (typingLabel.getWidth() != dimensionsComponent.width) typingLabel.setWidth(dimensionsComponent.width);
        if (typingLabel.getHeight() != dimensionsComponent.height) typingLabel.setHeight(dimensionsComponent.height);
    }
}
