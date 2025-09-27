package pages;

import actions.UIActions;
import mobile.android.AndroidActions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;

public class BasePage {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());
    protected final UIActions uiActions;
    protected final AndroidActions androidActions;

    public BasePage(UIActions uiActions) {
        this.uiActions = uiActions;
        this.androidActions = new AndroidActions(uiActions);
    }

    public void getTitle() throws InterruptedException {
        System.out.println(androidActions);
    }

    public void switchToWeb() {
        List<String> contextList = androidActions.getContextHandles();
        String webContext = contextList.stream()
                .filter(context -> context.toLowerCase().contains("web"))
                .findFirst()
                .orElse(null);
        androidActions.switchToContext(webContext);
    }
}
