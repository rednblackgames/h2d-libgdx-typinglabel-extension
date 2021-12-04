package games.rednblack.h2d.extension.typinglabel;

import com.artemis.systems.IteratingSystem;
import games.rednblack.editor.renderer.commons.IExternalItemType;
import games.rednblack.editor.renderer.factory.EntityFactory;
import games.rednblack.editor.renderer.factory.component.ComponentFactory;
import games.rednblack.editor.renderer.factory.v2.ComponentFactoryV2;
import games.rednblack.editor.renderer.systems.render.logic.DrawableLogic;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class TypingLabelItemType implements IExternalItemType {

    private ComponentFactory factory;
    private ComponentFactoryV2 factoryV2;
    private IteratingSystem system;
    private DrawableLogic drawableLogic;

    public TypingLabelItemType() {
        factory = new TypingLabelComponentFactory();
        factoryV2 = new TypingLabelComponentFactoryV2();
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
    public ComponentFactoryV2 getComponentFactoryV2() {
        return factoryV2;
    }

    @Override
    public void injectMappers() {
        ComponentRetriever.addMapper(TypingLabelComponent.class);
    }
}
