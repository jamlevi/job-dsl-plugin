package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import hudson.model.AbstractBuild;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.RunAction;
import hudson.util.XStream2;
import javaposse.jobdsl.dsl.GeneratedJob;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class GeneratedJobsBuildAction implements RunAction {
    public final Set<GeneratedJob> modifiedJobs;

    private transient AbstractBuild owner;
    private LookupStrategy lookupStrategy = LookupStrategy.JENKINS_ROOT;

    public GeneratedJobsBuildAction(Collection<GeneratedJob> modifiedJobs, LookupStrategy lookupStrategy) {
        this.modifiedJobs = Sets.newLinkedHashSet(modifiedJobs);
        this.lookupStrategy = lookupStrategy;
    }

    /**
     * No task list item.
     */
    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "Generated Items";
    }

    public String getUrlName() {
        return "generatedJobs";
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onAttached(Run run) {
        if (run instanceof AbstractBuild) {
            owner = (AbstractBuild) run;
        }
    }

    @Override
    public void onBuildComplete() {
    }

    public LookupStrategy getLookupStrategy() {
        return lookupStrategy == null ? LookupStrategy.JENKINS_ROOT : lookupStrategy;
    }

    public Collection<GeneratedJob> getModifiedJobs() {
        return modifiedJobs;
    }

    public Set<Item> getItems() {
        Set<Item> result = Sets.newLinkedHashSet();
        if (modifiedJobs != null) {
            for (GeneratedJob job : modifiedJobs) {
                Item item = getLookupStrategy().getItem(owner.getProject(), job.getJobName(), Item.class);
                if (item != null) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    // TODO Once we depend on Jenkins version 1.509.3 or higher we can implement the RunAction2 interface to set the AbstractBuild on load, instead of using this Converter.
    public static class ConverterImpl extends XStream2.PassthruConverter<GeneratedJobsBuildAction> {
        public ConverterImpl(XStream2 xStream) {
            super(xStream);
        }

        @Override
        protected void callback(GeneratedJobsBuildAction action, UnmarshallingContext context) {
            Iterator keys = context.keys();
            while (keys.hasNext()) {
                Object run = context.get(keys.next());
                if (run instanceof AbstractBuild) {
                    action.owner = (AbstractBuild) run;
                    return;
                }
            }
        }
    }
}
