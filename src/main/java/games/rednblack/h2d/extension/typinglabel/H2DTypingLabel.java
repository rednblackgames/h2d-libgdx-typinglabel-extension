package games.rednblack.h2d.extension.typinglabel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.github.tommyettinger.textra.Styles;
import com.github.tommyettinger.textra.TypingLabel;
import games.rednblack.editor.renderer.components.ParentNodeComponent;
import games.rednblack.editor.renderer.components.TransformComponent;
import games.rednblack.editor.renderer.components.ViewPortComponent;
import games.rednblack.editor.renderer.ecs.ComponentMapper;
import games.rednblack.editor.renderer.utils.TransformMathUtils;

/**
 * A {@link TypingLabel} that knows which entity draws it, so that pointer driven effects work.
 * <p>
 * Effects that react to the mouse, {@code {LINK=...}} above all, hit-test the glyphs inside
 * {@link TypingLabel#draw} against the position returned by {@link #screenToLocalCoordinates(Vector2)}.
 * That is a scene2d method and it converts nothing unless the actor belongs to a
 * {@link com.badlogic.gdx.scenes.scene2d.Stage}: here the label is drawn straight by the render
 * pipeline, which puts the entity transform in the batch, so the inherited implementation hands back
 * raw window pixels. Those get compared against glyph positions expressed in the entity coordinates,
 * which is why the link only answers on the patch of screen where the two happen to overlap, with
 * camera position and zoom ignored entirely.
 * <p>
 * The override does the conversion the pipeline implies — unproject through the viewport, then down
 * the chain of entity transforms — the same way
 * {@link games.rednblack.editor.renderer.systems.ButtonSystem} hit-tests its buttons.
 */
public class H2DTypingLabel extends TypingLabel {

    private int entity = -1;

    private ComponentMapper<TransformComponent> transformMapper;
    private ComponentMapper<ParentNodeComponent> parentMapper;
    private ComponentMapper<ViewPortComponent> viewPortMapper;

    public H2DTypingLabel(String text, Styles.LabelStyle style) {
        super(text, style);
    }

    /** Binds the label to the entity that draws it; without this the conversion below is skipped. */
    public void setEntity(int entity,
                          ComponentMapper<TransformComponent> transformMapper,
                          ComponentMapper<ParentNodeComponent> parentMapper,
                          ComponentMapper<ViewPortComponent> viewPortMapper) {
        this.entity = entity;
        this.transformMapper = transformMapper;
        this.parentMapper = parentMapper;
        this.viewPortMapper = viewPortMapper;
    }

    /**
     * @param screenCoords window coordinates with y pointing <b>up</b>, not down as scene2d usually
     *                     passes them: {@link TypingLabel#draw} flips the y itself before calling this
     *                     on a label with no parent, and the flip is undone here because the viewport
     *                     unprojects window coordinates with y pointing down.
     */
    @Override
    public Vector2 screenToLocalCoordinates(Vector2 screenCoords) {
        if (entity == -1) return super.screenToLocalCoordinates(screenCoords);

        screenCoords.y = Gdx.graphics.getHeight() - screenCoords.y;
        return TransformMathUtils.globalToLocalCoordinates(entity, screenCoords, transformMapper, parentMapper, viewPortMapper);
    }
}
