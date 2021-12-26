package games.rednblack.h2d.extension.typinglabel;

import com.artemis.systems.IteratingSystem;
import games.rednblack.editor.renderer.commons.IExternalItemType;
import games.rednblack.editor.renderer.data.LabelVO;
import games.rednblack.editor.renderer.factory.EntityFactory;
import games.rednblack.editor.renderer.factory.component.ComponentFactory;
import games.rednblack.editor.renderer.systems.render.logic.DrawableLogic;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class TypingLabelItemType implements IExternalItemType {

    private ComponentFactory factory;
    private IteratingSystem system;
    private DrawableLogic drawableLogic;

    public TypingLabelItemType() {
        factory = new TypingLabelComponentFactory();
        system = new TypingLabelSystem();
        drawableLogic = new TypingLabelDrawableLogic();
    }

    @Override
    public int getTypeId() {
        return EntityFactory.LABEL_TYPE;
    }

    @Override
    public DrawableLogic getDrawable() {
        return drawableLogic;
    }

    @Override
    public IteratingSystem getSystem() {
        return system;
    }

    @Override
    public ComponentFactory getComponentFactory() {
        return factory;
    }

    @Override
    public void injectMappers() {
        ComponentRetriever.addMapper(TypingLabelComponent.class);
    }

    @Override
    public boolean hasResources() {
        return false;
    }

    @Override
    public String formatResourcePath(String resName) {
        throw new RuntimeException("Method not implemented.");
    }
}
