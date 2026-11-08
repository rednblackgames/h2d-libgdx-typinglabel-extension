package games.rednblack.h2d.extension.typinglabel;

import games.rednblack.editor.renderer.ecs.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.TintComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.label.LabelComponent;
import games.rednblack.editor.renderer.systems.render.logic.DrawableLogic;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import games.rednblack.editor.renderer.utils.TransformMathUtils;

public class TypingLabelDrawableLogic implements DrawableLogic {

    protected ComponentMapper<LabelComponent> labelComponentMapper;
    protected ComponentMapper<TypingLabelComponent> typingLabelComponentMapper;
    protected ComponentMapper<TintComponent> tintComponentMapper;
    protected ComponentMapper<DimensionsComponent> dimensionsComponentMapper;
    protected ComponentMapper<TransformComponent> transformMapper;

    private final Color tmpColor = new Color();
    private final Color outlineColor = new Color();

    @Override
    public void draw(Batch batch, int entity, float parentAlpha, RenderingType renderingType) {
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE);
        TransformComponent entityTransformComponent = transformMapper.get(entity);
        LabelComponent labelComponent = labelComponentMapper.get(entity);
        DimensionsComponent dimensionsComponent = dimensionsComponentMapper.get(entity);
        TintComponent tint = tintComponentMapper.get(entity);
        TypingLabelComponent typingLabelComponent = typingLabelComponentMapper.get(entity);

        tmpColor.set(tint.color);
        //the alpha of every composite it is in, not only of the one holding it: a scroll pane fading
        //takes the rows of its content with it
        tmpColor.a *= parentAlpha;

        if (labelComponent.style.background != null) {
            batch.setColor(tmpColor);
            labelComponent.style.background.draw(batch, entityTransformComponent.x, entityTransformComponent.y, dimensionsComponent.width, dimensionsComponent.height);
        }

        if(labelComponent.style.fontColor != null) tmpColor.mul(labelComponent.style.fontColor);

        TransformMathUtils.computeTransform(entityTransformComponent).mulLeft(batch.getTransformMatrix());
        TransformMathUtils.applyTransform(batch, entityTransformComponent);

        if (typingLabelComponent == null) {
            //As LabelDrawableLogic does: the outline layer first, in its own colour.
            if (labelComponent.outlineCache != null) {
                outlineColor.set(labelComponent.borderColor);
                outlineColor.a *= tint.color.a * parentAlpha;
                labelComponent.outlineCache.tint(outlineColor);
                labelComponent.outlineCache.draw(batch);
            }
            labelComponent.cache.tint(tmpColor);
            labelComponent.cache.draw(batch);
        } else if (typingLabelComponent.typingLabel != null) {
            //TextraTypist leaves the batch in the label's colour, and drops the shader to null after
            //a distance-field font: the next thing drawn, or the whole scene when the batch draws
            //its frame buffer, would come out tinted, or without its own shader.
            float packedColor = batch.getPackedColor();
            ShaderProgram shader = batch.getShader();
            //The outline first, in its own colour and the label's transparency; the text over it, in the tint.
            if (typingLabelComponent.outlineLabel != null) {
                outlineColor.set(labelComponent.borderColor);
                outlineColor.a *= tint.color.a * parentAlpha;
                typingLabelComponent.outlineLabel.setColor(outlineColor);
                typingLabelComponent.outlineLabel.draw(batch, 1);
            }
            typingLabelComponent.typingLabel.setColor(tmpColor);
            typingLabelComponent.typingLabel.draw(batch, 1);
            batch.setPackedColor(packedColor);
            if (batch.getShader() != shader) batch.setShader(shader);
        }

        TransformMathUtils.resetTransform(batch, entityTransformComponent);
    }

    @Override
    public void beginPipeline() {

    }

    @Override
    public void endPipeline() {

    }

}
