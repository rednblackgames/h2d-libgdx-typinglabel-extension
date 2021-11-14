package games.rednblack.h2d.extension.typinglabel;

import com.artemis.ComponentMapper;
import com.artemis.EntityTransmuter;
import com.artemis.EntityTransmuterFactory;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import games.rednblack.editor.renderer.box2dLight.RayHandler;
import games.rednblack.editor.renderer.components.DimensionsComponent;
import games.rednblack.editor.renderer.components.label.LabelComponent;
import games.rednblack.editor.renderer.data.ProjectInfoVO;
import games.rednblack.editor.renderer.data.ResolutionEntryVO;
import games.rednblack.editor.renderer.factory.v2.ComponentFactoryV2;
import games.rednblack.editor.renderer.factory.v2.EntityFactoryV2;
import games.rednblack.editor.renderer.factory.v2.LabelComponentFactoryV2;
import games.rednblack.editor.renderer.resources.IResourceRetriever;

public class TypingLabelComponentFactoryV2 extends ComponentFactoryV2 {

    protected ComponentMapper<LabelComponent> labelCM;
    protected ComponentMapper<TypingLabelComponent> typingLabelCM;

    private EntityTransmuter transmuter;

    public TypingLabelComponentFactoryV2() {
        super();
    }

    @Override
    public void injectDependencies(com.artemis.World engine, RayHandler rayHandler, World world, IResourceRetriever rm) {
        super.injectDependencies(engine, rayHandler, world, rm);

        transmuter = new EntityTransmuterFactory(engine)
                .add(LabelComponent.class)
                .add(TypingLabelComponent.class)
                .build();
    }

    @Override
    public void transmuteEntity(int entity) {
        transmuter.transmute(entity);
    }

    @Override
    public int getEntityType() {
        return EntityFactoryV2.LABEL_TYPE;
    }

    @Override
    public void setInitialData(int entity, Object data) {
        LabelComponent component = labelCM.get(entity);
        Object[] params = (Object[]) data;
        component.setText((String) params[0]);
        component.fontName = (String) params[1];
        component.fontSize = (int) params[2];
        component.mono = (boolean) params[3];
        component.typingEffect = (boolean) params[4];
    }

    @Override
    protected void initializeTransientComponents(int entity) {
        super.initializeTransientComponents(entity);

        LabelComponent component = labelCM.get(entity);
        component.setStyle(LabelComponentFactoryV2.generateStyle(rm, component.fontName, component.fontSize, component.mono));

        ProjectInfoVO projectInfoVO = rm.getProjectVO();
        ResolutionEntryVO resolutionEntryVO = rm.getLoadedResolution();
        float multiplier = resolutionEntryVO.getMultiplier(rm.getProjectVO().originalResolution);

        component.setFontScale(multiplier / projectInfoVO.pixelToWorld);

        if (!component.typingEffect) {
            typingLabelCM.remove(entity);
        }
    }

    @Override
    protected void initializeDimensionsComponent(int entity) {
        ProjectInfoVO projectInfoVO = rm.getProjectVO();
        LabelComponent labelComponent = labelCM.get(entity);
        DimensionsComponent component = dimensionsCM.get(entity);
        if (component.width == 0 && component.height == 0) {
            Pool<GlyphLayout> layoutPool = Pools.get(GlyphLayout.class);
            GlyphLayout layout = layoutPool.obtain();
            layout.setText(labelComponent.cache.getFont(), labelComponent.getText());
            component.width = layout.width / projectInfoVO.pixelToWorld;
            component.width += (component.width * 20) / 100;
            component.height = layout.height / projectInfoVO.pixelToWorld;
            component.height += (component.height * 40) / 100;
            layoutPool.free(layout);
        }
    }
}
