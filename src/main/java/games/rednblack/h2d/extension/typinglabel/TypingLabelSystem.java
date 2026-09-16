package games.rednblack.h2d.extension.typinglabel;

import games.rednblack.editor.renderer.ecs.ComponentMapper;
import games.rednblack.editor.renderer.ecs.annotations.All;
import games.rednblack.editor.renderer.ecs.systems.IteratingSystem;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Styles;
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

        if (typingLabelComponent.typingLabel == null) {
            typingLabelComponent.labelStyle = new Styles.LabelStyle(labelComponent.style);
            H2DTypingLabel typingLabel = new H2DTypingLabel(labelComponent.getText().toString(), typingLabelComponent.labelStyle);
            // Lets effects that track the mouse, like {LINK=...}, turn the pointer into this entity's
            // coordinates; the label is drawn by the pipeline, not by a Stage, so nothing else can.
            typingLabel.setEntity(entity, transformMapper, parentMapper, viewPortMapper);
            typingLabelComponent.typingLabel = typingLabel;
            typingLabelComponent.setOriginalText(labelComponent.getText());

            float fontScaleX = labelComponent.fontScaleX;
            float fontScaleY = labelComponent.fontScaleY;

            Font font = typingLabelComponent.labelStyle.font;
            font.scale(fontScaleX, fontScaleY);
            typingLabelComponent.typingLabel.setSize(dimensionsComponent.width, dimensionsComponent.height);
            typingLabelComponent.typingLabel.setWrap(labelComponent.wrap);
            typingLabelComponent.typingLabel.setAlignment(labelComponent.labelAlign);
        } else {
            if (!typingLabelComponent.getOriginalText().equals(labelComponent.getText())){
                typingLabelComponent.typingLabel.setText(labelComponent.getText().toString());
                typingLabelComponent.setOriginalText(labelComponent.getText());
            }
            if (typingLabelComponent.typingLabel.isWrap() != labelComponent.wrap) {
                typingLabelComponent.typingLabel.setWrap(labelComponent.wrap);
            }
            if (typingLabelComponent.typingLabel.getAlignment() != labelComponent.labelAlign) {
                typingLabelComponent.typingLabel.setAlignment(labelComponent.labelAlign);
            }
            if (typingLabelComponent.typingLabel.getWidth() != dimensionsComponent.width) {
                typingLabelComponent.typingLabel.setWidth(dimensionsComponent.width);
            }
            if (typingLabelComponent.typingLabel.getHeight() != dimensionsComponent.height) {
                typingLabelComponent.typingLabel.setHeight(dimensionsComponent.height);
            }
        }

        typingLabelComponent.typingLabel.setTouchable(clickable ? Touchable.enabled : Touchable.disabled);
        typingLabelComponent.typingLabel.act(engine.getDelta());
    }
}

